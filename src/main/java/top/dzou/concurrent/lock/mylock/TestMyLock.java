package top.dzou.concurrent.lock.mylock;

/**
 * @author dingxiang
 * @date 19-7-21 下午12:07
 */
public class TestMyLock {
    MyLock myLock = new MyLock();

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
        TestMyLock myLock = new TestMyLock();
        for(int i=0;i<50;i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + ":" + myLock.getValue());
            }).start();
        }
    }
}
