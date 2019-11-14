package top.dzou.concurrent.lock.AQS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * AbstractQueuedSynchronizer 实现重入锁
 * @author dingxiang
 * @date 19-7-21 下午3:05
 */
public class MyAQSLock implements Lock {

    private final Sync sync;

    public MyAQSLock() {
        sync = new Sync();
    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1,unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

    /**
     * 把lock、unlock实现使用AQS构建为内部类
     */
    private class Sync extends AbstractQueuedSynchronizer{
        Condition newCondition(){
            return new ConditionObject();
        }

        @Override
        protected boolean tryAcquire(int arg) {
            //第一个线程进来拿到锁
            int state = getState();
            //用于重入锁判断
            Thread current = Thread.currentThread();
            if(state==0){
                if(compareAndSetState(0,arg)){
                    setExclusiveOwnerThread(Thread.currentThread());
                    return true;
                }
            }
            //重入锁判断 当前线程和独占锁线程相同，则再次获取
            else if(current==getExclusiveOwnerThread()){
                int next = state+arg;
                if(next<0){
                    throw new RuntimeException();
                }
                setState(next);
                return true;
            }
            return false;
        }

        /**
         * 可重入释放锁
         * @param arg
         * @return
         */
        @Override
        protected boolean tryRelease(int arg) {
            if(Thread.currentThread()!=getExclusiveOwnerThread()){
                throw new RuntimeException();
            }
            int state = getState()-arg;
            if(state==0){
                setExclusiveOwnerThread(null);
                setState(0);
                return true;
            }
            setState(0);
            return false;
        }

    }
}
