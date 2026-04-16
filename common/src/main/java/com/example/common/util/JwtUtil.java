package com.example.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * JWT 工具类（公共模块）
 * 统一管理 JWT 生成与解析
 * 
 * 配置说明：
 * - jwt.secret: JWT签名密钥，必须通过环境变量 JWT_SECRET 配置
 * - jwt.expire: JWT过期时间（毫秒），默认24小时
 * 
 * 安全要求：
 * 1. JWT Secret必须至少32位随机字符串
 * 2. 生产环境禁止使用默认值
 * 3. 定期更换Secret
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expire:86400000}") // 24小时 = 24 * 3600 * 1000
    private long expire;

    /**
     * 初始化校验JWT Secret安全性
     */
    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.trim().isEmpty()) {
            String errorMsg = "JWT Secret未配置，请设置环境变量 JWT_SECRET";
            log.error(errorMsg);
            throw new IllegalStateException(errorMsg);
        }
        
        // 检查是否使用了不安全的默认值
        if ("gateway-secret".equals(secret)) {
            String warningMsg = "警告：使用了不安全的默认JWT Secret，生产环境必须使用强随机Secret";
            log.warn(warningMsg);
            // 生产环境应该抛出异常，这里先记录警告
            // throw new IllegalStateException("生产环境禁止使用默认JWT Secret");
        }
        
        // 建议Secret长度至少32位
        if (secret.length() < 32) {
            log.warn("JWT Secret长度建议至少32位，当前长度：{}", secret.length());
        }
        
        log.info("JWT工具类初始化完成，Secret长度：{}，过期时间：{}ms", secret.length(), expire);
    }
    
    /**
     * 生成 Token
     * @param userId 用户ID
     * @return JWT Token字符串
     */
    public String generate(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户ID不能为空且必须大于0");
        }
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire);
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 解析 Token
     * @param token JWT Token字符串
     * @return Claims对象
     * @throws io.jsonwebtoken.JwtException 如果Token无效或过期
     */
    public Claims parse(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token不能为空");
        }
        
        // 清理Bearer前缀
        String cleanToken = token.replace("Bearer ", "").trim();
        
        if (cleanToken.isEmpty()) {
            throw new IllegalArgumentException("Token内容为空");
        }
        
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(cleanToken)
                .getBody();
    }

    /**
     * 从 Token 获取用户ID
     * @param token JWT Token字符串
     * @return 用户ID
     * @throws NumberFormatException 如果Subject不是有效的数字
     */
    public Long getUserId(String token) {
        Claims claims = parse(token);
        String subject = claims.getSubject();
        
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Token中未包含用户ID");
        }
        
        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Token中的用户ID格式无效: " + subject, e);
        }
    }

    /**
     * 验证 Token 是否有效
     * @param token JWT Token字符串
     * @return true如果Token有效，false如果无效
     */
    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取JWT配置信息（用于监控）
     * @return 配置信息字符串
     */
    public String getConfigInfo() {
        return String.format("JWT配置 - Secret长度:%d, 过期时间:%dms", 
                secret.length(), expire);
    }
}
