package top.dzou.concurrent.blocking_queue.synchronous_queue.producer_consumer;

/**
 * @author dingxiang
 * @date 19-8-5 下午6:50
 */
public class Main {
    public static void main(String[] args) {
        BreadShop shop = new BreadShop();
        BreadProducer producer = new BreadProducer(shop);
        BreadConsumer consumer = new BreadConsumer(shop);
        new Thread(producer).start();
        new Thread(consumer).start();
    }
}
