package com.chessopeningstats.backend.application.account;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.checkAccountClient.CheckAccountClientRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCheckService {
    private final CheckAccountClientRegistry clientRegistry;

    public boolean checkAccount(String username, Platform platform){
        return clientRegistry.getClient(platform).checkAccount(username);
    }
}
