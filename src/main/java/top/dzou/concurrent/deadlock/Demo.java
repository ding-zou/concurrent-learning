package top.dzou.concurrent.deadlock;

/**
 * 死锁
 * @author dingxiang
 * @date 19-7-19 下午2:19
 */
public class Demo {

    private final Object o1 = new Object();
    private final Object o2 = new Object();
    public void a(){
        synchronized (o1){
            synchronized (o2){
                System.out.println(Thread.currentThread().getName()+":"+"a");
            }
        }
    }
    public void b(){
        synchronized (o2){
            synchronized (o1){
                System.out.println(Thread.currentThread().getName()+":"+"b");
            }
        }
    }

    public static void main(String[] args) {
        Demo d = new Demo();
        for(int i=0;i<10;i++){
            new Thread(()->{
                d.a();
                d.b();
            }).start();
        }
    }
}
