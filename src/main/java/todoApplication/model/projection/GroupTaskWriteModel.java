package todoApplication.model.projection;

import org.springframework.format.annotation.DateTimeFormat;
import todoApplication.model.TaskGroup;
import todoApplication.model.Task;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class GroupTaskWriteModel {
    @NotBlank(message = "Task group's description mus not be empty")
    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime deadline;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }

    Task toTask(final TaskGroup group){
        return  new Task(description,deadline,group);
    }
}
