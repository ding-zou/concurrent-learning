## 一.创建线程方式

1. Thread
2. Runnable 面向接口编程，函数式接口（其实就是一个任务，最终由Thread构造函数传入）
3. Callable 带返回值线程
4. 定时器 Timer
5. Lambda表达式
6. 线程池 ThreadExecutorPool
7. 内部类 
8. Spring 中 注解＠Aysnc



> CPU分给每个线程一个时间片，很短，所以看起来是一起执行，切换过程就是==上下文切换==，会消耗一定的资源



## 二.线程问题

### 1.活跃性问题

- 死锁 ：互相等待 两个线程互相持有对方需要的资源并都不释放

- 饥饿：资源不足，线程优先级使得优先级低的得不到资源执行，高优先级吞噬低优先级时间片或者线程堵塞在同一个同步代码块

  > 避免饥饿：设置合理优先级；使用锁代替synchronized

- 活锁：是指线程1可以使用资源，但它很礼貌，让其他线程先使用资源，线程2也可以使用资源，但它很绅士，也让其他线程先使用资源。这样你让我，我让你，最后两个线程都无法使用资源。

> 吞吐量，是指在一次性能测试过程中网络上传输的数据量的总和。

### 2.线程安全性

- 数据竞争：多线程下多个线程共享变量，对资源进行了非原子型操作

解决途径：

1. synchronized（偏向锁、轻量级锁、重量级锁）
2. volatile
3. JDK原子类
4. Lock（独占锁、共享锁）



## 三.Synchronized

> 锁是互斥的：你能执行我就不能执行

- synchronized放在方法上：该方法为同步方法（静态、普通） ==锁的对象时当前类实例==
- 放在方法内部修饰代码块：该代码块为同步代码块

JDK6以后对``Synchronized``进行了优化：

1. 偏向锁： 当一个线程访问同步块时，获取锁时会在对象头和栈帧中的所记录中存储锁偏向的线程ID，以后该线程进入退出同步块时不需要进行CAS操作来加锁解锁，只需对象头中里是否存储该线程ID进行判断，如果没有再使用CAS竞争锁。它会等到竞争出现才释放。
2. 轻量级锁： 当一个线程访问同步块时，JVM先在当前线程栈帧中创建存储锁记录的空间，并将对象头中的Mark Word复制到锁记录中，然后线程使用CAS将对象头中的值替换为指向锁记录的指针，成功则获取到锁，否则有线程竞争使用自选获取。解锁时用CAS将指针替换为对象头的值，成功则解锁，否则有线程竞争，将升级为重量级锁，释放并唤醒等待线程。
3. 重量级锁：如果轻量级锁存在其他线程竞争锁CAS失败后自旋后去锁失败则升级为重量级锁。



## 四.锁的类型

### 1.重入锁

> 重入锁指的是可重复可递归调用的锁，在外层使用锁之后，在内层仍然可以使用，并且不发生死锁

- synchronized
- ReentrantLock

例：当a、b方法都被synchronized修饰时，在a方法中调用b方法是可以在一个线程进入的

```java
public synchronized void a(){
        System.out.println(Thread.currentThread().getName()+":"+"a");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        b();
    }

    public synchronized void b(){
        System.out.println(Thread.currentThread().getName()+":"+"b");
    }

    public static void main(String[] args) {
        Demo3 d = new Demo3();
        ExecutorService threadPool = Executors.newFixedThreadPool(30);
        for(int i=0;i<30;i++){
            threadPool.execute(d::a);
        }
    }
```

### 2.自旋锁

> 当一个线程要获取锁时，该锁已经被其他线程获取了，那么它会一直等待该线程释放锁，不但判断所能否被成功获取，直到成功

```java
while(Thread.activeCount()!=){
            //自旋
        }
        System.out.println("线程执行完毕！");
```

没有执行任何有效的任务，会造成 busy-waiting

### 3.死锁

> 互相等待 两个线程互相持有对方需要的资源并都不释放

```java
private final Object o1 = new Object();
    private final Object o2 = new Object();
    public void a(){
        synchronized (o1){
            synchronized (o2){
                System.out.println(Thread.currentThread().getName()+":"+"a");
            }
        }
    }
    public void b(){
        synchronized (o2){
            synchronized (o1){
                System.out.println(Thread.currentThread().getName()+":"+"b");
            }
        }
    }

    public static void main(String[] args) {
        Demo d = new Demo();
        for(int i=0;i<10;i++){
            new Thread(()->{
                d.a();
                d.b();
            }).start();
        }
    }
```



## 五.volatile

> volatile是一种轻量级锁，被volatile修饰的变量在线程中是可见的，一个线程修改了变量的值，两一个变量可以读取

生成汇编代码时会在volatile修饰的共享变量进行写操作的时候会多出**Lock前缀的指令**

- 把当前处理器缓存的内容写回到内存
- 这个写回内存的操作会使得CPU中缓存该内存地址的数据无效（保证数据一致性）

#### volatile 与 synchronized比较

- volatile更加轻量，保证数据可见性，不保证原子性
- synchronized重量级锁，保证数据原子性操作，可替代volatile



#### 原子性

**对基本数据类型变量读取和赋值是原子性的，要么成功要么失败，不可中断**

```java
int i = 10;//原子性
Integer i = 10;//原子性 Integer底层自动拆箱成int类型操作
b = a;//非原子 1.read a 2.给b赋值
c++;//非原子 1.read c 2.c add 1 3.给c重新赋值
c = c+1;//非原子 与c++相同
```

#### 可见性

volatile保证

## 六.JDK 原子类

### 原子类

在多线程环境下，当有多个线程同时执行这些类的实例包含的方法时，具有排他性，即当某个线程进入方法，执行其中的指令时，不会被其他线程打断，而别的线程就像自旋锁一样，一直等到该方法执行完成，才由JVM从等待队列中选择一个另一个线程进入，这只是一种逻辑上的理解。实际上是借助硬件的相关指令来实现的，不会阻塞线程(或者说只是在硬件级别上阻塞了)。

- AtomicInteger、AtomicLong、AtomicBoolean
- AtomicIntegerArray，AtomicLongArray
- AtomicLongFieldUpdater，AtomicIntegerFieldUpdater，AtomicReferenceFieldUpdater
- AtomicMarkableReference，AtomicStampedReference，AtomicReferenceArray

> 我们举AtomicInteger为例，当它执行==getAndIncrement()==自增时，它调用源码如下

```java
do {   var5 = this.getIntVolatile(var1, var2);
    } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
        return var5;
```

自增是一个非原子操作，包括取值，自增，和赋值三个步骤。在进行写数据时，会在内存中保留一份原来的旧值，写的时候判断当前要写的值和旧值是否相等，不相等就继续等待，直到相等才执行写操作。

> 当调用==getAndAddInt==时，调用源码如下

```java
do {
            var5 = this.getIntVolatile(var1, var2);
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
        return var5;
```

拿到内存最新值，使用CAS尝试将内存位置的值修改为目标值var5 + var4，如果修改失败，则获取该内存位置的新值v，然后继续尝试，直至修改成功。

==compareAndSet==方法调用了==compareAndSwap==

```java
public final boolean compareAndSet(int expect, int update) {
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
    }
```

核心：原子类的方法进行操作都是使用==unsafe==调用了==compareAndSwap== CAS 这个本地方法实现

### CAS

**CompareAndSwap** 比较并交换 根本是一个本地方法，有三个参数：内存地址、旧的值、即将更新的值

如果旧的值与将要更新的值相等，则把内存地址的值改为更新的值

利用CPU执行指令实现CAS

- 总线加锁
- 缓存加锁

CAS存在问题：

1. 循环时间长开销很大。
2. 只能保证一个共享变量的原子操作。
3. ABA问题（AtomicStampedReference可以通过版本控制解决==但是==使用互斥同步更高效）



### JDK1.8 LongAdder

在JDK8中新提出的一个原子类，在高并发时比AtomicInteger、AtomicLong效率更高

**Atomic类在线程竞争资源中会进行CAS，失败会自旋，我们都知道竞争只有一个线程可以成功，所以大部分线程将自旋，耗费CPU资源，于是设计了`LongAdder`，它通过内部维护一个`Cell`数组来对数组中的值进行CAS，也就是将一个值分成了多个值，多个线程可以竞争资源，最后返回Cell数组的累加和。**

> 其中数组下标是每个线程的hash值，存储在ThreadLocal中



参考博文：https://www.jianshu.com/p/22d38d5c8c2a

https://juejin.im/entry/5a5b7e8a51882573443ca7ee



## 七.Lock

### Lock接口方法

- void lock()  

  获取锁

- void unlock()

  释放锁

- Boolean tryLock()

  尝试去获取锁，立刻返回获取结果

- boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

  与tryLock不同点是在获取不到锁后会等待一段时间，还获取不到的话就返回false

- void lockInterruptibly() throws InterruptedException;

### ReentrantLock

ReentrantLock是唯一实现了Lock接口的类，并且ReentrantLock提供了更多的方法，是一个可重入锁

```java
			lock.lock();
            try{
               //处理任务
            }finally {
                //finally防止死锁
                lock.unlock();
            }
```

### lock与synchronized比较

lock需要自行获取和释放，synchronized不需要

lock可以提高读操作的效率，也可以知道是否获取到锁

### 自定义锁

> 实现Lock接口，实现其中方法

- 不可重入自定义锁

```java
private boolean isLocked = false;
   
    public synchronized void lock() {
        //如果锁被获取到则等待释放
        while (isLocked){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //等到unlock释放锁后 获取锁
        isLocked = true;
    }
    ......................
public synchronized void unlock() {
        //设置获取锁的标志为未获取
        isLocked = false;
        //通知其他线程获取锁
        notify();
    }
```

- 可重入自定义锁

```java
private Thread lockThread = null;
    private int lockCount = 0;
    private boolean isLocked = false;

    @Override
    public synchronized void lock() {
        //第二个条件判断被锁的线程与当前贤臣过是否为同一个`，同一个的话可重入
        while(isLocked&&Thread.currentThread()!=lockThread){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isLocked = true;
        lockCount++;
        lockThread = Thread.currentThread();
    }
    ..........................
public synchronized void unlock() {
        if(Thread.currentThread()==lockThread){
            lockCount--;
            if(lockCount==0){
                isLocked = false;
                notify();
            }
        }
    }
