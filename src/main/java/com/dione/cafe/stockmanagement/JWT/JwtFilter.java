package com.dione.cafe.stockmanagement.JWT;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class JwtFilter extends OncePerRequestFilter {

    //Nous allons effectuer ici la validation du Token

    private final JwtUtil jwtUtil;

    private final CustomerUsersDetailsService customerUsersDetailsService;

    Claims claims = null;
    private String userName = null;

    public JwtFilter(JwtUtil jwtUtil, CustomerUsersDetailsService customerUsersDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customerUsersDetailsService = customerUsersDetailsService;
    }


    //FILTRE
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if (httpServletRequest.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }else {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring(7);
                userName = jwtUtil.extracUsername(token);
                claims = jwtUtil.extractAllClaims(token);
            }

            if (userName !=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = customerUsersDetailsService.loadUserByUsername(userName);
                if (jwtUtil.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    //Vérifier si l'utilisateur est un admin
    public boolean isAdmin(){
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    //Vérifier si l'utilisateur est un user simple
    public boolean isUser(){
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }
}
