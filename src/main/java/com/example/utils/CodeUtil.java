package com.example.utils;

import java.util.Random;

public class CodeUtil {

    public static String codeUtils() {
        String str = "0123456789";
        StringBuilder st = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            char ch = str.charAt(new Random().nextInt(str.length()));
            st.append(ch);
        }
        return st.toString().toLowerCase();
    }
}

