package com.digicompass.backend.unit.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
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
                    Long id = claims.get("id", Long.class);
                    request.setAttribute("user", username);
                    request.setAttribute("id", id);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            chain.doFilter(req, res);
        }
        catch(IOException | ServletException e){
            throw e;
        }

    }
}
