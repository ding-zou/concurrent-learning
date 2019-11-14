package top.dzou.concurrent.container.hashTable;

import java.util.Hashtable;

/**
 * @author dingxiang
 * @date 19-8-14 下午5:15
 */
public class TestHashTable {
    private Hashtable<String,String> hashtable;
    public TestHashTable(Hashtable<String,String> hashtable){
        this.hashtable = hashtable;
    }
    public void set(String key,String val){
        hashtable.put(key,val);
    }
    public String get(String key){
        return hashtable.get(key);
    }

    public static void main(String[] args) {
        Hashtable<String,String> hashtable = new Hashtable<>();
        TestHashTable test = new TestHashTable(hashtable);
        new Thread(()->{
            test.set("key","value is hello");
        }).start();
        for(int i=0;i<10;i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+":"+test.get("key"));
            }).start();
        }
    }
}
