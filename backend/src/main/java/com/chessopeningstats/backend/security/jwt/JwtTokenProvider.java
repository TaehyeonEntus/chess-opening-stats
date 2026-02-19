package com.chessopeningstats.backend.security.jwt;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final AccountService accountService;
    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(AccountService accountService, @Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long validityInMilliseconds) {
        this.accountService = accountService;
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.validityInMilliseconds);

        return Jwts.builder()
                .setSubject(userDetails.getAccountId())
                .claim("auth", authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Account account = accountService.getAccount(Long.parseLong(claims.getSubject()));

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        CustomUserDetails userDetails = new CustomUserDetails(account, authorities);

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException
            return false;
        }
    }
}
