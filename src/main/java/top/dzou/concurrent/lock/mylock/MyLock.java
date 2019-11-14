package top.dzou.concurrent.lock.mylock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author dingxiang
 * @date 19-7-21 下午12:06
 */
public class MyLock implements Lock {

    //线程获取到锁的标志
    private boolean isLocked = false;
    @Override
    public synchronized void lock() {
        //如果锁被获取到则等待释放
        while (isLocked){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //等到unlock释放锁后 获取锁
        isLocked = true;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public synchronized void unlock() {
        //设置获取锁的标志为未获取
        isLocked = false;
        //通知其他线程获取锁
        notify();
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
