package com.example.trendlog.service.user;

import com.example.trendlog.domain.user.User;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.MailErrorCode;
import com.example.trendlog.global.exception.code.UserErrorCode;
import com.example.trendlog.global.exception.user.UserNotFoundException;
import com.example.trendlog.repository.user.UserRepository;
import com.example.trendlog.service.external.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private final long RESET_TOKEN_EXPIRATION_MINUTES = 30;

    /**
     * 비밀번호 재설정 이메일 전송
     */
    public void sendResetEmail(String email) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        String token = UUID.randomUUID().toString();

        String redisKey = buildResetKey(token);

        redisTemplate.opsForValue().set(redisKey, email, Duration.ofMinutes(RESET_TOKEN_EXPIRATION_MINUTES));

        //일단 이거 프론트랑 어떤식으로 구현할지 말은 안했지만,,, 웹뷰/페이지 URL로 사용하는 방식이 일반적인 것 같아서 구현했어요
        String resetLink = "http://localhost:3000/reset-password?token=" + token;

        String subject = "Trendlog 비밀번호 재설정 링크";
        String htmlContent = "<p>안녕하세요, Trendlog입니다.</p>" +
                "<p>비밀번호를 재설정하시려면 아래 링크를 클릭해주세요. 이 링크는 30분간 유효합니다.</p>" +
                "<a href=\"" + resetLink + "\">비밀번호 재설정하기</a>";
        // 이메일 전송
        try {
            mailService.sendHtmlMessage(email, subject, htmlContent);
        } catch (MessagingException e) {
            throw new AppException(MailErrorCode.MAIL_SEND_FAIL); // 직접 정의한 에러코드 사용
        }
    }

    /**
     * 비밀번호 재설정
     */
    public void resetPassword(String token, String newPassword){
        String redisKey = buildResetKey(token);
        String email=redisTemplate.opsForValue().get(redisKey);

        if (email==null){
            throw new AppException(UserErrorCode.PASSWORD_RESET_TOKEN_EXPIRED); //USER-017
        }

        User user=userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new); //USER-001
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redisTemplate.delete(redisKey); //토큰 1회용이니까 즉시 삭제
    }

    private String buildResetKey(String token) {
        return "reset:pw:" + token;
    }
}
