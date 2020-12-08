package todoApplication.logic;

import todoApplication.TaskConfigurationProperties;
import todoApplication.model.*;
import todoApplication.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {
    @Test
    @DisplayName("Should throw IllegalStateException when configured to allow just 1 group and the other undone group exist")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupsExists_throwsExceptionIllegalStateException() {
        // given
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(true);
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        // system under test
        var toTest = new ProjectService(null,mockGroupRepository,null,mockConfig);
        //when
        Throwable exception= catchThrowable(()-> toTest.createGroup(LocalDateTime.now(),0));
        //  then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one undone group");
        }
    @Test
    @DisplayName("Should throw IllegalArgumentException when configuration ok and no project for a given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        // given
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        ProjectService toTest = new ProjectService(mockRepository,null,null,mockConfig);
        //when
        Throwable exception= catchThrowable(()-> toTest.createGroup(LocalDateTime.now(),0));
        //  then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }
    @Test
    @DisplayName("Should throw IllegalArgumentException when configured to allow just 1 group and no groups and projects for a given id")
    void createGroup_noMultipleGroupsConfig_And_noUndoneGroupExists_noProjects_throwsIllegalArgumentException() {
        // given
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        // and
        TaskGroupRepository mockTaskGroupRepository = groupRepositoryReturning(false);
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        ProjectService toTest = new ProjectService(mockRepository,mockTaskGroupRepository,null,mockConfig);
        //when
        Throwable exception= catchThrowable(()-> toTest.createGroup(LocalDateTime.now(),0));
        //  then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }
    @Test
    @DisplayName("Should create a new group from project")
    void createGroup_configurationOk_existingProject_createsAndSavesGroup(){
        // given
        LocalDateTime today = LocalDate.now().atStartOfDay();
        //and
        Project project =projectWith("bar",Set.of(-1,-2));
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt()))
                .thenReturn(Optional.of(project));
        // and
        InMemoryGroupRepository inMemoryGroupRepo = new InMemoryGroupRepository();
        TaskGroupService serviceWithInMemRepo = dummyGroupService(inMemoryGroupRepo);
        int countBeforeCall=inMemoryGroupRepo.count();
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        ProjectService toTest = new ProjectService(mockRepository,inMemoryGroupRepo,serviceWithInMemRepo,mockConfig);
        //when
        GroupReadModel result =toTest.createGroup(today,1);
        //then
        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks().stream().allMatch(task->task.getDescription().equals("foo")));
        assertThat(countBeforeCall+1).isEqualTo(inMemoryGroupRepo.count());
    }

    private TaskGroupService dummyGroupService(final InMemoryGroupRepository inMemoryGroupRepo) {
        return new TaskGroupService(inMemoryGroupRepo, null);
    }

    private Project projectWith(String projectDescription, Set<Integer> daysToDeadLine){
        Set<ProjectStep> steps = daysToDeadLine.stream()
                .map(days -> {
                    ProjectStep step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("foo");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                }).collect(Collectors.toSet());
        Project result=mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);
        when(result.getSteps()).thenReturn(steps);
        return result;
    }

    private TaskGroupRepository groupRepositoryReturning(final boolean result) {
        TaskGroupRepository mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private TaskConfigurationProperties configurationReturning(final boolean result) {
        TaskConfigurationProperties.Template mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);
        TaskConfigurationProperties mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }
   private static class InMemoryGroupRepository implements TaskGroupRepository{
               private int index=0;
               private Map<Integer, TaskGroup> map = new HashMap<>();
               public int count(){
                   return map.values().size();
               }

               @Override
               public List<TaskGroup> findAll() {
                   return new ArrayList<>(map.values());
               }

               @Override
               public Optional<TaskGroup> findById(final Integer id) {
                   return Optional.ofNullable(map.get(id));
               }

               @Override
               public TaskGroup save(TaskGroup entity) {
                   if(entity.getId()==null){
                       try {
                           Field field = TaskGroup.class.getDeclaredField("id");
                           field.setAccessible(true);
                           field.set(entity,++index);
                       } catch(NoSuchFieldException | IllegalAccessException e) {
                           throw new RuntimeException(e);
                       }
                   }
                   map.put(entity.getId(),entity);
                   return entity;
               }

               @Override
               public boolean existsByDoneIsFalseAndProject_Id(final Integer projectId) {
                   return map.values().stream().filter(group->!group.isDone())
                           .anyMatch(group->group.getProject()!= null && group.getProject().getId()==projectId);
               }
           }
       }