package top.dzou.concurrent.container.arrayList;

/**
 * @author dingxiang
 * @date 19-8-14 下午4:11
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 把ArrayList转换成同步的
 */
public class TestArrayListToSync<V> {

    public List<V> getSyncArrayList(List<V> list){
        return Collections.synchronizedList(list);
    }
    public static void main(String[] args) {
        TestArrayListToSync<Integer> test = new TestArrayListToSync<>();
        List<Integer> list = new ArrayList<>();
        List<Integer> syncList = test.getSyncArrayList(list);
    }
}
