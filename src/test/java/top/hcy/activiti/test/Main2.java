package top.hcy.activiti.test;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Main2 {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    HistoryService historyService;

    String uri = "templates/test1.bpmn";

    @Test//加签
    public void test6(){
        List<HashMap<String,String>> res = new ArrayList<>();
        List<Task> list = taskService.createTaskQuery().taskCandidateOrAssigned("test").list();
        list.forEach(task -> {
            task.setAssignee("tttt");
            taskService.saveTask(task);
            // System.out.println(task.getExecutionId());
            // processEngine.getManagementService().executeCommand(new AddMultiInstanceCmd("9","test"));
            // processEngine.getManagementService().executeCommand(new DeleteMultiInstanceCmd("30",true));
        });
    }

    @Test//历史
    public void test7(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().list();
        list.forEach(historicTaskInstance -> {
        });
    }

}
