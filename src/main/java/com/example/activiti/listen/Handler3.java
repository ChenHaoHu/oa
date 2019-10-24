package com.example.activiti.listen;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public  class Handler3 implements JavaDelegate {

  @Override
  public void execute(DelegateExecution delegateExecution)  {
    System.out.println("3333333333");
   
  }
}