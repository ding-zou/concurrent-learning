package top.dzou.concurrent.fork_join.basis;

/**
 * @author dingxiang
 * @date 19-8-12 下午4:28
 */

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * fork join分治法1*100000
 */
public class MyForkJoinTask extends RecursiveTask<BigInteger> {
    private long start;
    private long end;
//    private long res;
    public MyForkJoinTask(long start,long end){
        this.start = start;
        this.end = end;
//        res = 1;
    }
    @Override
    protected BigInteger compute() {
        BigInteger res = new BigInteger("1");
        if(end-start<=2&&end-start>0){
            //计算
            for(long i=start;i<=end;i++){
                res = res.multiply(BigInteger.valueOf(i));
            }
        }else {
            //分治
            long half = (end-start)/2;
            MyForkJoinTask leftTask = new MyForkJoinTask(start,start+half);
            MyForkJoinTask rightTask = new MyForkJoinTask(start+half+1,end);
            leftTask.fork();
            rightTask.fork();

            BigInteger a = leftTask.join();
            BigInteger b = rightTask.join();
            res = a.multiply(b);
        }
        return res;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        MyForkJoinTask task = new MyForkJoinTask(1,100000);
        Future<BigInteger> future = forkJoinPool.submit(task);
        try {
            System.out.println(future.get());
            long end = System.currentTimeMillis();
            System.out.println("耗时："+(end-start));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
