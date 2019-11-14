package top.dzou.concurrent.lock.AQS;

/**
 * 测试AbstractQueuedSynchronizer实现的非重入锁
 * @author dingxiang
 * @date 19-7-21 下午3:20
 */
public class TestAQSLock {
    MyAQSLock myLock = new MyAQSLock();

    private int value;

    public int getValue(){
        myLock.lock();
        try {
            return value++;
        }finally {
            myLock.unlock();
        }
    }

    public static void main(String[] args) {
        TestAQSLock myLock = new TestAQSLock();
        for(int i=0;i<50;i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + ":" + myLock.getValue());
            }).start();
        }
    }
}
