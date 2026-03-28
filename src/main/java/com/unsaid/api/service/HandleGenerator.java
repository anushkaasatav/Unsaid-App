package com.unsaid.api.service;

import java.util.Random;

public class HandleGenerator {

    private static final String[] WORDS = {
            "silent", "hidden", "calm", "lost", "brave",
            "blue", "dark", "quiet", "soft", "wild"
    };

    private static final Random random = new Random();

    public static String generate() {
        String word = WORDS[random.nextInt(WORDS.length)];
        int number = 1000 + random.nextInt(9000);
        return word + "-" + number;
    }
}
