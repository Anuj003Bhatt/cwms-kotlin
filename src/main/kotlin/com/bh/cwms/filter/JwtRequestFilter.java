package com.bh.cwms.filter;

import com.bh.cwms.model.dto.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static com.bh.cwms.util.JwtUtilKt.extractAllClaims;

public class JwtRequestFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authorization = request.getHeader(AUTHORIZATION_HEADER);
        String username = null;
        Claims claims = null;
        String jwt;

        if (null != authorization && authorization.startsWith("Bearer ")) {
            jwt = authorization.substring(7);
            claims = extractAllClaims(jwt);
            username = claims.getSubject();
        }
        if (null != username && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserContext user = new UserContext(UUID.fromString(claims.get("userId").toString()));
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
