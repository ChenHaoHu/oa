package com.example.activiti.test;

import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.context.transaction.TestTransaction.start;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Main {


    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    String uri = "templates/test1.bpmn";

    @Test //部署
    public void test1(){
        Deployment re = repositoryService
                .createDeployment()
                .name("aaa")
                .key("aaa")
                .category("aaa")
                .addClasspathResource(uri)
                .deploy();
        String id = re.getId();
        String key = re.getKey();
        System.out.println("id = "+id); //150001
        System.out.println("key = "+ key);
    }


    @Test//创建流程
    public void test2(){

        HashMap<String,Object> map = new HashMap<>();
        ArrayList<String> list= new ArrayList<>();
        list.add("user1__2");
        list.add("user2__2");
        list.add("user3__2");
        map.put("users",list);
        map.put("num",1);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("test7",map);
        String id = processInstance.getId();
        System.out.println("Process id = "+id);//95001
        System.out.println("processInstanceName = "+processInstance.getName());  //测试流程
        System.out.println("ProcessDefinitionId = "+processInstance.getProcessDefinitionId()); //hcy:2:92504
    }


    @Test//查看某流程
    public void test3(){//test5:1:150004
        List<Task> list = taskService.createTaskQuery().processDefinitionId("test6:1:160004").list();
        System.out.println(list.size());
        list.forEach(task -> {
            System.out.println(task.getName());

        });
    }

    @Test//查看某个人的审批任务
    public void test4(){
        List<Task> list = taskService.createTaskQuery().processDefinitionId("test7:1:175004").list();
        list.forEach(task -> {
            System.out.println(task.getName());
            System.out.println(task.getId());
             taskService.complete(task.getId());
        });
    }

    @Test//查看某个人的审批任务
    public void test5(){
        //.processDefinitionId("test5:1:150004")
        List<Task> list = taskService.createTaskQuery().processDefinitionId("test7:1:175004").list();
        list.forEach(task -> {
            System.out.println(task.getId());
            System.out.println(task.getAssignee());
            System.out.println(task.getName());

        });
    }


    /**查看当前任务办理人的个人任务*/
    @Test
    public void findPersonnelTaskList(){
        String assignee = "hcy";//当前任务办理人
        List<Task> tasks = //与任务相关的Service
                taskService.createTaskQuery()//创建一个任务查询对象
                        .taskAssignee(assignee)
                        .list();
        if(tasks !=null && tasks.size()>0){
            for(Task task:tasks){
                System.out.println("任务ID:"+task.getId());
                System.out.println("任务的办理人:"+task.getAssignee());
                System.out.println("任务名称:"+task.getName());
                System.out.println("任务的创建时间:"+task.getCreateTime());
                System.out.println("任务ID:"+task.getId());
                System.out.println("流程实例ID:"+task.getProcessInstanceId());
                System.out.println("#####################################");
            }
        }
    }

    // 查询流程定义
    @Test
    public void findProcessDifinitionList() {
        List<ProcessDefinition> list = repositoryService
                .createProcessDefinitionQuery()
                // 查询条件
                .processDefinitionKey("hcy")// 按照流程定义的key
                // .processDefinitionId("helloworld")//按照流程定义的ID
                .orderByProcessDefinitionVersion().desc()// 排序
                // 返回结果
                // .singleResult()//返回惟一结果集
                // .count()//返回结果集数量
                // .listPage(firstResult, maxResults)
                .list();// 多个结果集

        if(list!=null && list.size()>0){
            for(ProcessDefinition pd:list){
                System.out.println("流程定义的ID："+pd.getId());
                System.out.println("流程定义的名称："+pd.getName());
                System.out.println("流程定义的Key："+pd.getKey());
                System.out.println("流程定义的部署ID："+pd.getDeploymentId());
                System.out.println("流程定义的资源名称："+pd.getResourceName());
                System.out.println("流程定义的版本："+pd.getVersion());
                System.out.println("########################################################");
            }
        }

    }


    //删除流程定义
    @Test
    public void deleteProcessDifinition(){
        //部署对象ID
        String deploymentId = "601";
        //获取流程定义和部署对象相关的Service
        repositoryService.deleteDeployment(deploymentId,true);
        System.out.println("删除成功~~~");//使用部署ID删除流程定义,true表示级联删除
    }



    @Test//加签
    public void test6(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processInstanceId("95001").list();
        list.forEach(processInstance -> {
            System.out.println(processInstance.getProcessDefinitionId());
            System.out.println(processInstance.getId());
            System.out.println(processInstance.getDeploymentId());
            System.out.println(processInstance.getProcessDefinitionKey());
            taskService.createTaskQuery().processInstanceId("95001").list().forEach(task -> {
                System.out.println(task.getId()); //95008
                System.out.println(task.getName()); //UserTask
                taskService.complete(task.getId());
            });


            taskService.createTaskQuery().processInstanceId("95001").list().forEach(task -> {
                System.out.println(task.getId()); //95008
                System.out.println(task.getName()); //UserTask
            });
        });
    }

}
