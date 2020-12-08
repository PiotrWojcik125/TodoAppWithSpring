package todoApplication.logic;

import todoApplication.model.TaskGroup;
import todoApplication.model.TaskRepository;
import todoApplication.model.TaskGroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {
     @Test
     @DisplayName("Should throw IllegalStateException where undone group exists")
    void existByDoneisFalse_returns_true_throwsIllegalStateException(){
         // given
         TaskRepository mockTaskRepository=mock(TaskRepository.class);
         when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(true);
         // system under test
         TaskGroupService SUT = new TaskGroupService(null,mockTaskRepository);
         // when
         Throwable exception= catchThrowable(()-> SUT.toogleGroup(1));
         //  then
         assertThat(exception)
                 .isInstanceOf(IllegalStateException.class)
                 .hasMessageContaining("Group has undone tasks. Done all the tasks first");
     }
    @Test
    @DisplayName("Should throw IllegalArgumentException when all groups are done but there is no TaskGroup with given id")
    void existByDoneisFalse_returns_false_and_findById_returns_emptyOptional_throws_IllegalArgumentException(){
        // given
        TaskRepository mockTaskRepository=mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(false);
        //and
        TaskGroupRepository mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.empty());
        // system under test
        TaskGroupService SUT = new TaskGroupService(mockTaskGroupRepository,mockTaskRepository);
        // when
        Throwable exception= catchThrowable(()-> SUT.toogleGroup(1));
        //  then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TaskGroup with given id not found");
    }
    @Test
    @DisplayName("Should toogle done ")
    void everything_Ok_should_toogle_done(){
        // given
        TaskRepository mockTaskRepository=mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(false);
        // and
        TaskGroup mockTaskGroup = new TaskGroup();
        // and
        TaskGroupRepository mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.of(mockTaskGroup));
        // system under test
        TaskGroupService SUT = new TaskGroupService(mockTaskGroupRepository,mockTaskRepository);
        // when
        SUT.toogleGroup(1);
        //  then
        assertThat(true).isEqualTo(mockTaskGroup.isDone());
    }


     }

