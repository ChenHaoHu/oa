package com.example.activiti.listen;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;

/**
 *
 * 任务监听器，实现TaskListener接口
 *
 * */
public class EXListenerDemo implements ExecutionListener {
	@Override
	public void notify(DelegateExecution delegateExecution) {
		System.out.println("我是监听器ex-----------------"+delegateExecution.getId());
	}
}