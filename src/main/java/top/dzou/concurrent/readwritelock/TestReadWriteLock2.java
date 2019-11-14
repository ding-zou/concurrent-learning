package top.dzou.concurrent.readwritelock;

/**
 * @author dingxiang
 * @date 19-8-4 下午2:55
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁降级
 */
public class TestReadWriteLock2 {
    private Map<String,Object> map = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    private volatile boolean isUpdate;

    /**
     * 锁降级
     */
    public void readWrite(){
        //因为要读取isUpdate，所以获取读锁
        readLock.lock();
        if(!isUpdate) {
            readLock.unlock();
            //获取写锁写操作
            writeLock.lock();
            try {
                //重新检查isUpdate的值，可能之前有其他线程获取写锁更改了其的值
                if (!isUpdate) {
                    map.put("key", "value");
                }
                //获取读锁
                readLock.lock();
            }finally {
                //释放写锁
                writeLock.unlock();
            }
        }
        //使用读锁读取数据
        try{
            System.out.println(Thread.currentThread().getName()+":"+map.get("key"));
        }finally {
            readLock.unlock();
        }
    }

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
        TestReadWriteLock2 test = new TestReadWriteLock2();
            new Thread(test::readWrite).start();
        }
    }
