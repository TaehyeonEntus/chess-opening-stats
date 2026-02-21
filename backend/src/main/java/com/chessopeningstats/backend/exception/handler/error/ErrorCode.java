package com.chessopeningstats.backend.exception.handler.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(400, "C001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "C002", "서버 내부 오류가 발생했습니다."),

    // Domain - Player
    PLAYER_NOT_FOUND(404, "P001", "플레이어를 찾을 수 없습니다."),

    // Domain - PlayerAccount
    PLAYER_ACCOUNT_NOT_FOUND(404, "P002", "해당 플랫폼 플레이어를 찾을 수 없습니다."),

    // Domain - Game
    INVALID_PGN_FORMAT(400, "G001", "PGN 형식이 올바르지 않습니다."),
    UNRECOGNIZED_GAME_DATA(400, "G002", "인식할 수 없는 게임 데이터가 포함되어 있습니다."),
    GAME_NOT_FOUND(404, "G003", "게임을 찾을 수 없습니다."),

    // Domain - GamePlayer
    GAME_PLAYER_NOT_FOUND(404, "GP001", "게임 플레이어를 찾을 수 없습니다."),

    // Security
    USERNAME_ALREADY_EXISTS(409, "S001", "이미 존재하는 아이디 입니다."),
    NICKNAME_ALREADY_EXISTS(409, "S002", "이미 존재하는 닉네임 입니다."),
    INVALID_PASSWORD(400, "S003", "비밀번호가 올바르지 않습니다."),
    PASSWORD_MISMATCH(400, "S004", "비밀번호가 일치하지 않습니다."),

    // Application
    UNSUPPORTED_PLATFORM(400, "A001", "지원하지 않는 플랫폼입니다."),
    SYNC_ALREADY_RUNNING(409, "A004", "이미 동기화 작업이 진행 중입니다."),
    PLAYER_ALREADY_LINKED(409, "A005", "이미 연동된 계정입니다."),

    // Infra
    REMOTE_API_TIMEOUT(504, "I001", "외부 API 호출 시간이 초과되었습니다."),
    REMOTE_API_ERROR(502, "I002", "외부 API 서버에 오류가 발생했습니다."),
    DATA_CONFLICT(409, "I003", "데이터 충돌이 발생했습니다. (e.g. Unique key violation)");

    private final int status;
    private final String code;
    private final String message;
}
