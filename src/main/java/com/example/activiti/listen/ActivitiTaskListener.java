package com.example.activiti.listen;

import org.activiti.engine.delegate.DelegateTask;

public interface ActivitiTaskListener {
    default void notify(DelegateTask delegateTask,String state){
        if ("create".equals(state)){
            create(delegateTask);
        }else if("assignment".equals(state)){
            assignment(delegateTask);
        }else if("complete".equals(state)){
            complete(delegateTask);
        }
    }
    void create(DelegateTask delegateTask);
    void assignment(DelegateTask delegateTask);
    void complete(DelegateTask delegateTask);
}
