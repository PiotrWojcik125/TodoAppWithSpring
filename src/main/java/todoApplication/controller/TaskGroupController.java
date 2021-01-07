package todoApplication.controller;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import todoApplication.model.Task;
import todoApplication.model.TaskGroupRepository;
import todoApplication.model.projection.GroupReadModel;
import todoApplication.model.projection.TaskWriteModel;
import todoApplication.model.projection.GroupWriteModel;
import todoApplication.logic.TaskGroupService;
import todoApplication.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
@IllegalExceptionProcessing
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
@RequestMapping("/groups")
public class TaskGroupController {
    private final TaskGroupService service;
    private final TaskRepository repository;
    private final TaskGroupRepository groupRepository;
    TaskGroupController(final TaskGroupService service, final TaskRepository repository, final TaskGroupRepository groupRepository){
        this.service=service;
        this.repository=repository;
        this.groupRepository=groupRepository;
    }
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model){
        model.addAttribute("group",new GroupWriteModel());
        return "groups";
    }
    @PostMapping(params = "addTask",produces = MediaType.TEXT_HTML_VALUE)
    String addGroupTask(@ModelAttribute("group") GroupWriteModel current){
        current.getTasks().add(new TaskWriteModel());
        return "groups";
    }
    @PostMapping(params = "deleteTask",produces = MediaType.TEXT_HTML_VALUE,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String deleteGroupTask(@ModelAttribute("group") GroupWriteModel current,@RequestParam("deleteTask")int id){
        current.getTasks().remove(id);
        return "groups";
    }
    @PostMapping(produces = MediaType.TEXT_HTML_VALUE,consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(
            @ModelAttribute("group") @Valid GroupWriteModel current,
            BindingResult bindingResult,
            Model model
    ){
        if(bindingResult.hasErrors()){
            return "groups";
        }
        service.createGroup(current);
        model.addAttribute("group",new GroupWriteModel());
        model.addAttribute("groups",getGroups());
        model.addAttribute("message","Group added!");
        return "groups";
    }
    @ResponseBody
    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id){
        return ResponseEntity.ok(repository.findAllByGroup_Id(id));
    }
    @ResponseBody
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate){
        GroupReadModel result = service.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/"+result.getId())).body(result);
    }
    @ResponseBody
    @PostMapping("/{id}")
    ResponseEntity<Task> addTaskToGroup(@RequestBody @Valid Task toCreate,@PathVariable Integer id){
        try {
            Task createdTask = service.addTaskToGroup(toCreate, id);
            return ResponseEntity.created(URI.create("/" + createdTask.getId())).body(createdTask);
        }catch (IllegalStateException e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping(value = "/{id}", params = "deleteGroup")
    String deleteGroup(@PathVariable int id,Model model)
    {
        model.addAttribute("group",new GroupWriteModel());
        groupRepository.deleteById(id);
        model.addAttribute("groups",getGroups());
        return "groups";
    }
    @PostMapping(value = "/{id}", params = "closeGroup")
    String closeGroup (@PathVariable int id, Model model){
        try {
            service.closeGroup(id);
        } catch (IllegalStateException | IllegalArgumentException | IllegalCallerException e){
            if (e instanceof IllegalStateException) {
                model.addAttribute("errorMessage", "Group has undone tasks. Done all the tasks first");
            }
            else if (e instanceof IllegalArgumentException) {
                model.addAttribute("errorMessage", "TaskGroup with given id not found");
            }else {
                model.addAttribute("errorMessage", "Group is already closed");
            }
        }
        model.addAttribute("group",new GroupWriteModel());
        model.addAttribute("groups",getGroups());
        return "groups";
    }
    @ModelAttribute("groups")
    public List<GroupReadModel> getGroups() {
        return service.readAll();
    }
}
