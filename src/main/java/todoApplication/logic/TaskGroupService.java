package todoApplication.logic;
import todoApplication.model.Project;
import todoApplication.model.TaskGroup;
import todoApplication.model.TaskRepository;
import todoApplication.model.projection.GroupReadModel;
import todoApplication.model.projection.GroupWriteModel;
import todoApplication.model.TaskGroupRepository;

import java.util.List;
import java.util.stream.Collectors;

public class TaskGroupService {
    private TaskGroupRepository repository;
    private TaskRepository taskRepository;

    TaskGroupService(final TaskGroupRepository repository,final TaskRepository taskRepository){
        this.repository=repository;
        this.taskRepository=taskRepository;
    }
    public GroupReadModel createGroup(final GroupWriteModel source){
        return createGroup(source,null);
    }
    GroupReadModel createGroup(final GroupWriteModel source, Project project){
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }
    public List<GroupReadModel> readAll(){
        return repository.findAll()
                .stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }
    public void toogleGroup(int groupId){
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Done all the tasks first");
        }
        TaskGroup result = repository.findById(groupId)
                .orElseThrow(()->new IllegalArgumentException(("TaskGroup with given id not found")));
        result.setDone(!result.isDone());
        repository.save(result);
    }


}
