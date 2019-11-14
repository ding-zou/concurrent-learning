package top.dzou.concurrent.createThread;

public class TestInnerClass {
    public static void main(String[] args) {
        new Thread(){
            @Override
            public void run() {
                while (true){
                    System.out.println(1);
                }
            }
        }.start();
        new Thread(() -> {
            while (true){
                System.out.println(1);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    System.out.println(1);
                }
            }
        }).start();
        new Thread(() -> {
            while (true){
                System.out.println(1);
            }
        }).start();
    }
}
