package top.dzou.concurrent.longadder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author dingxiang
 * @date 19-8-20 上午11:37
 */

/**
 * LongAdder
 */
public class TestLongAdder {
    private LongAdder value;
    public TestLongAdder(){
        value = new LongAdder();
    }

    private void writeIncrement(){
        value.increment();
    }

    private void writeAdd(long x){
        value.add(x);
    }

    private long readValue(){
        return value.longValue();
    }

    public static void main(String[] args) {
        TestLongAdder test = new TestLongAdder();
        ExecutorService service = Executors.newFixedThreadPool(20);
        new Thread(() -> test.writeAdd((long) (Math.random()*100000))).start();
        for(int i=0;i<20;i++) {
            service.execute(() -> {
                System.out.println(Thread.currentThread().getName()+":"+test.readValue());
            });
        }
    }
}
