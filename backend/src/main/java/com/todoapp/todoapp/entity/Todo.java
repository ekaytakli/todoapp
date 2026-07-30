package com.todoapp.todoapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Todo uygulamasındaki görevleri temsil eden entity sınıfıdır.
 *
 * Bu sınıf Hibernate tarafından "todos" tablosuna karşılık gelir.
 * Her Todo kaydı yalnızca bir kullanıcıya aittir.
 */
@Entity
@Table(name = "todos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    /**
     * Todo kaydının benzersiz kimliğidir.
     * Değer veritabanı tarafından otomatik oluşturulur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Todo başlığı.
     *
     * @NotBlank sayesinde boş gönderilemez.
     */
    @NotBlank(message = "Başlık boş bırakılamaz")
    @Column(nullable = false)
    private String title;

    /**
     * Todo hakkında isteğe bağlı açıklama alanı.
     */
    private String description;

    /**
     * Görevin tamamlanma durumunu tutar.
     *
     * Varsayılan değer false olarak ayarlanmıştır.
     */
    @Column(nullable = false)
    private boolean completed = false;

    /**
     * Todo oluşturulma tarihini tutar.
     *
     * updatable = false sayesinde kayıt oluşturulduktan sonra
     * bu alan güncellenemez.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Entity ilk kez veritabanına kaydedilmeden hemen önce çalışır.
     *
     * Böylece oluşturulma tarihi otomatik olarak atanır.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Her Todo yalnızca bir kullanıcıya aittir.
     *
     * Bir kullanıcı ise birden fazla Todo kaydına sahip olabilir.
     */
    @ManyToOne

    /**
     * Veritabanında user_id isimli foreign key sütununu oluşturur.
     */
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}