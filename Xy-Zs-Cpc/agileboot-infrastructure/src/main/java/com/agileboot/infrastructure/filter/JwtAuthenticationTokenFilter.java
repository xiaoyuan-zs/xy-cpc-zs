package com.agileboot.infrastructure.filter;

import com.agileboot.infrastructure.security.AuthenticationUtils;
import com.agileboot.infrastructure.web.domain.login.LoginUser;
import com.agileboot.infrastructure.web.service.TokenService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * token过滤器 验证token有效性
 * 继承OncePerRequestFilter类的话  可以确保只执行filter一次， 避免执行多次
 * @author ruoyi
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @NonNull
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null && AuthenticationUtils.getAuthentication() == null) {
            tokenService.verifyToken(loginUser);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser,
                null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            log.debug("request process in jwt token filter. get login user id: {}", loginUser.getUserId());
        }
        chain.doFilter(request, response);
    }

}
