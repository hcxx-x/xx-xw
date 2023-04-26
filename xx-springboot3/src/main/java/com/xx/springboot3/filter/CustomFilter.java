package com.xx.springboot3.filter;

import jakarta.servlet.*;

import java.io.IOException;

/**
 * @author hanyangyang
 * @since 2023/4/25
 */
public class CustomFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
