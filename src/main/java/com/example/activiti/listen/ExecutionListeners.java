package com.example.activiti.listen;

import org.activiti.engine.delegate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 任务监听器，实现ExecutionListener接口
 *
 * */
public class ExecutionListeners implements ExecutionListener, JavaDelegate {
	Logger logger = LoggerFactory.getLogger(TaskListeners.class);

	void invoke(DelegateExecution delegateExecution){
		ActivitiExecutionListener listen = ActivitiConfig.ExecutionListenMap.get(delegateExecution.getCurrentActivityId());
		if (listen!=null){
			listen.notify(delegateExecution,delegateExecution.getEventName());
		}else{
			//	logger.info("Task : "+delegateExecution.getCurrentActivityId()+" id : "+delegateExecution.getId()+"没有设置监听器");
		}
	}

	@Override
	public void notify(DelegateExecution delegateExecution) {
		invoke(delegateExecution);
	}

	@Override
	public void execute(DelegateExecution delegateExecution) {
		invoke(delegateExecution);
	}
}