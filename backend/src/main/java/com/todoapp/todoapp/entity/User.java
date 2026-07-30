package com.todoapp.todoapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Sistemdeki kullanıcıları temsil eden entity sınıfıdır.
 *
 * Bu sınıf Hibernate tarafından users tablosuna dönüştürülür.
 * Kullanıcı bilgileri veritabanında bu yapı üzerinden saklanır.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Kullanıcının benzersiz kimlik numarasıdır.
     *
     * Değer veritabanı tarafından otomatik oluşturulur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Kullanıcının sisteme giriş yapmak için kullandığı kullanıcı adıdır.
     *
     * unique=true sayesinde aynı kullanıcı adı iki kez oluşturulamaz.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Kullanıcının e-posta adresidir.
     *
     * Her kullanıcı için benzersiz olmak zorundadır.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Kullanıcının şifresidir.
     *
     * Şifre BCrypt algoritması ile hashlenerek saklanır.
     *
     * @JsonIgnore sayesinde API cevaplarında frontend'e gönderilmez.
     * Böylece şifre hash'i istemci tarafından görüntülenemez.
     */
    @Column(nullable = false)
    @JsonIgnore
    private String password;
}