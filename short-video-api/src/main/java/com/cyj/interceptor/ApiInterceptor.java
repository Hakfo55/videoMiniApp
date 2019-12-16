package com.cyj.interceptor;

import com.alibaba.druid.support.json.JSONUtils;
import com.cyj.utils.JsonUtils;
import com.cyj.utils.RedisOperator;
import com.cyj.utils.VideoJSONResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class ApiInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user_redis_session";

    public void returnErrorResponse(HttpServletResponse response, VideoJSONResult result) throws IOException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally {
            if (out!=null){
                out.close();
            }
        }
    }

    /**
     * 拦截请求，在controller调用之前
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        /**
         * 返回false：请求被拦截，返回
         * 返回true：请求ok，可以继续执行，放行
         */

        String userId = httpServletRequest.getHeader("userId");
        String userToken = httpServletRequest.getHeader("userToken");

        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String uniqueToken = redisOperator.get(USER_REDIS_SESSION + ":" + userId);
            //由于token的值是有返回到前端那里去，然后前端也可以拿得到，但是redis设置了过期时间，所以需要再一次判断
            //前端那边的值是一直在的，但是假设redis过期了，你是查不到的
            if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)){
                System.out.println("请登录...");

//                httpServletResponse.setContentType("text/json");
//                httpServletResponse.setCharacterEncoding("utf-8");
//                OutputStream outputStream = httpServletResponse.getOutputStream();
//                outputStream.write(JsonUtils.objectToJson(VideoJSONResult.errorTokenMsg("错误")).getBytes("utf-8"));
//                outputStream.flush();

                returnErrorResponse(httpServletResponse,new VideoJSONResult().errorTokenMsg("请登录...."));
                return false;
            }else {
                if (!userToken.equals(uniqueToken)){
                    System.out.println("账号在别处登录");
                    returnErrorResponse(httpServletResponse,new VideoJSONResult().errorTokenMsg("账号在别处登录"));
                    return false;
                }
            }
        }else {
            System.out.println("请登录...");
            returnErrorResponse(httpServletResponse,new VideoJSONResult().errorTokenMsg("请登录...."));
            return false;
        }

        return true;
    }

    /**
     * 请求controller之后，渲染视图之前
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求controller之后，视图渲染之后
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
