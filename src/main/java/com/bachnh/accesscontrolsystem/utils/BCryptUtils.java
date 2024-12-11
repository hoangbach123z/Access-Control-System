package com.bachnh.accesscontrolsystem.utils;

import com.password4j.Argon2Function;
import com.password4j.BcryptFunction;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Argon2;
import com.password4j.types.Bcrypt;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

public class BCryptUtils {
//    @Value("${argon2.pepper}")
//     String pepper ;
//    @Value("${argon2.salt}")
//     String salt ;
//    public String hashPassword(String password) {
//        Argon2Function argon2 = Argon2Function.getInstance(14, 20, 1, 32, Argon2.ID);
//        Hash hash = Password.hash(password)
//                .addPepper(pepper)
//                .addSalt(salt)
//                .with(argon2);
//
//       return hash.getResult();
//    }
//    public  boolean verifiedPassword(String password,String hash) {
//        Argon2Function argon2 = Argon2Function.getInstance(14, 20, 1, 32, Argon2.ID);
//
//        boolean verified = Password.check(password, hash)
//                .addPepper(pepper)
//                .with(argon2);
//        if (verified) {
//            return true;
//        }
//        return false;
//    }

}
