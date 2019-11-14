package top.dzou.concurrent.semaphore;

/**
 * @author dingxiang
 * @date 19-8-10 上午11:00
 */

import java.util.concurrent.Semaphore;

/**
 * 模拟上厕所的资源通过Semaphore控制
 */
public class Toilet implements Runnable{
    private Semaphore semaphore;
    private static final int TOILET_SIZE = 5;
    public Toilet(Semaphore semaphore){
        this.semaphore = semaphore;
    }
    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName()+":我来上厕所了");
            Thread.sleep(1000);
            if (semaphore.availablePermits()<=0){
                System.out.println(Thread.currentThread().getName()+":怎么没位置了");
            }
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName()+":我抢到厕所了。");
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName()+":我上完了，你们可以进去一个了。");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(TOILET_SIZE);
        for(int i=0;i<15;i++){
            new Thread(new Toilet(semaphore)).start();
        }
    }
}
