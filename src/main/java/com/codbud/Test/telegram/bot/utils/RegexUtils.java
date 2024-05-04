package com.codbud.Test.telegram.bot.utils;

import java.util.regex.Pattern;

public class RegexUtils {
    private static final Pattern EMAIL = Pattern.compile("[^@ \\t\\r\\n]+@[^@ \\t\\r\\n]+\\.[^@ \\t\\r\\n]+");
    public static boolean isEmail(String s)
    {
        return EMAIL.matcher(s).matches();
    }


}
