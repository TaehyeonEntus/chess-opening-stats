package com.chessopeningstats.backend.web.view;

import com.chessopeningstats.backend.security.CustomUserDetails;
import com.chessopeningstats.backend.usecase.GetHomeViewUseCase;
import com.chessopeningstats.backend.web.view.dto.home.HomeView;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/views")
@RequiredArgsConstructor
public class ViewController {
    private final GetHomeViewUseCase getHomeViewUseCase;

    @GetMapping("/home")
    public HomeView getHomeView(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return getHomeViewUseCase.getHomeView(userDetails.getId());
    }
}
