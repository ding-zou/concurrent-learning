package top.dzou.concurrent.blocking_queue.synchronous_queue.producer_consumer;

/**
 * @author dingxiang
 * @date 19-8-5 下午6:51
 */

import java.util.concurrent.SynchronousQueue;

/**
 * 面包商店
 */
public class BreadShop {
    private SynchronousQueue<Bread> queue;

    public BreadShop(){
        queue = new SynchronousQueue<>();
    }

    public void purchaseByConsumer() throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+":买到到一个面包"+queue.take());
        Thread.sleep(1000);
    }

    public void getFromProducer() throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+":生产者正在生产面包");
        queue.put(new Bread());
        System.out.println(Thread.currentThread().getName()+":生产者生产完成一个面包");
        Thread.sleep(1000);
    }
}
