package top.dzou.concurrent.readwritelock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author dingxiang
 * @date 19-8-4 下午1:35
 */
public class TestReadWriteLock {
    private Map<String,Object> map = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    public Object get(String key){
        readLock.lock();
        try {
            return map.get(key);
        }finally {
            readLock.unlock();
        }
    }

    public void put(String key,Object o){
        writeLock.lock();
        try {
            map.put(key, o);
        }finally {
            writeLock.unlock();
        }
    }
    public static void main(String[] args) {
        TestReadWriteLock test = new TestReadWriteLock();


        for(int i=0;i<100;i++){
            if(i<10){
                new Thread(()->{
                    test.put("key",new Random().nextInt(1000));
                }).start();
            }
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+":"+test.get("key"));
            }).start();
        }
    }
}
