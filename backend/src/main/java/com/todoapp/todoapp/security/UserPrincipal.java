package com.todoapp.todoapp.security;

import com.todoapp.todoapp.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * User entity'sini Spring Security'nin kullandığı
 * UserDetails yapısına dönüştüren sınıftır.
 *
 * Spring Security kullanıcı doğrulama işlemlerini
 * UserDetails arayüzü üzerinden gerçekleştirdiği için
 * User nesnesi bu sınıf aracılığıyla sisteme uyarlanır.
 */
public class UserPrincipal implements UserDetails {

    /**
     * Veritabanından gelen kullanıcı bilgilerini tutar.
     */
    private final User user;

    /**
     * User entity'sini UserPrincipal nesnesine dönüştürür.
     *
     * @param user doğrulanan kullanıcı
     */
    public UserPrincipal(User user) {
        this.user = user;
    }

    /**
     * Kullanıcının sahip olduğu roller döndürülür.
     *
     * Bu projede rol sistemi kullanılmadığı için
     * boş liste döndürülmektedir.
     *
     * @return kullanıcı rolleri
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Spring Security'nin kullanacağı şifre bilgisini döndürür.
     *
     * @return kullanıcının hashlenmiş şifresi
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Spring Security'nin kullanacağı kullanıcı adını döndürür.
     *
     * @return kullanıcı adı
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Giriş yapan kullanıcının veritabanındaki ID değerini döndürür.
     *
     * Controller katmanında @AuthenticationPrincipal kullanılarak
     * kullanıcıya ait Todo kayıtlarını filtrelemek için kullanılır.
     *
     * @return kullanıcı ID değeri
     */
    public Long getId() {
        return user.getId();
    }

    /**
     * Hesabın kullanım süresinin dolup dolmadığını belirtir.
     *
     * Bu projede hesap süresi kontrolü olmadığı için
     * her zaman true döndürülmektedir.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Hesabın kilitli olup olmadığını belirtir.
     *
     * Bu projede hesap kilitleme özelliği bulunmadığı için
     * her zaman true döndürülmektedir.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Kullanıcının giriş bilgilerinin geçerli olup olmadığını belirtir.
     *
     * Bu projede şifre süresi kontrolü bulunmadığı için
     * her zaman true döndürülmektedir.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Kullanıcı hesabının aktif olup olmadığını belirtir.
     *
     * Bu projede tüm kullanıcılar aktif kabul edildiği için
     * her zaman true döndürülmektedir.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}