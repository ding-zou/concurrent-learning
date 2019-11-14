package top.dzou.concurrent.synchronize;

/**
 * 线程安全性问题
 * @author dingxiang
 * @date 19-7-19 上午11:58
 */
public class Demo1 {
    private int value;

    //添加synchronized关键字防止资源竞争
    public synchronized int getValue(){
        return value++;
    }

    public static void main(String[] args) {
        Demo1 d = new Demo1();
        new Thread(()->{
            while (true){
                System.out.println(Thread.currentThread().getName()+":"+d.getValue());
            }
        }).start();
    }
}
