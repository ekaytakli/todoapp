package com.todoapp.todoapp.security;

import com.todoapp.todoapp.entity.User;
import com.todoapp.todoapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Spring Security'nin kullanıcı doğrulama işlemleri sırasında
 * veritabanından kullanıcı bilgilerini yükleyen servis sınıfıdır.
 *
 * Login sırasında JwtAuthenticationFilter tarafından çağrılır ve
 * kullanıcı bilgileri UserPrincipal nesnesine dönüştürülerek
 * Spring Security'ye teslim edilir.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Kullanıcı bilgilerine veritabanı üzerinden erişmek için kullanılır.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Kullanıcı adına göre kullanıcıyı veritabanından yükler.
     *
     * Spring Security bu metodu otomatik olarak çağırır.
     * Kullanıcı bulunursa UserPrincipal nesnesine dönüştürülür,
     * bulunamazsa UsernameNotFoundException fırlatılır.
     *
     * @param username giriş yapan kullanıcının kullanıcı adı
     * @return Spring Security tarafından kullanılacak UserDetails nesnesi
     * @throws UsernameNotFoundException kullanıcı bulunamazsa
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        /*
         * Kullanıcı adına göre veritabanında arama yapılır.
         */
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Kullanıcı bulunamadı."));

        /*
         * User entity'si Spring Security'nin anlayacağı
         * UserPrincipal nesnesine dönüştürülür.
         */
        return new UserPrincipal(user);
    }
}