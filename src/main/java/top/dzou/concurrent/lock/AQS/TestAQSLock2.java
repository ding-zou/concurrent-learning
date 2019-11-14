package top.dzou.concurrent.lock.AQS;

/**
 * AbstractQueuedSynchronizer测试自定义重入锁
 * @author dingxiang
 * @date 19-7-21 下午3:37
 */
public class TestAQSLock2 {
    MyAQSLock myLock = new MyAQSLock();

    private int value;
    private int value2;

    public int a(){
        myLock.lock();
        try {
            b();
            return value++;
        }finally {
            myLock.unlock();
        }
    }
    public void b(){
        myLock.lock();
        try {
            System.out.println(++value2);
        }finally {
            myLock.unlock();
        }
    }

    public static void main(String[] args) {
        TestAQSLock2 myLock = new TestAQSLock2();
        for(int i=0;i<50;i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + ":" + myLock.a());
            }).start();
        }
    }
}
