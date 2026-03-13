package com.chessopeningstats.backend.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class CsrfConfig {

    @Value("${csrf.cookie-domain}")
    private String cookieDomain;

    @Value("${csrf.cookie-sameSite}")
    private String cookieSameSite;

    @Value("${csrf.cookie-secure}")
    private boolean cookieSecure;

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repo.setCookieCustomizer(cookie -> {
            cookie.domain(cookieDomain);
            cookie.sameSite(cookieSameSite);
            cookie.secure(cookieSecure);
        });
        return repo;
    }

    @Bean
    public OncePerRequestFilter csrfCookieSetter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

                if (csrfToken != null) {
                    csrfToken.getToken();
                }

                filterChain.doFilter(request, response);
            }
        };
    }
}
