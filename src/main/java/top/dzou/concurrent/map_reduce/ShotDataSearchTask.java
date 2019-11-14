package top.dzou.concurrent.map_reduce;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author dingxiang
 * @date 19-8-12 下午8:55
 */

/**
 * MapReduce JDK9之并行流实现12万数据查询
 */
public class ShotDataSearchTask {

    public static void main(String[] args) {
        List<ShotDataSet> list = ShotDataSetLoader.loadCsv("/home/dzou/Documents/shot_logs.csv");
        long start = System.currentTimeMillis();
        Predicate<ShotDataSet> filter = shotDataSet -> shotDataSet.getPlayId().equals("977");
        List<ShotDataSet> resList = list
                .parallelStream()
                .skip(1)
                .filter(filter)
                .collect(Collectors.toList());
        System.out.println(resList);
        long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-start));
    }
}
