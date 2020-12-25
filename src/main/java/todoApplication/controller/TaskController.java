package todoApplication.controller;

import org.springframework.context.ApplicationEventPublisher;
import todoApplication.logic.TaskService;
import todoApplication.model.Task;
import todoApplication.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@IllegalExceptionProcessing
@RequestMapping("/tasks")
class TaskController {

    private final TaskRepository repository;
    private final TaskService service;
    private final ApplicationEventPublisher eventPublisher;
    private static final LocalDateTime today = LocalDateTime.of(
            LocalDateTime.now().getYear(),
            LocalDateTime.now().getMonth(),
            LocalDateTime.now().getDayOfMonth(),
            23,
            59,
            59
            );
    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    TaskController(final TaskRepository repository, final TaskService service,ApplicationEventPublisher eventPublisher){
        this.repository=repository;
        this.service = service;
        this.eventPublisher=eventPublisher;
    }
    @GetMapping(params={"!sort","!page","!size"})
    CompletableFuture<ResponseEntity<List<Task>>>readAllTasks(){
        logger.warn("exposing all the tasks!");
        return service.findAllAsync().thenApply(ResponseEntity::ok);
    }
    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.info("Custom pageable");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }
    @GetMapping("/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id){
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
        return ResponseEntity.ok(repository.findByDone(state));
    }
    @GetMapping("/today")
    ResponseEntity<List<Task>> findTasksForToday() {
        List<Task> result = repository.findAllByDoneIsFalseAndDeadlineIsNull();
        result.addAll(repository.findAllByDoneIsFalseAndDeadlineIsLessThanEqual(today));
        return ResponseEntity.ok().body(result);
    }
    @PutMapping("/{id}")
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        if(!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.findById(id).ifPresent(task->{
            task.updateFrom(toUpdate);
            repository.save(task);
        });
        repository.save(toUpdate);
        return ResponseEntity.noContent().build();
    }
    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody @Valid Task toCreate){
        Task createdTask = repository.save(toCreate);
        return ResponseEntity.created(URI.create("/"+createdTask.getId())).body(createdTask);
    }
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTask(@PathVariable int id){
        repository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toogleTask(@PathVariable int id){
        if(!repository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .map(Task::toogle)
                .ifPresent(eventPublisher::publishEvent);
        return ResponseEntity.noContent().build();
    }
}
