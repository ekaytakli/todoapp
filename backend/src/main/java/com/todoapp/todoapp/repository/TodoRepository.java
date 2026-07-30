package com.todoapp.todoapp.repository;

import com.todoapp.todoapp.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Todo entity'sine ait veritabanı işlemlerini yöneten repository arayüzüdür.
 *
 * JpaRepository sayesinde temel CRUD işlemleri (kaydetme, silme,
 * güncelleme ve listeleme) otomatik olarak Spring Data JPA tarafından sağlanır.
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * Belirtilen kullanıcıya ait tüm Todo kayıtlarını getirir.
     *
     * @param userId kullanıcının ID değeri
     * @return kullanıcıya ait Todo listesi
     */
    List<Todo> findByUserId(Long userId);

    /**
     * Belirtilen kullanıcıya ait tamamlanmış veya tamamlanmamış
     * Todo kayıtlarını getirir.
     *
     * @param userId kullanıcının ID değeri
     * @param completed tamamlanma durumu
     * @return filtrelenmiş Todo listesi
     */
    List<Todo> findByUserIdAndCompleted(Long userId, boolean completed);

    /**
     * Belirtilen ID'ye sahip Todo kaydını yalnızca belirtilen kullanıcıya
     * aitse getirir.
     *
     * Bu metot güvenlik amacıyla kullanılır. Böylece kullanıcı sadece
     * kendi Todo kayıtları üzerinde işlem yapabilir.
     *
     * @param id Todo ID değeri
     * @param userId giriş yapan kullanıcının ID değeri
     * @return bulunan Todo nesnesi
     */
    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}