package todoApplication.model;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface TaskRepository {
    List<Task> findAll();

    Optional<Task> findById(Integer id);

    Task save(Task entity);

    void deleteById(Integer id);

    Page<Task> findAll(Pageable page);

    boolean existsById(Integer id);

    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);

    List<Task> findAllByGroup_Id(Integer groupId);

    List<Task> findByDone(@Param("state") boolean done);

    List<Task> findAllByDoneIsFalseAndDeadlineIsNull();

    List<Task> findAllByDoneIsFalseAndDeadlineIsLessThanEqual(final LocalDateTime today);



}
