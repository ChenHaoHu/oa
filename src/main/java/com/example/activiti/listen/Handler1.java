package com.example.activiti.listen;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public  class Handler1 implements JavaDelegate {

  @Override
  public void execute(DelegateExecution delegateExecution)  {
    System.out.println("11111111111");
    delegateExecution.setTransientVariable("state",8);
      delegateExecution.setVariable("state",8);

  }
}