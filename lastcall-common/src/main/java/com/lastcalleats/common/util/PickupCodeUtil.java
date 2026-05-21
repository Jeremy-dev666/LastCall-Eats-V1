package com.lastcalleats.common.util;

import java.security.SecureRandom;

public class PickupCodeUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PickupCodeUtil() {}

    public static String generateNumericCode() {
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }
}