```



## 八.AbstractQueuedSynchronizer(AQS)

详细看我另一篇博客：https://www.dzou.top/post/7a5625fe.html



## 九.读写锁

### ReentrantReadWriteLock

读写锁中读操作是共享的，并不互斥，读操作可以一起进行；写操作是互斥的，同时只能一个线程获取写锁；

使用如下：

```java
//添加参数指定公平或者非公平
private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
```

#### 实现原理

***实现与``ReentrantLock``类似写锁使用继承AQS类的同步器``Sync``的``tryRelease``和``tryAcquire``，重入次数由int值控制，最大16位二进制-1；读锁是共享锁，使用``Sync``的``tryReleaseShare``和``tryAcquireShare``,读锁重入的次数使用``HoldCounter``内部类（由``ThreadLocal``存储）***

#### 状态维护

状态由32位二进制整数维护，前16位负责读，后16位负责写，通过位运算确定状态，假设当前状态为``S``，写状态等于``S&0x0000FFFF``（将高16位抹去），读状态等于``S>>>16``无符号右移16位。写状态+1时，``S+1``；读状态+1时，``S+(1<<16)``，也就是下面的``S+SHARED_UNIT``

1. 读锁的获取：当没有线程处于获取到写锁或者处于请求获取写锁的状态时，可以获取读锁
2. 写锁的获取：首先写锁请求数+1，判断如果没有线程持有写锁并且没有线程持有读锁则可以获取
3. 死锁情况：线程1获取到读锁，线程2请求获取写锁，此时线程1再请求获取读锁就会发生死锁

```java
static final int SHARED_SHIFT   = 16;
static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;
```

#### 锁降级

1. 锁升级：读锁升级为写锁
2. 锁降级：写锁降级为读锁

降级过程：如果当前线程获取到写锁，再获取到读锁，随后释放写锁的过程。

``ReentrantReadWriteLock``不支持锁升级，支持把写锁降级到读锁

```java
public void readWrite(){
        //因为要读取isUpdate，所以获取读锁
        readLock.lock();
    //isUpdate volatile修饰，保持可见性
        if(!isUpdate) {
            readLock.unlock();
            //获取写锁写操作
            writeLock.lock();
            try {
                //重新检查isUpdate的值，可能之前有其他线程获取写锁更改了其的值
                if (!isUpdate) {
                    map.put("key", "value");
                }
                //获取读锁
                readLock.lock();
            }finally {
                //释放写锁
                writeLock.unlock();
            }
        }
        //使用读锁读取数据
        try{
            System.out.println(Thread.currentThread().getName()+":"+map.get("key"));
        }finally {
            readLock.unlock();
        }
    }
```

第二次获取读锁是否必要：

必要的。因为你先释放写锁再获取读锁的话，中间可能有其他线程获取写锁，导致你无法获取到读锁，无法感知数据的更新。

不支持锁升级原因：读锁可以被多个线程获取，其中任意线程获取到写锁更新数据后，都会导致其他获取读锁的线程无法感知数据的更新。



### StampedLock

StampedLock是JUC并发包里面JDK1.8版本新增的一个锁，该锁提供了三种模式的读写控制，当调用获取锁的系列函数的时候，会返回一个long 型的变量`stamp`票据

**内部状态(读、写、乐观读)使用二进制数维护**

- 独占写Writing：与`ReentrantLock`写锁一样
- 悲观读锁Reading：与`ReentrantLock`共享读锁一样
- 乐观读锁Optimistic reading：读线程获取到了读锁，写线程尝试获取写锁也不会阻塞，这相当于对读模式的优化，但是可能会导致数据不一致的问题

> ReentrantReadWriteLock在没有任何读写锁时才可以获取写锁，但是针对读多写少情况，使用 ReentrantReadWriteLock 可能会使写入线程遭遇饥饿（Starvation）问题，也就是写入线程吃吃无法竞争到锁定而一直处于等待状态。所以出现了StampLock的乐观读锁，读的时候可以获取写锁。

> 乐观锁的意思就是先假定在乐观锁获取期间，共享变量不会被改变，既然假定不会被改变，那就不需要上锁。在获取乐观读锁之后进行了一些操作，然后又调用了validate方法，这个方法就是用来验证tryOptimisticRead之后，是否有写操作执行过，如果有，则获取一个读锁，这里的读锁和ReentrantReadWriteLock中的读锁类似。

JDK官方文档案例：

```java
class Point {
    private double x, y;
    private final StampedLock sl = new StampedLock();

