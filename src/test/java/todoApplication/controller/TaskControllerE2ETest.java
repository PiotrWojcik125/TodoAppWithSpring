package todoApplication.controller;

import todoApplication.model.Task;
import todoApplication.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import java.time.LocalDateTime;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TaskRepository repo;

    @Test
    void httpGet_returnsAllTasks(){
        //given
        int initial = repo.findAll().size();
        repo.save((new Task("foo", LocalDateTime.now())));
        repo.save((new Task("bar", LocalDateTime.now())));
        //when
        Task[] result = restTemplate.getForObject("http://localhost:"+port+"/tasks",Task[].class);
        //then
        assertThat(result).hasSize(initial+2);
    }
    @Test
    void httpPost_create_Task() throws Exception {
        //given
        int initial = repo.findAll().size();
        initial++;
        Task toCreate = new Task("newTask", LocalDateTime.now());
        restTemplate.postForObject("http://localhost:"+port+"/tasks",toCreate,Task.class);
        // when
        Task result =restTemplate.getForObject("http://localhost:"+port+"tasks/"+initial,Task.class);
        // then
        assertThat(result.getId()).isEqualTo(initial);
    }
}