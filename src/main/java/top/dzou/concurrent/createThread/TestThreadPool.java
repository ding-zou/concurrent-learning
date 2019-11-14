package top.dzou.concurrent.createThread;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadPool {
    public static void main(String[] args) {

        //线程池 FixedThreadPool 固定容量线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for(int i=0;i<50;i++) {
            threadPool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "is Running...");
            });
        }

        threadPool.shutdown();
    }

    @Test
    public void test(){
        //CachedThreadPool 根据内存创建 不够就创建
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for(int i=0;i<50;i++) {
            threadPool.execute(() -> {
                System.out.println(Thread.currentThread().getName() + "is Running...");
            });
        }
        threadPool.shutdown();
    }
}
