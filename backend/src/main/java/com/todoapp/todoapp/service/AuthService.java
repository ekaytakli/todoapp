package com.todoapp.todoapp.service;

import com.todoapp.todoapp.dto.LoginRequest;
import com.todoapp.todoapp.dto.LoginResponse;
import com.todoapp.todoapp.dto.RegisterRequest;
import com.todoapp.todoapp.entity.User;
import com.todoapp.todoapp.repository.UserRepository;
import com.todoapp.todoapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Kullanıcı kayıt ve giriş işlemlerine ait iş kurallarını yöneten
 * servis sınıfıdır.
 *
 * Controller katmanından gelen istekler burada işlenir.
 * Kullanıcı kontrolü, şifre doğrulama, şifre hashleme ve JWT üretme
 * işlemleri bu sınıfta gerçekleştirilir.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * Kullanıcı bilgilerine veritabanı üzerinden erişmek için kullanılır.
     */
    private final UserRepository userRepository;

    /**
     * Kullanıcı şifrelerini BCrypt ile hashlemek ve giriş sırasında
     * karşılaştırmak için kullanılır.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Başarılı giriş işleminden sonra JWT oluşturmak için kullanılır.
     */
    private final JwtService jwtService;

    /**
     * Kullanıcının sisteme giriş işlemini gerçekleştirir.
     *
     * Öncelikle kullanıcı adına göre kullanıcı veritabanında aranır.
     * Ardından frontend'den gelen şifre ile veritabanındaki hashlenmiş
     * şifre karşılaştırılır.
     *
     * Bilgiler doğruysa kullanıcı için JWT oluşturulur ve kullanıcı
     * bilgileriyle birlikte LoginResponse olarak döndürülür.
     *
     * @param loginRequest kullanıcı adı ve şifre bilgilerini taşıyan istek
     * @return JWT ve kullanıcı bilgilerini içeren giriş cevabı
     */
    public LoginResponse login(LoginRequest loginRequest) {

        /*
         * Kullanıcı adına göre veritabanında arama yapılır.
         *
         * Kullanıcı bulunamazsa güvenlik amacıyla kullanıcı adı veya şifreden
         * hangisinin yanlış olduğu ayrı ayrı belirtilmez.
         */
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("Hatalı kullanıcı adı veya şifre!"));

        /*
         * Frontend'den gelen düz metin şifre, veritabanında bulunan
         * BCrypt hash değeriyle karşılaştırılır.
         *
         * Hashlenmiş şifre tekrar düz metne çevrilmez.
         */
        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Hatalı kullanıcı adı veya şifre!");
        }

        /*
         * Kullanıcı bilgileri doğruysa kullanıcıya özel JWT oluşturulur.
         */
        String token = jwtService.generateToken(user);

        /*
         * Frontend'e token, kullanıcı ID'si ve kullanıcı adı gönderilir.
         */
        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername()
        );
    }

    /**
     * Yeni kullanıcı kayıt işlemini gerçekleştirir.
     *
     * Kayıt işleminden önce kullanıcı adı ve e-posta adresinin daha önce
     * kullanılıp kullanılmadığı kontrol edilir.
     *
     * Şifre veritabanına düz metin olarak kaydedilmez. PasswordEncoder
     * kullanılarak hashlenir ve ardından kullanıcı kaydedilir.
     *
     * @param registerRequest kayıt sırasında gönderilen kullanıcı bilgileri
     * @return veritabanına kaydedilen kullanıcı
     */
    public User register(RegisterRequest registerRequest) {

        /*
         * Aynı kullanıcı adıyla daha önce hesap oluşturulup oluşturulmadığını
         * kontrol eder.
         */
        if (userRepository.existsByUsername(
                registerRequest.getUsername()
        )) {
            throw new RuntimeException(
                    "Bu kullanıcı adı zaten kullanılıyor!"
            );
        }

        /*
         * Aynı e-posta adresiyle daha önce hesap oluşturulup oluşturulmadığını
         * kontrol eder.
         */
        if (userRepository.existsByEmail(
                registerRequest.getEmail()
        )) {
            throw new RuntimeException(
                    "Bu e-posta adresi zaten kayıtlı!"
            );
        }

        /*
         * Veritabanına kaydedilecek yeni kullanıcı nesnesi oluşturulur.
         */
        User user = new User();

        /*
         * Kayıt isteğinden gelen kullanıcı adı ve e-posta bilgileri
         * entity nesnesine aktarılır.
         */
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());

        /*
         * Şifre veritabanına düz metin olarak kaydedilmez.
         * BCrypt ile hashlenerek güvenli biçimde saklanır.
         */
        user.setPassword(
                passwordEncoder.encode(
                        registerRequest.getPassword()
                )
        );

        /*
         * Oluşturulan kullanıcı veritabanına kaydedilir ve kaydedilen
         * entity geri döndürülür.
         */
        return userRepository.save(user);
    }
}