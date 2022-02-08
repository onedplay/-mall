package com.wfz.shop.sso.handler;

import com.wfz.shop.common.context.ThreadContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 写入request/response
 */
@Slf4j
@Component
public class RequestInterceptorAdapter implements AsyncHandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        ThreadContextHolder.setHttpResponse(response);
        ThreadContextHolder.setHttpRequest(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

        ThreadContextHolder.setHttpResponse(response);
        ThreadContextHolder.setHttpRequest(request);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        ThreadContextHolder.remove();
    }
}
