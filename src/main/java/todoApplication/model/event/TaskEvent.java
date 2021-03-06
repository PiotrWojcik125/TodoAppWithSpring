package todoApplication.model.event;

import todoApplication.model.Task;

import java.time.Clock;
import java.time.Instant;

public abstract class TaskEvent {
    private final int taskId;
    private final Instant occurrence;

    public static TaskEvent changed(Task source){
        return source.isDone() ? new TaskDone(source) : new TaskUndone(source);
    }

    TaskEvent(final int taskId, Clock clock){
        this.taskId=taskId;
        this.occurrence=Instant.now(clock);
    }
    public int getTaskId() {
        return taskId;
    }

    public Instant getOccurrence() {
        return occurrence;
    }
    @Override
    public String toString(){
        return getClass().getSimpleName() + "{" +
                "taskId" + taskId +
                ", occurrence= " +occurrence +
                "}";
    }
}
