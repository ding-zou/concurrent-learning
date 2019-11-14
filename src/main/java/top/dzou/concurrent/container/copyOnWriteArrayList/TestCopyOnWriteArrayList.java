package top.dzou.concurrent.container.copyOnWriteArrayList;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author dingxiang
 * @date 19-8-14 下午4:31
 */
public class TestCopyOnWriteArrayList {
    private CopyOnWriteArrayList<Integer> list;
    public TestCopyOnWriteArrayList(CopyOnWriteArrayList<Integer> list){
        this.list = list;
    }
    public void add(Integer i){
        list.add(i);
    }
    public String get(){
        return list.toString();
    }
    public static void main(String[] args) {
        CopyOnWriteArrayList<Integer> list = new CopyOnWriteArrayList<>();
        TestCopyOnWriteArrayList test = new TestCopyOnWriteArrayList(list);
        new Thread(()->{
            test.add((int) (Math.random()*1000));
        }).start();
        for(int i=0;i<10;i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+":get "+ test.get());
            }).start();
        }

    }
}
