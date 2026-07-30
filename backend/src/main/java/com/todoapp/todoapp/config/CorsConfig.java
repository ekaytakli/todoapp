package com.todoapp.todoapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Frontend uygulamasının backend API'ye farklı bir origin üzerinden
 * erişebilmesi için gerekli CORS ayarlarını içerir.
 *
 * Örneğin:
 * Frontend: http://localhost:5173
 * Backend:  http://localhost:8080
 *
 * Bu adreslerin portları farklı olduğu için tarayıcı bunları farklı origin
 * olarak değerlendirir. CORS yapılandırması, izin verilen frontend
 * adreslerinden gelen isteklere backend'in cevap verebilmesini sağlar.
 */
@Configuration
public class CorsConfig {

    /**
     * Backend'e erişmesine izin verilen frontend adreslerini
     * application.properties veya environment variable üzerinden alır.
     *
     * Herhangi bir değer verilmezse geliştirme ortamında varsayılan olarak
     * localhost üzerindeki tüm portlara izin verilir.
     *
     * Birden fazla adres virgülle ayrılarak tanımlanabilir.
     *
     * Örnek:
     * http://localhost:5173,https://todo-app.vercel.app
     */
    @Value("${app.cors.allowed-origins:http://localhost:*}")
    private String allowedOrigins;

    /**
     * Uygulamanın genel CORS politikasını oluşturur.
     *
     * Bu Bean, SecurityConfig sınıfındaki cors(Customizer.withDefaults())
     * yapılandırması tarafından kullanılır.
     *
     * @return uygulamanın CORS ayarlarını içeren yapılandırma kaynağı
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        /*
         * Virgülle ayrılmış origin listesini parçalar.
         *
         * trim() kullanılması, environment variable içerisinde adreslerin
         * arasında boşluk bulunması durumunda hatalı origin oluşmasını önler.
         */
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();

        /*
         * setAllowedOriginPatterns kullanılması, geliştirme ortamındaki
         * http://localhost:* gibi wildcard içeren adreslerin desteklenmesini
         * sağlar.
         */
        configuration.setAllowedOriginPatterns(origins);

        /*
         * Frontend'in backend üzerinde kullanmasına izin verilen
         * HTTP metotlarıdır.
         *
         * OPTIONS metodu tarayıcıların asıl istekten önce gönderdiği
         * preflight kontrolü için gereklidir.
         */
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        /*
         * Authorization ve Content-Type dahil olmak üzere frontend'den
         * gönderilecek tüm request headerlarına izin verir.
         *
         * JWT, Authorization header içerisinde gönderildiğinden bu ayar
         * kimlik doğrulama akışı için önemlidir.
         */
        configuration.setAllowedHeaders(List.of("*"));

        /*
         * Tarayıcının kimlik bilgileri içeren cross-origin isteklere
         * izin vermesini sağlar.
         *
         * Bu proje JWT'yi Authorization header üzerinden gönderdiği için
         * kullanılabilir. Ancak production ortamında izin verilen originler
         * açıkça sınırlandırılmalıdır.
         */
        configuration.setAllowCredentials(true);

        /*
         * Oluşturulan CORS yapılandırmasını uygulamadaki bütün endpointlere
         * uygular.
         */
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}