package top.dzou.concurrent.wait_notify.toilet;

import java.util.Random;

/**
 * @author dingxiang
 * @date 19-8-5 下午6:06
 */

/**
 * 模拟10个厕所坑位，20个人上，出来一个可以进去一个
 */
public class Toilet {
    private volatile int toilet = 10;

    public void getOneToilet() throws InterruptedException {
        synchronized (this) {
            if (toilet > 0) {
                toilet = toilet - 1;
                System.out.println(Thread.currentThread().getName() + ":get one toilet.");
            } else {
                wait();
                getOneToilet();
            }
        }
    }

    public void releaseToilet(){
        synchronized (this) {
            toilet = toilet + 1;
            System.out.println(Thread.currentThread().getName()+":release one toilet");
            notify();
        }
    }

    public static void main(String[] args) {
        Toilet toilet = new Toilet();
        for(int i=0;i<20;i++){
            new Thread(()->{
                try {
                    toilet.getOneToilet();
                    Thread.sleep(new Random().nextInt(7000));
                    toilet.releaseToilet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
