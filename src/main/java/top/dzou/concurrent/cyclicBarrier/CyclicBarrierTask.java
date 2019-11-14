package top.dzou.concurrent.cyclicBarrier;

import java.util.concurrent.CyclicBarrier;

/**
 * @author dingxiang
 * @date 19-8-9 下午5:25
 */

/**
 * 10个运动员都到起跑线准备好才可以开始比赛
 */
public class CyclicBarrierTask implements Runnable{
    private CyclicBarrier cyclicBarrier;
    private static final int COMPETITOR_NUMBER = 10;
    public CyclicBarrierTask(Thread barrierThread){
        cyclicBarrier = new CyclicBarrier(COMPETITOR_NUMBER, barrierThread);
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+":走入赛场");
        try {
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName()+":准备好开始比赛");
            cyclicBarrier.await();
            System.out.println(Thread.currentThread().getName()+":出发");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CyclicBarrierTask cyclicBarrierTask = new CyclicBarrierTask(new Thread(()->{
            System.out.println("裁判吹响口哨，开始比赛！！！");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        for(int i=0;i<COMPETITOR_NUMBER;i++){
            new Thread(cyclicBarrierTask).start();
        }
        Thread.sleep(10000);
        System.out.println("比赛结束，运动员回家，裁判回家。");
    }
}
