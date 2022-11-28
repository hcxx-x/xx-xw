package com.xx.springboot.redis.config;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>Redis工具类</p>
 * ----------------------
 * 唯一构造器需要注入 redisTemplate；
 * 如果redis不可用，统一返回 null
 * ----------------------
 *
 * @author 土味儿
 * Date 2022/9/12
 * @version 1.0
 */
@Component
public class RedisUtil<V>{
    // ============================= 设置template ============================


    private static RedisTemplate redisTemplate;

    /**
     * 唯一构造器
     *
     * @param redisTemplate
     */
    /*public RedisUtil(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }*/

    // ============================= 通用 ============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public Boolean expire(@NonNull String key, @NonNull long time) {
        try {
            if (time < 0) {
                return false;
            }
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            // redis不可用时
            return null;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public Long getExpire(@NonNull String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            // redis不可用时
            return null;
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(@NonNull String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            // redis不可用时
            return null;
        }
    }

    // ------------- 删除key ---------------
    /**
     * 删除缓存（数组格式）
     *
     * @param key 可以传一个值 或多个
     * @return 被删数量
     */
    @SuppressWarnings("unchecked")
    public Long del(@NonNull String... key) {
        try {
            return redisTemplate.delete((Collection<String>) CollectionUtils.arrayToList(key));
        } catch (Exception e) {
            // redis不可用时
            return null;
        }
    }

    /**
     * 删除缓存（List格式）
     *
     * @param keys list集合
     * @return 被删数量
     */
    public Long del(@NonNull List<String> keys) {
        try {
            return redisTemplate.delete(keys);
        } catch (Exception e) {
            // redis不可用时
            return null;
        }
    }


    /**
     * 删除以 prefix 为前缀的所有缓存
     * 如：prefix = abc，则可以删除 abc，abc123，abc_ed，abc_234_ecd
     *
     * @param prefix
     * @return
     */
    public Long delByPrefix(String prefix) {
        try {
            return redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(prefix + "*")));
        } catch (Exception e) {
            // redis不可用时
            return null;
        }
    }

    /**
     * 删除当前库中所有缓存
     */
    public Long delAll() {
        try {
            return redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys("*")));
        } catch (Exception e) {
            // redis不可用时
            return null;
        }
    }

    // ============================ String =============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public V get(@NonNull String key) {
        try {
            return ((RedisTemplate<String,V>)redisTemplate).opsForValue().get(key);
        } catch (Exception e) {
            // redis不可用时
            return null;
        }
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public Boolean set(@NonNull String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public Boolean set(@NonNull String key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     */
    public Long incr(@NonNull String key, long delta) {
        try {
            if (delta < 0) {
                throw new RuntimeException("递增因子必须大于0");
            }
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     */
    public Long decr(@NonNull String key, long delta) {
        try {
            if (delta < 0) {
                throw new RuntimeException("递增因子必须大于0");
            }
            return redisTemplate.opsForValue().increment(key, -delta);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    // ================================ Map =================================

    /**
     * HashGet
     *
     * @param key  键
     * @param item 项
     */
    public Object hget(@NonNull String key, @NonNull String item) {
        try {
            return redisTemplate.opsForHash().get(key, item);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(@NonNull String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     */
    public Boolean hmsetByMap(@NonNull String key, @NonNull Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public Boolean hmsetByMap(@NonNull String key, @NonNull Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public Boolean hset(@NonNull String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public Boolean hset(@NonNull String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键
     * @param item 项 可以使多个 不能为null
     * @return 删除数量
     */
    public Long hdel(@NonNull String key, @NonNull Object... item) {
        try {
            return redisTemplate.opsForHash().delete(key, item);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键
     * @param item 项
     * @return true 存在 false不存在
     */
    public Boolean hHasKey(@NonNull String key, String item) {
        try {
            return redisTemplate.opsForHash().hasKey(key, item);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     */
    public Double hincr(@NonNull String key, String item, double by) {
        try {
            return redisTemplate.opsForHash().increment(key, item, by);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     */
    public Double hdecr(@NonNull String key, String item, double by) {
        try {
            return redisTemplate.opsForHash().increment(key, item, -by);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    // ============================ set =============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     */
    public Set<V> sGet(@NonNull String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public Boolean sHasKey(@NonNull String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSet(@NonNull String key, @NonNull V... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSetAndTime(@NonNull String key, long time, @NonNull V... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key
     * @param time
     * @param values Set集合
     * @return
     */
    public Long sSetAndTime(@NonNull String key, long time, Set<V> values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, (V[]) values.toArray());
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public Long sGetSetSize(@NonNull String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long setRemove(@NonNull String key, @NonNull Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    // =============================== list =================================

    /**
     * 获取list缓存的全部内容
     *
     * @param key 键
     */
    public List<V> lGetAll(@NonNull String key) {
        try {
            return redisTemplate.opsForList().range(key, 0, -1);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     */
    public List<V> lGet(@NonNull String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public Long lSize(@NonNull String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public V lGetIndex(@NonNull String key, long index) {
        try {
            return (V) redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 通过对象获取index值
     *
     * @param key
     * @param value
     * @return
     */
    public Long lGetIndex(@NonNull String key, V value) {
        try {
            return redisTemplate.opsForList().indexOf(key, value);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 弹出list中的第一个值；并删除
     *
     * @param key 键
     */
    public V lPopFirst(@NonNull String key) {
        try {
            return (V) redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public Long lSet(@NonNull String key, V value) {
        try {
            return redisTemplate.opsForList().rightPush(key, value);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     */
    public Long lSet(@NonNull String key, V value, long time) {
        try {
            Long count = redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public Long lSetByList(@NonNull String key, List<V> value) {
        try {
            return redisTemplate.opsForList().rightPushAll(key, value);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public Long lSetByList(@NonNull String key, List<V> value, long time) {
        try {
            Long count = redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public Boolean lUpdateIndex(@NonNull String key, long index, V value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 用 newValue 去替换 oldValue
     * ---------------------------
     * 原理：
     * 1、获取到 oldValue 的 index 值
     * 2、用 newValue 去替换 index 位置的值
     * ---------------------------
     * 注意：
     * 如果用 oldValue 去生成 newValue，注意㳀拷贝和深拷贝问题；
     * oldValue 要和 redis 中保存的一致才可以查出来；
     * <p>
     * 可以把该方法分解成两个方法：
     * Long lGetIndex(@NonNull String key,V value),
     * Boolean lUpdateIndex(@NonNull String key, long index, V value)
     * ---------------------------
     *
     * @param key
     * @param oldValue
     * @param newValue
     * @return
     */
    public Boolean lUpdate(@NonNull String key, V oldValue, V newValue) {
        try {
            Long index = redisTemplate.opsForList().indexOf(key, oldValue);
            redisTemplate.opsForList().set(key, index, newValue);
            return true;
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long lRemove(@NonNull String key, long count, V value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            //e.printStackTrace();
            // redis不可用时
            return null;
        }
    }

    @Resource
    public void setRedisTemplate(RedisTemplate<String, V> redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }

    private RedisTemplate<String,V> getRedisTemplate(){
        return  (RedisTemplate<String,V>)RedisUtil.redisTemplate;
    }
}

