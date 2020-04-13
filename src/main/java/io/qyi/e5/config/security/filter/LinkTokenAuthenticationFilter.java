package io.qyi.e5.config.security.filter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.qyi.e5.config.security.UsernamePasswordAuthenticationToken;
import io.qyi.e5.util.SpringUtil;
import io.qyi.e5.util.redis.RedisUtil;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Token校验
 *
 * @program: e5
 * @description:
 * @author: 落叶随风
 * @create: 2020-04-05 00:42
 **/
public class LinkTokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = httpServletRequest.getHeader("token");
        if (token != null) {
            RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);
            if (redisUtil.hasKey("token:" + token)) {
                Map<Object, Object> userInfo = redisUtil.hmget("token:" +token);
                //        将未认证的Authentication转换成自定义的用户认证Token
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken();
                UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken(userInfo.get("github_name").toString(),
                        userInfo.get("avatar_url").toString(), (int) userInfo.get("github_id"), AuthorityUtils.createAuthorityList("user"));
                authenticationToken1.setDetails(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken1);
                System.out.println("完成授权");
            }
        }
        System.out.println("--------------Token鉴权---------------");
        /*设置跨域*/
        HttpServletResponse response = httpServletResponse;
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    public void sendJson(HttpServletResponse httpServletResponse, Object o) throws IOException {
        Gson gson = new Gson();
        String s = gson.toJson(o);
        PrintWriter writer = httpServletResponse.getWriter();
        writer.write(s);
        writer.flush();
    }
}
