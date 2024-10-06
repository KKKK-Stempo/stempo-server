package com.stempo.api.global.util;

import com.stempo.api.global.config.AesConfig;
import com.stempo.api.global.exception.DecryptionException;
import com.stempo.api.global.exception.EncryptionException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtil {

    private final String secretKey;
    private final int ivLengthBytes;
    private final int gcmTagLengthBits;

    private EncryptionUtil(String secretKey, int ivLengthBytes, int gcmTagLengthBits) {
        this.secretKey = secretKey;
        this.ivLengthBytes = ivLengthBytes;
        this.gcmTagLengthBits = gcmTagLengthBits;
    }

    public static EncryptionUtil create(AesConfig aesConfig) {
        return new EncryptionUtil(aesConfig.getSecretKey(), aesConfig.getIvLengthBytes(), aesConfig.getGcmTagLengthBits());
    }

    public String encrypt(String strToEncrypt) {
        try {
            byte[] iv = generateRandomIV(this.ivLengthBytes);
            SecretKeySpec keySpec = new SecretKeySpec(this.secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(this.gcmTagLengthBits, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            byte[] combined = concat(iv, cipherText);
            return Base64.getEncoder().encodeToString(combined);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Invalid key length.");
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException("Encryption block size error.");
        } catch (BadPaddingException e) {
            throw new EncryptionException("Bad padding.");
        } catch (Exception e) {
            throw new EncryptionException("An error occurred during encryption.");
        }
    }

    public String decrypt(String strToDecrypt) {
        try {
            byte[] combined = Base64.getDecoder().decode(strToDecrypt);
            byte[] iv = Arrays.copyOfRange(combined, 0, this.ivLengthBytes);
            byte[] cipherText = Arrays.copyOfRange(combined, this.ivLengthBytes, combined.length);
            SecretKeySpec keySpec = new SecretKeySpec(this.secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(this.gcmTagLengthBits, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText, StandardCharsets.UTF_8);
        } catch (InvalidKeyException e) {
            throw new DecryptionException("Invalid key length.");
        } catch (IllegalBlockSizeException e) {
            throw new DecryptionException("Decryption block size error.");
        } catch (BadPaddingException e) {
            throw new DecryptionException("Bad padding.");
        } catch (Exception e) {
            throw new DecryptionException("An error occurred during decryption.");
        }
    }

    public boolean compareEncryptedValues(String encryptedValue1, String encryptedValue2) {
        try {
            String decryptedValue1 = decrypt(encryptedValue1);
            String decryptedValue2 = decrypt(encryptedValue2);

            return decryptedValue1.equals(decryptedValue2);
        } catch (DecryptionException e) {
            return false;
        }
    }

    private byte[] generateRandomIV(int ivLengthBytes) {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[ivLengthBytes];
        random.nextBytes(iv);
        return iv;
    }

    private byte[] concat(byte[] a, byte[] b) {
        if ((long) a.length + (long) b.length > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Resulting array size is too large to handle.");
        }

        byte[] combined = new byte[a.length + b.length];
        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);
        return combined;
    }
}
