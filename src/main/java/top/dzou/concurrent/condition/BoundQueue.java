package top.dzou.concurrent.condition;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dingxiang
 * @date 19-8-6 下午3:01
 */

/**
 * 使用Condition+Lock实现的有界队列
 */
public class BoundQueue {

    private Lock lock = new ReentrantLock();

    private Condition addCondition = lock.newCondition();
    private Condition pollCondition = lock.newCondition();

    private Object[] boundQueue = new Object[20];

    private int count;
    private int addIndex;
    private int pollIndex;

    public void add(Object x){
        lock.lock();
        try {
            while (count >= boundQueue.length) {
                addCondition.await();
            }
            System.out.println(Thread.currentThread().getName()+":"+x+"入队列，当前有："+ ++count+"个元素");
            boundQueue[addIndex] = x;
            if(++addIndex==boundQueue.length){
                addIndex = 0;
            }
            pollCondition.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void poll(){
        lock.lock();
        try {
            while (count <= 0) {
                pollCondition.await();
            }
            System.out.println(Thread.currentThread().getName()+":"+boundQueue[pollIndex]+"出队列，当前有："+ --count+"个元素");
            boundQueue[pollIndex] = null;
            if(++pollIndex==boundQueue.length){
                pollIndex = 0;
            }
            addCondition.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BoundQueue boundQueue = new BoundQueue();
        Random random = new Random();
        new Thread(()->{
            while (true){
                try {
                    Thread.sleep(1000);
                    boundQueue.poll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(1000);
                    boundQueue.add(random.nextInt(100000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(()->{
            while (true){
                try {
                    Thread.sleep(1000);
                    boundQueue.poll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(1000);
                    boundQueue.add(random.nextInt(100000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(()->{
            while (true){
                try {
                    Thread.sleep(1000);
                    boundQueue.poll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(()->{
            while (true) {
                try {
                    Thread.sleep(1000);
                    boundQueue.add(random.nextInt(100000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
