package top.hcy.activiti.controller;

import org.activiti.engine.runtime.ExecutionQuery;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.springframework.web.bind.annotation.CrossOrigin;
import top.hcy.activiti.UserTaskMapper;
import top.hcy.activiti.command.AddMultiInstanceCmd;
import top.hcy.activiti.command.DeleteParallelMultiInstanceCmd;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.hcy.activiti.command.DeleteSerialMultiInstanceCmd;
import top.hcy.activiti.entity.UserTask;

import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/oa2")
public class OA2Controller {

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
    @Autowired
    UserTaskMapper userTaskMapper;

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
    public Object buildProcess(@RequestParam("deploy")String deploy,
                               @RequestParam("name")String name,
                               @RequestParam("author")String author,
                               @RequestParam("desc")String desc,
                               @RequestParam("level1")String level1,
                               @RequestParam("level2")String level2){
        HashMap<String, Object> map = new HashMap<>();
        String[] le1= level1.split(" ");
        List<String> user1 = Arrays.asList(le1);
        map.put("users1",user1);
        String[] le2= level2.split(" ");
        List<String> user2 = Arrays.asList(le2);
        map.put("users2",user2);
        map.put("num",1);
        map.put("name",name);
        map.put("desc",desc);
        map.put("author",author);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(deploy,map);
        UserTask userTask = new UserTask();
        userTask.setUsername(author);
        userTask.setProcessinstance(processInstance.getId());
        userTaskMapper.save(userTask);
        map.put("id",processInstance.getId());
        map.put("name",processInstance.getName());
        map.put("DeploymentId",processInstance.getDeploymentId());
        map.put("ProcessInstanceId",processInstance.getProcessInstanceId());
        map.put("ProcessDefinitionId",processInstance.getProcessDefinitionId());
        return map;
    }


    /**
     * 获取用户自己的任务
     * @param user
     * @return
     */
    @RequestMapping(value = "/userselftask")
    public Object userselftask(@RequestParam("user")String user){
        List<HashMap<String,Object>> res = new ArrayList<>();
        List<UserTask> tasks = userTaskMapper.findUserTasksByUsername(user);
        for (int i = 0; i < tasks.size(); i++) {
            UserTask userTask = tasks.get(i);
            String processinstance = userTask.getProcessinstance();
            List<Task> list = taskService.createTaskQuery().processInstanceId(processinstance).list();
            list.forEach(task -> {
                String taskId = task.getId();
                HashMap<String, Object> map = new HashMap<>();
                map.put("processname",taskService.getVariable(taskId, "name"));
                map.put("desc",taskService.getVariable(taskId, "desc"));
                map.put("author",taskService.getVariable(taskId, "author"));
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
                SimpleDateFormat temp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("time",temp.format(processInstance.getStartTime()));
                if (task.getAssignee().isEmpty()){
                    map.put("state","审批完成");
                }else{
                    map.put("state","审批中");
                }

                map.put("assignee",task.getAssignee());
                map.put("id",task.getId());
                map.put("name",task.getName());
                res.add(map);
            });
        }

        return res;
    }

    /**
     * 用户主页
     * 获取用户任务
     */
    @RequestMapping(value = "/data")
    public Object userData(@RequestParam("user")String user){
        List<HashMap<String,Object>> res = new ArrayList<>();
        List<Task> list = taskService.createTaskQuery().taskCandidateOrAssigned(user).list();
        list.forEach(task -> {
            String taskId = task.getId();
            HashMap<String,Object> map = new HashMap<>();
            map.put("id",taskId);
            map.put("name",task.getName());
            map.put("processname",taskService.getVariable(taskId, "name"));
            map.put("desc",taskService.getVariable(taskId, "desc"));
            map.put("author",taskService.getVariable(taskId, "author"));
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            SimpleDateFormat temp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("time",temp.format(processInstance.getStartTime()));
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
        String executionid = "";
        List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(processid).parentId(processid).list();
        if (list.size() == 1){
            executionid = list.get(0).getId();
        }
        if(executionid == ""){
            HashMap<String, String> map = new HashMap<>();
            map.put("state","没有这个流程");
            return map;
        }
        processEngine.getManagementService().executeCommand(new AddMultiInstanceCmd(String.valueOf(executionid),user,"users"));
        HashMap<String, String> map = new HashMap<>();
        map.put("state","加签成功");
        return map;
    }


    /**
     * 并行减签
     * @param taskid
     * @return
     */
    @RequestMapping(value = "/task/parallel/dele")
    public Object deleteParallelTask(@RequestParam("taskid")String taskid ){
        Task task = taskService.createTaskQuery().taskId(taskid).singleResult();
        if(task == null){
            HashMap<String, String> map = new HashMap<>();

            map.put("state","没有这个任务");
            return map;
        }
        processEngine.getManagementService().executeCommand(new DeleteParallelMultiInstanceCmd(taskid,true));
        HashMap<String, String> map = new HashMap<>();
        map.put("state","减签成功");
        return map;
    }


    /**
     * 串行减签——只能当前任务之后的签
     * @param
     * @return
     */
    @RequestMapping(value = "/task/serial /dele")
    public Object deleteSerialTask(@RequestParam("assign")String assign,
                                   @RequestParam("process")String processid){
        String executionid = "";
        List<Execution> list = runtimeService.createExecutionQuery().processInstanceId(processid).parentId(processid).list();
        if (list.size() == 1){
            executionid = list.get(0).getId();
        }
        if(executionid == ""){
            HashMap<String, String> map = new HashMap<>();
            map.put("state","没有这个流程");
            return map;
        }
        processEngine.getManagementService().executeCommand(new DeleteSerialMultiInstanceCmd(String.valueOf(executionid),assign,"users"));
        HashMap<String, String> map = new HashMap<>();
        map.put("state","减签成功");
        return map;
    }
}

