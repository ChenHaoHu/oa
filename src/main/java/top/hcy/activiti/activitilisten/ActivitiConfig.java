package top.hcy.activiti.activitilisten;


import top.hcy.activiti.listen.StuServiceListener;
import top.hcy.activiti.listen.StuTaskListener;
import top.hcy.activiti.listen.TeaTaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ActivitiConfig {

  public static HashMap<String, ActivitiTaskListener> TaskListenMap  = new HashMap<>();
  public static HashMap<String, ActivitiExecutionListener>  ExecutionListenMap  = new HashMap<>();

  @Autowired
  StuTaskListener stuPassListen;
  @Autowired
  TeaTaskListener teaTaskListener;
  @Autowired
  StuServiceListener stuServiceListener;

  @Autowired
  private void initActivitiTaskListen(){
    //key 为 task 的 NAME
    TaskListenMap.put("stus",stuPassListen);
    TaskListenMap.put("teas",teaTaskListener);
    return;
  }

  @Autowired
  private void initActivitiExecutionListen(){
    //key 为 Execution 的 ID  或者 task 的 ID
    ExecutionListenMap.put("stu_service",stuServiceListener);
    return;
  }

}
