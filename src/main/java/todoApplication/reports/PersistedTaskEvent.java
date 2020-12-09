package todoApplication.reports;


import todoApplication.model.event.TaskEvent;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name ="task_events")
class PersistedTaskEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    LocalDateTime occurrence;
    int taskId;
    String name;
    public PersistedTaskEvent(){}

    PersistedTaskEvent(TaskEvent source){
        taskId=source.getTaskId();
        name=source.getClass().getSimpleName();
        occurrence=LocalDateTime.ofInstant(source.getOccurrence(), ZoneId.systemDefault());
    }
}
