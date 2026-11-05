package com.jseed.web.starter.filter;


import com.jseed.web.starter.xss.XssHttpServletRequestWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filters requests for XSS payloads.
 *
 * @author cyrus
 */
public class XssFilter implements Filter {
    /**
     * Excluded URLs
     */
    public List<String> excludes = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        if (StringUtils.isNotEmpty(tempExcludes)) {
            String[] url = tempExcludes.split(",");
            for (int i = 0; i < url.length; i++) {
                excludes.add(url[i]);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (handleExcludeURL(req, resp)) {
            chain.doFilter(request, response);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(xssRequest, response);
    }

    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
        String url = request.getServletPath();
        String method = request.getMethod();
        // Skip GET and DELETE requests.
        if (method == null || method.matches("GET") || method.matches("DELETE")) {
            return true;
        }
        if(StringUtils.isBlank(url)){
            return false;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        for (String pattern : excludes) {
            if (matcher.match(pattern, url)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
