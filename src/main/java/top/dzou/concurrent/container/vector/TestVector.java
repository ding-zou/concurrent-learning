package top.dzou.concurrent.container.vector;

import java.util.Vector;

/**
 * @author dingxiang
 * @date 19-8-14 下午3:52
 */
public class TestVector {

    public static void main(String[] args) {
        Vector<Integer> vector = new Vector<>();
        for(int i=0;i<10;i++){
            new Thread(()->{
                vector.add((int) (Math.random() * 100));
            }).start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(vector);
    }
}
