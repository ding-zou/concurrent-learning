package top.dzou.concurrent.spinlock;

/**
 * spinlock 自旋锁
 * @author dingxiang
 * @date 19-7-19 下午2:04
 */
public class Demo {

    public static void main(String[] args) {
        for(int i=0;i<5;i++){
            new Thread(()->{
                    System.out.println(Thread.currentThread().getName()+"开始执行");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+"执行完毕");
            }).start();
        }
        while(Thread.activeCount()!=1){
            //自旋
        }
        System.out.println("线程执行完毕！");
        System.exit(0);
    }
}