    void move(double deltaX, double deltaY) {
        long stamp = sl.writeLock();    //涉及对共享资源的修改，使用写锁-独占操作
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    /**
     * 使用乐观读锁访问共享资源
     * 注意：乐观读锁在保证数据一致性上需要拷贝一份要操作的变量到方法栈，并且在操作数据时候可能其他写线程已经修改了数据，
     * 而我们操作的是方法栈里面的数据，也就是一个快照，所以最多返回的不是最新的数据，但是一致性还是得到保障的。

     */
    double distanceFromOrigin() {
        long stamp = sl.tryOptimisticRead();    // 使用乐观读锁
        double currentX = x, currentY = y;      // 拷贝共享资源到本地方法栈中
        if (!sl.validate(stamp)) {              // 如果有写锁被占用，可能造成数据不一致，所以要切换到普通读锁模式
            stamp = sl.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    void moveIfAtOrigin(double newX, double newY) {
        long stamp = sl.readLock();
        try {
            while (x == 0.0 && y == 0.0) {
                long ws = sl.tryConvertToWriteLock(stamp);  //读锁转换为写锁
                if (ws != 0L) {
                    stamp = ws;
                    x = newX;
                    y = newY;
                    break;
                } else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }
}
```

优点：

- 减少饥饿现象
- 读时可获取写锁(乐观读)
- 支持读写锁转换

缺点：

- stamp的存在使得获取锁释放锁代码更复杂，而不是专注业务代码



参考博文：https://zhuanlan.zhihu.com/p/45323907

https://segmentfault.com/a/1190000015808032

## 十.线程通信

### wait notify

传统可使用下面代码等待，但是资源消耗太大：

```java
protected MySignal sharedSignal = ...

while(!sharedSignal.hasDataToProcess()){
  //do nothing... busy waiting
}
```

`wait`：会释放线程资源，停止运行状态（释放锁）

``notify``：通知某一个处于等待状态的线程（随机）

```java
/**
 * 模拟10个厕所坑位，20个人上，出来一个可以进去一个
 */
public class Toilet {
    private volatile int toilet = 10;
    public void getOneToilet() throws InterruptedException {
        synchronized (this) {
            if (toilet > 0) {
                toilet = toilet - 1;
                System.out.println(Thread.currentThread().getName() + ":get one toilet.");
            } else {
                wait();
                getOneToilet();
            }
        }
    }
    public void releaseToilet(){
        synchronized (this) {
            toilet = toilet + 1;
            System.out.println(Thread.currentThread().getName()+":release one toilet");
            notify();
        }
    }
    public static void main(String[] args) {
        Toilet toilet = new Toilet();
        for(int i=0;i<20;i++){
            new Thread(()->{
                try {
                    toilet.getOneToilet();
                    Thread.sleep(new Random().nextInt(7000));
                    toilet.releaseToilet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```



### 生产者和消费者

面包店生产面包，放在商店里，消费者来店里购买面包；没有面包时，店家告诉你生产出来告诉你，告诉厂家生产面包，到货了，你再过来购买。



### Condition——线程通信更高效的方式

`AQS内部类ConditionObject`实现`Condition`接口

监视器方法包括wait()/notify()/notifyAll(),需要配合synchronized使用.而Condition是配合lock使用的。

主要方法：

```java
void await();
void signal();
```

> 用`await()`替换`wait()`，用`signal()`替换`notify()`，用`signalAll()`替换`notifyAll()`，传统线程的通信方式，Condition都可以实现，这里注意，Condition是被绑定到Lock上的，要创建一个Lock的Condition必须用newCondition()方法。

使用：

```java
private Lock lock = new ReentrantLock();
private Condition addCondition = lock.newCondition();
private Condition pollCondition = lock.newCondition();
```

#### Condition实现的有界队列

```java
public class BoundQueue {
    private Lock lock = new ReentrantLock();
    private Condition addCondition = lock.newCondition();
    private Condition pollCondition = lock.newCondition();
    private Object[] boundQueue = new Object[20];
    private int count;//队列元素数量
    private int addIndex;//入队索引
    private int pollIndex;//出队索引
    public void add(Object x){
        lock.lock();
        try {
            while (count >= boundQueue.length) {
                addCondition.await();//等待
            }
            System.out.println(Thread.currentThread().getName()+":"+x+"入队列，当前有："+ ++count+"个元素");
            boundQueue[addIndex] = x;//入队
            if(++addIndex==boundQueue.length){
                addIndex = 0;//入队到尽头时，从队列开始入队
            }
            pollCondition.signal();//唤醒
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public void poll(){
        lock.lock();
        try {
            while (count <= 0) {
                pollCondition.await();
            }
            System.out.println(Thread.currentThread().getName()+":"+boundQueue[pollIndex]+"出队列，当前有："+ --count+"个元素");
            boundQueue[pollIndex] = null;//出队
            if(++pollIndex==boundQueue.length){
                pollIndex = 0;//如果出队的索引到队列尽头，从头开始出队
            }
            addCondition.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
```



#### Condition源码分析

Condition接口实现类就是AQS同步器的`ConditionObject`类，每一个ConditionObject维护一个等待队列（单向队列）

该类维护着一个与AQS中`ReentrantLock`的同步队列类似，叫等待队列，所有调用condition.await方法的线程会加入到等待队列中，并且线程状态转换为等待状态。

- 等待队列：存储者待唤醒的线程的引用
- 同步队列：存储同步竞争资源的线程的引用

##### await

把线程加入等待队列

```java
public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
    //主要 把当前线程引用用Node保存，添加到等待队列
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
    //while循环把该线程的节点添加到同步队列 阻塞
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null) // clean up if cancelled
                //清空等待队列中处于CANCEL状态的节点
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
        }
```

##### addConditionWaiter

把当前线程引用用Node保存，添加到等待队列

```java
private Node addConditionWaiter() {
            Node t = lastWaiter;
            // If lastWaiter is cancelled, clean out.
            if (t != null && t.waitStatus != Node.CONDITION) {
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);
    //如果等待队列中最后一个节点为空，则该节点为第一个节点
            if (t == null)
                firstWaiter = node;
            else
                //否则为最后一个节点的后一个节点
                t.nextWaiter = node;
    //把最后一个节点指向该节点
            lastWaiter = node;
            return node;
        }
```

##### unlinkCancelledWaiters

清空等待队列中CANCEL状态的节点

```java
private void unlinkCancelledWaiters() {
            Node t = firstWaiter;
            Node trail = null;
            while (t != null) {
                Node next = t.nextWaiter;
                //如果状态不为CONDITION（Condition等待队列），把它清除
                if (t.waitStatus != Node.CONDITION) {
                    t.nextWaiter = null;
                    if (trail == null)
                        firstWaiter = next;
                    else
                        trail.nextWaiter = next;
                    if (next == null)
                        lastWaiter = trail;
                }
                else
                    trail = t;
                t = next;
            }
        }
```

##### signal

唤醒等待队列中第一个队列到同步队列

```java
public final void signal() {
    //如果不是独占模式抛出异常
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
    //唤醒第一个节点对应线程
            Node first = firstWaiter;
            if (first != null)
                doSignal(first);
        }

```

##### doSignal

```java
private void doSignal(Node first) {
    //唤醒第一个节点，失败唤醒下一个，成功后停止循环
            do {
                if ( (firstWaiter = first.nextWaiter) == null)
                    lastWaiter = null;
                first.nextWaiter = null;
            } while (!transferForSignal(first) &&
                     (first = firstWaiter) != null);
        }
```

##### transferForSignal

把线程改变成唤醒状态

```java
final boolean transferForSignal(Node node) {
        /*
         * If cannot change waitStatus, the node has been cancelled.
         */
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;
//把当前节点添加到同步队列中 竞争资源
        Node p = enq(node);
        int ws = p.waitStatus;
    //如果该线程状态为CANCEL或者设置状态为SIGNAL（唤醒状态）失败的话，使用UNSAFE的unpark方法唤醒该线程，但是这样的话节点状态可能短暂是错误的，但是影响并不大（在同步队列中会使用CAS设定状态）
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            LockSupport.unpark(node.thread);
        return true;
    }
```



### 数据库连接池

#### 使用Condition+ReentrantLock实现

```java
public class MyDataSource {
    private List<Connection> pool = new LinkedList<>();
    private static final int INIT_CONNECTION = 10;
    private static String URL = "jdbc:mysql://localhost:3306/springboot";
    private static String USER = "xxxx";
    private static String PASSWORD = "xxxx";
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public MyDataSource(){
        for(int i=0;i<INIT_CONNECTION;i++) {
            try {
                pool.add(DriverManager.getConnection(URL,USER,PASSWORD));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public Connection getConnection(){
        lock.lock();
        try {
            while (pool.isEmpty()){
                condition.await();
            }
            System.out.println(Thread.currentThread().getName()+":获取到连接，目前连接池还有"+ (pool.size() - 1) +"个连接");
            return pool.remove(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            lock.unlock();
        }
    }
    public void release(Connection con) throws SQLException {
        lock.lock();
        try {
            if (con != null) {
                System.out.println(Thread.currentThread().getName()+":释放连接，目前连接池还有"+(pool.size() + 1)+"个连接");
                pool.add(con);
                condition.signal();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) {
        MyDataSource dataSource = new MyDataSource();
        for(int i=0;i<5;i++){
            new Thread(()->{
                try {
                    Connection con = dataSource.getConnection();
                    Thread.sleep(5000);
                    dataSource.release(con);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
```



### 线程 Join

让“主线程”等待“子线程”结束之后才能继续运行。

原理：

```java
while(join线程存活){
    主线程wait
}
```

concurrent.join包下



### ThreadLocal

ThreadLocal是一个关于创建线程局部变量的类。

通常情况下，我们创建的变量是可以被任何一个线程访问并修改的。而使用ThreadLocal创建的变量只能被当前线程访问，其他线程则无法访问和修改。

各个线程使用`set`、`get`和`remove`对局部变量进行操作，实现了数据的隔离

> 总而言之，往ThreadLocal中存的变量属于当前线程的，其他线程无法获取

#### 使用方法

- 支持泛型

```java
ThreadLocal<String> mStringThreadLocal = new ThreadLocal<>();

mStringThreadLocal.set("dzou.top");

mStringThreadLocal.get();
```

- 为ThreadLocal设置初始值：重写`initialValue`方法

```java
ThreadLocal<Integer> mThreadLocal = new ThreadLocal<String>() {
    @Override
    protected Integer initialValue() {
      return 1;
    }
};
```

- 存储在栈内存中

每个线程都有一个栈内存，属于线程的私有空间。而堆内存对所有线程可见。

#### 实现原理

- 看一下`get`方法

```java
public T get() {
    //获取当前线程对象
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            //通过当前线程对象获取值
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
```

这里使用了`ThreadLocalMap`这个Map存储，内部使用了`Entry`这个类存储

> Entry：Entry<K,V>它表示Map中的一个实体（一个key-value对）

其中Map的key值就是`当前线程对象`，value就是存储的值，所以保证了只有当前线程才能获取到值

- 看一下`set`方法

```java
public void set(T value) {
    //获取线程对象
        Thread t = Thread.currentThread();
    //根据线程对象获取Map
        ThreadLocalMap map = getMap(t);
        if (map != null)
            //Map不为空设置value
            map.set(this, value);
        else
            //为空创建Map并设置value
            createMap(t, value);
    }
```

- `remove`方法

```java
public void remove() {
         ThreadLocalMap m = getMap(Thread.currentThread());
         if (m != null)
             //不为空，根据当前线程对象作为Key删除该Entry
             m.remove(this);
     }
```

#### 总结

1. ThreadLocal中维护着一个ThreadLocalMap对象
2. ThreadLocalMap时ThreadLocal的内部类，内部使用Entry进行存储
3. 调用key时，是根据线程对象获取value



#### 内存泄露

因为`ThreadLocal`中局部变量是根据当前线程创建的，所以`ThreadLocal`和线程有相同的存活时间，当线程没有结束，导致对应Map中`Entry`依然存在，所以为了防止内存泄漏要自己收到在不需要时把Entry给`remove`掉



#### ThreadLocal应用

- 实现单个线程单例以及单个线程上下文信息存储，比如交易id等
- 实现线程安全，非线程安全的对象使用ThreadLocal之后就会变得线程安全，因为每个线程都会有一个对应的实例
- 承载一些线程相关的数据，避免在方法中来回传递参数



#### InheritableThreadLocal

继承自ThreadLocal

ThreadLocal设计之初就是为了绑定当前线程，如果希望当前线程的ThreadLocal能够被子线程使用，实现方式就会相当困难，解决此问题

```java
protected T childValue(T parentValue) {
    //获取父线程值
        return parentValue;
    }
```

`InheritableThreadLocal`主要用于`子线程创建`时，需要自动继承父线程的ThreadLocal变量，方便必要信息的进一步传递。

- shiro：Shiro中使用ThreadLocal存储用户主体Subject对象，通过`getSubject`获取，`InheritableThreadLocal` 可以让用户自行 new Thread 出来的线程可以获取到 Subject，否则用户还要额外想办法怎么获取到这个 Subject



## 十一.并发工具类

### CountDownLatch——共享锁

***该类提供一种等待一个或者多个并发任务完成的机制。内部有一个计数器，主线程要继续执行需要完成指定数量的并发任务。***

业务场景：有一个业务分成多个部分，需要完成指定的部分后才能完成剩下的指定部分。

实现：可以用`Join`、`wait/notify`、`Condition`实现

但是Java中AQS同步器又为我们实现了该工具类（AQS真的是并发之宝），类似倒计时的工具类

使用：

```java
CountDownLatch latch = new CountDownLatch(5);//初始化指定需要完成5个并发任务
latch.await();//完成5个之前等待，等待完成5个唤醒
latch.countDown();//计数任务-1
```

- `await()`：调用该方法的线程等待count值减到0时才能被唤醒继续执行，否则等待
- `countDown()`：使计数任务值-1

#### 使用

全班20名同学完成作业后才可以放学

```java
private static final int STUDENTS_NUMBER = 20;
    private static CountDownLatch finishHW = new CountDownLatch(STUDENTS_NUMBER);
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<STUDENTS_NUMBER;i++){
            executorService.execute(()->{
                System.out.println(Thread.currentThread().getName()+":完成了作业");
                sleep(500);
                finishHW.countDown();
            });
        }
        finishHW.await();
        sleep(1000);
        System.out.println("全部人完成了任务，可以放学了");
    }
    private static void sleep(int milliSecond){
        try {
            TimeUnit.MILLISECONDS.sleep(milliSecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```



#### 源码分析

CountDownLatch也是基于AQS实现的，使用一个int型(原子)volatile(可见)变量维护状态

- await

调用`sync.acquireSharedInterruptibly(1);`

```java
public final void acquireSharedInterruptibly(int arg)
            throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (tryAcquireShared(arg) < 0)
            doAcquireSharedInterruptibly(arg);
    }
```



```java
protected int tryAcquireShared(int acquires) {
    //如果状态不为0也就是count！=0，则执行等待doAcquireSharedInterruptibly
            return (getState() == 0) ? 1 : -1;
        }
```



```java
private void doAcquireSharedInterruptibly(int arg)
        throws InterruptedException {
    //以共享模式将节点添加到队列中
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            //循环执行，等待其他线程执行到足够的任务数，count=0时，唤醒该等待线程
            for (;;) {
                //获取前一个节点
                final Node p = node.predecessor();
                if (p == head) {
                    //r>=0代表等待完成，count=0
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        //设置当前节点为头结点并唤醒等待的线程
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

```



- countDown

  调用`sync.releaseShared(1);`

```java
public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }
```



```java
protected boolean tryReleaseShared(int releases) {
    //循环设置状态count=count-1，任务数减一，成功返回count是否为0，即是否还有任务
            for (;;) {
                int c = getState();
                if (c == 0)
                    return false;//没有任务了直接返回false，无需线程需要唤醒
                int nextc = c-1;
                if (compareAndSetState(c, nextc))//CAS设置状态
                    return nextc == 0;
            }
        }
```

doReleaseShared()方法主要作用是唤醒调用了await()方法的线程

```java
private void doReleaseShared() {
    //循环唤醒所有await的线程，共享模式而非独占模式
        for (;;) {
            Node h = head;
            //头节点不为空并且节点数>1
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {// SIGNAL状态表示当前节点正在等待被唤醒
                    //清除等待状态
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue;            // loop to recheck cases
                    //唤醒线程
                    unparkSuccessor(h);
                }
                else if (ws == 0 &&
                         !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                    continue;                // loop on failed CAS
            }
            if (h == head)                   // loop if head changed
                break;
        }
    }
```

总结：

`CountDownLatch`是一个共享锁，锁住了共享资源，调用`CountDownLatch`释放一个资源，当所有资源被释放时，唤醒所有等待的线程。





### CyclicBarrier——屏障

`barrier`是屏障，它的作用就是在让多个线程在完成了屏障前的工作后，在屏障那等待，知道其他线程也完成了屏障前的工作才可以一起执行屏障后的工作。

`cyclic`代表循环，可以复用

构造方法：

- `public CyclicBarrier(int parties, Runnable barrierAction)`

  创建一个新的CyclicBarrier，给定数量的参与者，给定屏障线程，当等待线程达到参与者数量时，屏障线程工作，等待线程继续执行。

- `public CyclicBarrier(int parties)`

  创建一个新的CyclicBarrier，给定数量的参与者，当等待线程达到参与者数量时，等待线程继续执行。

#### 使用

一个长跑比赛需要运动员进场到`起跑线`准备好裁判才会吹响比赛，多个线程来到起跑线就进行等待，直到所有的参赛选手都到起跑线准备好比赛才可以开始。

```java
public class CyclicBarrierTask implements Runnable{
    private CyclicBarrier cyclicBarrier;
    private static final int COMPETITOR_NUMBER = 10;
    public CyclicBarrierTask(Thread barrierThread){
        cyclicBarrier = new CyclicBarrier(COMPETITOR_NUMBER, barrierThread);
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+":走入赛场");
        try {
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName()+":准备好开始比赛");
            cyclicBarrier.await();
            System.out.println(Thread.currentThread().getName()+":出发");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        CyclicBarrierTask cyclicBarrierTask = new CyclicBarrierTask(new Thread(()->{
            System.out.println("裁判吹响口哨，开始比赛！！！");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
        for(int i=0;i<COMPETITOR_NUMBER;i++){
            new Thread(cyclicBarrierTask).start();
        }
        Thread.sleep(10000);
        System.out.println("比赛结束，运动员回家，裁判回家。");
    }
}
```

- `reset`方法

  当有线程来到屏障前等待，调用reset方法会抛出`BrokenBarrierException`异常

- `isBroken`方法

  线程是否被中断

- `getNumberWaiting`方法

  获取正在等待线程数量

#### 源码分析

`CyclicBarrier`内部没有使用AQS实现，而是使用`ReentrantLock`和`Condition`实现

内部维护了一个计数器，用`count`值存储，每次-1

构造方法：

```java
public CyclicBarrier(int parties, Runnable barrierAction) {
        if (parties <= 0) throw new IllegalArgumentException();
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierAction;
    }
```

构造方法中使用了一个`count`变量和一个`parties`变量，不只是用parties的为了复用，在一次count=0后，创建新的`Generation`，把count的值赋值为parties的值达到复用。

内部维护了一个`Generation`的类，用来维护`CyclicBarrier`的循环使用(复用)，每完成一次计数，通过`nextGeneration`方法，唤醒线程并创建一个新的Generation赋值给该屏障的`generation`变量(换代)

- 里面只有一个变量`broken`：存储该屏障是否被破坏，被破坏则不能再使用

```java
private static class Generation {
        boolean broken = false;
    }
```

- nextGeneration

```java
private void nextGeneration() {
        // 使用Condition唤醒所有等待线程
        trip.signalAll();
        // 换代并更新count值达到复用
        count = parties;
        generation = new Generation();
    }
```

- 最主要的await方法调用了下面的`dowait`方法
  1. count-1，为0则更新并唤醒
  2. 不为0，await，等待被唤醒并更新

```java
private int dowait(boolean timed, long nanos)
        throws InterruptedException, BrokenBarrierException,
               TimeoutException {
        final ReentrantLock lock = this.lock;
        //count是共享变量，需要使用Lock
        lock.lock();
        try {
            final Generation g = generation;//当前Generation赋值给g
            if (g.broken)//如果当前代屏障已经被破坏则抛出异常，无法再使用
                throw new BrokenBarrierException();
            if (Thread.interrupted()) {//如果线程被中断，就破坏屏障，broken=true,并抛出异常
                breakBarrier();
                throw new InterruptedException();
            }
            //count递减，等待线程增加1个
            int index = --count;
            if (index == 0) {  // 如果等待线程数达到头，需要唤醒线程并执行屏障线程并换代
                boolean ranAction = false;
                try {
                    final Runnable command = barrierCommand;
                    if (command != null)
                        command.run();//执行屏障线程
                    ranAction = true;
                    nextGeneration();//换代操作
                    return 0;//返回0为结束了
                } finally {
                    if (!ranAction)//如果屏障线程执行出错,破坏该屏障，无法再次使用
                        breakBarrier();
                }
            }

            // 无限循环等待换代成功(全部线程通过屏障),或者抛出异常
            for (;;) {
                try {
                    if (!timed)//如果没有超时要求,直接调用Condition的await
                        trip.await();
                    else if (nanos > 0L)//否则等待相应超时时间
                        nanos = trip.awaitNanos(nanos);
                } catch (InterruptedException ie) {//发生小概率事件(被中断)
                    if (g == generation && ! g.broken) {//破坏该屏障
                        breakBarrier();
                        throw ie;
                    } else {
                        Thread.currentThread().interrupt();
                    }
                }
                if (g.broken)//如果屏障被破坏了，抛出异常
                    throw new BrokenBarrierException();
                if (g != generation)//如果正常情况下等待后会换代成功，返回该线程所在屏障的下标
                    return index;
                if (timed && nanos <= 0L) {//超时，抛出异常并破坏屏障
                    breakBarrier();
                    throw new TimeoutException();
                }
            }
        } finally {
            lock.unlock();
        }
    }
```



***因为处于Lock中，唤醒操作是将线程从`condtion队列`(等待/条件队列)中全部放入AQS的同步队列中***



#### CyclicBarrier和CountDownLatch

- 复用性：CyclicBarrier支持(更强大)，CountDownLatch不支持
- 内部实现：CyclicBarrier使用Lock+Condition，CountDownLatch使用AQS的共享模式
- 内部都使用一个计数器

CountDownLatch需要使用`CountDownLatch`触发计数器事件，`CyclicBarrier`自动在await处计数。

场景分析：

- CountDownLatch更注重在需要等待多个任务完成后执行
- CyclicBarrier更注重等待大家汇聚在每个屏障处再执行后面的任务



### Semaphore——信号量

***信号量机制用于控制对一个或多个资源的访问。***

- `acquire()`

  调用该方法时，如果内部资源数>0，则资源数`减一`并获取对该资源的访问；如果资源数=0，则线程会被祖塞，获取不到资源，直到某个线程调用`release`方法，才有可能被唤醒竞争资源。

- `release()`

  调用该方法时，说明该线程访问的资源已经获取并使用完毕，需要归还给控制中心让其他线程竞争，资源数执行`加一`操作，会唤醒一个处于等待的线程，这个线程获取到该资源并结束它的`acquire`方法，其他线程将会一直等待，直到又有资源释放。

#### 初始化

- 只传入一个资源访问控制数

  用于控制同时几个线程可以访问该资源，默认为非公平模式

```java
public Semaphore(int permits) {
        sync = new NonfairSync(permits);
    }
```

- 传入访问控制数和一个boolean型变量(用于设置公平或者非公平模式)

```java
public Semaphore(int permits, boolean fair) {
        sync = fair ? new FairSync(permits) : new NonfairSync(permits);
    }
```



#### 使用

上厕所的资源控制，5个坑，15个人上

```java
public class Toilet implements Runnable{
    private Semaphore semaphore;
    private static final int TOILET_SIZE = 5;
    public Toilet(Semaphore semaphore){
        this.semaphore = semaphore;
    }
    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName()+":我来上厕所了");
            Thread.sleep(1000);
            if (semaphore.availablePermits()<=0){
                System.out.println(Thread.currentThread().getName()+":怎么没位置了");
            }
            semaphore.acquire();
            System.out.println(Thread.currentThread().getName()+":我抢到厕所了。");
            Thread.sleep(3000);
            System.out.println(Thread.currentThread().getName()+":我上完了，你们可以进去一个了。");
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(TOILET_SIZE);
        for(int i=0;i<15;i++){
            new Thread(new Toilet(semaphore)).start();
        }
    }
}
```



#### 源码分析

内部是由AQS的同步器(公平和非公平)实现的，获取使用的是共享模式(Share)

`Semaphore`中使用一个`permits`变量存储能访问资源数，为1时，就是一个互斥锁，也由AQS的`state`来维护

- acquire方法调用 acquireSharedInterruptibly(1)

  ```java
  public final void acquireSharedInterruptibly(int arg)
              throws InterruptedException {
          if (Thread.interrupted())
              throw new InterruptedException();
      //尝试获取一个访问资源
          if (tryAcquireShared(arg) < 0)
              //<0代表没有资源，获取失败，则执行等待并重新获取
              doAcquireSharedInterruptibly(arg);
  }
  ```



##### acquire

- doAcquireSharedInterruptibly

  AQS中的方法，循环获取该资源访问

```java
private void doAcquireSharedInterruptibly(int arg)
        throws InterruptedException {
        final Node node = addWaiter(Node.SHARED);//添加节点到同步队列(共享模式)
        boolean failed = true;
        try {
            for (;;) {//循环获取
                final Node p = node.predecessor();//获取前一个节点
                if (p == head) {
                    int r = tryAcquireShared(arg);//尝试再次获取资源
                    if (r >= 0) {//获取资源成功
                        setHeadAndPropagate(node, r);//设置为头结点并设置状态
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
```



- 非公平tryAcquireShared

```java
final int nonfairTryAcquireShared(int acquires) {
            for (;;) {//循环知道获取失败或者成功
                int available = getState();//获取AQS存储的state，即可获取的资源数
                int remaining = available - acquires;//-1
                if (remaining < 0 ||//<0则没有可获取的
                    compareAndSetState(available, remaining))//获取成功并设置状态
                    return remaining;
            }
        }
```

- 公平tryAcquireShared

  与非公平唯一区别就是 hasQueuedPredecessors 方法

  判断”当前线程”是不是在CLH队列的队首，来实现公平性。

```java
protected int tryAcquireShared(int acquires) {
            for (;;) {
                if (hasQueuedPredecessors())
                    return -1;
                int available = getState();
                int remaining = available - acquires;
                if (remaining < 0 ||
                    compareAndSetState(available, remaining))
                    return remaining;
            }
        }
```



##### release

 与CountDownLatch 的`countDown`一样的实现

![{% qnimg concurrent/1.png %}](http://dzou.wangminwei.top/static/images/concurrent/1.png)



### Exchanger——交换者

用于两个线程间的数据交换，如果有第三个线程，对不起，我做不到。

`Exchanger`提供一个同步点，在这个同步点处，两个线程进行数据交换。

主要方法

- `exchange(V v)` 返回值V

  线程会在这检查是否有线程等待交换，没有就继续等待，有就进行数据交换。

#### 使用

还可以用于遗传算法，遗传需要两个对象，分别提供相应的基因，在进行交换根据一定规则进行遗传。

例子：用于效验结果比对，模拟两个线程分别计算

```java
public class TaskA implements Runnable{
    private Exchanger<Integer> exchanger;
    public TaskA(Exchanger<Integer> exchanger){
        this.exchanger = exchanger;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+":正在进行计算");
        try {
            Thread.sleep(2000);
            Integer x = 1026;
            System.out.println(Thread.currentThread().getName()+":计算结束，等待效验");
            exchanger.exchange(x);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class TaskB implements Runnable{
    private Exchanger<Integer> exchanger;
    public TaskB(Exchanger<Integer> exchanger){
        this.exchanger = exchanger;
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+":正在进行计算");
        try {
            Thread.sleep(2000);
            Integer res = 1026;
            System.out.println(Thread.currentThread().getName()+":计算结束，等待效验");
            Integer x = exchanger.exchange(res);
            Thread.sleep(1000);
            System.out.println("效验结果是："+x.equals(res));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


public class ExchangerTask {
    public static void main(String[] args) {
        Exchanger<Integer> exchanger = new Exchanger<>();
        new Thread(new TaskA(exchanger)).start();
        new Thread(new TaskB(exchanger)).start();
    }
}
```

#### 实现原理

内部使用了`Node`存储每个线程的信息，包括数据，把它放在`Participant`中，这是一个继承自`ThreadLocal`并重写了其初始化方法的类，用于把Node保存到Participant中。实现类似SynchronousQueue，后面再继续学习。



## 十二.Future

***异步：提供一种高效的线程执行方式，你可以在执行一个耗时长的任务时去做其他的事，而无需停在那等待。***

### Callable

我们在看`Future`前先看一下`Callable`，我们都知道`Runnable`的线程是一种没有返回值的线程。

当我们线程需要返回某个结果时，我们就需要使用`Callable<V>`，它是一种有返回值的线程。

与Runnable函数式接口对应的，Callable的方法是：

```java
V call() throws Exception;
```

> 用`泛型`支持返回值

1. 线程返回值通过什么获取？

答：`Future`

1. Callable和Runnable区别

答：Callable允许抛出异常(在被取消时抛出)；Callable的call方法由FutureTask中`run`方法调用

### Future

使用`Callable`的线程会把返回值存在一个`Future`中，Future就支持泛型了

主要方法如下：

```java
    boolean cancel(boolean mayInterruptIfRunning);//取消线程执行
    boolean isCancelled();//判断是否取消
    boolean isDone();//判断是否完成任务
    V get() throws InterruptedException, ExecutionException;//获取线程返回值
```

- 我们将通过`Future.get`获取返回值，如果未完成任务，会在get方法处`阻塞`，等待任务结束获取到返回值。

### Future和Callable使用

Runnable：我们一般可以使用`ExecutorService`接口中的`execute`方法执行没有返回值的线程

Callable：现在有了有返回值的线程，我们可以使用`ExecutorService`中的`submit`方法提交线程任务并获取一个`Future`对象，通过该对象`get`方法获取返回值

```java
public class TestFuture {
//Callable任务
    public static class FutureTaskTest implements Callable<Long> {
        @Override
        public Long call() throws Exception {
            long start = System.currentTimeMillis();
            Thread.sleep(new Random().nextInt(3000));
            long end = System.currentTimeMillis();
            //返回值就是执行时间
            return end-start;
        }
    }
    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(10);
        List<Future<Long>> result = new ArrayList<>();
        //执行10个线程任务，通过submit把Future对象存在ArrayList中
        for (int i=0;i<10;i++) {
            result.add(es.submit(new FutureTaskTest()));
        }
        //使用lambda输出返回值
        result.forEach((o)->{
            try {
                System.out.println(o.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        es.shutdown();
    }
}
```



- ExecutorService中的submit方法

```java
Future<T> submit(Callable<T> task);//获取返回值的Callable线程
Future<T> submit(Runnable task, T result);//传入无返回值的线程和结果的引用，最后返回值存在result中，这是有返回值的submit
Future<?> submit(Runnable task);//无返回值
```

看一下submit实现：

在`AbstractExecutorService`类中实现

```java
public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }

public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task, result);
        execute(ftask);//执行了RunnableFuture的run方法，也就是FutureTask的run方法
        return ftask;
    }
```

> 可以看到他把线程任务封装成一个`RunnableFuture`接口的实现类`FutureTask`对象执行

```java
public interface RunnableFuture<V> extends Runnable, Future<V> {
    void run();
}
```

> 可以看到，RunnableFuture继承自Runnable和Future，它的`run`方法在`FutureTask`中实现

```java
protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new FutureTask<T>(callable);
    }

protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new FutureTask<T>(runnable, value);
    }
```

> 这个方法返回了一个`FutureTask`对象，稍后讲到



### FutureTask

封装了`Future`线程的执行和`Runnable`线程的执行，并返回返回值result，使用适配器模式将Runnable线程转换为Callable线程执行

![](http://dzou.wangminwei.top/static/images/concurrent/2.png)

构造方法：

- 传入Runnable和result

```java
public FutureTask(Runnable runnable, V result) {
        this.callable = Executors.callable(runnable, result);
        this.state = NEW;       // ensure visibility of callable
    }
```

> 调用Executors中的callable方法，将Runnable线程转换为Callable线程作为FutureTask的callable参数

- 传入Callable

```java
public FutureTask(Callable<V> callable) {
        if (callable == null)
            throw new NullPointerException();
        this.callable = callable;
        this.state = NEW;       // ensure visibility of callable
    }
```

callable方法

```java
public static <T> Callable<T> callable(Runnable task, T result) {
        if (task == null)
            throw new NullPointerException();
        return new RunnableAdapter<T>(task, result);
    }
```

> 这个方法就是把Runnable线程转换为Callable线程执行，使用适配器模式

- RunnableAdapter

```java
static final class RunnableAdapter<T> implements Callable<T> {
        final Runnable task;
        final T result;
        RunnableAdapter(Runnable task, T result) {
            this.task = task;
            this.result = result;
        }
        public T call() {
            task.run();
            return result;
        }
    }
```

> 这就是一种`适配器模式`的应用，实现目标接口Callable，在call方法中调用Runnable的run方法，并返回计算结果result

可以参考我的另一篇博文，Java设计模式——适配器模式

### FutureTask和Callable使用

- 不使用线程池的submit方法，创建一个FutureTask对象接收返回值

```java
public class TestCallable {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable<Porridge> call = () -> {
            System.out.println("正在煮粥");
            Thread.sleep(5000);
            return new Porridge();
        };
        FutureTask<Porridge> task = new FutureTask<>(call);
        new Thread(task).start();
        Thread.sleep(500);
        System.out.println("我去睡个觉");
        Porridge i = task.get();
        System.out.println("我拿到了煮粥结果："+i);
    }

    static class Porridge{
        public Porridge(){}
    }
}
```

- Future和FutureTask区别

答：Future只支持Callable线程执行，FutureTask还支持Runnable线程，并且有返回值

### FutureTask源码

- 变量

```java
/*当前任务的执行状态*/
private volatile int state;
/*当前要执行的任务，执行完后将被设置为null*/
private Callable<V> callable;
/*当前任务的执行结果，当发生异常或被取消时为对应异常，非volatile，由state状态保证线程安全*/
private Object outcome; 
/*执行当callable任务的线程引用，为当前线程通过CAS方式原子设置*/
private volatile Thread runner;
/*Treiber椎，用于保存由于调用Future.get方法而阻塞的线程*/
private volatile WaitNode waiters;
```

- 状态

```java
//任务的初始化状态
private static final int NEW          = 0;
//正在执行状态
private static final int COMPLETING   = 1;
//任务正常执行完成的状态
private static final int NORMAL       = 2;
//任务执行抛出异常的状态
private static final int EXCEPTIONAL  = 3;
//调用Future.cancell取消任务的状态
private static final int CANCELLED    = 4;
//执行任务的线程被中断的中间状态
private static final int INTERRUPTING = 5;
//执行任务的线程被中断的最终状态
private static final int INTERRUPTED  = 6;
```

上面已经提到，线程池调用submit的时候或者单独使用时都是由`AbstractExecutorService`类调用`FutureTask`的`run`方法

#### run

- run

```java
public void run() {
    //初始化状态并设置为当前线程
        if (state != NEW ||
            !UNSAFE.compareAndSwapObject(this, runnerOffset,
                                         null, Thread.currentThread()))
            return;//失败返回
        try {
            Callable<V> c = callable;//c=Callable线程
            if (c != null && state == NEW) {//如果非空并且为初始化状态执行任务
                V result;
                boolean ran;
                try {
                    result = c.call();//执行call获取返回结果
                    ran = true;//成功设置ran为true
                } catch (Throwable ex) {
                    result = null;//失败设置ran为false，result为空
                    ran = false;
                    setException(ex);//并设置异常状态
                }
                if (ran)//执行成功则设置result
                    set(result);
            }
        } finally {
            runner = null;
            int s = state;
            if (s >= INTERRUPTING)
                handlePossibleCancellationInterrupt(s);
        }
```

- setException

  设置异常执行结果

```java
protected void setException(Throwable t) {
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {//把状态设置为正在执行
            outcome = t;//把异常作为执行任务结果
            UNSAFE.putOrderedInt(this, stateOffset, EXCEPTIONAL); //再把状态设置为异常
            finishCompletion();//任务执行失败，唤醒其他线程
        }
    }
```

- finishCompletion

  唤醒其他线程

```java
private void finishCompletion() {
        for (WaitNode q; (q = waiters) != null;) {//等待线程不为空的话循环唤醒所有
            if (UNSAFE.compareAndSwapObject(this, waitersOffset, q, null)) {//如果CAS设置等待线程为空成功
                for (;;) {//就循环唤醒线程
                    Thread t = q.thread;//t为当前等待线程
                    if (t != null) {//不为空
                        q.thread = null;//线程引用置为空
                        LockSupport.unpark(t);//唤醒该线程
                    }
                    WaitNode next = q.next;//下个等待线程
                    if (next == null)//为空结束
                        break;
                    q.next = null; // unlink to help gc
                    q = next;//q为下一个，继续执行唤醒
                }
                break;
            }
        }
        done();//结束唤醒
        callable = null;        // to reduce footprint
    }
```

- set

  设置正常执行结果

```java
protected void set(V v) {
        if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {//把状态设置为正在执行
            outcome = v;//接受返回值作为任务执行结果
            UNSAFE.putOrderedInt(this, stateOffset, NORMAL); //把状态设置为正常结束
            finishCompletion();//任务正常结束，唤醒其他线程
        }
    }
```



#### get

获取返回值

- get

```java
public V get() throws InterruptedException, ExecutionException {
        int s = state;
        if (s <= COMPLETING)//如果状态<=在执行状态
            s = awaitDone(false, 0L);//执行等待
        return report(s);//返回获取的结果(返回值或者异常)
    }
```

- awaitDone

  执行等待操作

```java
private int awaitDone(boolean timed, long nanos)//是否设置超时时间
        throws InterruptedException {
        final long deadline = timed ? System.nanoTime() + nanos : 0L;
        WaitNode q = null;
        boolean queued = false;
        for (;;) {//循环等待状态改变
            if (Thread.interrupted()) {//如果被中断，删除等待线程，抛出异常
                removeWaiter(q);
                throw new InterruptedException();
            }
            int s = state;//获取状态
            if (s > COMPLETING) {//状态>正在执行状态，即完成状态(正常、异常)
                if (q != null)//等待线程不为null的话把它置为null，返回状态
                    q.thread = null;
                return s;
            }
            else if (s == COMPLETING) //如果正在执行，当前线程让出一会的CPU时间
                Thread.yield();
            else if (q == null)//如果等待线程为空，创建一个新的等待节点(第一次循环时)
                q = new WaitNode();
            else if (!queued)//为当前等待节点设置线程为当前线程
                queued = UNSAFE.compareAndSwapObject(this, waitersOffset,
                                                     q.next = waiters, q);
            else if (timed) {//如果设置了超时时间，等待
                nanos = deadline - System.nanoTime();
                if (nanos <= 0L) {//超时时间过了没有执行完成则终止该任务并删除当前等待线程并返回状态
                    removeWaiter(q);
                    return state;
                }
                LockSupport.parkNanos(this, nanos);//执行超时等待时间
            }
            else
                LockSupport.park(this);//执行等待
        }
    }
```



- report

  根据状态判断获取的返回值

```java
private V report(int s) throws ExecutionException {
        Object x = outcome;
        if (s == NORMAL)//如果正常执行完毕
            return (V)x;//返回执行的结果(正常返回值)
        if (s >= CANCELLED)//如果被取消或者被中断 抛出异常
            throw new CancellationException();
        throw new ExecutionException((Throwable)x);//否则为异常状态，则抛出outcome结果存储的异常
    }
```



## 十三.Fork/Join框架

- Fork：“分”
- Join：“加”

意思就是**把任务分成多个任务，每个任务执行完后合起来获得最终结果**，也叫`分而治之`法

> 为什么并发中要出现这种方法呢？
>
> 答：我们都知道多线程是为了提高效率而同时执行多个任务的操作，但是这个时候出现了`多核CPU`，如果我们只利用一个核，那么CPU资源就浪费太多了，处于这一点考虑，我们为了`充分利用CPU`资源，提出了分而治之法，在JUC中就是`Fork/Join框架`

- Fork/Join也属于一种执行器，也就是它的`ForkJoinPool`

![](http://dzou.wangminwei.top/static/images/concurrent/4.png)

### 原理

分而治之把一个大任务分成多组若干个小任务，每组任务开辟一个线程执行，每组线程都有一个队列负责该组任务的执行和等待

- 工作窃取算法：每组任务执行速度不同，有的快有的慢，快的执行完后等待慢的执行完的话就浪费资源了，所以快的执行完后会帮还未执行完成的线程从`队列`中拿出任务去执行，但是这样会很混乱，所以规定了**自己线程的任务是从队列头获取，其他线程来拿的任务从队列尾获取**

![](http://dzou.wangminwei.top/static/images/concurrent/3.png)



### 局限性

- 具体使用Fork Join还需根据实际情况分析，较小或较大的数据量都会导致执行效率比串行执行慢
- 如果是`IO`操作较多的任务，尽量不使用，因为IO占CPU较少，`计算`占CPU多，适用于多计算的任务
- 不能在任务内部抛出效验异常，必须处理
- 细分出来的基本任务不能过大或过小，保持在100-10000个基本计算之间



### 使用

Fork/Join包括下面的几个基本类

- ForkJoinPool：实现了ExecutorService和Executor接口，Java提供一个默认的ForkJoinPool对象(公用池)

```java
ForkJoinPool.commonPool();
```

- ForkJoinTask：实现了Future接口，作为ForkJoin任务的抽象类，`submit返回该类对象`接收结果(Future也行)，类似FutureTask

```java
public abstract class ForkJoinTask<V> implements Future<V>
```

- RecursiveTask：常用，扩展ForkJoinTask，也是一个抽象类，通过`继承`让子类重写`compute`方法实现有`返回结果`的ForkJoin任务
- RecursiveAction：扩展ForkJoinTask，也是一个抽象类，通过`继承`让子类重写`compute`方法实现`不带返回结果`的ForkJoin任务
- CountedCompleter：扩展ForkJoinTask，也是抽象类，`实现某个大任务完成后执行其他任务`，子类重写`compute`方法实现完成大任务的计算和重写`onCompletion`方法实现完成之后任务的计算

**方法**

- fork：把创建的新的小任务发送给`ForkJoinPool`执行器，由它管理并分到其他线程执行
- join：等待一个任务执行完成并获取它的`返回结果`，与JUC中join方法类似，多了返回值
- quietlyJoin：没有返回值，与join区别是join任务被`撤销`的话，就会抛出异常，该方法不抛出异常

#### 案例1

计算1乘到100000的计算(过大，得使用BigInteger)，比较串行和ForkJoin的效率

- 串行

```java
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
```

耗时

```java
耗时：5464ms
```

- ForkJoin

```java
public class MyForkJoinTask extends RecursiveTask<BigInteger> {
    private long start;
    private long end;
    public MyForkJoinTask(long start,long end){
        this.start = start;
        this.end = end;
    }
    @Override
    protected BigInteger compute() {
        BigInteger res = new BigInteger("1");//使用BigInteger类型存储过大的数
        if(end-start<=2&&end-start>0){//表示可以进行计算，最基本任务计算2个数相乘
            //计算
            for(long i=start;i<=end;i++){
                res = res.multiply(BigInteger.valueOf(i));
            }
        }else {
            //分治
            long half = (end-start)/2;
            MyForkJoinTask leftTask = new MyForkJoinTask(start,start+half);
            MyForkJoinTask rightTask = new MyForkJoinTask(start+half+1,end);
            leftTask.fork();//提交任务到执行器
            rightTask.fork();
            BigInteger a = leftTask.join();//等待任务执行完并获取结果
            BigInteger b = rightTask.join();
            res = a.multiply(b);
        }
        return res;
    }
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        MyForkJoinTask task = new MyForkJoinTask(1,100000);
        Future<BigInteger> future = forkJoinPool.submit(task);//接受Future对象作为返回值
        try {
            System.out.println(future.get());
            long end = System.currentTimeMillis();
            System.out.println("耗时："+(end-start));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
```

耗时

```java
耗时：1143
```

> 可以明显看到，当数据计算较大时，使用ForkJoin比串行执行有高出4秒的效率



#### 案例2

我们用一个12万数据量的样本来测试，筛选其中的某个key中value值等于一个值的全部数据

样本：NBA投篮数据

筛选：与Kobe有关的全部数据并打印

样本地址：https://www.kaggle.com/dansbecker/nba-shot-logs/downloads/nba-shot-logs.zip/1

- 数据对象bean

```java
@Data
@AllArgsConstructor
public class ShotDataSet {
    private String game_id;
    private String matchUp;
    private String location;
    private String w;
    private String finalMargin;
    private String shot_number;
    private String period;
    private String gameClock;
    private String shotClock;
    private String dribbles;
    private String touchTime;
    private String shotDist;
    private String ptsType;
    private String shotRes;
    private String closestDefend;
    private String cDPId;
    private String cDDist;
    private String fgm;
    private String points;
    private String playName;
    private String playId;
}
```

- CSV数据加载器

  把CSV中数据转换成一个存储`ShotDataSet`对象的集合`ArrayList`，使用了Github上的Java工具项目`Hutool`

```java
public class ShotDataSetLoader {
    public static List<ShotDataSet> loadCsv(String path) {
        List<ShotDataSet> res = new ArrayList<ShotDataSet>();
        CsvReader reader = CsvUtil.getReader();
//从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file(path));
        List<CsvRow> rows = data.getRows();
//遍历行
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            List<String> strList = csvRow.getRawList();
            ShotDataSet dataSet = new ShotDataSet(strList.get(0),
                    strList.get(1),strList.get(2),strList.get(3),
                    strList.get(4),strList.get(5),strList.get(6),
                    strList.get(7),strList.get(8),strList.get(9),
                    strList.get(10),strList.get(11),strList.get(12),
                    strList.get(13),strList.get(14),strList.get(15),
                    strList.get(16),strList.get(17),strList.get(18),
                    strList.get(19),strList.get(20));
            res.add(dataSet);
        }
        return res;
    }
}
```

- 任务类 继承RecursiveTask

  Kobe的PlayerId值等于997，把加载出来的全部数据对象使用ForkJoin分治，保证最基本任务里执行计算的步骤不要太多或太少，我们选取最小集合中有50个对象时执行基本任务。

```java
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
                if(dataSet.getPlayId().trim().equals("977")){//kobe的playerId值等于997
                    resList.add(dataSet);
                }
            }
        }else {
            long half = list.size()/2;
            ShotDataSearchTask leftTask = new ShotDataSearchTask(list.stream().limit(half).collect(Collectors.toList()));//用limit限制集合里数量为一半并且是前一半
            ShotDataSearchTask rightTask = new ShotDataSearchTask(list.stream().skip(half).collect(Collectors.toList()));//用skip跳过集合中前一半
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
```



### 总结

执行器中执行任务的方法：

- `submit`：提交任务到执行器并获取返回对象Future，返回值用该对象或者ForkJoinTask接收，用get方法获取
- `invoke`：提交任务到执行器，任务完后执行方可返回，不是Future。
- `execute`：提交任务到执行器，没有返回值。

ForkJoin任务基本形式如下：

```java
 	if(size>DEFALUT_SIZE){
            MyForkJoinTask leftTask = new MyForkJoinTask();
            MyForkJoinTask rightTask = new MyForkJoinTask();
            leftTask.fork();//提交任务到执行器
            rightTask.fork();
            leftTask.join();//等待任务执行完并获取结果
            rightTask.join();
        }else {
            //计算
        	res = solveProblem();
        	return res;
        }
```



## 十四.Map/Reduce

一个主要区别是，[F-J](http://gee.cs.oswego.edu/dl/papers/fj.pdf)似乎设计为在单个Java VM上工作，而[M-R](http://static.googleusercontent.com/external_content/untrusted_dlcp/labs.google.com/en/us/papers/mapreduce-osdi04.pdf)则明确设计为在大型机器集群上工作。这些是非常不同的场景。

MapReduce是一种编程模型，用于分布式大规模数据计算。

- Map：数据筛选和转换
- Reduce：数据汇总

在Java中的API就是`Stream`，分为并行流和串行流

使用：

- 大数据数值分析
- 信息检索
- 推荐系统



## 十五.同步容器和并发容器

`Vector`—> `ArrayList`—> `CopyOnWriteArrayList`

`HashTable`—> `HashMap` —> `ConcurrentHashMap`

### Vector

- Vector 实现 List 接口，继承 `AbstractList` 类，所以我们可以将其看做队列，支持相关的添加、删除、修改、遍历等功能。
- Vector 实现 `RandmoAccess` 接口，即提供了随机访问功能，提供提供快速访问功能。在 Vector 我们可以直接访问元素。
- Vector 实现了 `Cloneable` 接口，支持 clone() 方法，可以被克隆。

**线程安全，在方法上添加了`synchronized`，效率较低，可扩容**

**扩容原理：当数组满了以后，创建一个新数组，使用`Arrays.copyOf`，拷贝原来数据到新数组，再把引用指向新数组，`capacityIncrement`是指定的增加的容量，默认1倍，也可以在初始化容器时传入。**

- add

```java
public synchronized boolean add(E e) {
        modCount++;//索引
        ensureCapacityHelper(elementCount + 1);//确认容器大小，如果操作容量则扩容操作
        elementData[elementCount++] = e; //将e元素添加至末尾
        return true;
    }
```

- get

```java
public synchronized E get(int index) {
        if (index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        return elementData(index);
    }
```

### ArrayList

也是一个可扩容的`数组`，初始容量为10。

**扩容原理：当数组长度达到列表长度以后，创建一个新数组，使用`Arrays.copyOf`，拷贝原来数据到新数组，再把引用指向新数组，扩容倍数`1.5`。**

**ArrayList 实现不是同步的，没有使用任何同步机制。**

- 可以使用`Collections`类提供的方法获得一个`同步的`ArrayList

```java
Collections.synchronizedList(list)；
```

它返回的是一个`Collections`的内部类`SynchronizedList`：它在`add、get、remove`等方法内部添加了`synchronized`块，与Vector相比更高效一些

- add

```java
public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // 检查并扩容
        elementData[size++] = e;//添加数据
        return true;
    }
```

- get

```java
public E get(int index) {
        rangeCheck(index);//检查是否超出范围，抛出IndexOutOfBoundsException异常
        return elementData(index);
    }
```

- subList操作：**subList 返回的只是原列表的一个视图，它所有的操作最终都会作用在原列表上。**



### CopyOnWriteArrayList

**CopyOnWrite容器即`写时复制`的容器。通俗的理解是当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行`Copy`，复制出一个新的容器(长度+1)，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。这样做的好处是我们可以对CopyOnWrite容器进行并发的读，而不需要加锁，因为当前容器不会添加任何元素。所以CopyOnWrite容器也是一种读写分离的思想，读和写不同的容器。**

> 使用`ReentrantLock`加锁和`Arrays.copyOf()`复制

- add

```java
public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len + 1);//复制
            newElements[len] = e;//添加新的值
            setArray(newElements);//设置引用
            return true;
        } finally {
            lock.unlock();
        }
    }
```

- get

  无需锁，因为获取到的数组没有被改变，写时是复制后写的，写完赋予新的引用，使用`volatile`保持数组可见性

```java
public E get(int index) {
        return get(getArray(), index);
    }
```

还有：

- `CopyOnWriteSet`
- `CopyOnWriteMap`

> 适用于`读多写少`的场景

问题：

**内存占用。**保存了两个值，过大时导致相应时间过长，FullGC。

**数据一致性问题**。CopyOnWrite容器只能保证数据的最终一致性，不能保证数据的实时一致性。所以如果你希望写入的的数据，马上能读到，请不要使用CopyOnWrite容器。



### HashTable

**HashTable与HashMap的关系与Vector与ArrayList关系一致，HashTable在方法上添加`synchronized`关键字，保证线程安全性，并发时效率低，HashMap是非同步的**



### HashMap

也可以使用`Collections`中方法转换成线程安全的HashMap，`方法中有synchronized块`

```java
Collections.synchronizedMap(map)
```



### ConcurrentHashMap

#### JDK 1.7

在JDK 1.7中使用`Segment`实现的`分段锁`技术

- 首先将数据分成一段一段的存储，然后给每一段数据配一把锁，当一个线程占用锁访问其中一个段数据的时候，其他段的数据也能被其他线程访问。

Segment中有一把可重入锁ReentrantLock，一段数据对应一个Segment

![](http://dzou.wangminwei.top/static/images/concurrent/5.png)

#### JDK 1.8

1.8中放弃了Segment臃肿的设计，取而代之的是采用Node+CAS+Synchronized来保证并发安全进行实现，结构如下：

![](http://dzou.wangminwei.top/static/images/concurrent/6.png)

### fail fast机制

**“快速失败”也就是 fail-fast，它是 Java 集合的一种错误检测机制。当多个线程对集合进行结构上的改变的操作时，有可能会产生 fail-fast 机制。记住是有可能，而不是一定。例如：假设存在两个线程（线程 1、线程 2），线程 1 通过 Iterator 在遍历集合 A 中的元素，在某个时候线程 2 修改了集合 A 的结构（是结构上面的修改，而不是简单的修改集合元素的内容），那么这个时候程序就会抛出 ConcurrentModificationException 异常，从而产生 fail-fast 机制。**



## 十六.阻塞和非阻塞队列

### ConcurrentLinkedQueue—非阻塞队列

***这是一种`非阻塞`的线程安全性`链队列`，使用`循环CAS`方式保证非阻塞***

以JDK1.8中分析

**队列由`head`和`tail`节点组成，每个节点Node包含`节点元素item`和下一个节点引用`next`组成，初始情况head节点元素为`null`，tail指向head节点**

```java
public ConcurrentLinkedQueue() {
        head = tail = new Node<E>(null);//初始化时插入一个空节点，head=tail
    }
```

#### 入队 `offer`方法

![](http://dzou.wangminwei.top/static/images/concurrent/7.png)

入队过程：

- 添加元素1，t指向元素为空的tail节点，p指向t，q指向tail节点后一个节点，此时为空，进入第一个if条件语句，尝试使用CAS设置p的next为插入节点，成功再判断p是否指向t，此时p、t指向tail节点，不执行设置tail节点操作。
- 添加元素2，此时tail节点还是第一个空元素节点，此时q不为空且q不等于p，执行第3个条件语句，此时p等于t，把p指向q也就是p的next就是插入的元素1；再次循环q等于p next为空，执行CAS设置p next为元素2节点，成功后现在p指向元素1节点，t指向head节点，不相等，CAS设置tail节点为新插入节点。CAS失败继续循环设置。
- 添加元素3就和添加元素1一样。
- 添加元素4就和添加元素2一样

> 第二个条件语句存在是因为多线程时，有其他线程执行了出队操作出现了自引用状态(指向自己)，也就是tail哨兵节点，则需要让p不指向它，而指向head，在进行CAS设置item后更新tail位置，让那个自引用节点被垃圾回收。

```java
public boolean offer(E e) {
        checkNotNull(e);
    // 如果e为null，则直接抛出NullPointerException异常
        final Node<E> newNode = new Node<E>(e);
        for (Node<E> t = tail, p = t;;) { // 循环CAS直到入队成功
            Node<E> q = p.next;
            if (q == null) {// 判断p是不是尾节点，tail节点不一定是尾节点
                if (p.casNext(null, newNode)) {// 设置p节点的下一个节点为新节点
                    if (p != t) // 如果p != t，则将入队节点设置成tail节点
                        casTail(t, newNode);  // Failure is OK.
                    return true;
                }
            }
            else if (p == q)//出队时自引用需要重新找到head节点
                //!=不是原子操作，在执行!=时，会先取得t的值，再执行t = tail，并取得新的t的值，然后比较这两个值是否相等。多线程情况出现t！=t
                p = (t != (t = tail)) ? t : head;
            else//寻找尾节点
                p = (p != t && t != (t = tail)) ? t : q;
        }
    }
```



#### 出队 `poll`方法

![](http://dzou.wangminwei.top/static/images/concurrent/8.png)

出队流程：

- 出队元素1节点(head节点元素为空不用操作)，此时p=h=head，此时item为空，p next不为空，p！=q，所以执行第四个条件语句让q指向p也就是让p指向元素1节点，循环，此时item不为空尝试CAS设置item为空，成功后此时p！=h，需要更新头结点位置指向元素2节点。
- 出队元素2节点，p=h=元素2节点，item不为空尝试设置item为null，成功后p=h不更新头结点位置。
- 出队元素3节点，此时head指向空的元素2节点，执行第四个条件语句，让p指向元素4节点，循环与元素1出队一样。
- 当出队到最后一个元素4节点时，走入第一个条件语句，当出现CAS设置item为空失败时，代表有其他线程在设置该item，走入第三个条件语句，只需要更新头结点，返回空，因为被其他线程出队了。

> 条件语句3是在多线程中，p=q出现自引用，需要跳到外层循环restartFromHead，重新获取当前队列队头 head

```java
public E poll() {
        restartFromHead:
        for (;;) {
            for (Node<E> h = head, p = h, q;;) {
                E item = p.item;
                // 如果p节点的元素不为null，则通过CAS来设置p节点引用的元素为null
                if (item != null && p.casItem(item, null)) {//条件语句1
                        updateHead(h, ((q = p.next) != null) ? q : p);
                    return item;
                }
                //多线程竞争出队失败线程来到这条件语句中
                else if ((q = p.next) == null) {//条件语句2
                    updateHead(h, p);
                    return null;
                }
                //自引用需要从头结点重新开始
                else if (p == q)//条件语句3
                    continue restartFromHead;
                else//条件语句4
                    p = q;
            }
        }
    }
```



#### 重要总结：

进行CAS更新head或者tail节点的位置的操作是需要耗费时间和资源的，减少CAS次数可以提高入队出队效率，所以有了：

- tail节点不总是尾节点，只有在插入节点时，tail后节点不为空才需要更新节点，也就是相隔一个节点更新一次tail
- head节点不总是头结点，只有当出队节点时，当前head节点元素为空时才需要更新head，也是相隔一个节点更新一次head

参考博文：

https://www.cnblogs.com/huangjuncong/p/9196240.html

https://blog.csdn.net/qq_38293564/article/details/80798310



### 阻塞队列

![](http://dzou.wangminwei.top/static/images/concurrent/9.png)

阻塞队列：

- 入队方法：当队列满了，阻塞插入线程，等待不为满。
- 出队方法：当队列空了，阻塞出队线程，等待不为空。

可实现生产者消费者

> 猜测：类似使用wait/notify或者condition，Condition更高效，猜测使用Condition实现

**会阻塞的方法**：插入`put`，移除`take`

其他方法：

- **add**        增加                     如果队列已满，则抛出一个IIIegaISlabEepeplian异常
- **remove**   移除并返回队列头部的元素    如果队列为空，则抛出一个NoSuchElementException异常
- **element**  返回队列头部的元素             如果队列为空，则抛出一个NoSuchElementException异常
- **offer**       添加一个元素并返回true       如果队列已满，则返回false
- **poll**         移除并返问队列头部的元素    如果队列为空，则返回null
- **peek**       返回队列头部的元素             如果队列为空，则返回null

JDK提供以下阻塞队列：

1. `ArrayBlockingQueue`：数组有界阻塞队列
2. `LinkedBlockingQueued`：链表有界阻塞队列
3. `PriorityBlockingQueue`：支持优先级排序的无界阻塞队列
4. `DelayQueue`：延时获取元素的无界阻塞队列
5. `SynchronousQueue`：不存储元素的阻塞队列
6. `LinkedTransferQueue`：链表无界阻塞队列
7. `LinkedBlockingQueue`：链表双向阻塞队列

> 无界阻塞队列，队列永远不为满，使用put或者offer永远不阻塞，且使用offer永远返回true

#### ArrayBlockingQueue

继承`AbstractQueue`，实现`BlockingQueue`，可在构造方法传入是否`公平`，保证公平吞吐量小

**数组构成的有界阻塞队列**

使用：`生产者-消费者模型` 模拟面包店生产面包并出售

- BreadShop

```java
/**
 * 面包商店
 */
public class BreadShop {
    //最多30个面包
    private static final int MAX_SIZE_BREAD = 30;
    private ArrayBlockingQueue<Bread> queue;
    public BreadShop(){
        queue = new ArrayBlockingQueue<Bread>(MAX_SIZE_BREAD);
    }
    //消费
    public void purchaseByConsumer() throws InterruptedException {
        Bread bread = queue.take();
        System.out.println(Thread.currentThread().getName()+":买到到一个面包");
        Thread.sleep(1000);
    }
    //生产
    public void getFromProducer() throws InterruptedException {
        queue.put(new Bread());
        System.out.println(Thread.currentThread().getName()+":生产者生产一个面包");
        Thread.sleep(1000);
    }
    public int getSize(){
        return queue.size();
    }
}
```

- 生产者线程

```java
/**
 * 面包生产者
 */
public class BreadProducer implements Runnable{
    private BreadShop shop;
    public BreadProducer(BreadShop shop){
        this.shop = shop;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(2000);
                shop.getFromProducer();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

- 消费者线程

```java
/**
 * 面包消费者
 */
public class BreadConsumer implements Runnable{
    private BreadShop shop;

    public BreadConsumer(BreadShop shop){
        this.shop = shop;
    }
    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(2000);
                shop.purchaseByConsumer();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

- 测试

```java
public static void main(String[] args) {
        BreadShop shop = new BreadShop();
        System.out.println("目前有"+shop.getSize()+"片面包。");
        BreadProducer producer = new BreadProducer(shop);
        BreadConsumer consumer = new BreadConsumer(shop);
        new Thread(consumer).start();
        new Thread(consumer).start();
        new Thread(producer).start();
        new Thread(consumer).start();
        new Thread(consumer).start();
    }

输出
目前有0片面包。
Thread-2:生产者生产一个面包
Thread-0:买到到一个面包
Thread-2:生产者生产一个面包
Thread-1:买到到一个面包
Thread-2:生产者生产一个面包
Thread-3:买到到一个面包
----------------------
```



源码分析：

> 使用Condition+ReentrantLock实现

```java
    private final Condition notEmpty;//等待获取的Condition
    private final Condition notFull;//等待放入的Condition
```

- 阻塞入队方法 put

```java
public void put(E e) throws InterruptedException {
        checkNotNull(e);//检查e不为空
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();//可被中断
        try {
            while (count == items.length)//如果满了，放入的线程阻塞
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }
```

- enqueue

```java
private void enqueue(E x) {
        final Object[] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length)
            putIndex = 0;
        count++;
        notEmpty.signal();//唤醒等待获取的线程
    }
```

- 阻塞出队方法 take

```java
public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0)
                notEmpty.await();//如果空了，获取的线程阻塞
            return dequeue();
        } finally {
            lock.unlock();
        }
    }
```

- dequeue

```java
private E dequeue() {
        final Object[] items = this.items;
        E x = (E) items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length)
            takeIndex = 0;
        count--;
        if (itrs != null)
            itrs.elementDequeued();
        notFull.signal();//唤醒等待放入的线程
        return x;
    }
```



#### LinkedBlockingQueue

**用链表实现的阻塞队列，初始化可以传入容量，默认Integer.MAX_VALUE，内部使用count计数**

```java
private final AtomicInteger count = new AtomicInteger();
```

> 维护了两把锁，`take`一把，`put`一把，也就是读写各一把，take在队列头操作，put在队尾操作,互不影响，可以带来`更高的效率和吞吐量`

```java
/** Lock held by take, poll, etc */
    private final ReentrantLock takeLock = new ReentrantLock();

    /** Lock held by put, offer, etc */
    private final ReentrantLock putLock = new ReentrantLock();
```

再需要同事用到两把锁时，规定了两把锁加锁顺序，防止死锁出现：

```java
void fullyLock() {//加锁
        putLock.lock();//先写锁
        takeLock.lock();//后读锁
    }
    void fullyUnlock() {//释放
        takeLock.unlock();//先释放读锁
        putLock.unlock();//在释放写锁
    }
```

内存爆满：不指定容量，队列过长时将达到Integer.MAX_VALUE，内存将出现问题



#### ArrayBlockingQueue能使用双锁吗？

> 问题来了，既然LinkedBlockingQueue的双锁带来更高效率，那么ArrayBlockingQueue能否使用双锁？

答案是可以的，并且可以带来更高的效率，至于为什么作者不使用，可能也考虑到count改为AtomicInteger使用CAS带来一定的影响吧。但是效率肯定时可以提高的。



#### SynchronousQueue

一对一数据传输

**一个没有数据缓冲(存储数据的容器)的BlockingQueue，它的`put`方法会阻塞，等待`take`方法的调用。**所以无法获取数据的状态。

> 可以理解为：生产者生产东西后拿着等着消费者来取，消费者取走后一同离开。

可在构造方法传入是否公平，默认非公平

##### 使用：

也使用面包店例子，修改一下`BreadShop`

```java
/**
 * 面包商店
 */
public class BreadShop {
    private SynchronousQueue<Bread> queue;
    public BreadShop(){
        queue = new SynchronousQueue<>();
    }
    public void purchaseByConsumer() throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+":买到到一个面包"+queue.take());
        Thread.sleep(1000);
    }
    public void getFromProducer() throws InterruptedException {
        System.out.println(Thread.currentThread().getName()+":生产者正在生产面包");
        queue.put(new Bread());
        System.out.println(Thread.currentThread().getName()+":生产者生产完成一个面包");
        Thread.sleep(1000);
    }
}
```

- 测试

```java
public static void main(String[] args) {
        BreadShop shop = new BreadShop();
        BreadProducer producer = new BreadProducer(shop);
        BreadConsumer consumer = new BreadConsumer(shop);
        new Thread(producer).start();
        new Thread(consumer).start();
    }

输出：
Thread-0:生产者正在生产面包
Thread-0:生产者生产完成一个面包
Thread-1:买到到一个面包com.ding.java_basis.concurrent.blocking_queue.synchronous_queue.producer_consumer.Bread@744261db
Thread-0:生产者正在生产面包
Thread-0:生产者生产完成一个面包
Thread-1:买到到一个面包com.ding.java_basis.concurrent.blocking_queue.synchronous_queue.producer_consumer.Bread@7a858123
Thread-0:生产者正在生产面包
Thread-0:生产者生产完成一个面包
Thread-1:买到到一个面包com.ding.java_basis.concurrent.blocking_queue.synchronous_queue.producer_consumer.Bread@7eba0aab
-----------------------------
```

##### 线程池中的使用

- Executors中获取CacheThreadPool方法就使用SynchronousQueue

```java
public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
```



**实现原理：**

- 阻塞算法实现通常在内部采用一个锁来保证多个线程中的put()和take()方法是串行执行的。采用锁的开销是比较大的，还会存在一种情况是线程A持有线程B需要的锁，B必须一直等待A释放锁，即使A可能一段时间内因为B的优先级比较高而得不到时间片运行。所以在高性能的应用中我们常常希望规避锁的使用。
- `SynchronousQueue`使用一种`无锁机制`，竞争机制支持公平和非公平两种：非公平竞争模式使用的数据结构是后进先出栈(Lifo Stack)；公平竞争模式则使用先进先出队列（Fifo Queue），性能上两者是相当的，一般情况下，Fifo通常可以支持更大的吞吐量。内部使用`CAS`而非锁。

维护着`TransferStack(非公平)`和`TransferQueue(公平)`，里面只有一个`transfer`方法

```java
abstract E transfer(E e, boolean timed, long nanos);
```

- 调用put时传入数据e
- 调用take时传入e为null

**非公平 Stack** 后来线程的将先被获取

维护了三个常量

```java
        static final int REQUEST    = 0;//代表消费者
        static final int DATA       = 1;//代表生产者
        static final int FULFILLING = 2;//匹配消费者或者生产者的状态
```

```java
static final class SNode {
            volatile SNode next;        // next node in stack
            volatile SNode match;       // 匹配的线程节点
            volatile Thread waiter;     // 需要等待唤醒的线程
            Object item;                // 数据
            int mode;//状态，也就是上面三种状态之一
```

**阻塞操作：阻塞是代价非常大的操作，要保存当前线程的很多数据，并且要切换上下文，等线程解释阻塞的时候还有切换回来。**

**自旋操作：阻塞前会自旋一小段时间，如果有take线程进来就停止自旋，直接获取就比直接阻塞更加高效。**

**实现流程：**

> 第一个`put`线程进来，如果没有等待时间，将`自旋`一小段时间检查是否有`take`线程匹配，没有将被`UNSAFE.park`阻塞，如果此时第二个`put`线程进来，也将执行相应的操作，但是该线程会被放在第一个线程的前面(Stack栈)，之后，一个`take`线程进来了入栈顶，它将匹配栈顶往下的第一个put线程，拿到第二个put2线程的结果，然后该take线程和put2线程出栈清空，head指向第一个put线程；最后来了take2线程，匹配了put1线程，拿取put1线程的结果，然后它俩出栈，栈为空，head指向null。



**公平 Queue 先进的先出去执行**

插入节点主要流程如下代码：

```java
for(;;){
    1.判断head和tail是否为空，为空继续循环
    2.判断head=tail或者与前一个节点模式相同(同为put、take)，则维护队列，进行自旋、阻塞更新尾节点，多线程更改了tail重新循环
    3.否则就是与前一个不同模式操作，既有take又有put，此时进行匹配，从头结点开始，匹配到第一个put并唤醒获取put结果
}
```



参考博文：https://blog.csdn.net/yanyan19880509/article/details/52562039



#### 双向队列

> 双向队列可用于`工作窃取算法`，ForkJoin



## 十七.线程池

优点：

- 降低资源消耗，重复利用已创建的线程，而无需一直创建
- 无需关心线程的管理和执行，只需要提交任务
- 提高响应速度，任务的执行不需要等待创建线程

### 创建线程池：

#### ThreadPoolExecutor

```java
public ThreadPoolExecutor(int corePoolSize,//线程池基本大小，没有那么多任务也会创建的线程数量
                              int maximumPoolSize,//线程最大数量，优先使用队列，当队列满了以后使用该参数创建线程，无界队列该参数无用
                              long keepAliveTime,//线程活动存活时间
                              TimeUnit unit,//存活时间单位
                              BlockingQueue<Runnable> workQueue，//阻塞队列
                              ThreadFactory threadFactory，//创建线程的工厂
                              RejectedExecutionHandler handler)//饱和策略
```

其中`RejectedExecutionHandler`和`ThreadFactory`是可选

- `RejectedExecutionHandler`是一种`饱和策略`，当线程池满了，必须使用其他策略处理提交的新任务，可以是`丢弃任务`，或者`调用main线程执行`
- `ThreadFactory`代表创建线程使用的工厂

#### Executors

```java
//线程池 FixedThreadPool 固定容量线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
//CachedThreadPool 根据内存创建 不够就创建
        ExecutorService threadPool = Executors.newCachedThreadPool();
//带延时的线程池ScheduledThreadPool
		ExecutorService threadPool = Executors.newScheduledThreadPool();
//ForkJoin使用的线程池(工作窃取算法)WorkStealingPool
		ExecutorService threadPool = Executors.newWorkStealingPool();
```

> `ScheduledThreadPool`内部使用`DelayedWorkQueue`延时阻塞队列，在`schedule`方法设置延时时间并执行任务返回`ScheduledFuture`

##### WorkStealingPool

使用ForkJoinPool，内部实现是`工作窃取算法`，详见ForkJoin那节

```java
public static ExecutorService newWorkStealingPool(int parallelism) {
        return new ForkJoinPool
            (parallelism,
             ForkJoinPool.defaultForkJoinWorkerThreadFactory,
             null, true);
    }
```



#### 线程池状态

| 状态       | 解释                                                         |
| ---------- | ------------------------------------------------------------ |
| RUNNING    | 运行态，可处理新任务并执行队列中的任务                       |
| SHUTDOW    | 关闭态，不接受新任务，但处理队列中的任务                     |
| STOP       | 停止态，不接受新任务，不处理队列中任务，且打断运行中任务     |
| TIDYING    | 整理态，所有任务已经结束，workerCount = 0 ，将执行terminated()方法 |
| TERMINATED | 结束态，terminated() 方法已完成                              |

```java
//其中AtomicInteger变量ctl的功能非常强大：利用低29位表示线程池中线程数，通过高3位表示线程池的运行状态
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
//状态存储在高三位
    private static final int RUNNING    = -1 << COUNT_BITS;//111
    private static final int SHUTDOWN   =  0 << COUNT_BITS;//000
    private static final int STOP       =  1 << COUNT_BITS;//001
    private static final int TIDYING    =  2 << COUNT_BITS;//010
    private static final int TERMINATED =  3 << COUNT_BITS;//011
```

![](http://dzou.wangminwei.top/static/images/concurrent/10.png)



#### 

