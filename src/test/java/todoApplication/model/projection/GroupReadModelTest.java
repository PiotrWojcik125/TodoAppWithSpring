package todoApplication.model.projection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.configuration.IMockitoConfiguration;
import todoApplication.model.Task;
import todoApplication.model.TaskGroup;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GroupReadModelTest {
    @Test
    @DisplayName("should create null deadline for group when no task deadlines")
    void contructor_noDeadlines_createsNullDeadLine(){
        //given
        TaskGroup mockGroup = mock(TaskGroup.class);
        when(mockGroup.getId()).thenReturn(100);
        when(mockGroup.getDescription()).thenReturn("foo");
        when(mockGroup.getTasks()).thenReturn(Set.of(new Task("bar",null)));

        //when
        GroupReadModel result = new GroupReadModel(mockGroup);

        //then
        assertThat(result).hasFieldOrPropertyWithValue("deadline",null);
    }
}