package todoApplication.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import todoApplication.model.event.TaskDone;
import todoApplication.model.event.TaskEvent;
import todoApplication.model.event.TaskUndone;

@Service
class ChangedTaskeventListener {
    private static final Logger logger= LoggerFactory.getLogger(ChangedTaskeventListener.class);

    private final PersistedTaskEventRepository repository;

    ChangedTaskeventListener(PersistedTaskEventRepository repository){
        this.repository=repository;
    }
    @Async
    @EventListener
    public void on(TaskDone event){
        onChanged(event);
    }
    @Async
    @EventListener
    public void off(TaskUndone event){
        onChanged(event);
    }
    private void onChanged(final TaskEvent event){
        logger.info("Got " +event);
        repository.save(new PersistedTaskEvent(event));
    }
}
