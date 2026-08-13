package vn.edu.crs.registrationservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Lấy Authorization header
        String authHeader = request.getHeader("Authorization");

        // Không có token hoặc không phải Bearer token
        // thì chưa xác thực, tiếp tục qua SecurityConfig xử lý
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bỏ chữ "Bearer " ở đầu
        String token = authHeader.substring(7);

        try {

            // Tạo key từ secret giống auth-service
            SecretKey key = Keys.hmacShaKeyFor(
                    secret.getBytes(StandardCharsets.UTF_8)
            );

            // Verify chữ ký + đọc payload JWT
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // auth-service lưu username trong subject
            String username = claims.getSubject();

            // auth-service lưu role trong claim "role"
            String role = claims.get("role", String.class);

            // Chuyển ADMIN -> ROLE_ADMIN
            // STUDENT -> ROLE_STUDENT
            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority(
                            "ROLE_" + role
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(authority)
                    );

            // Báo cho Spring Security rằng user đã authenticated
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception e) {

            // JWT sai, hết hạn, sai chữ ký, sai secret...
            // thì xóa authentication
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}