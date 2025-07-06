package com.example.trendlog.service;

import com.example.trendlog.domain.User;
import com.example.trendlog.dto.auth.*;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.ErrorCode;
import com.example.trendlog.global.exception.code.CommonErrorCode;
import com.example.trendlog.global.exception.user.*;
import com.example.trendlog.global.security.jwt.JwtTokenProvider;
import com.example.trendlog.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입
     */
    public void signup(UserSignupRequest request){
        //이메일 중복 확인
        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException(); // USER-004
        }
        User user=User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .provider("local")
                .signDate(LocalDateTime.now())
                .publicProfile(true)
                .locationTracing(false)
                .alarm(true)
                .build();
        userRepository.save(user);
    }
    /**
     * 로그인
     */
    public LoginResponse login(UserLoginRequest request){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    )
            );
            User user=userRepository.findByEmail(request.getEmail())
                    .orElseThrow(InvalidLoginInfoException::new);//USER-002
            String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

            //Redis에 RefreshToken 저장
            long refreshTokenExpiration=jwtTokenProvider.getRefreshTokenExpirationMillis();
            refreshTokenService.saveRefreshToken(user.getEmail(),refreshToken,refreshTokenExpiration);
            return new LoginResponse(accessToken, refreshToken);
        }catch(AuthenticationException e){
            throw new InvalidLoginInfoException(); // USER-002
        }catch (Exception e){
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 로그아웃
     */
    public void logout(String email){
        refreshTokenService.deleteRefreshToken(email);
    }

    /**
     * Access Token 재발급
     */
    public TokenRefreshResponse refresh(TokenRefreshRequest request){
        String refreshToken= request.getRefreshToken();
        try{
            //토큰 유효성 검사
            if(!jwtTokenProvider.validateToken(refreshToken)){
                throw new InvalidRefreshTokenException(); //USER-010
            }

            //이메일 추출
            String email = jwtTokenProvider.extractSubject(refreshToken);

            //저장된 refreshToken과 일치하는지 확인
            if(!refreshTokenService.isValidRefreshToken(email,refreshToken)){
                throw new InvalidRefreshTokenException(); //USER-010
            }

            //새로운 토큰 생성
            String newAccessToken = jwtTokenProvider.generateAccessToken(email);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(email);

            //Redis에 새로운 refreshTken 저장
            refreshTokenService.saveRefreshToken(email,newRefreshToken, jwtTokenProvider.getRefreshTokenExpirationMillis());

            return new TokenRefreshResponse(newAccessToken,newRefreshToken);
        }catch(JwtException e){
            throw new InvalidRefreshTokenException(); // USER-010
        }catch (Exception e){
            throw new AppException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

}
