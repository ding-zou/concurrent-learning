package top.dzou.concurrent.fork_join.basis;

/**
 * @author dingxiang
 * @date 19-8-12 下午5:57
 */

import java.math.BigInteger;

/**
 * for循环计算1*到50
 */
public class ForLoopTask {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        BigInteger res = BigInteger.valueOf(1);
        for(int i=1;i<=100000;i++){
            res = res.multiply(BigInteger.valueOf(i));
        }
        System.out.println(res);
        long end = System.currentTimeMillis();
        System.out.println("耗时："+(end-start));
    }
}
