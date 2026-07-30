package com.todoapp.todoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Başarılı giriş işleminden sonra frontend'e gönderilen
 * cevap nesnesidir.
 *
 * JWT tokenı ve giriş yapan kullanıcıya ait temel bilgiler
 * bu DTO içerisinde taşınır.
 */
@Getter
@AllArgsConstructor
public class LoginResponse {

    /**
     * Kullanıcının kimliğini doğrulayan JWT erişim anahtarı.
     */
    private String token;

    /**
     * Giriş yapan kullanıcının veritabanındaki benzersiz ID değeri.
     */
    private Long userId;

    /**
     * Giriş yapan kullanıcının kullanıcı adı.
     */
    private String username;
}