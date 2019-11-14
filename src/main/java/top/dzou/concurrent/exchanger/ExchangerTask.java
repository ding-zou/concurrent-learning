package top.dzou.concurrent.exchanger;

/**
 * @author dingxiang
 * @date 19-8-10 下午1:15
 */

import java.util.concurrent.Exchanger;

/**
 * 用于数据效验，模拟两个线程分别计算结果，最后一起比对结果
 */
public class ExchangerTask {
    public static void main(String[] args) {
        Exchanger<Integer> exchanger = new Exchanger<>();
        new Thread(new TaskA(exchanger)).start();
        new Thread(new TaskB(exchanger)).start();
    }
}
