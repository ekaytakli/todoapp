package com.todoapp.todoapp.service;

import com.todoapp.todoapp.entity.Todo;
import com.todoapp.todoapp.entity.User;
import com.todoapp.todoapp.repository.TodoRepository;
import com.todoapp.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Todo işlemlerine ait iş kurallarını yöneten servis sınıfıdır.
 *
 * Todo oluşturma, listeleme, filtreleme, güncelleme,
 * tamamlanma durumunu değiştirme ve silme işlemleri burada gerçekleştirilir.
 *
 * Kullanıcının yalnızca kendi Todo kayıtları üzerinde işlem yapabilmesi için
 * Todo ID'si ile birlikte kullanıcı ID'si de kontrol edilir.
 */
@Service
@RequiredArgsConstructor
public class TodoService {

    /**
     * Todo kayıtları üzerinde veritabanı işlemleri yapmak için kullanılır.
     */
    private final TodoRepository todoRepository;

    /**
     * Todo oluşturulurken ilgili kullanıcının veritabanında bulunup
     * bulunmadığını kontrol etmek için kullanılır.
     */
    private final UserRepository userRepository;

    /**
     * Belirtilen kullanıcı için yeni bir Todo oluşturur.
     *
     * Öncelikle kullanıcı ID'sine göre kullanıcı bulunur.
     * Daha sonra Todo ile kullanıcı arasında ilişki kurulur
     * ve kayıt veritabanına kaydedilir.
     *
     * @param todo frontend tarafından gönderilen Todo bilgileri
     * @param userId giriş yapan kullanıcının ID değeri
     * @return veritabanına kaydedilen Todo
     */
    public Todo createTodo(Todo todo, Long userId) {

        /*
         * Todo'nun bağlanacağı kullanıcı veritabanında aranır.
         * Kullanıcı bulunamazsa kayıt işlemi durdurulur.
         */
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Kullanıcı bulunamadı!"));

        /*
         * Oluşturulan Todo, giriş yapan kullanıcıya bağlanır.
         * Böylece veritabanındaki user_id foreign key alanı doldurulur.
         */
        todo.setUser(user);

        /*
         * Kullanıcıyla ilişkilendirilen Todo veritabanına kaydedilir.
         */
        return todoRepository.save(todo);
    }

    /**
     * Belirtilen kullanıcıya ait tüm Todo kayıtlarını getirir.
     *
     * @param userId kullanıcının ID değeri
     * @return kullanıcıya ait Todo listesi
     */
    public List<Todo> getTodosByUserId(Long userId) {

        return todoRepository.findByUserId(userId);
    }

    /**
     * Kullanıcıya ait Todo kayıtlarını tamamlanma durumuna göre filtreler.
     *
     * completed değeri true ise tamamlanmış,
     * false ise tamamlanmamış Todo kayıtları döndürülür.
     *
     * @param userId kullanıcının ID değeri
     * @param completed aranacak tamamlanma durumu
     * @return filtrelenmiş Todo listesi
     */
    public List<Todo> getTodosByUserIdAndCompleted(
            Long userId,
            boolean completed
    ) {

        return todoRepository.findByUserIdAndCompleted(
                userId,
                completed
        );
    }

    /**
     * Belirtilen Todo kaydını yalnızca giriş yapan kullanıcıya aitse getirir.
     *
     * Bu ortak kontrol güncelleme, toggle ve silme işlemlerinde kullanılır.
     * Böylece başka bir kullanıcıya ait Todo üzerinde işlem yapılması önlenir.
     *
     * @param id Todo ID değeri
     * @param userId giriş yapan kullanıcının ID değeri
     * @return bulunan Todo
     */
    public Todo getTodoByIdAndUserId(Long id, Long userId) {

        return todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Hata: Todo bulunamadı veya bu kullanıcıya ait değil!"
                        ));
    }

    /**
     * Kullanıcının Todo kaydını günceller.
     *
     * Güncelleme yapılmadan önce Todo'nun giriş yapan kullanıcıya ait
     * olup olmadığı kontrol edilir.
     *
     * @param id güncellenecek Todo ID değeri
     * @param updatedTodo frontend tarafından gönderilen yeni bilgiler
     * @param userId giriş yapan kullanıcının ID değeri
     * @return güncellenmiş Todo
     */
    public Todo updateTodo(
            Long id,
            Todo updatedTodo,
            Long userId
    ) {

        /*
         * Todo hem ID hem kullanıcı ID'si ile aranır.
         */
        Todo todo = getTodoByIdAndUserId(id, userId);

        /*
         * Gelen yeni bilgiler mevcut Todo kaydına aktarılır.
         */
        todo.setTitle(updatedTodo.getTitle());
        todo.setDescription(updatedTodo.getDescription());
        todo.setCompleted(updatedTodo.isCompleted());

        /*
         * Güncellenen kayıt veritabanına kaydedilir.
         */
        return todoRepository.save(todo);
    }

    /**
     * Todo'nun tamamlanma durumunu tersine çevirir.
     *
     * false olan değer true,
     * true olan değer ise false yapılır.
     *
     * @param id durumu değiştirilecek Todo ID değeri
     * @param userId giriş yapan kullanıcının ID değeri
     * @return tamamlanma durumu değiştirilmiş Todo
     */
    public Todo toggleTodoCompletion(Long id, Long userId) {

        /*
         * İşlem yapılacak Todo'nun kullanıcıya ait olduğu doğrulanır.
         */
        Todo todo = getTodoByIdAndUserId(id, userId);

        /*
         * Mevcut completed değeri tersine çevrilir.
         */
        todo.setCompleted(!todo.isCompleted());

        /*
         * Yeni durum veritabanına kaydedilir.
         */
        return todoRepository.save(todo);
    }

    /**
     * Belirtilen Todo kaydını siler.
     *
     * Silme işleminden önce kaydın giriş yapan kullanıcıya ait olduğu
     * doğrulanır.
     *
     * @param id silinecek Todo ID değeri
     * @param userId giriş yapan kullanıcının ID değeri
     */
    public void deleteTodo(Long id, Long userId) {

        Todo todo = getTodoByIdAndUserId(id, userId);

        todoRepository.delete(todo);
    }
}