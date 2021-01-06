package todoApplication.logic;
import todoApplication.model.*;
import todoApplication.model.projection.GroupReadModel;
import todoApplication.model.projection.GroupWriteModel;

import java.util.List;
import java.util.stream.Collectors;

public class TaskGroupService {
    private final TaskGroupRepository repository;
    private final TaskRepository taskRepository;

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
    public void closeGroup(int groupId){
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Done all the tasks first");
        }
        TaskGroup result = repository.findById(groupId)
                .orElseThrow(()->new IllegalArgumentException(("TaskGroup with given id not found")));
        result.setDone(!result.isDone());
        repository.save(result);
    }
    public Task addTaskToGroup(Task toAdd,Integer id){
        TaskGroup group =repository.findById(id).orElseThrow(()->new IllegalArgumentException(("TaskGroup with given id not found")));
        if(group.isDone())
            throw new IllegalStateException("Cannot add task to closed group");
        return taskRepository.save(new Task(toAdd.getDescription(), toAdd.getDeadline(),group));
    }

}
