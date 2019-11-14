package top.dzou.concurrent.wait_notify.producer_consumer;

/**
 * @author dingxiang
 * @date 19-8-5 下午6:50
 */
public class Main {
    public static void main(String[] args) {
        BreadShop shop = new BreadShop();
        System.out.println("目前有"+shop.getI()+"片面包。");
        BreadProducer producer = new BreadProducer(shop);
        BreadConsumer consumer = new BreadConsumer(shop);
        new Thread(consumer).start();
        new Thread(consumer).start();
        new Thread(consumer).start();
        new Thread(consumer).start();
        new Thread(producer).start();
        new Thread(consumer).start();
        new Thread(consumer).start();
        new Thread(consumer).start();
        new Thread(consumer).start();
    }
}
