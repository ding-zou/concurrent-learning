package top.dzou.concurrent.countDownLatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author dingxiang
 * @date 19-8-8 下午9:15
 */

/**
 * 上完四个学期才能毕业
 */
public class CountDownTask {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch first = new CountDownLatch(1);
        CountDownLatch second = new CountDownLatch(1);
        CountDownLatch third = new CountDownLatch(1);
        CountDownLatch fourth = new CountDownLatch(1);
        new Thread(()->{
            System.out.println("我正在上大学一年级");
            sleep(1);
            first.countDown();
        }).start();
        new Thread(()->{
            try {
                first.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("我正在上大学二年级");
            sleep(1);
            second.countDown();
        }).start();
        new Thread(()->{
            try {
                second.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("我正在上大学三年级");
            sleep(1);
            third.countDown();
        }).start();
        new Thread(()->{
            try {
                third.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("我正在上大学四年级");
            sleep(1);
            fourth.countDown();
        }).start();
        fourth.await();
        sleep(2);
        System.out.println("我可以毕业了，耶耶耶！");
    }

    private static void sleep(int second){
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
