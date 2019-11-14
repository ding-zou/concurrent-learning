package top.dzou.concurrent.future.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author dingxiang
 * @date 19-8-11 下午4:38
 */
public class TestFuture {

    public static class FutureTaskTest implements Callable<Long> {

        @Override
        public Long call() throws Exception {
            long start = System.currentTimeMillis();
            Thread.sleep(new Random().nextInt(3000));
            long end = System.currentTimeMillis();
            return end-start;
        }
    }
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        List<Future<Long>> result = new ArrayList<>();
        for (int i=0;i<10;i++) {
            result.add(es.submit(new FutureTaskTest()));
        }
        result.forEach((o)->{
            try {
                System.out.println(o.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        es.shutdown();
    }
}
