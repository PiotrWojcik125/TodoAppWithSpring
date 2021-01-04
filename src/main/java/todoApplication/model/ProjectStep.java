package todoApplication.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "project_steps")
public class ProjectStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Project step's must not be empty")
    private String description;
    private Integer daysToDeadline;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    public ProjectStep(){}
    public Integer getId(){
        return id;
    }
    void setIdId(Integer id){
        this.id=id;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public Integer getDaysToDeadline(){
        return daysToDeadline;
    }
    public void setDaysToDeadline(Integer daysToDeadline){
        this.daysToDeadline=daysToDeadline;
    }
    public Project getProject() {
        return project;
    }
    public void setProject(final Project project) {
        this.project = project;
    }
}
