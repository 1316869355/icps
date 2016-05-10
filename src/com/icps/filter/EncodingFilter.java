package com.icps.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet Filter implementation class EncodingFilter
 */
//@WebFilter("/*")
public class EncodingFilter implements Filter {
	FilterConfig config = null;
    private String targetEncoding = "UTF-8";
    /**
     * Default constructor. 
     */
    public EncodingFilter() {
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)req;
        request.setCharacterEncoding(targetEncoding);
        resp.setCharacterEncoding(targetEncoding);
		// pass the request along the filter chain
		chain.doFilter(req, resp);
	}
	public void init(FilterConfig fConfig) throws ServletException {
	        this.config = fConfig;
	        this.targetEncoding = config.getInitParameter("encoding");
	}

}
