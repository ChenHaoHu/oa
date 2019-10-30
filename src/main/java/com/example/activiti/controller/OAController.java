package com.example.activiti.controller;

import com.example.activiti.command.AddMultiInstanceCmd;
import com.example.activiti.command.DeleteMultiInstanceCmd;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    ProcessEngine processEngine;
    @Autowired
    HistoryService historyService;

    /**
     * 部署流程
     */
    @RequestMapping(value = "/deploy")
    public Object deployProcess(@RequestParam("bpmnName")String bomnName){
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("templates/"+bomnName+".bpmn")
                .name(bomnName).deploy();
        return deployment;
    }

    /**
     * 创建流程
     */
    @RequestMapping(value = "/process")
    public Object buildProcess(@RequestParam("deploy")String deploy){
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        list.add("hcy");
        list.add("cww");
        list.add("cly");
        map.put("users",list);
        map.put("num",1);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(deploy,map);
        map.put("id",processInstance.getId());
        map.put("name",processInstance.getName());
        map.put("DeploymentId",processInstance.getDeploymentId());
        map.put("ProcessInstanceId",processInstance.getProcessInstanceId());
        map.put("ProcessDefinitionId",processInstance.getProcessDefinitionId());
        return map;
    }

    /**
     * 用户主页
     */
    @RequestMapping(value = "/data")
    public Object userData(@RequestParam("user")String user){
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
    @RequestMapping(value = "/data/proid")
    public Object proidData(@RequestParam("proid")String proid){
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
    @RequestMapping(value = "/pass")
    public Object passTask(@RequestParam("taskid")String taskid){
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


    /**
     * 加签
     * @param processid
     * @param user
     * @return
     */
    @RequestMapping(value = "/task/user/add")
    public Object addTask(@RequestParam("process")String processid,
                          @RequestParam("user")String user){
        int executionid = 0;
        List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(processid).list();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getParentId()!=null){
                if (Integer.valueOf(list.get(i).getParentId()) > executionid){
                    executionid = Integer.valueOf(list.get(i).getParentId());
                }
            }
        }
        if(executionid == 0){
            HashMap<String, String> map = new HashMap<>();
            map.put("state","没有这个流程");
            return map;
        }
        processEngine.getManagementService().executeCommand(new AddMultiInstanceCmd(String.valueOf(executionid),user));
        HashMap<String, String> map = new HashMap<>();
        map.put("state","加签成功");
        return map;
    }


    /**
     * 减签
     * @param taskid
     * @return
     */
    @RequestMapping(value = "/task/user/dele")
    public Object deleteTask(@RequestParam("taskid")String taskid ){

        Task task = taskService.createTaskQuery().taskId(taskid).singleResult();

        if(task == null){
            HashMap<String, String> map = new HashMap<>();

            map.put("state","没有这个任务");
            return map;
        }
        processEngine.getManagementService().executeCommand(new DeleteMultiInstanceCmd(taskid,true));
        HashMap<String, String> map = new HashMap<>();
        map.put("state","减签成功");
        return map;
    }
}

