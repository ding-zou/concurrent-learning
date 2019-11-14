package top.dzou.concurrent.wait_notify.producer_consumer;

/**
 * @author dingxiang
 * @date 19-8-5 下午6:47
 */

/**
 * 面包生产者
 */
public class BreadProducer implements Runnable{
    private BreadShop shop;
    public BreadProducer(BreadShop shop){
        this.shop = shop;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(2000);
                shop.getFromProducer();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
