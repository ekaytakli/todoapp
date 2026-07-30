package com.todoapp.todoapp.service;

import com.todoapp.todoapp.entity.User;
import com.todoapp.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Kullanıcı işlemlerine ait iş kurallarını yöneten servis sınıfıdır.
 *
 * Kullanıcı kaydı, ID'ye göre kullanıcı getirme ve
 * tüm kullanıcıları listeleme işlemleri burada gerçekleştirilir.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    /**
     * Kullanıcı kayıtlarına veritabanı üzerinden erişmek için kullanılır.
     */
    private final UserRepository userRepository;

    /**
     * Kullanıcı şifrelerini BCrypt ile hashlemek için kullanılır.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Yeni bir kullanıcı hesabı oluşturur.
     *
     * Kayıt işleminden önce kullanıcı adı ve e-posta adresinin
     * daha önce kullanılıp kullanılmadığı kontrol edilir.
     *
     * @param user kaydedilecek kullanıcı bilgileri
     * @return veritabanına kaydedilen kullanıcı
     */
    public User registerUser(User user) {

        /*
         * Aynı kullanıcı adıyla ikinci bir hesap oluşturulmasını engeller.
         */
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException(
                    "Hata: Bu kullanıcı adı zaten kullanılmaktadır!"
            );
        }

        /*
         * Aynı e-posta adresiyle ikinci bir hesap oluşturulmasını engeller.
         */
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException(
                    "Hata: Bu e-posta adresi zaten kullanılmaktadır!"
            );
        }

        /*
         * Şifre veritabanına düz metin olarak kaydedilmez.
         * BCrypt ile hashlenerek saklanır.
         */
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        /*
         * Kontrollerden geçen kullanıcı veritabanına kaydedilir.
         */
        return userRepository.save(user);
    }

    /**
     * Belirtilen ID değerine sahip kullanıcıyı getirir.
     *
     * Kullanıcı bulunamazsa hata fırlatılır.
     *
     * @param id kullanıcı ID değeri
     * @return bulunan kullanıcı
     */
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Hata: " + id
                                        + " ID'li kullanıcı bulunamadı!"
                        ));
    }

    /**
     * Sistemde kayıtlı tüm kullanıcıları getirir.
     *
     * @return kullanıcı listesi
     */
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }
}