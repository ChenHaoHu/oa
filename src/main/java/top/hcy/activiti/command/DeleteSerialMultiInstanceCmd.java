package top.hcy.activiti.command;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.List;

public class DeleteSerialMultiInstanceCmd implements Command {
    protected final String NUMBER_OF_INSTANCES = "nrOfInstances";
    protected final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
    private String multiRootExecutionId;
    private String deleUserTaskAssign;
    private String usersStr;

    public DeleteSerialMultiInstanceCmd(String multiRootExecutionId,String deleUserTaskAssign, String usersStr) {
        this.multiRootExecutionId = multiRootExecutionId;
        this.deleUserTaskAssign = deleUserTaskAssign;
        this.usersStr = usersStr;
    }

    public Object execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        //获取多实例父级执行实例
        ExecutionEntity multiExecutionEntity = executionEntityManager.findById(multiRootExecutionId);
        //判断当前执行实例的节点是否是多实例节点
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(multiExecutionEntity.getProcessDefinitionId());
        Activity miActivityElement = (Activity) bpmnModel.getFlowElement(multiExecutionEntity.getCurrentActivityId());
        MultiInstanceLoopCharacteristics loopCharacteristics = miActivityElement.getLoopCharacteristics();
        if(loopCharacteristics == null){
            throw new ActivitiException("此节点不是多实例节点");
        }
        if(loopCharacteristics.isSequential()){
            List users = (List)multiExecutionEntity.getVariable(usersStr);
            Integer nrOfCompletedInstances = (Integer)multiExecutionEntity.getVariable("nrOfCompletedInstances");
            int i = users.indexOf(deleUserTaskAssign);
            if (i<=nrOfCompletedInstances){
                return null;
            }else{
                users.remove(deleUserTaskAssign);
            }
            multiExecutionEntity.setVariable(usersStr,users);
            multiExecutionEntity.setVariable("nrOfInstances",Integer.valueOf(String.valueOf(multiExecutionEntity.getVariable("nrOfInstances")))-1);
            users.forEach(o -> {
                System.out.print(o+"____");
            });
            System.out.println();


            return null;
        }
        return null;
    }

}