// SPDX-License-Identifier: Apache-2.0
package com.jseed.crypto.starter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Base64;

/**
 * @author cyrus
 */
class CryptoServiceTest {
    @Test
    void autoConfiguresAuthenticatedEncryptionAndPasswordVerification() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(CryptoAutoConfiguration.class))
                .withPropertyValues("jseed.crypto.password-strength=4")
                .run(
                        context -> {
                            var crypto = context.getBean(CryptoService.class);
                            String key = crypto.generateKey();
                            String ciphertext = crypto.encrypt("种子", key);
                            assertThat(crypto.encrypt("种子", key)).isNotEqualTo(ciphertext);
                            assertThat(crypto.decrypt(ciphertext, key)).isEqualTo("种子");
                            byte[] tampered = Base64.getUrlDecoder().decode(ciphertext);
                            tampered[tampered.length - 1] ^= 1;
                            String invalid = Base64.getUrlEncoder().encodeToString(tampered);
                            assertThatIllegalArgumentException()
                                    .isThrownBy(() -> crypto.decrypt(invalid, key));
                            String hash = crypto.hashPassword("demo");
                            assertThat(crypto.matchesPassword("demo", hash)).isTrue();
                            assertThat(crypto.matchesPassword("incorrect", hash)).isFalse();
                            String signature = crypto.hmacSha256("demo", key);
                            assertThat(crypto.verifyHmac("demo", signature, key)).isTrue();
                            assertThat(crypto.verifyHmac("other", signature, key)).isFalse();
                        });
    }
}
