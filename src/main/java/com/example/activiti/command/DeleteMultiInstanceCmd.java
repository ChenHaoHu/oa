package com.example.activiti.command;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.ActivitiEngineAgenda;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.deploy.DeploymentManager;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.Activiti5Util;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.repository.ProcessDefinition;

public class DeleteMultiInstanceCmd implements Command {
    protected final String NUMBER_OF_INSTANCES = "nrOfInstances";
    protected final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
    protected final String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";
    protected String collectionElementIndexVariable = "loopCounter";

    private String taskId;
    private Boolean isNormalComplete;

    public DeleteMultiInstanceCmd(String taskId,boolean isNormalComplete) {
        this.taskId = taskId;
        this.isNormalComplete = isNormalComplete;
    }

    public Object execute(CommandContext commandContext) {
           ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();

        //根据任务id获取任务实例
        TaskEntity taskEntity = taskEntityManager.findById(taskId);
        //根据执行实例ID获取三级执行实例
        ExecutionEntity execution = executionEntityManager.findById(taskEntity.getExecutionId());
        //首先判断当前任务是否是属于多实例节点
        String processDefinitionId = execution.getProcessDefinitionId();
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionId);
        Activity activityElement = (Activity)bpmnModel.getFlowElement(execution.getCurrentActivityId());
        MultiInstanceLoopCharacteristics loopCharacteristics = activityElement.getLoopCharacteristics();
        if(loopCharacteristics == null){
            throw new RuntimeException("没有找到多实例");
        }
        if(!(activityElement.getBehavior() instanceof MultiInstanceActivityBehavior)){
            throw new RuntimeException("此节点不是多实例节点");
        }
        DeploymentManager deploymentManager = commandContext.getProcessEngineConfiguration().getDeploymentManager();
        ProcessDefinition definition = deploymentManager.findDeployedProcessDefinitionById(processDefinitionId);
        //判断是否是5的版本
        if(Activiti5Util.isActiviti5ProcessDefinition(commandContext,definition)){
            throw new RuntimeException("不支持5版本");
        }

        boolean isSequential = loopCharacteristics.isSequential();
        //获取二级执行实例
        ExecutionEntity sencondExecution = execution.getParent();
        //删除所要删除的实例相关数据
        if(!isSequential){
            //并行多实例
            executionEntityManager.deleteChildExecutions(execution,"减签",false);
            executionEntityManager.deleteExecutionAndRelatedData(execution,"减签",true);
        }

        //获取二级执行实例关联的变量
        Integer nrOfInstances = (Integer) sencondExecution.getVariable(NUMBER_OF_INSTANCES);
        Integer nrOfActiveInstances = (Integer) sencondExecution.getVariable(NUMBER_OF_ACTIVE_INSTANCES);
        Integer nrOfCompletedInstances = (Integer) sencondExecution.getVariable(NUMBER_OF_COMPLETED_INSTANCES);
        //更新二级执行实例关联的变量
        if(isNormalComplete){
            //正常完成
            sencondExecution.setVariable(NUMBER_OF_COMPLETED_INSTANCES,nrOfCompletedInstances+1 );
            if(isSequential){
                //串行多实例
                Integer loopCounter =getLoopVariable(execution,collectionElementIndexVariable);
                execution.setVariableLocal(collectionElementIndexVariable,loopCounter+1);
            }else{
                //并行多实例
                sencondExecution.setVariableLocal(NUMBER_OF_ACTIVE_INSTANCES,nrOfActiveInstances-1);
            }
        }else{
            //非正常（就当作没有过此任务）
            sencondExecution.setVariableLocal(NUMBER_OF_INSTANCES,nrOfInstances-1);
            if(isSequential){
                //串行多实例
                Integer loopCounter = (Integer) execution.getVariable(collectionElementIndexVariable);
                execution.setVariableLocal(collectionElementIndexVariable,loopCounter+1);
            }else{
                //并行多实例
                //并行多实例
                sencondExecution.setVariableLocal(NUMBER_OF_ACTIVE_INSTANCES,nrOfActiveInstances-1);
            }
        }
        //删除任务
        taskEntityManager.delete(taskId);

        //触发流程运转
        ActivitiEngineAgenda agenda = commandContext.getAgenda();
        agenda.planContinueProcessInCompensation(execution);


        return null;
    }
    protected Integer getLoopVariable(DelegateExecution execution, String variableName) {
        Object value = execution.getVariableLocal(variableName);
        DelegateExecution parent = execution.getParent();
        while (value == null && parent != null) {
            value = parent.getVariableLocal(variableName);
            parent = parent.getParent();
        }
        return (Integer) (value != null ? value : 0);
    }
}
