package top.dzou.concurrent.blocking_queue.array_blocking_queue.producer_consumer;

/**
 * @author dingxiang
 * @date 19-8-5 下午6:50
 */

/**
 * 面包消费者
 */
public class BreadConsumer implements Runnable{
    private BreadShop shop;

    public BreadConsumer(BreadShop shop){
        this.shop = shop;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(2000);
                shop.purchaseByConsumer();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
