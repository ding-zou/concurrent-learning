package top.dzou.concurrent.join;

/**
 * @author dingxiang
 * @date 19-8-8 下午1:22
 */
public class MainThread implements Runnable{
    @Override
    public void run() {
        System.out.println("主线程执行");
        try {
            Thread.sleep(2000);
            Thread joinThread = new Thread(new JoinThread());
            joinThread.start();
            joinThread.join();
            System.out.println("等待Join Thread执行完毕再执行主线程");
            System.out.println("正在执行主线程剩余部分");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("主线程执行完毕");
    }
}
