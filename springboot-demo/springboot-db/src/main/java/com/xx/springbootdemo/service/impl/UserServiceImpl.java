package com.xx.springbootdemo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.springbootdemo.entity.User;
import com.xx.springbootdemo.mapper.UserMapper;
import com.xx.springbootdemo.service.IUserService;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;


/**
 * @author hanyangyang
 * @date 2025/3/4
 **/
@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements IUserService{

  @Resource
  private RedissonClient redissonClient;
  @Autowired
  private ApplicationContext applicationContext;

  @Resource
  private TransactionTemplate transactionTemplate;


  @Transactional(rollbackFor = Exception.class)
  public void repeatInsertUser1(String phone) throws InterruptedException {
    RLock lock = redissonClient.getLock("phone_lock");
    lock.lock();
    this.remove(null);
    try{
      applicationContext.getBean(UserServiceImpl.class).doSaveRepeat(phone);
      // 不能找到对应的额事务id
    }catch (Exception e){
      e.printStackTrace();
    }
    finally {
      // 注册事务同步回调：在事务提交后释放锁(声明式事务和编程式事务中都可以使用)
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
    TimeUnit.SECONDS.sleep(5);
  }





  @Override
  @Transactional(rollbackFor = Exception.class)
  public void repeatInsertUser(String phone) throws InterruptedException {
    RLock lock = redissonClient.getLock("phone_lock");
    lock.lock();
    this.remove(null);
    try{

      transactionTemplate.execute(new TransactionCallback<Object>() {
        @Override
        public Object doInTransaction(TransactionStatus status) {
          applicationContext.getBean(UserServiceImpl.class).doSaveRepeat(phone);
          return null;
        }
      });

    }catch (Exception e){
      e.printStackTrace();
    }
    finally {
      // 注册事务同步回调：在事务提交后释放锁(声明式事务和编程式事务中都可以使用)
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
    TimeUnit.SECONDS.sleep(5);
  }


  @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
  public void doSaveRepeat(String phone){
    User exitUser = this.getOne(Wrappers.lambdaQuery(User.class).eq(User::getPhone,phone));
    if (exitUser!=null){
      throw new RuntimeException("手机号已存在");
    }
    User user = new User();
    user.setName(UUID.randomUUID().toString());
    user.setPhone(phone);
    user.setGender("MAN");
    save(user);
    int i = 1/0;
  }
}
