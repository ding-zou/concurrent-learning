package top.dzou.concurrent.synchronize;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * synchronized 重入锁
 * @author dingxiang
 * @date 19-7-19 下午1:52
 */
public class Demo3 {
    public synchronized void a(){
        System.out.println(Thread.currentThread().getName()+":"+"a");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        b();
    }

    public synchronized void b(){
        System.out.println(Thread.currentThread().getName()+":"+"b");
    }

    public static void main(String[] args) {
        Demo3 d = new Demo3();
        ExecutorService threadPool = Executors.newFixedThreadPool(30);
        for(int i=0;i<30;i++){
            threadPool.execute(d::a);
        }
    }
}
