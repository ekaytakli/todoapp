package com.todoapp.todoapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Kullanıcının giriş yaparken frontend'den gönderdiği
 * kullanıcı adı ve şifre bilgilerini taşıyan DTO sınıfıdır.
 *
 * Bu sınıf yalnızca veri taşımak amacıyla kullanılır ve
 * veritabanı ile doğrudan ilişkili değildir.
 */
@Getter
@Setter
public class LoginRequest {

    /**
     * Kullanıcının giriş yapmak için girdiği kullanıcı adı.
     */
    private String username;

    /**
     * Kullanıcının giriş yapmak için girdiği şifre.
     */
    private String password;
}