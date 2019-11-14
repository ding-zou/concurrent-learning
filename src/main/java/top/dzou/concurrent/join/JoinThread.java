package top.dzou.concurrent.join;

/**
 * @author dingxiang
 * @date 19-8-8 下午1:21
 */
public class JoinThread implements Runnable{
    @Override
    public void run() {
        System.out.println("Join Thread执行");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Join Thread执行完毕");
    }
}
