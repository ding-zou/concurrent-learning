package top.dzou.concurrent.lock;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试Lock中tryLock方法
 * @author dingxiang
 * @date 19-7-21 下午12:02
 */
public class TestTryLock {
        private ArrayList<Integer> arrayList = new ArrayList<Integer>();
        private Lock lock = new ReentrantLock();    //注意这个地方
        public static void main(String[] args)  {
            final TestTryLock test = new TestTryLock();

            for(int i=0;i<20;i++){
                new Thread(()->{
                    test.insert(Thread.currentThread());
                }).start();
            }
        }

        public void insert(Thread thread) {
            if(lock.tryLock()) {
                try {
                    System.out.println(thread.getName()+"得到了锁");
                    for(int i=0;i<5;i++) {
                        arrayList.add(i);
                    }
                }finally {
                    System.out.println(thread.getName()+"释放了锁");
                    lock.unlock();
                }
            } else {
                System.out.println(thread.getName()+"获取锁失败");
            }
        }
    }
