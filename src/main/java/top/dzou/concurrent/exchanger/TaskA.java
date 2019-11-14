package top.dzou.concurrent.exchanger;

import java.util.concurrent.Exchanger;

/**
 * @author dingxiang
 * @date 19-8-10 下午1:15
 */
public class TaskA implements Runnable{
    private Exchanger<Integer> exchanger;
    public TaskA(Exchanger<Integer> exchanger){
        this.exchanger = exchanger;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+":正在进行计算");
        try {
            Thread.sleep(2000);
            Integer x = 1026;
            System.out.println(Thread.currentThread().getName()+":计算结束，等待效验");
            exchanger.exchange(x);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
