package com.todoapp.todoapp.security;

import com.todoapp.todoapp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT (JSON Web Token) oluşturma ve doğrulama işlemlerini gerçekleştiren
 * servis sınıfıdır.
 *
 * Bu sınıf;
 * - Kullanıcı giriş yaptıktan sonra JWT üretir.
 * - Gelen token içerisindeki kullanıcı bilgisini okur.
 * - Tokenın geçerliliğini kontrol eder.
 */
@Service
public class JwtService {

    /**
     * JWT oluşturulurken ve doğrulanırken kullanılan gizli anahtardır.
     *
     * Secret key application.properties veya environment variable
     * üzerinden okunur.
     */
    private final SecretKey secretKey;

    /**
     * Uygulama başlatılırken secret değeri okunur ve
     * JWT işlemlerinde kullanılacak güvenli anahtar oluşturulur.
     *
     * @param secret application.properties içerisindeki gizli anahtar
     */
    public JwtService(@Value("${app.jwt.secret}") String secret) {

        this.secretKey =
                Keys.hmacShaKeyFor(
                        secret.getBytes(StandardCharsets.UTF_8)
                );
    }

    /**
     * Başarılı giriş yapan kullanıcı için JWT oluşturur.
     *
     * Token içerisine:
     * - Kullanıcı adı (Subject)
     * - Oluşturulma zamanı
     * - Son kullanma zamanı
     * eklenir ve gizli anahtar ile imzalanır.
     *
     * Bu projede tokenın geçerlilik süresi 1 saattir.
     *
     * @param user giriş yapan kullanıcı
     * @return oluşturulan JWT
     */
    public String generateToken(User user){

        return Jwts.builder()

                /*
                 * Tokenın sahibini belirtir.
                 * Bu projede kullanıcı adı Subject olarak kullanılmaktadır.
                 */
                .setSubject(user.getUsername())

                /*
                 * Tokenın oluşturulduğu zamanı ekler.
                 */
                .setIssuedAt(new Date())

                /*
                 * Tokenın sona ereceği zamanı belirler.
                 * Burada süre 1 saat olarak ayarlanmıştır.
                 */
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60)
                )

                /*
                 * Token gizli anahtar kullanılarak dijital olarak imzalanır.
                 * Böylece tokenın sonradan değiştirilmesi engellenir.
                 */
                .signWith(secretKey)

                /*
                 * JWT metni oluşturularak String olarak döndürülür.
                 */
                .compact();

    }

    /**
     * JWT içerisindeki kullanıcı adını okur.
     *
     * @param token JWT
     * @return kullanıcı adı
     */
    public String extractUsername(String token) {

        return getClaims(token).getSubject();
    }

    /**
     * Tokenın belirtilen kullanıcıya ait olup olmadığını ve
     * süresinin dolup dolmadığını kontrol eder.
     *
     * @param token doğrulanacak JWT
     * @param username beklenen kullanıcı adı
     * @return token geçerliyse true
     */
    public boolean isTokenValid(String token, String username) {

        String tokenUsername = extractUsername(token);

        return tokenUsername.equals(username)
                && !isExpired(token);
    }

    /**
     * Tokenın son kullanma tarihini kontrol eder.
     *
     * @param token JWT
     * @return süresi dolmuşsa true
     */
    private boolean isExpired(String token) {

        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    /**
     * JWT içerisindeki tüm Claim bilgilerini okur.
     *
     * Claims içerisinde subject, oluşturulma tarihi,
     * son kullanma tarihi gibi bilgiler bulunur.
     *
     * @param token JWT
     * @return token içerisindeki Claim bilgileri
     */
    private Claims getClaims(String token) {

        return Jwts.parserBuilder()

                /*
                 * Tokenın hangi gizli anahtarla doğrulanacağını belirtir.
                 */
                .setSigningKey(secretKey)

                .build()

                /*
                 * Tokenın imzasını doğrular ve içeriğini çözer.
                 */
                .parseClaimsJws(token)

                .getBody();
    }

}