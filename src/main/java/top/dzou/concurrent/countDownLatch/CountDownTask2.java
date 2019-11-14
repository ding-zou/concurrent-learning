package top.dzou.concurrent.countDownLatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author dingxiang
 * @date 19-8-8 下午9:32
 */

/**
 * 只有全班20个人写完作业才可以放学
 */
public class CountDownTask2 {
    private static final int STUDENTS_NUMBER = 20;
    private static CountDownLatch finishHW = new CountDownLatch(STUDENTS_NUMBER);
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<STUDENTS_NUMBER;i++){
            executorService.execute(()->{
                System.out.println(Thread.currentThread().getName()+":完成了作业");
                sleep(500);
                finishHW.countDown();
            });
        }
        finishHW.await();
        sleep(1000);
        System.out.println("全部人完成了任务，可以放学了");
    }
    private static void sleep(int milliSecond){
        try {
            TimeUnit.MILLISECONDS.sleep(milliSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
