package top.hcy.activiti.activitilisten;

import org.activiti.engine.delegate.DelegateExecution;

public interface ActivitiExecutionListener {
    default void notify(DelegateExecution delegateExecution, String state){
        if ("start".equals(state)){
            start(delegateExecution);
        }else if("end".equals(state)){
            end(delegateExecution);
        }
    }
    void start(DelegateExecution delegateExecution);
    void end(DelegateExecution delegateExecution);

}
