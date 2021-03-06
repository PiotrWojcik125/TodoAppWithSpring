package todoApplication.model.projection;
import todoApplication.model.TaskGroup;
import todoApplication.model.Project;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupWriteModel {
    @NotBlank(message = "Task group's description must not be empty")
    private String description;
    @Valid
    private List<TaskWriteModel> tasks=new ArrayList<>();
    public GroupWriteModel(){
        tasks.add(new TaskWriteModel());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public List<TaskWriteModel> getTasks() {
        return tasks;
    }
    public void setTasks(final List<TaskWriteModel> tasks) {
        this.tasks = tasks;
    }
    public TaskGroup toGroup(final Project project){
        TaskGroup result = new TaskGroup();
        result.setDescription(description);
        result.setTasks(
                tasks.stream()
                .map(source->source.toTask(result))
                .collect(Collectors.toSet())
        );
        result.setProject(project);
    return result;
    }
}
