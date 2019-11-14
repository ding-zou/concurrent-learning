package top.dzou.concurrent.fork_join.recursiveTask.big_data_search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

/**
 * @author dingxiang
 * @date 19-8-12 下午6:05
 */

/**
 * ForkJoin框架分而治之实现12万数据查询
 */
public class ShotDataSearchTask extends RecursiveTask<List<ShotDataSet>> {
    private List<ShotDataSet> list;
    private static List<ShotDataSet> resList = new ArrayList<>();
    public ShotDataSearchTask(List<ShotDataSet>  list){
        this.list = list;
    }
    @Override
    protected List<ShotDataSet> compute() {
        if(list.size()<50){
            for(int i=0;i<50;i++){
                ShotDataSet dataSet = list.get(i);
                if(dataSet.getPlayId().trim().equals("977")){
                    resList.add(dataSet);
                }
            }
        }else {
            long half = list.size()/2;
            ShotDataSearchTask leftTask = new ShotDataSearchTask(list.stream().limit(half).collect(Collectors.toList()));
            ShotDataSearchTask rightTask = new ShotDataSearchTask(list.stream().skip(half).collect(Collectors.toList()));
            leftTask.fork();
            rightTask.fork();
            leftTask.quietlyJoin();
            rightTask.quietlyJoin();
        }
        return resList;
    }

    public static void main(String[] args) {
        List<ShotDataSet> list = ShotDataSetLoader.loadCsv("/home/dzou/Documents/shot_logs.csv");
        long start = System.currentTimeMillis();
        ShotDataSearchTask task = new ShotDataSearchTask(list);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        Future<List<ShotDataSet>> future = pool.submit(task);
        try {
            System.out.println(future.get());
            long end = System.currentTimeMillis();
            System.out.println("耗时："+(end-start));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
