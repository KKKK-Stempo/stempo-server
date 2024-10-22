package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stempo.config.AesConfig;
import com.stempo.exception.DecryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EncryptionUtilsTest {

    private EncryptionUtils encryptionUtils;

    @BeforeEach
    void setUp() {
        AesConfig aesConfig = new AesConfig("01234567890123456789012345678901", 12, 128);
        encryptionUtils = EncryptionUtils.create(aesConfig);
    }

    @Test
    void 암호화된_값을_복호화하면_원본값과_일치한다() {
        // given
        String plainText = "Hello, World!";

        // when
        String encryptedText = encryptionUtils.encrypt(plainText);
        String decryptedText = encryptionUtils.decrypt(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(plainText);
    }

    @Test
    void 해싱된_IV로_암호화된_값은_복호화할_수_있다() {
        // given
        String plainText = "Hello, World!";
        String uniqueValue = "deviceTag123";

        // when
        String encryptedText = encryptionUtils.encryptWithHashedIV(plainText, uniqueValue);
        String decryptedText = encryptionUtils.decryptWithHashedIV(encryptedText, uniqueValue);

        // then
        assertThat(decryptedText).isEqualTo(plainText);
    }

    @Test
    void 다른_IV로_암호화된_값은_복호화할_수_없다() {
        // given
        String plainText = "Hello, World!";
        String uniqueValue = "deviceTag123";
        String differentUniqueValue = "deviceTag456";

        // when
        String encryptedText = encryptionUtils.encryptWithHashedIV(plainText, uniqueValue);

        // then
        assertThatThrownBy(() -> encryptionUtils.decryptWithHashedIV(encryptedText, differentUniqueValue))
                .isInstanceOf(DecryptionException.class);
    }

    @Test
    void 동일한_원본값을_암호화한_경우_비교가_성공한다() {
        // given
        String plainText1 = "Text1";
        String plainText2 = "Text1";
        String plainText3 = "Text3";

        // when
        String encryptedValue1 = encryptionUtils.encrypt(plainText1);
        String encryptedValue2 = encryptionUtils.encrypt(plainText2);
        String encryptedValue3 = encryptionUtils.encrypt(plainText3);

        // then
        assertThat(encryptionUtils.compareEncryptedValues(encryptedValue1, encryptedValue2)).isTrue();
        assertThat(encryptionUtils.compareEncryptedValues(encryptedValue1, encryptedValue3)).isFalse();
    }

    @Test
    void 잘못된_암호화된_문자열은_복호화할_수_없다() {
        // given
        String invalidEncryptedText = "InvalidEncryptedText";

        // when, then
        assertThatThrownBy(() -> encryptionUtils.decrypt(invalidEncryptedText))
                .isInstanceOf(DecryptionException.class)
                .hasMessageContaining("Bad padding");
    }

    @Test
    void 해싱된_IV가_정확히_생성된다() {
        // given
        String uniqueValue = "test-deviceTag";
        int ivLength = 12;

        // when
        byte[] iv = encryptionUtils.generateIVFromUniqueValue(uniqueValue);

        // then
        assertThat(iv).isNotNull();
        assertThat(iv.length).isEqualTo(ivLength);
    }

    @Test
    void 임의의_IV가_정확히_생성된다() {
        int ivLength = 12;

        byte[] iv = encryptionUtils.generateRandomIV(ivLength);

        assertThat(iv).isNotNull();
        assertThat(iv.length).isEqualTo(ivLength);
    }

    @Test
    void 두_배열이_정상적으로_결합된다() {
        byte[] array1 = {1, 2, 3};
        byte[] array2 = {4, 5, 6};

        byte[] combinedArray = encryptionUtils.concat(array1, array2);

        assertThat(combinedArray).containsExactly(1, 2, 3, 4, 5, 6);
    }
}
