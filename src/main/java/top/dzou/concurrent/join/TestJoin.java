package top.dzou.concurrent.join;

/**
 * @author dingxiang
 * @date 19-8-8 下午1:13
 */
public class TestJoin {
    public static void main(String[] args) {
        Thread mainThread = new Thread(new MainThread());
        mainThread.start();
    }
}
