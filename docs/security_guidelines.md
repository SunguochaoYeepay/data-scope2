# Security Guidelines

## Overview
This document outlines security requirements and best practices for the DataScope system to ensure data protection and secure access.

## Data Protection

### Password Encryption
```java
@Component
public class PasswordEncryptor {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    @Value("${security.encryption.key}")
    private String secretKey;

    public String encrypt(String password) {
        try {
            byte[] salt = generateSalt();
            byte[] iv = generateIV();
            SecretKey key = deriveKey(secretKey, salt);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            
            byte[] encrypted = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(
                ByteBuffer.allocate(salt.length + iv.length + encrypted.length)
                    .put(salt)
                    .put(iv)
                    .put(encrypted)
                    .array()
            );
        } catch (Exception e) {
            throw new SecurityException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedPassword) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);
            
            byte[] salt = new byte[SALT_LENGTH];
            byte[] iv = new byte[IV_LENGTH];
            byte[] encrypted = new byte[decoded.length - SALT_LENGTH - IV_LENGTH];
            
            buffer.get(salt);
            buffer.get(iv);
            buffer.get(encrypted);
            
            SecretKey key = deriveKey(secretKey, salt);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SecurityException("Decryption failed", e);
        }
    }

    private SecretKey deriveKey(String secret, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
```

### Sensitive Data Masking
```java
public enum MaskType {
    FULL,      // Replace all characters
    PARTIAL,   // Show first/last N characters
    CUSTOM     // Custom masking pattern
}

@Component
public class DataMasker {
    public String mask(String value, MaskType type, String pattern) {
        if (value == null) return null;
        
        switch (type) {
            case FULL:
                return "*".repeat(value.length());
            case PARTIAL:
                if (value.length() <= 4) return value;
                return value.substring(0, 2) + 
                       "*".repeat(value.length() - 4) + 
                       value.substring(value.length() - 2);
            case CUSTOM:
                return applyCustomMask(value, pattern);
            default:
                return value;
        }
    }
}
```

## Access Control

### Rate Limiting
```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimiter rateLimiter;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        String userId = extractUserId(request);
        if (!rateLimiter.tryAcquire(userId)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
        return true;
    }
}
```

### Query Timeout
```java
@Component
public class QueryExecutor {
    @Value("${query.timeout.seconds:30}")
    private int queryTimeout;
    
    public ResultSet executeQuery(String sql, Connection conn) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setQueryTimeout(queryTimeout);
            return stmt.executeQuery();
        }
    }
}
```

## Security Headers

### Configuration
```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("default-src 'self'")
                .and()
                .frameOptions().deny()
                .and()
                .httpStrictTransportSecurity()
                .and()
            .build();
    }
}
```

## Input Validation

### Request Validation
```java
@Validated
@RestController
public class DataSourceController {
    @PostMapping("/datasources")
    public ResponseEntity<DataSourceDTO> create(
            @Valid @RequestBody DataSourceDTO dto) {
        // Implementation
    }
}

public class DataSourceDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;
    
    @NotNull(message = "Port is required")
    @Range(min = 1, max = 65535, message = "Port must be between 1 and 65535")
    private Integer port;
    
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", 
             message = "Database name contains invalid characters")
    private String databaseName;
}
```

### SQL Injection Prevention
```java
@Component
public class SQLValidator {
    private static final Pattern UNSAFE_PATTERN = 
        Pattern.compile("(?i)(delete|drop|truncate|alter|create|exec|union)");
    
    public void validate(String sql) {
        if (UNSAFE_PATTERN.matcher(sql).find()) {
            throw new SecurityException("Unsafe SQL detected");
        }
    }
}
```

## Audit Logging

### Audit Events
```java
@Entity
@Table(name = "tbl_audit_log")
public class AuditLog {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String action;
    
    @Column(nullable = false)
    private String resource;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column
    private String ipAddress;
    
    @Column
    private String userAgent;
    
    @Column(length = 1000)
    private String details;
}
```

## Security Checklist

### Development
- [ ] Use HTTPS everywhere
- [ ] Implement input validation
- [ ] Encrypt sensitive data
- [ ] Use prepared statements
- [ ] Implement rate limiting
- [ ] Set security headers
- [ ] Enable CSRF protection
- [ ] Configure CORS properly
- [ ] Implement audit logging
- [ ] Use secure password storage

### Deployment
- [ ] Use secure configurations
- [ ] Enable firewalls
- [ ] Update dependencies
- [ ] Configure TLS properly
- [ ] Set up monitoring
- [ ] Configure backups
- [ ] Use secure protocols
- [ ] Implement access controls
- [ ] Regular security updates
- [ ] Incident response plan

### Testing
- [ ] Security testing
- [ ] Penetration testing
- [ ] Vulnerability scanning
- [ ] Load testing
- [ ] Audit log review
- [ ] Access control testing
- [ ] Input validation testing
- [ ] Error handling testing
- [ ] Authentication testing
- [ ] Authorization testing

## Security Best Practices

### Authentication
- Use strong password policies
- Implement MFA where possible
- Secure session management
- Token-based authentication
- Regular session timeout

### Authorization
- Role-based access control
- Principle of least privilege
- Regular access review
- Resource-level permissions
- Dynamic authorization

### Data Protection
- Encrypt data at rest
- Encrypt data in transit
- Secure key management
- Regular key rotation
- Data classification

### Monitoring
- Security event logging
- Real-time alerting
- Regular log review
- Anomaly detection
- Incident response

### Compliance
- Data privacy laws
- Industry regulations
- Security standards
- Regular audits
- Documentation