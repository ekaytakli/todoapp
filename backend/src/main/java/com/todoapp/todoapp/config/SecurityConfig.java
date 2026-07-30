package com.todoapp.todoapp.config;

import com.todoapp.todoapp.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Uygulamanın Spring Security yapılandırmasını içerir.
 *
 * Bu sınıfta:
 * - Herkese açık endpointler belirlenir.
 * - Todo endpointleri kimlik doğrulamasına karşı korunur.
 * - JWT tabanlı stateless oturum yapısı etkinleştirilir.
 * - JWT doğrulama filtresi Spring Security zincirine eklenir.
 * - Kullanıcı şifreleri için BCrypt encoder tanımlanır.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Her HTTP isteğinde Authorization header içerisindeki JWT'yi kontrol eden
     * özel güvenlik filtresidir.
     *
     * RequiredArgsConstructor sayesinde constructor injection ile otomatik
     * olarak bu sınıfa enjekte edilir.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Kullanıcı şifrelerinin güvenli biçimde hashlenmesi için kullanılan
     * PasswordEncoder nesnesini Spring Bean olarak oluşturur.
     *
     * BCrypt tek yönlü bir hash algoritmasıdır. Bu nedenle kullanıcının gerçek
     * şifresi veritabanında açık metin olarak saklanmaz.
     *
     * @return BCrypt kullanan şifre kodlayıcı
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Uygulamadaki HTTP güvenlik kurallarını tanımlar.
     *
     * @param http Spring Security tarafından sağlanan yapılandırma nesnesi
     * @return oluşturulan güvenlik filtre zinciri
     * @throws Exception güvenlik yapılandırması sırasında hata oluşursa
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                /*
                 * Uygulama session ve form tabanlı kimlik doğrulama yerine
                 * JWT kullandığı için CSRF koruması devre dışı bırakılır.
                 *
                 * JWT, her istekte Authorization header üzerinden gönderilir.
                 */
                .csrf(AbstractHttpConfigurer::disable)

                /*
                 * CorsConfig sınıfında tanımlanan CORS ayarlarının
                 * Spring Security tarafından kullanılmasını sağlar.
                 */
                .cors(Customizer.withDefaults())

                /*
                 * JWT tabanlı kimlik doğrulamada sunucu tarafında kullanıcı
                 * oturumu tutulmaz.
                 *
                 * Her istek kendi JWT bilgisini taşıdığı için uygulama
                 * STATELESS olarak yapılandırılır.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Hangi endpointlere kimlik doğrulaması olmadan erişilebileceği,
                 * hangi endpointlerin JWT gerektirdiği burada belirlenir.
                 */
                .authorizeHttpRequests(auth -> auth

                        /*
                         * Kullanıcının sisteme kayıt olabilmesi ve giriş
                         * yapabilmesi için bu iki POST endpointi herkese açıktır.
                         *
                         * Kullanıcı henüz token sahibi olmadığı için bu
                         * endpointlerde JWT zorunlu tutulamaz.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()

                        /*
                         * Todo işlemlerine yalnızca kimliği doğrulanmış
                         * kullanıcılar erişebilir.
                         */
                        .requestMatchers("/api/todos/**").authenticated()

                        /*
                         * Yukarıda açıkça izin verilmeyen diğer tüm endpointler
                         * için de kimlik doğrulaması zorunludur.
                         */
                        .anyRequest().authenticated()
                )

                /*
                 * JWT filtresini Spring Security'nin kullanıcı adı ve şifre
                 * filtresinden önce çalıştırır.
                 *
                 * Böylece istek controller'a ulaşmadan önce token doğrulanır
                 * ve geçerli kullanıcı SecurityContext içerisine yerleştirilir.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        /*
         * Yapılandırması tamamlanan SecurityFilterChain nesnesini
         * Spring uygulamasına teslim eder.
         */
        return http.build();
    }
}