package com.xx.springbootdemo.learn;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xx.springbootdemo.entity.User;
import com.xx.springbootdemo.service.IUserService;
import com.xx.springbootdemo.service.impl.UserServiceImpl;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 结论：
 *  1、无论是声明式事务还是手动事务，如果内部通过代理调用的方法使用了声明式事务，那么声明式事务也会生效（如事务的传播特性之类的）
 *  2、对于和分布式锁一起使用锁的范围太大的问题这个无解，主要原因还是事务的范围太大，而锁需要锁的范围是整个事务包含事务和想关的验证，否则不生效
 * @author hanyangyang
 * @date 2025/3/4
 **/
@Component
public class TransactionWithLockLearn {

  @Resource
  private RedissonClient redissonClient;
  @Autowired
  private ApplicationContext applicationContext;

  @Resource
  private TransactionTemplate transactionTemplate;

  @Resource
  private IUserService userService;

  @Autowired
  private PlatformTransactionManager transactionManager;


  /**
   * 注解式事务和分布式锁一起使用
   *
   * @param phone
   * @throws InterruptedException
   */
  @Transactional(rollbackFor = Exception.class)
  public void annotationTransaction(String phone) throws InterruptedException {
    RLock lock = redissonClient.getLock("phone_lock");
    lock.lock();
    User user = new User();
    user.setName(UUID.randomUUID().toString());
    user.setPhone(phone);
    user.setGender("MAN");
    userService.save(user);
    try {
      // 若目标方法事务隔离级别为REQUIRES_NEW，则外部方法报错不会影响到目标方法事务
      applicationContext.getBean(TransactionWithLockLearn.class).doUpdate(phone);
      // 不能找到对应的额事务id
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // 注册事务同步回调：在事务提交后释放锁(声明式事务和编程式事务中都可以使用)，这里获取的是外层事务@Transactional注解对应的事务
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              System.out.println("事务提交");
              lock.unlock();
            }

            @Override
            public void afterCompletion(int status) {

              // 事务回滚时也释放锁（可选，根据业务需求）
              if (status == STATUS_ROLLED_BACK) {
                System.out.println("事务回滚");
                lock.unlock();
              }
            }
          });
    }
    TimeUnit.SECONDS.sleep(5);
  }

  /**
   * 通过自定义的transactionTemplate修改编程式事务的属性
   * @return
   */
  /*@Bean
  public TransactionTemplate transactionTemplate() {
    TransactionTemplate customerTransactionTemplate = new TransactionTemplate();
    customerTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    customerTransactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    return customerTransactionTemplate;
  }*/

  /**
   * transactionTemplate 手动编程事务和分布式锁一起使用
   *
   * @param phone
   * @throws InterruptedException
   */
  @Transactional(rollbackFor = Exception.class)
  public void transactionTemplateTransaction(String phone) throws InterruptedException {
    RLock lock = redissonClient.getLock("phone_lock");
    lock.lock();
    User user = new User();
    user.setName(UUID.randomUUID().toString());
    user.setPhone(phone);
    user.setGender("MAN");
    userService.save(user);
    try {
      transactionTemplate.execute(new TransactionCallback<Object>() {
        @Override
        public Object doInTransaction(TransactionStatus status) {
          boolean isNew = status.isNewTransaction();
          System.out.println("transactionTemplate默认是否开启新事务测试："+isNew);
          // 若目标方法事务隔离级别为REQUIRES_NEW，则外部方法报错不会影响到目标方法事务
          try {
            // 内层开启新事物有效，如果内层报错，内层回滚，外层通过捕获异常的方式不会回滚事务
            applicationContext.getBean(TransactionWithLockLearn.class).doUpdate(phone);
          }catch (Exception e){
            System.out.println("最内层方法调用出现错误");
            e.printStackTrace();
          }
          return null;
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      // 注册事务同步回调：在事务提交后释放锁(声明式事务和编程式事务中都可以使用)，
      // 这里获取的是外层事务@Transactional注解对应的事务，如果要获取内层事务需要在transactionTemplate.execute()方法里面加入下面的代码
      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              System.out.println("事务提交");
              lock.unlock();
            }

            @Override
            public void afterCompletion(int status) {
              System.out.println("事务完成");
              // 事务回滚时也释放锁（可选，根据业务需求）
              if (status == STATUS_ROLLED_BACK) {
                lock.unlock();
              }
            }
          });
    }

    //TimeUnit.SECONDS.sleep(5);
  }


  public void TransactionDefinitionTx(String phone) throws InterruptedException {
    // 创建事务定义（可自定义属性）
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

    TransactionStatus status = transactionManager.getTransaction(definition);
    try {
      // 执行业务逻辑（如数据库操作）

      User user = new User();
      user.setName(UUID.randomUUID().toString());
      user.setPhone(phone);
      user.setGender("MAN");
      userService.save(user);

      try {
        // 内层开启新事物有效，如果内层报错，内层回滚，外层通过捕获异常的方式不会回滚事务
        applicationContext.getBean(this.getClass()).doUpdate(phone);
      }catch (Exception e){
        System.out.println("最内层方法调用出现错误");
        e.printStackTrace();
      }

      // 提交外层事务
      transactionManager.commit(status);
    } catch (Exception e) {
      // 回滚事务
      transactionManager.rollback(status);
      throw e;
    }
  }


  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public void doSaveRepeat(String phone) {
    User exitUser = userService.getOne(Wrappers.lambdaQuery(User.class).eq(User::getPhone, phone));
    if (exitUser != null) {
      throw new RuntimeException("手机号已存在");
    }
    User user = new User();
    user.setName(UUID.randomUUID().toString());
    user.setPhone(phone);
    user.setGender("MAN");
    userService.save(user);
    int i = 1 / 0;

  }

  @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
  public void doUpdate(String phone) {
    userService.update(Wrappers.lambdaUpdate(User.class).set(User::getGender, "WOMAN")
        .eq(User::getId, 22));
    int i = 1 / 0;

  }
}
