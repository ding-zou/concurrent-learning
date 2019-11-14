package top.dzou.concurrent.future.futureTask;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author dingxiang
 * @date 19-8-11 下午3:39
 */
public class TestFutureTask {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<Porridge> call = () -> {
            System.out.println("正在煮粥");
            Thread.sleep(5000);
            return new Porridge();
        };
        FutureTask<Porridge> task = new FutureTask<>(call);
        new Thread(task).start();
        Thread.sleep(500);
        System.out.println("我去睡个觉");
        Porridge i = task.get();
        System.out.println("我拿到了煮粥结果："+i);
    }

    static class Porridge{
        public Porridge(){}
    }
}
