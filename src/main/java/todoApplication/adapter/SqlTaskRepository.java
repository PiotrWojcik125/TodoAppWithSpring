package todoApplication.adapter;

import todoApplication.model.Task;
import todoApplication.model.TaskRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
interface SqlTaskRepository extends TaskRepository,JpaRepository <Task,Integer> {
    @Override
    @Query(nativeQuery = true,value="select count(*)>0 from TASKS where id=:id")
    boolean existsById(@Param("id") Integer id);

    @Override
    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);

    @Override
    List<Task> findAllByGroup_Id(Integer groupId);

    @Override
    List<Task> findAllByDoneIsFalseAndDeadlineIsNull();

    @Override
    List<Task> findAllByDoneIsFalseAndDeadlineIsLessThanEqual(final LocalDateTime today);

}
