package com.todoapp.todoapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Uygulamaya gelen HTTP isteklerindeki JWT bilgisini kontrol eden
 * güvenlik filtresidir.
 *
 * Bu filtre her istek için bir kez çalışır. Authorization header içinde
 * geçerli bir JWT bulunursa kullanıcı doğrulanır ve Spring Security'nin
 * SecurityContext yapısına eklenir.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT içerisindeki kullanıcı bilgisini okumak ve token doğrulamak için
     * kullanılan servis sınıfıdır.
     */
    private final JwtService jwtService;

    /**
     * Token içerisinden alınan kullanıcı adına göre kullanıcı bilgilerini
     * veritabanından yükleyen servistir.
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Her HTTP isteğinde çalışan ana filtre metodudur.
     *
     * Bu metot:
     * - Authorization header bilgisini kontrol eder.
     * - Bearer tokenı ayırır.
     * - Token içerisinden kullanıcı adını çıkarır.
     * - Kullanıcıyı veritabanından yükler.
     * - Token geçerliyse kullanıcıyı SecurityContext içine ekler.
     *
     * @param request gelen HTTP isteği
     * @param response oluşturulacak HTTP cevabı
     * @param filterChain diğer güvenlik filtrelerinin devam etmesini sağlayan zincir
     * @throws ServletException servlet işlemleri sırasında hata oluşursa
     * @throws IOException giriş veya çıkış işlemleri sırasında hata oluşursa
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * JWT bilgisi frontend tarafından Authorization header içinde
         * "Bearer token" formatında gönderilir.
         */
        String authHeader = request.getHeader("Authorization");

        /*
         * Authorization header bulunmuyorsa veya Bearer formatında değilse
         * bu filtre kimlik doğrulama yapmadan isteği sonraki filtreye gönderir.
         *
         * Login ve register gibi herkese açık endpointlerde token
         * bulunmaması normaldir.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            /*
             * "Bearer " ifadesi 7 karakterden oluştuğu için ilk 7 karakter
             * kaldırılarak yalnızca JWT değeri alınır.
             */
            String token = authHeader.substring(7);

            /*
             * Token içerisindeki subject bilgisinden kullanıcı adı alınır.
             */
            String username = jwtService.extractUsername(token);

            /*
             * Kullanıcı adı bulunmuşsa ve kullanıcı bu istek için daha önce
             * doğrulanmamışsa kimlik doğrulama işlemine devam edilir.
             *
             * Authentication kontrolü aynı istek içinde gereksiz yere
             * yeniden doğrulama yapılmasını önler.
             */
            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                /*
                 * Token içerisinden alınan kullanıcı adına ait güncel
                 * kullanıcı bilgileri veritabanından yüklenir.
                 */
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                /*
                 * Tokenın kullanıcıya ait olduğu ve geçerlilik süresinin
                 * dolmadığı JwtService tarafından kontrol edilir.
                 */
                if (jwtService.isTokenValid(
                        token,
                        userDetails.getUsername()
                )) {

                    /*
                     * Doğrulanan kullanıcı için Spring Security tarafından
                     * kullanılacak authentication nesnesi oluşturulur.
                     *
                     * İkinci parametre credentials bilgisidir. JWT zaten
                     * doğrulandığı için burada şifre tutulmaz ve null verilir.
                     */
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * IP adresi ve session bilgisi gibi isteğe ait
                     * ek detaylar authentication nesnesine eklenir.
                     */
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Kullanıcı doğrulama bilgisi SecurityContext içine eklenir.
                     *
                     * Böylece controller katmanında @AuthenticationPrincipal
                     * kullanılarak giriş yapan kullanıcıya erişilebilir.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);
                }
            }
        } catch (RuntimeException ignored) {

            /*
             * Token bozuksa, süresi dolmuşsa veya okunamıyorsa mevcut
             * authentication bilgisi temizlenir.
             *
             * İstek yine filtre zincirinde devam eder. Korunan endpointlere
             * erişim kararı daha sonra Spring Security tarafından verilir.
             */
            SecurityContextHolder.clearContext();
        }

        /*
         * JWT kontrolünden sonra isteğin diğer filtrelere ve uygun
         * controller metoduna ilerlemesini sağlar.
         */
        filterChain.doFilter(request, response);
    }
}