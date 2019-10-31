package top.hcy.activiti.listen;

import top.hcy.activiti.activitilisten.ActivitiExecutionListener;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class StuServiceListener implements ActivitiExecutionListener {

    @Override
    public void start(DelegateExecution delegateExecution) {

    }

    @Override
    public void end(DelegateExecution delegateExecution) {
        Object nrOfCompletedInstances = delegateExecution.getVariable("nrOfCompletedInstances");
        Object nrOfInstances  = delegateExecution.getVariable("nrOfInstances");
        System.out.println(nrOfCompletedInstances+"------------------"+nrOfInstances);
        System.out.println("学生会审核完成");
        System.out.println("正在推送给老师审核");
        System.out.println("成功推送给老师审核");

        ArrayList<String> list = new ArrayList<>();
        list.add("teacher_A");
        list.add("teacher_B");
        list.add("teacher_C");
        list.add("teacher_D");
        list.add("teacher_E");
        list.add("teacher_F");
        list.add("teacher_G");
        delegateExecution.setVariable("users",list);
    }
}
