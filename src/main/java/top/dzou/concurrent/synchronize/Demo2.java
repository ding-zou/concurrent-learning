package top.dzou.concurrent.synchronize;

/**
 * 资源竞争
 * @author dingxiang
 * @date 19-7-19 下午12:34
 */
public class Demo2 {

    private int value;

    public int getValue() {
        synchronized (Integer.valueOf(value )) {
            return value++;
        }
    }

    public static void main(String[] args) {
        Demo2 d = new Demo2();
        new Thread(()->{
            while (true){
                System.out.println(Thread.currentThread().getName()+":"+d.getValue());
            }
        }).start();
    }
}
