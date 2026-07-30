package com.todoapp.todoapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Yeni kullanıcı kaydı sırasında frontend'den gönderilen
 * bilgileri taşıyan DTO sınıfıdır.
 *
 * Bu sınıf yalnızca kayıt isteğinde kullanılan verileri
 * servis katmanına iletmek amacıyla oluşturulmuştur.
 */
@Getter
@Setter
public class RegisterRequest {

    /**
     * Kullanıcının sisteme kayıt olmak için belirlediği kullanıcı adı.
     */
    private String username;

    /**
     * Kullanıcının kayıt sırasında girdiği e-posta adresi.
     */
    private String email;

    /**
     * Kullanıcının belirlediği şifre.
     *
     * Şifre düz metin olarak yalnızca istek sırasında taşınır.
     * Servis katmanında BCrypt ile hashlenerek veritabanına kaydedilir.
     */
    private String password;
}