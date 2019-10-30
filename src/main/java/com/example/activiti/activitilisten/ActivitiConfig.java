package com.example.activiti.activitilisten;


import com.example.activiti.listen.StuServiceListener;
import com.example.activiti.listen.StuTaskListener;
import com.example.activiti.listen.TeaTaskListener;
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
    TaskListenMap.put("stus",stuPassListen);
    TaskListenMap.put("teas",teaTaskListener);
    return;
  }

  @Autowired
  private void initActivitiExecutionListen(){
    ExecutionListenMap.put("stu_service",stuServiceListener);
    return;
  }

}
