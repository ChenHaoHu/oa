package top.hcy.activiti;

import org.activiti.engine.impl.variable.JPAEntityMappings;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import top.hcy.activiti.entity.UserTask;

import java.util.List;


@Mapper
public interface UserTaskMapper  extends JpaRepository<UserTask,Integer> {

    List<UserTask> findUserTasksByUsername(String username);
}
