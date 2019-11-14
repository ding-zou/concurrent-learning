package top.dzou.concurrent.atomic.cas;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author dingxiang
 * @date 19-7-19 下午7:23
 */
public class TestCAS {
    /**
     * 错误代码，多线程下不安全
     */
    class MyLock{
        private boolean flag = false;
        public boolean lock(){
            if(!flag){
                return true;
            }
            return false;
        }
    }

    /**
     * 使用同步方法实现CAS
     */
    class MyLock2{
        private boolean flag = false;
        public synchronized boolean lock(){
            if(!flag){
                return true;
            }
            return false;
        }
    }

    /**
     * 使用原子类实现CAS
     */
    class MyLock3{
        private AtomicBoolean flag = new AtomicBoolean(false);
        public boolean lock(){
            return flag.compareAndSet(false,true);
        }
    }
}
