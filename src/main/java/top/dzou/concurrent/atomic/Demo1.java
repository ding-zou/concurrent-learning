package top.dzou.concurrent.atomic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test AtomicInteger
 * @author dingxiang
 * @date 19-7-19 下午6:01
 */
public class Demo1 {
    private AtomicInteger value = new AtomicInteger(0);

    public int getValue(){
        return value.getAndIncrement();
    }

    public static void main(String[] args) {
        Demo1 d = new Demo1();
        new Thread(()->{
            while (true){
                System.out.println(Thread.currentThread().getName()+":"+d.getValue());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(()->{
            while (true){
                System.out.println(Thread.currentThread().getName()+":"+d.getValue());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
