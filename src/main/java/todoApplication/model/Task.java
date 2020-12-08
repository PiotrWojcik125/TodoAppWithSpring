package todoApplication.model;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name="tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Task's description must not be empty")
    private String description;
    private boolean done;
    private LocalDateTime deadline;
    @Embedded
    private Audit audit=new Audit();
    @ManyToOne
    @JoinColumn(name="task_group_id")
    private TaskGroup group;
    /**
     * Hibernate (JPA needs it)
     */
    @SuppressWarnings("unused")

    Task(){}
    public Task(String description, LocalDateTime deadline){
        this(description,deadline,null);
    }
    public Task(String description, LocalDateTime deadline,TaskGroup group){
        this.description=description;
        this.deadline=deadline;
        if(group!=null)
            this.group=group;
    }

    public Integer getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    void setDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }

    TaskGroup getGroup() {
        return group;
    }

    void setGroup(final TaskGroup group) {
        this.group = group;
    }

    public void updateFrom(final Task source){
        description=source.description;
        done= source.done;
        deadline=source.deadline;
        group= source.group;
    }

}
