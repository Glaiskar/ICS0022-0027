package com.taltech.alpopo.securepasswordmanager.util;

import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    public String encrypt(String plainText, String masterPassword) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(masterPassword);
        return textEncryptor.encrypt(plainText);
    }

    public String decrypt(String cipherText, String masterPassword) {
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(masterPassword);
        return textEncryptor.decrypt(cipherText);
    }
}
