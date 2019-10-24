package com.example.activiti.controller;

import com.example.activiti.entity.LeaveBillBean;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.List;


public class DemoController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    Deployment deployment = null;

    /**
     * 部署流程
     */
    @RequestMapping(value = "/deploy")
    public String deployProcess(){
        deployment = repositoryService.createDeployment().addClasspathResource("templates/tt.bpmn").name("tt").deploy();
        System.out.println(deployment.getName());
        return "部署成功！";
    }


    @RequestMapping(value = "/start")
    public String start(){
        HashMap<String, Object> params = new HashMap<>();
        params.put("task2UserID", "李四");
        ProcessInstance tt = runtimeService.startProcessInstanceByKey("myProcess", params);
        List<Task> list = taskService.createTaskQuery().processInstanceId(tt.getId()).list();
        if(list != null && list.size() > 0){
            for (Task task1: list) {
                System.out.println("执行" + task1.getId() + " 任务名字：" + task1.getName());
                taskService.complete(task1.getId());
                System.out.println("已执行！");
            }
        }
        return tt.getId();
    }


    @RequestMapping(value = "/next/{id}")
    public String next(@PathVariable("id")String id){
        List<Task> list = taskService.createTaskQuery().processInstanceId(id).list();
        if(list != null && list.size() > 0){
            for (Task task1: list) {
                System.out.println("执行" + task1.getId() + " 任务名字：" + task1.getName());
                taskService.complete(task1.getId());
                System.out.println("已执行！");
            }
        }
        return id;
    }


    /**
     * 开启一个流程实例，并走完流程
     */
    @RequestMapping(value = "/testActiviti")
    public String firstDemo() {
        //根据流程的key启动
        HashMap<String, Object> params = new HashMap<>();
        params.put("task2UserID", "李四");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("myProcess", params);
        String processId = pi.getId();
        System.out.println("流程创建成功，当前流程实例ID：" + processId);

        /**
         * 查询任务并完成
         */
        List<Task> tasks1 = taskService.createTaskQuery().processInstanceId(processId).list();
        if(tasks1 != null && tasks1.size() > 0){
            for (Task task1: tasks1) {
                System.out.println("第一次执行前，任务id：" + task1.getId() + " 任务名字：" + task1.getName());
                taskService.complete(task1.getId());
                System.out.println("王五提交了请假申请！\n");
            }
        }

        /**
         * 查询任务并完成
         */
        List<Task> tasks2 = taskService.createTaskQuery().taskAssignee("李四").list();
        if(tasks2 != null && tasks2.size() > 0){
            for (Task task2 : tasks2) {
                System.out.println("第二次执行前，任务id：" + task2.getId() + " 任务名字：" + task2.getName());
                //取流程变量
                LeaveBillBean leaveBillBean = (LeaveBillBean)taskService.getVariable(task2.getId(), "leaveBillBean");
                if(leaveBillBean != null){
                    System.out.println(leaveBillBean.getPerson() + "的请假理由：" + leaveBillBean.getReason() + " 请假时间：" + leaveBillBean.getTime());
                }
                //完成任务
                taskService.complete(task2.getId());
                System.out.println("李四已经审批了！\n");
            }
        }

        /**
         * 查询的任务为空，即流程实例执行结束
         */
        Task taskNow = taskService.createTaskQuery().processInstanceId(processId).singleResult();
        if(taskNow == null){
            System.out.println("流程已经走完了！");
        }else{
            System.out.println("流程还没有结束！");
        }

        //删除流程部署
        //repositoryService.deleteDeployment(deployment.getId());
        return "王五请假及李四审批成功！";
    }

    /**
     * 开启一个流程实例，提交请假请求
     */
    @RequestMapping(value = "/askLeave")
    public String askLeave(){
        //根据流程的key启动
        HashMap<String, Object> params = new HashMap<>();
        params.put("task2UserID", "李四111");
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("myProcess", params);

        String processId = pi.getId();
        System.out.println("流程创建成功，当前流程实例ID：" + processId);

        /**
         * 查询任务并完成
         */
        List<Task> tasks1 = taskService.createTaskQuery().processInstanceId(processId).list();
        if(tasks1 != null && tasks1.size() > 0){
            for (Task task1: tasks1) {
                System.out.println("第一次执行前，任务id：" + task1.getId() + " 任务名字：" + task1.getName());
                //设置任务变量
                LeaveBillBean leaveBillBean = new LeaveBillBean("张三", "2018-08-22", "家中有急事！");
                taskService.setVariable(task1.getId(), "leaveBillBean", leaveBillBean);
                //完成任务
                taskService.complete(task1.getId());
                System.out.println("张三提交了请假申请！\n");
            }
        }
        return "张三请假成功!";
    }


    /**
     * 审核通过
     */
    @RequestMapping(value = "/passLeave/{ass}")
    public String passLeave(@PathVariable("ass") String ass){
        List<Task> tasks2 = taskService.createTaskQuery().taskAssignee(ass).list();
        if(tasks2 != null && tasks2.size() > 0){
            for (Task task2 : tasks2) {
                System.out.println("第二次执行前，任务id：" + task2.getId() + " 任务名字：" + task2.getName());
                //取流程变量
                LeaveBillBean leaveBillBean = (LeaveBillBean)taskService.getVariable(task2.getId(), "leaveBillBean");
                if(leaveBillBean != null){
                    System.out.println(leaveBillBean.getPerson() + "的请假理由：" + leaveBillBean.getReason() + " 请假时间：" + leaveBillBean.getTime());
                }
                //完成任务
                taskService.complete(task2.getId());
                System.out.println("李四已经审批了！\n");
            }
        }


        return "ok";
    }
}

