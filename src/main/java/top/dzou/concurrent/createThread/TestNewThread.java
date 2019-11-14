package top.dzou.concurrent.createThread;

public class TestNewThread extends  Thread{

    private static int s=0;

    public TestNewThread(String name){
        super(name);
    }
    @Override
    public void run() {
        for(int j=0;j<1000;j++){
            append(j,this.getName());
        }
    }

    int append(int i,String name){
        i+=1;
        System.out.println(name+":"+i);
        return i;
    }

    public static void main(String[] args) throws InterruptedException {
        TestNewThread thread = new TestNewThread("t1");
        TestNewThread thread2 = new TestNewThread("t2");
        thread.start();
        thread2.start();
        thread.join();
        thread2.join();
        System.out.println(s);
    }
}
