package com.example.trendlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 저장: refreshToken을 이메일을 key로 사용해 저장
    public void saveRefreshToken(String email, String refreshToken,long expirationMillis) {
        redisTemplate.opsForValue().set(email,refreshToken,expirationMillis, TimeUnit.MILLISECONDS);
    }

    //조회: email로 저장된 refreshToken 확인
    public String getRefreshToken(String email) {
        Object token = redisTemplate.opsForValue().get(email);
        return token!=null?token.toString():null;
    }

    //삭제: 로그아웃 시 refreshToken 제거
    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }

    //검증: 요청받은 refreshToken이 저장된 값과 일치하는지 확인
    public boolean isValidRefreshToken(String email,String refreshToken) {
        String savedToken=getRefreshToken(email);
        return refreshToken.equals(savedToken);
    }

}
