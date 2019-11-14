package top.dzou.concurrent.lock.mylock;

/**
 * @author dingxiang
 * @date 19-7-21 下午12:24
 */
public class TestMyOverWriteLock {
    MyOverwriteLock myLock = new MyOverwriteLock();

    private int value1;
    private int value2;

    public void a(){
        myLock.lock();
        System.out.println(Thread.currentThread().getName()+" value1:"+ ++value1);
        b();
        myLock.unlock();
    }
    public void b(){
        myLock.lock();
        System.out.println(Thread.currentThread().getName()+" value2:"+ ++value2);
        myLock.unlock();
    }

    public static void main(String[] args) {
        TestMyOverWriteLock testMyLock = new TestMyOverWriteLock();
        for(int i=0;i<50;i++) {
            new Thread(testMyLock::a).start();
        }
    }
}
