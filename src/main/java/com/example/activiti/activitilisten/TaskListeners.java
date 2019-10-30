package com.example.activiti.activitilisten;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * 任务监听器，实现TaskListener接口
 *
 * */
public class TaskListeners implements TaskListener {
	Logger logger = LoggerFactory.getLogger(TaskListeners.class);

	@Override
	public void notify(DelegateTask delegateTask) {
		ActivitiTaskListener listen = ActivitiConfig.TaskListenMap.get(delegateTask.getName());
		if (listen!=null){
		listen.notify(delegateTask,delegateTask.getEventName());
		}else{
			//logger.info("Task : "+delegateTask.getName()+" id : "+delegateTask.getId()+"没有设置监听器");
		}
	}
}