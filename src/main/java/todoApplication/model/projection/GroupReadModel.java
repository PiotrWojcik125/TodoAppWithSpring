package todoApplication.model.projection;

import todoApplication.model.TaskGroup;
import todoApplication.model.Task;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupReadModel {
    private int id;
    private String description;
    /**
     * Deadline from the latest task in group
     */
    private LocalDateTime deadline;
    private Set<TaskReadModel> tasks;
    private boolean done;
    public GroupReadModel(TaskGroup source){
        id=source.getId();
        description= source.getDescription();
        source.getTasks().stream()
                .map(Task::getDeadline)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresent(date->deadline=date);
        tasks=source.getTasks()
                .stream()
                .map(TaskReadModel::new)
                .collect(Collectors.toSet());
        done=source.isDone();
    }
    public int getId() {
        return id;
    }

    public boolean isDone(){
        return done;
    }
    public void setDone(boolean done){
        this.done=done;
    }
    void setId(final int id) {
        this.id = id;
    }

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

    public Set<TaskReadModel> getTasks() {
        return tasks;
    }

    public void setTasks(final Set<TaskReadModel> tasks) {
        this.tasks = tasks;
    }
}
