package todoApplication.controller;

import io.micrometer.core.annotation.Timed;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import todoApplication.logic.ProjectService;
import todoApplication.model.Project;
import todoApplication.model.ProjectRepository;
import todoApplication.model.ProjectStep;
import todoApplication.model.Task;
import todoApplication.model.projection.ProjectWriteModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@IllegalExceptionProcessing
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService service;
    private final ProjectRepository repository;
    ProjectController(final ProjectService service,ProjectRepository repository){
        this.service=service;
        this.repository=repository;
    }
    @GetMapping
    String showProjects(Model model){
            model.addAttribute("project", new ProjectWriteModel());
            return "projects";
    }
    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current){
        current.getSteps().add(new ProjectStep());
        return "projects";
    }
    @PostMapping(params="deleteStep")
    String deleteProjectStep(@ModelAttribute("project") ProjectWriteModel current, @RequestParam("deleteStep") int id){
        current.getSteps().remove(id);
        return "projects";
    }
    @PostMapping
    String addProject(
            @ModelAttribute("project") @Valid ProjectWriteModel current,
            BindingResult bindingResult,
            Model model
    ){
        if(bindingResult.hasErrors()){
            return "projects";
        }
        service.save(current);
        model.addAttribute("project",new ProjectWriteModel());
        model.addAttribute("projects",getProjects());
        model.addAttribute("message","Project added!");
        return "projects";
    }
    @Timed(value="project.create.group",histogram = true,percentiles = {0.5,0.95,0.99})
    @PostMapping("/{id}")
    String createGroup(
            @ModelAttribute("project") ProjectWriteModel current,
            Model model,
            @PathVariable int id,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline
    ){
        try {
            if(deadline==null)
                throw new IllegalStateException();
            service.createGroup(deadline,id);
            model.addAttribute("message","Group added!");
        } catch (IllegalStateException | IllegalArgumentException e){
            model.addAttribute("message","Error occurred, group was not created!");
        }
        return "projects";
    }
    @ModelAttribute("projects")
    List<Project> getProjects(){
        return service.findAll();
    }

    @PostMapping(value = "/{id}", params = "deleteProject")
    String deleteTask(@PathVariable int id,Model model){
        repository.deleteById(id);
        model.addAttribute("project",new ProjectWriteModel());
        model.addAttribute("projects",getProjects());
        return "projects";
    }
}
