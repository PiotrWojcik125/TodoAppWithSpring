package todoApplication.logic;

import todoApplication.model.TaskRepository;
import todoApplication.TaskConfigurationProperties;
import todoApplication.model.ProjectRepository;
import todoApplication.model.TaskGroupRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LogicConfiguration {
    @Bean
    ProjectService projectService(
            final ProjectRepository repository,
            final TaskGroupRepository taskGroupRepository,
            final TaskGroupService taskGroupService,
            final TaskConfigurationProperties config
    ){
        return new ProjectService(repository,taskGroupRepository,taskGroupService,config);
    }
    @Bean
    TaskGroupService taskGroupService (
            final TaskGroupRepository repository,
            final TaskRepository taskRepository
    ){
        return new TaskGroupService(repository,taskRepository);
    }

}
