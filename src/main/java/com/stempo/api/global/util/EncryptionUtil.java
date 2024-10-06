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

/**
 * AES-GCM 암호화를 사용하여 문자열을 암호화 및 복호화하는 유틸리티 클래스.
 * 고유한 값에 기반한 IV(Initialization Vector)를 생성하여 일관된 암호화 결과를 제공하는 기능을 포함합니다.
 */
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

    /**
     * 임의로 생성된 IV(Initialization Vector)를 사용하여 AES-GCM 암호화로 문자열을 암호화.
     *
     * @param strToEncrypt 암호화할 문자열.
     * @return Base64 형식으로 인코딩된 암호화된 문자열.
     * @throws EncryptionException 암호화 중 오류가 발생할 경우.
     */
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

    /**
     * AES-GCM 암호화로 암호화된 문자열을 복호화.
     *
     * @param strToDecrypt 복호화할 Base64 형식의 암호화된 문자열.
     * @return 복호화된 문자열.
     * @throws DecryptionException 복호화 중 오류가 발생할 경우.
     */
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

    /**
     * 두 개의 암호화된 문자열을 복호화하여 값이 동일한지 비교하는 메소드.
     *
     * @param encryptedValue1 첫 번째 암호화된 값.
     * @param encryptedValue2 두 번째 암호화된 값.
     * @return 두 값이 동일한지 여부를 반환.
     */
    public boolean compareEncryptedValues(String encryptedValue1, String encryptedValue2) {
        try {
            String decryptedValue1 = decrypt(encryptedValue1);
            String decryptedValue2 = decrypt(encryptedValue2);

            return decryptedValue1.equals(decryptedValue2);
        } catch (DecryptionException e) {
            return false;
        }
    }

    /**
     * 암호화를 위해 임의의 IV(Initialization Vector)를 생성.
     *
     * @param ivLengthBytes 생성할 IV의 길이 (바이트 단위).
     * @return 생성된 IV를 나타내는 byte 배열.
     */
    private byte[] generateRandomIV(int ivLengthBytes) {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[ivLengthBytes];
        random.nextBytes(iv);
        return iv;
    }

    /**
     * 두 개의 byte 배열을 하나의 배열로 결합.
     *
     * @param a 첫 번째 byte 배열.
     * @param b 두 번째 byte 배열.
     * @return 결합된 byte 배열.
     */
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
