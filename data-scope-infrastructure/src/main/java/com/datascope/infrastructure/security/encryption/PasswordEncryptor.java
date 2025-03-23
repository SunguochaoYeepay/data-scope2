package com.datascope.infrastructure.security.encryption;

import com.datascope.domain.datasource.gateway.PasswordEncryptorGateway;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 */
@Component
public class PasswordEncryptor implements PasswordEncryptorGateway {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String MASTER_KEY = "DataScope-Secret-Key"; // 应从配置中读取

    /**
     * 生成随机盐值
     *
     * @return Base64编码的盐值
     */
    public String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密密码
     *
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密码
     */
    public String encrypt(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(MASTER_KEY.toCharArray(), saltBytes, ITERATION_COUNT, KEY_LENGTH);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // 将IV和加密后的密码合并
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 解密密码
     *
     * @param encryptedPassword 加密后的密码
     * @param salt              盐值
     * @return 原始密码
     */
    public String decrypt(String encryptedPassword, String salt) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedPassword);
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            // 提取IV
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            // 提取加密后的密码
            byte[] encryptedBytes = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(MASTER_KEY.toCharArray(), saltBytes, ITERATION_COUNT, KEY_LENGTH);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("密码解密失败", e);
        }
    }
}
