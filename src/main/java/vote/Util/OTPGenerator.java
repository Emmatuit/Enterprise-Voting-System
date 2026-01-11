package vote.Util;


import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OTPGenerator {

    private static final String NUMBERS = "0123456789";
    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom random = new SecureRandom();

    public String generateOTP() {
        return generateOTP(6, true); // Default: 6-digit numeric OTP
    }

    public String generateOTP(int length, boolean numericOnly) {
        StringBuilder otp = new StringBuilder(length);
        String characters = numericOnly ? NUMBERS : ALPHANUMERIC;

        for (int i = 0; i < length; i++) {
            otp.append(characters.charAt(random.nextInt(characters.length())));
        }

        return otp.toString();
    }

    public String generateAlphaNumericOTP(int length) {
        return generateOTP(length, false);
    }

    public String generateSessionToken() {
        return generateOTP(32, false); // 32-character alphanumeric session token
    }
}