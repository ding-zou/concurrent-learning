package top.dzou.concurrent.blocking_queue.array_blocking_queue.producer_consumer;

/**
 * @author dingxiang
 * @date 19-8-5 下午6:51
 */

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 面包商店
 */
public class BreadShop {
    //最多30个面包
    private static final int MAX_SIZE_BREAD = 30;
    private ArrayBlockingQueue<Bread> queue;

    public BreadShop(){
        queue = new ArrayBlockingQueue<Bread>(MAX_SIZE_BREAD);
    }
    //目前有30个面包
    private int i = 0;
    public void purchaseByConsumer() throws InterruptedException {
        Bread bread = queue.take();
        System.out.println(Thread.currentThread().getName()+":买到到一个面包");
        Thread.sleep(1000);
    }

    public void getFromProducer() throws InterruptedException {
        queue.put(new Bread());
        System.out.println(Thread.currentThread().getName()+":生产者生产一个面包");
        Thread.sleep(1000);
    }

    public int getSize(){
        return queue.size();
    }
}
