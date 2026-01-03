package com.shirval.service;

import java.util.concurrent.ThreadLocalRandom;

public class CodeGenerator {

    public static String generateNumSeq(int length) {
        StringBuilder bld = new StringBuilder();
        for (int i = 0; i < length; i++) {
            bld.append(ThreadLocalRandom.current().nextInt(0, 9));
        }
        return bld.toString();
    }
}
