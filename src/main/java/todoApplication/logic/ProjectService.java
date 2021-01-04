package todoApplication.logic;
import todoApplication.model.Project;
import todoApplication.model.ProjectRepository;
import todoApplication.model.TaskGroupRepository;
import todoApplication.model.projection.GroupReadModel;
import todoApplication.model.projection.TaskWriteModel;
import todoApplication.model.projection.GroupWriteModel;
import todoApplication.model.projection.ProjectWriteModel;
import todoApplication.TaskConfigurationProperties;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


public class ProjectService {
    private final ProjectRepository repository;
    private final TaskConfigurationProperties config;
    private final TaskGroupRepository taskGroupRepository;
    private final TaskGroupService taskGroupService;

    public ProjectService(ProjectRepository repository, TaskGroupRepository groupRepository, TaskGroupService taskGroupService, TaskConfigurationProperties config) {
        this.repository = repository;
        this.config = config;
        this.taskGroupRepository = groupRepository;
        this.taskGroupService =taskGroupService;
    }

    public List<Project> findAll() {
        return repository.findAll();
    }

    public Project save(final ProjectWriteModel toSave) {
        return repository.save(toSave.toProject());
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }
        return repository.findById(projectId)
                .map(project -> {
                    GroupWriteModel targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> {
                                            var task = new TaskWriteModel();
                                            task.setDescription(project.getDescription());
                                            task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                            return task;
                                    })
                                    .collect(Collectors.toList())
                    );
                    return taskGroupService.createGroup(targetGroup,project);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
    }
}