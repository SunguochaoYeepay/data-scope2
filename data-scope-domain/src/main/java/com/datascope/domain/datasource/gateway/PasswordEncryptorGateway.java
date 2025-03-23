package com.datascope.domain.datasource.gateway;

public interface PasswordEncryptorGateway {
    String generateSalt();

    String encrypt(String password, String salt);

    String decrypt(String encryptedPassword, String salt);
}