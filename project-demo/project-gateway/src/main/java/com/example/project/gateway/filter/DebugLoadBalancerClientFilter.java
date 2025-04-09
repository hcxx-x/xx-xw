package com.example.project.gateway.filter;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InterfaceAddress;
import java.net.SocketException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.*;

/**
 * 用于在测试环境和开发环境中选择同一网段的服务实例，用于解决跨网段的问题（开发环境启动项目注册到测试环境注册中心）。
 */
@Slf4j
@Component
public class DebugLoadBalancerClientFilter implements GlobalFilter, Ordered {

    public static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10100 - 1;

    private final Random random = new Random(); // 将Random对象声明为类成员变量


    protected final LoadBalancerClient loadBalancer;

    private GatewayLoadBalancerProperties properties;

    List<InterfaceAddress> interfaceAddresses;

    private static final Map<Boolean, Set<String>> hostMap = new HashMap<>();

    @Autowired
    private DiscoveryClient discoveryClient;

    public DebugLoadBalancerClientFilter(LoadBalancerClient loadBalancer,
                                         GatewayLoadBalancerProperties properties) {
        this.loadBalancer = loadBalancer;
        this.properties = properties;
        interfaceAddresses = NetUtil.getNetworkInterfaces().stream().filter(n -> {
            try {
                return !n.isLoopback();
            } catch (SocketException e) {
                return true;
            }
        }).filter(n -> !n.isVirtual()).filter(n -> CollUtil.isNotEmpty(n.getInterfaceAddresses())).map(n -> n.getInterfaceAddresses().get(0)).filter(i -> Validator.isIpv4(i.getAddress().getHostAddress())).collect(Collectors.toList());
        log.info("this host interfaceAddresses: {}", interfaceAddresses);
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 仅限测试环境生效
        if (CharSequenceUtil.endWithIgnoreCase("prd", SpringUtil.getActiveProfile()) || CharSequenceUtil.endWithIgnoreCase("prod", SpringUtil.getActiveProfile())) {
            if (log.isDebugEnabled()) {
                log.debug("环境:{}，跳过DebugLoadBalancerClientFilter", SpringUtil.getActiveProfile());
            }
            return chain.filter(exchange);
        }
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        if (url == null || (!"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix))) {
            log.debug("请求uri不是lb协议，跳过DebugLoadBalancerClientFilter");
            return chain.filter(exchange);
        }
        // preserve the original url
        addOriginalRequestUrl(exchange, url);

        if (log.isTraceEnabled()) {
            log.trace("LoadBalancerClientFilter url before: " + url);
        }

        final ServiceInstance instance = choose(exchange);

        if (instance == null) {
            throw NotFoundException.create(properties.isUse404(), "Unable to find instance for " + url.getHost());
        }

        URI uri = exchange.getRequest().getURI();

        // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
        // if the loadbalancer doesn't provide one.
        String overrideScheme = instance.isSecure() ? "https" : "http";
        if (schemePrefix != null) {
            overrideScheme = url.getScheme();
        }

        URI requestUrl = loadBalancer.reconstructURI(
                new DelegatingServiceInstance(instance, overrideScheme), uri);

        if (log.isTraceEnabled()) {
            log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
        }

        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        return chain.filter(exchange);
    }

    /**
     * 选择服务实例。
     *
     * @param exchange 服务器Web交换对象，用于获取请求的URL属性。
     * @return 如果存在与网关在同一网段的服务实例，随机选择一个返回；如果没有找到合适的实例，使用负载均衡器选择实例。
     */
    protected ServiceInstance choose(ServerWebExchange exchange) {
        // 获取所有服务实例
        URI uri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        if (uri == null) {
            return null;
        }
        // 过滤出与网关在同一网段的服务实例
        final String serviceName = uri.getHost();
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances == null || instances.isEmpty()) {
            // 记录日志或抛出异常
            return null;
        }

        // 过滤出与网关在同一网段的服务实例
        List<ServiceInstance> instanceInRanges = instances.stream()
                .filter(instance -> isMatchedHost(serviceName, instance))
                .collect(Collectors.toList());
        // 如果存在与网关在同一网段的服务实例，随机选择一个返回
        if (CollUtil.isNotEmpty(instanceInRanges)) {
            ServiceInstance serviceInstance = instanceInRanges.get(random.nextInt(instanceInRanges.size()));
            log.info("{} was chosen from matched instances(size:{})", getInstanceDesc(serviceName, serviceInstance), instanceInRanges.size());
            return serviceInstance;
        }

        // 如果没有找到合适的实例，使用负载均衡器选择实例
        ServiceInstance choose = loadBalancer.choose(serviceName);
        log.info("loadBalancer.choose instance: {}", getInstanceDesc(serviceName, choose));
        return choose;
    }


    /**
     * 检查服务实例的主机是否匹配。
     *
     * @param serviceName
     * @param instance    需要检查的服务实例。
     * @return 如果主机匹配，返回true；否则返回false。
     */
    private boolean isMatchedHost(String serviceName, ServiceInstance instance) {
        // 获取主机地址
        final String host = instance.getHost();
        // 获取主机描述
        final String instanceDesc = getInstanceDesc(serviceName, instance);
        // 定义两个集合用于存储匹配和不匹配的主机
        final Set<String> matchedHosts = hostMap.computeIfAbsent(true, k -> ConcurrentHashMap.newKeySet());
        final Set<String> unmatchedHosts = hostMap.computeIfAbsent(false, k -> ConcurrentHashMap.newKeySet());        if(matchedHosts.contains(host)){
            log.debug("{} 曾被判定已匹配，直接返回", instanceDesc);
            return true;
        }
        if(unmatchedHosts.contains(host)){
            log.debug("{} 曾被判定不匹配，过滤", instanceDesc);
            return false;
        }

        synchronized (this) { // 确保线程安全
            for (InterfaceAddress address : interfaceAddresses) {
                if (isInRange(host, address) || isCanSendData(instance.getHost(), instance.getPort()) || isCanPingSuccess(host)) {
                    matchedHosts.add(host);
                    return true;
                }
            }
            for (String localIpv4 : NetUtil.localIpv4s()) {
                if (isInRange(host, localIpv4, 24)) {
                    matchedHosts.add(host);
                    return true;
                }
            }
            log.info("{}, isMatchedHost: false", instanceDesc);
            unmatchedHosts.add(host);
            return false;
        }
    }
    
    private static boolean isCanPingSuccess(String ip){
        boolean pingResult = NetUtil.ping(ip, 500);
        log.info("host:{}, ping result: {}", ip, pingResult);
        return pingResult;
    }

    private static boolean isInRange(String ip, InterfaceAddress address) {
        return isInRange(ip, address.getBroadcast().getHostAddress(), address.getNetworkPrefixLength());
    }

    private static boolean isInRange(String ip, String host, int mark) {
        boolean inRange = NetUtil.isInRange(ip, String.format("%s/%s", host, mark));
        log.info("host:{}  net:[{}/{}] matcher result: {}", ip, host, mark, inRange);
        return inRange;
    }
    
    private static boolean isCanSendData(String host, int port){
        try{
            NetUtil.netCat(host, port, "".getBytes());
        } catch (IORuntimeException e) {
            log.info("host:{}, port:{} can not connect", host, port);
            return false;
        }
        log.info("host:{}, port:{} can send data", host, port);
        return true;
    }

    private String getInstanceDesc(String serviceName, ServiceInstance instance){
        return String.format("Service[%s]'s instance[%s:%d]", serviceName, instance.getHost(), instance.getPort());
    }

}