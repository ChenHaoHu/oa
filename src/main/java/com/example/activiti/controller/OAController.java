package com.example.activiti.controller;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
public class OAController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 部署流程
     */
    @RequestMapping(value = "/deploy/{bpmnName}")
    public Object deployProcess(@PathVariable("bpmnName")String bomnName){
       Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("templates/"+bomnName+".bpmn")
                .name(bomnName).deploy();
        return deployment;
    }

    /**
     * 创建流程
     */
    @RequestMapping(value = "/process/{deploy}")
    public Object buildProcess(@PathVariable("deploy")String deploy){
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        list.add("hcy");
        list.add("cww");
        list.add("cly");
        map.put("users",list);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(deploy,map);
        map.put("id",processInstance.getId());
        map.put("name",processInstance.getName());
        map.put("DeploymentId",processInstance.getDeploymentId());
        map.put("ProcessInstanceId",processInstance.getProcessInstanceId());
        map.put("ProcessInstanceId",processInstance.getProcessDefinitionId());
        return map;
    }


    /**
     * 用户主页
     */
    @RequestMapping(value = "/data/{user}")
    public Object userData(@PathVariable("user")String user){
        List<HashMap<String,String>> res = new ArrayList<>();
        List<Task> list = taskService.createTaskQuery().taskCandidateOrAssigned(user).list();
        list.forEach(task -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("id",task.getId());
            map.put("name",task.getName());
            map.put("assignee",task.getAssignee());
            map.put("ProcessInstanceId",task.getProcessInstanceId());
            map.put("ProcessInstanceId",task.getProcessDefinitionId());
            res.add(map);
        });
        return res;
    }


    /**
     * 流程主页
     */
    @RequestMapping(value = "/data/proid/{proid}")
    public Object proidData(@PathVariable("proid")String proid){
        List<HashMap<String,String>> res = new ArrayList<>();
        List<Task> list = taskService.createTaskQuery().processInstanceId(proid).list();
        list.forEach(task -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("id",task.getId());
            map.put("name",task.getName());
            map.put("assignee",task.getAssignee());
            map.put("ProcessInstanceId",task.getProcessInstanceId());
            map.put("ProcessInstanceId",task.getProcessDefinitionId());
            res.add(map);
        });
        return res;
    }

    /**
     * 审核步骤
     */
    @RequestMapping(value = "/pass/{taskid}")
    public Object passTask(@PathVariable("taskid")String taskid){
        Task task = taskService.createTaskQuery().taskId(taskid).singleResult();

        if(task == null){
            HashMap<String, String> map = new HashMap<>();

            map.put("state","没有这个任务");
            return map;
        }
        taskService.complete(task.getId());
        HashMap<String, String> map = new HashMap<>();
        map.put("id",task.getId());
        map.put("name",task.getName());
        map.put("assignee",task.getAssignee());
        map.put("ProcessInstanceId",task.getProcessInstanceId());
        map.put("ProcessInstanceId",task.getProcessDefinitionId());
        map.put("state","成功审核");
        return map;
    }
}

