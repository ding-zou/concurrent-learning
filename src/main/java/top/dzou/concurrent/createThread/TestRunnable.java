package top.dzou.concurrent.createThread;

public class TestRunnable implements Runnable{
    @Override
    public void run() {
        while (true){
            System.out.println(1);
        }
    }

    public static void main(String[] args) {
        Thread thread1 = new Thread(new TestRunnable());
        Thread thread2 = new Thread(new TestRunnable());
        thread1.start();
        thread2.start();
    }
}
