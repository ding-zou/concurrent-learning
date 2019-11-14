package top.dzou.concurrent.threadLocal;

/**
 * @author dingxiang
 * @date 19-8-8 下午1:31
 */
public class TestThreadLocal implements Behavior{
    private ThreadLocal<Integer> num = new ThreadLocal<>();

    @Override
    public void setNum(Integer i) {
        num.set(i);
    }

    @Override
    public Integer getNum() {
        return num.get();
    }

    public static void main(String[] args) {
        TestThreadLocal testThreadLocal = new TestThreadLocal();
        new Thread(new Task(testThreadLocal)).start();
        new Thread(new Task(testThreadLocal)).start();
        new Thread(new Task(testThreadLocal)).start();
        new Thread(new Task(testThreadLocal)).start();
    }
}
