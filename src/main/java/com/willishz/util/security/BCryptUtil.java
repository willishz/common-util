package com.willishz.util.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt加密
 */
public class BCryptUtil {

    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean check(String candidate, String hashed) {
        return BCrypt.checkpw(candidate, hashed);
    }


    public static void main(String[] args) {
//        System.out.println(BCryptUtil.check("1234qwer","$2a$10$kmJ6TRXne8cZ.khxAs2RPu.sQQwXqmZB5KMYgJwQZqMax3U69WfL6"));
        System.out.println(BCryptUtil.encrypt("qweASD1"));
//        System.out.println(BCryptUtil.encrypt("1234qwer"));
    }
}
