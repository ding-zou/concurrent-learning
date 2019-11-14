package top.dzou.concurrent.volatile_test;

import java.util.Random;

/**
 * @author dingxiang
 * @date 19-7-19 下午2:28
 */
public class Demo {
    private volatile int value;

    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }

    public static void main(String[] args) {
        Demo d = new Demo();
        for(int i=0;i<10;i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+":"+d.getValue());
            }).start();
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+" before:"+d.getValue());
                d.setValue(new Random().nextInt(100));
                System.out.println(Thread.currentThread().getName()+" after:"+d.getValue());
            }).start();

        }


        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("最终value:"+d.getValue());
    }
}
