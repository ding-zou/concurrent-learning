package top.dzou.concurrent.wait_notify.producer_consumer;

/**
 * @author dingxiang
 * @date 19-8-5 下午6:51
 */

/**
 * 面包商店
 */
public class BreadShop {
    //最多30个面包
    private final int MAX_SIZE_BREAD = 30;
    //目前有30个面包
    private int i = 0;
    public synchronized void purchaseByConsumer() throws InterruptedException {
        while (i<=0){
            System.out.println(Thread.currentThread().getName()+":等待生产者生产。");
            wait();
        }
        i = i - 1;
        System.out.println(Thread.currentThread().getName()+" consumer:purchase a piece of bread.");
        //通知所有等待的生产者
        notifyAll();
        Thread.sleep(1000);
    }

    public synchronized void getFromProducer() throws InterruptedException {
        while (i>=MAX_SIZE_BREAD){
            System.out.println(Thread.currentThread().getName()+":等待消费者消费。");
            wait();
        }
        i = i +10;
        System.out.println(Thread.currentThread().getName()+" producer:get ten piece of bread from producer.");
        //通知所有等待的消费者
        notifyAll();
        Thread.sleep(1000);
    }

    public int getI() {
        return i;
    }
}
