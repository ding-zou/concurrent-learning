package top.dzou.concurrent.lock;

import java.util.concurrent.locks.ReentrantLock;

public class TestReetrantLock extends Thread{
    //所有线程公用一把锁
    private static ReentrantLock lock = new ReentrantLock();
    private static int i = 0;

    public TestReetrantLock(String name){
        super.setName(name);
    }

    @Override
    public void run() {
        for(int j=0;j<200000;j++){
            lock.lock();
            try{
                System.out.println(i);
                i+=1;
            }finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TestReetrantLock thread1 = new TestReetrantLock("1");
        TestReetrantLock thread2 = new TestReetrantLock("2");
        thread1.start();;
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(i);
    }
}
