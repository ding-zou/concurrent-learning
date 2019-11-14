package top.dzou.concurrent.threadLocal;

import java.util.concurrent.TimeUnit;

/**
 * @author dingxiang
 * @date 19-8-8 下午7:45
 */
public class Task implements Runnable{
    private Behavior behavior;
    public Task(Behavior behavior){
        this.behavior = behavior;
    }
    @Override
    public void run() {
        while (true) {
            behavior.setNum((int) (Math.random() * 1000));
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName() + ":" + behavior.getNum());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
