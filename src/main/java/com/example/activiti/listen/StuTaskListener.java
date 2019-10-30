package com.example.activiti.listen;

import com.example.activiti.activitilisten.ActivitiTaskListener;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.springframework.stereotype.Component;

@Component
public class StuTaskListener implements ActivitiTaskListener {

    @Override
    public void create(DelegateTask delegateTask) {
        System.out.println(delegateTask.getId()+"-----------"+delegateTask.getAssignee()+"--------任务创建");
    }

    @Override
    public void assignment(DelegateTask delegateTask) {

    }

    @Override
    public void complete(DelegateTask delegateTask) {
        DelegateExecution execution = delegateTask.getExecution();
        Object num = execution.getVariable("num");
        delegateTask.setVariable("num",Integer.parseInt(num+"")+1);
        System.out.println(delegateTask.getId()+"-----------"+delegateTask.getAssignee()+"--------完成审核");
    }


}
