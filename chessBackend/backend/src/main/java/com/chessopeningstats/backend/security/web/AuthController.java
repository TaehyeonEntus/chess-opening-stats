package com.chessopeningstats.backend.security.web;

import com.chessopeningstats.backend.security.AuthService;
import com.chessopeningstats.backend.security.web.dto.LoginRequest;
import com.chessopeningstats.backend.security.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String jwt = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("accessToken", jwt)
                .httpOnly(true)      // 🔥 JS 접근 차단
                .secure(false)       // HTTPS에서만 전송 (개발 중이면 false 가능)
                .path("/")
                .sameSite("Strict")  // CSRF 방어
                .maxAge(60 * 60)     // 1시간
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);
        return ResponseEntity.ok().body("registered successfully");
    }
}
