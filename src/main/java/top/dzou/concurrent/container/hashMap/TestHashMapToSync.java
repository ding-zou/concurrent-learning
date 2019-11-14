package top.dzou.concurrent.container.hashMap;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingxiang
 * @date 19-8-14 下午5:01
 */
public class TestHashMapToSync {
    public static Map<String, Integer> getSyncHashMap(Map<String,Integer> map){
        return Collections.synchronizedMap(map);
    }
    public static void main(String[] args) {
        Map<String,Integer> map = new HashMap<>();
        Map<String,Integer> newSyncMap = TestHashMapToSync.getSyncHashMap(map);
    }
}
