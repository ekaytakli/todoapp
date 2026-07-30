package com.todoapp.todoapp.repository;

import com.todoapp.todoapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User entity'sine ait veritabanı işlemlerini yöneten repository arayüzüdür.
 *
 * JpaRepository sayesinde kullanıcı ekleme, silme, güncelleme ve
 * listeleme gibi temel işlemler otomatik olarak sağlanır.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Kullanıcı adına göre kullanıcıyı getirir.
     *
     * Giriş (login) işlemi sırasında kullanıcı doğrulamak için kullanılır.
     *
     * @param username kullanıcının kullanıcı adı
     * @return bulunan kullanıcı
     */
    Optional<User> findByUsername(String username);

    /**
     * Belirtilen kullanıcı adının sistemde kayıtlı olup olmadığını kontrol eder.
     *
     * Kayıt sırasında aynı kullanıcı adının tekrar oluşturulmasını engellemek
     * amacıyla kullanılır.
     *
     * @param username kontrol edilecek kullanıcı adı
     * @return kullanıcı adı varsa true, yoksa false
     */
    boolean existsByUsername(String username);

    /**
     * Belirtilen e-posta adresinin sistemde kayıtlı olup olmadığını kontrol eder.
     *
     * Aynı e-posta adresiyle ikinci kez hesap oluşturulmasını önlemek için
     * kullanılır.
     *
     * @param email kontrol edilecek e-posta adresi
     * @return e-posta kayıtlıysa true, değilse false
     */
    boolean existsByEmail(String email);
}