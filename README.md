# activiti — OA

> **介绍**
* 实现对监听器的封装，方便对监听器的增加，无需修改bpmn文件
* 实现简单的流程，多实例审批，并行加串行
* 示例中多实例判断条件是
`
${nrOfCompletedInstances/nrOfInstances >= 0.5}
`
* 实现简单的会签功能——加减签
* 串行多实例减签目前只能减去未来的签
* 只实现了流程，业务层面没有涉及
* 个人学习制作，不保证平稳运行


>  **接口文档**
# [POSTMAN](https://documenter.getpostman.com/view/5178290/SW11WyJq?version=latest)


> **流程图**

![流程图](/src/main/resources/templates/oa.png)