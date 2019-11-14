package top.dzou.concurrent.exchanger;

import java.util.concurrent.Exchanger;

/**
 * @author dingxiang
 * @date 19-8-10 下午1:16
 */
public class TaskB implements Runnable{
    private Exchanger<Integer> exchanger;
    public TaskB(Exchanger<Integer> exchanger){
        this.exchanger = exchanger;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+":正在进行计算");
        try {
            Thread.sleep(2000);
            Integer res = 1026;
            System.out.println(Thread.currentThread().getName()+":计算结束，等待效验");
            Integer x = exchanger.exchange(res);
            Thread.sleep(1000);
            System.out.println("效验结果是："+x.equals(res));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
