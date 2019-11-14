package top.dzou.concurrent.lock.mylock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author dingxiang
 * @date 19-7-21 下午12:23
 */
public class MyOverwriteLock implements Lock {
    private Thread lockThread = null;
    private int lockCount = 0;
    private boolean isLocked = false;

    @Override
    public synchronized void lock() {
        //第二个条件判断被锁的线程与当前贤臣过是否为同一个`，同一个的话可重入
        while(isLocked&&Thread.currentThread()!=lockThread){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isLocked = true;
        lockCount++;
        lockThread = Thread.currentThread();
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
        if(Thread.currentThread()==lockThread){
            lockCount--;
            if(lockCount==0){
                isLocked = false;
                notify();
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
