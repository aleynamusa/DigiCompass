package com.digicompass.backend.application.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JWTAuthFilter implements Filter {

    private final JWTToken jwt;

    public JWTAuthFilter(JWTToken jwt) {
        this.jwt = jwt;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {

        try{
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            String token = null;

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }


            if (token != null) {
                try {
                    Claims claims = jwt.extractAllClaims(token);
                    String username = claims.getSubject();

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(username, null, List.of());

                    SecurityContextHolder.getContext().setAuthentication(auth);

                } catch (Exception _) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }


            chain.doFilter(req, res);
        }
        catch(IOException | ServletException e){
            throw new ServletException("Error processing JWT authentication", e);
        }

    }
}
