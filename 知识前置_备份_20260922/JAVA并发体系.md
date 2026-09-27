## 并发

并发就是多个线程对同一个目标同时工作
JMM内存模型 ： 多线程并发下的内存访问抽象模型，不是真实内存分区，多线程读写共享变量的一套行为准则，解决并发 bug。

## JAVA内存模型(JMM)

### 线程通信机制

 机制包括 ： 内存共享和消息传递模型

 共享内存 ： 多线程共享一份数据（主内存），其他线程通过共同读写这份数据来通信。                 
 消息传递 ： 线程之间没有共享数据，通过发送消息来通信（比如 wait/notify、BlockingQueue）

 Java 并发主要依赖共享内存模型，但并发工具类（如 BlockingQueue）也模拟了“消息传递”的效果，用于线程间协作。

 #### 共享内存的机制
```
    线程 A 工作内存 ←→ 主内存 ←→ 线程 B 工作内存
    线程 A 修改共享变量，写入本地缓存，然后刷新到主内存
    线程 B 从主内存中读取最新值
```
为了保证正确性，JMM 提供了三个关键机制： 可见性（volatile）、原子性（synchronized）、有序性（happens-before）。

#### 消息传递模型
```
    线程 A 生成消息 -> 线程 B 消费消息
    虽然是直接生产与消费，但是本质上与Kafka一样，通过队列传递信息，信息多时会导致堵塞
```
Java 中的 wait/notify 属于更底层的消息传递机制，BlockingQueue 是其高级封装。


## 内存模型

### 重排序（没用）

目的是为了程序的性能，处理器，编译器都会对程序进行重排序处理。在不改变单线程执行结果的前提下，调整指令执行顺序的一种行为。
就是说 程序是这么些，但是JVM 和 CPU 不是按这个顺序运行。

条件 ： 
        单线线程环境下不能改变程序运行的结果
        存在数据依赖关系的不允许重排序

重排序发生在哪几个阶段？                                
                 
编译器重排序	            
          Java 编译器在编译 .java 到 .class 时，调整没有数据依赖的指令顺序                
处理器重排序	            
          CPU 在执行机器码时，会根据缓存情况动态调整指令顺序              
内存系统重排序	            
         写缓存区（Store Buffer）导致写操作看起来是乱序的         

但是弊端就是单线程不会出现错误，多线程就会出现错误，导致数据不一致。

volatile 和 synchronized 可以禁止重排序，保证程序在多线程环境下的正确性。所以我们要将一些必要的数据设置可见。
       
## 顺序一致性
这是 多线程环境下的理论参考模型
     为程序提供了极强的内存可见性保证

### 特性
```
    一个线程中的所有操作必须按照程序的顺序来执行

    所有线程都只能看到一个单一的操作执行顺序，不管程序是否同步

    每个操作都必须原子执行且立刻对所有线程可见
```
但是顺序一致性模型性能太低，现代 JVM 不会采用。JVM会根据JMM所定制的规则有效执行

## CAS （Compare And Swqp 乐观锁） 比较与交换
    这是一个无锁的原子操作，用于在多线程环境下安全的更新共享变量

CAS 的三个参数
```
    内存位置  V  是要修改变量的地址
    预期值    A  读取到的旧值
    新值      B  写入的新值
```
执行逻辑
```
    如果 V 的当前值 == A：
    把 V 更新为 B
否则：
    什么都不做，返回 false（让调用方重试）
```
    总结 ： 先检查当前值是否和预期值一致，如果一致就更新，否则重试。

CAS 的两个问题
```
    ABA 问题（自旋问题）
        线程 T1 读到值 A
        线程 T2 把 A 改成 B，再改回 A
        线程 T1 再次检查，发现还是 A，认为“没变过”，于是更新
    解决 ： 使用原子操作 AtomicStampedReference 或 AtomicMarkableReference，通过版本号判断值是否被修改过。（Stramped 标记的，带版本号的）
```
```
    自旋开销：
        如果竞争激烈就会反复重试 CAS ，消耗 CPU     
    解决 ： 减少 CAS 操作，用 synchronized 或 ReentrantLock 进行锁操作。        
```
    
## happens-before 
    这是JMM内存体系中最核心的理论，保证内存的可见性。
    在JMM中，如果一个操作执行的结果需要对另一个操作可见，那么两个操作之间必须存在happens-before关系

### 理论：
    如果一个操作happens-before另一个操作，那么第一个操作的执行结果将对第二个操作可见，而且第一个操作的执行顺序排在第二个操作之前。
    那么就是 ： 如果操作 A happens-before 操作 B，那么 A 的结果对 B 是可见的。

### 程序顺序规则
    同一个线程中，前面的操作hapens-before后面的操作。
    int a = 1;      // A
    int b = a + 1;  // B
// A happens-before B，a 的新值对 b 可见

### 锁规则
    对锁的解锁 happens-before 后续对同一把锁的加锁。
    synchronized (lock) {
        value = 10;   // 解锁前写入
    }
    // 解锁 happens-before 后续加锁
    synchronized (lock) {
        System.out.println(value); // 一定能看到 10
    }

### 线程启动规则
    Thread.start() happens-before 新线程中的任何操作。

### 线程终止规则
    线程中的任何操作 happens-before Thread.join() 返回。

### 传递性规则
    如果 A happens-before B，B happens-before C，则 A happens-before C。

总结：以上不用记住，看懂就行，记住这一句就行
    happens-before 是 JMM 用来保证可见性的一组规则。它规定一个操作的结果在什么情况下对另一个操作可见，比如程序顺序规则、volatile 规则、锁规则等。这组规则是 JMM 的核心，程序员不需要了解底层实现细节，只需要正确使用 volatile 和 synchronized，就能保证可见性。

## synchronized 锁
    synchronized 是由 JVM 实现的一种互斥同步方式，被 synchronized 修饰的代码块，编译器会在前后生成 monitorenter 和 monitorexit 两条字节码指令。

### synchronized 做了两件事
很多人以为 synchronized 只做"锁住代码不让人进来"，实际上它同时干了两件事：

| 作用 | 说明 |
|------|------|
| **互斥（原子性）** | 同一时刻只有一个线程能进入同步块 |
| **可见性** | **解锁前**把工作内存中的所有修改**刷回主内存**；**加锁时**清空工作内存，**从主内存重新读取**所有变量 |

### monitorenter 和 monitorexit 的工作细节
```
monitorenter（加锁）：
    ① 清空当前线程的工作内存（所有变量失效）
    ② 从主内存重新读取共享变量到工作内存
    ③ 尝试获取对象的锁（修改对象头的 Mark Word）
        ├── 成功 → 锁计数器 +1，进入同步块
        └── 失败 → 阻塞/自旋等待，直到锁被释放

monitorexit（解锁）：
    ① 将工作内存中的所有修改刷新到主内存
    ② 释放对象的锁（还原对象头的 Mark Word），锁计数器 -1
    ③ 唤醒等待队列中的其他线程
```
    总结 ： monitorenter 承担"清空缓存 + 获取锁"双重任务，monitorexit 承担"刷新内存 + 释放锁"双重任务。这就是 synchronized 能保证互斥 + 可见性的底层原因。

### 可重入性
    synchronized 是可重入锁。同一个线程可以多次获取同一把锁，锁计数器会累加。每执行一次 monitorexit，计数器 -1，直到计数器归零才真正释放锁。
    例如递归调用 synchronized 方法时不会把自己锁死。
```
public synchronized void methodA() {
    methodB();            // 同一个线程，可以直接进去
}

public synchronized void methodB() {
    // 不需要重新竞争锁，直接进入
}
```

### 锁原理
    就是将一个对象锁住，只有持有锁的线程才能进行读写，保证数据是最新的

### 锁升级过程（本质就是 JVM 对JAVA原生锁的优化）
    无锁 → 偏向锁                →                轻量级锁                     →                             重量级锁
        (只有一个线程)   （第二个线程来争抢）  （少量竞争，用自旋） （自旋次数超过一定次数或者线程过多）    （大量竞争，操作系统介入）
          
偏向锁	            只有一个线程频繁获取锁（在对象头里标记线程ID，不加锁）              
轻量级锁（自旋）	少量线程竞争，持有锁时间短（用CAS自旋等锁，不阻塞线程）              
重量级锁	        多线程竞争激烈，持有锁时间长（挂起线程，阻塞时间长）        
当没有竞争出现时就是默认使用偏向锁    

> ⚠️ **偏向锁已废弃**：JDK 15 开始默认禁用偏向锁（`-XX：-UseBiasedLocking`），JDK 21 中已彻底移除偏向锁代码。原因是偏向锁的撤销逻辑（当有线程竞争时需要撤销偏向）带来的性能开销反而大于收益，尤其在高并发场景下。现在 synchronized 的锁升级路径为：**无锁 → 轻量级锁（CAS 自旋）→ 重量级锁**。面试问到偏向锁时建议提一句"JDK 15+ 已废弃"。    
```
自旋锁的原理（也是轻量级锁的原理）
    线程 A 持有锁
        │
        ▼
    线程 B 尝试获取锁
        │
        ▼
    锁被占用，但线程 B 不放弃 CPU
        │
        ▼
    在线程 B 的 CPU 时间片内循环等待（自旋）
        │
        ▼
    线程 A 释放锁
        │
        ▼
    线程 B 立刻获取锁，继续执行
```
### 与 ReentrantLock（可重入锁） 的区别（面试必问）
    “Reentrant” = 可重入，意思是同一个线程可以多次获取同一把锁，不会自己把自己锁死。
    ReentrantLock 是 java.util.concurrent.locks 包下的可重入互斥锁，功能与性能和 synchronized 类似，但更灵活。它支持尝试获取锁（tryLock）、超时获取、可中断获取和公平锁。使用时需要手动加锁和解锁，通常把 unlock() 放在 finally 块中，确保锁一定能释放。

    区别 ： 两者都是互斥锁，主要区别有三点：ReentrantLock 需要手动释放锁；ReentrantLock 支持可中断和超时获取锁；ReentrantLock 可以选择公平锁。面试时还会遇到追问，比如 tryLock 的应用场景（如避免死锁），以及公平锁的性能开销问题

### 锁对象
```
    普通同步方法，锁是当前实例对象
    public synchronized void method1() {
        // 锁的是当前实例对象（this）
    }

    静态同步方法，锁是当前类的class对象
    public static synchronized void method2() {
        // 锁的是当前类的 Class 对象
    }

    同步方法块，锁是括号里面的对象
    synchronized (obj) {
        // 锁的是 obj 对象
    }
```
    总结 ： 锁的本质是 monitorexit 和 monitorenter 字节码指令中的一个 Reference 类型的参数，即要锁定和解锁的对象，synchronized 所修饰的代码块就是锁的对象，可以是普通实例，可以是类，也可以是方法。

## volatile规则
    它的本质是告诉 JVM：这个变量很特殊，每次读写都必须从主内存操作，不能使用 CPU 缓存。
    是一个变量修饰符，用来标记“这个变量需要在多线程间共享可见”。

### 保证看见
        线程修改 volatile 变量后，立即刷新到主内存，其他线程立即看到最新值；如果其他线程修改，主线程会立刻感知。
    适用场景 ： 状态的标记（开关控制）

### 不保证原子性
    volatile 只保证单次读/写的原子性（如 flag = true），不保证复合操作的原子性（如 i++）
    复合操作需要用 synchronized 或 AtomicInteger（加锁保证）

### 禁止指令重排序（重排序是 JVM为了节省CPU而不按程序顺序进行运行）
通过内存屏障（Memory Barrier）实现      
```    
    写屏障：写入 volatile 后，所有之前的普通写操作已同步到主内存,确保写入时对其他线程可变。            
    读屏障：读取 volatile 后，之后的所有读操作从主内存读取最新值，让屏幕后的所有普通读操作直接从主内存读取最新值。
```

### 实现机制
```
    缓存一致性协议（MESI）：保证多核 CPU 缓存数据一致（虽然不读写缓存，但是会将变量暂时存入CPU缓存中）
    总线锁：早期方案，锁粒度大，性能低，已优化为缓存锁
```

```
    volatile boolean flag = false;
    // 线程 A
    value = 10;     // 1
    flag = true;    // 2 (volatile 写)

    // 线程 B
    if (flag) {     // 3 (volatile 读)
        System.out.println(value); // 一定能看到 10
    }
    // 2 happens-before 3，所以 value = 10 对线程 B 可见
```
### 与synchronized的区别
```
    volatile 保证可见性和禁止重排序，但不保证原子性。适合一个线程写、多个线程读的状态标记场景。底层通过内存屏障和缓存一致性协议实现。i++ 这种复合操作不能用 volatile，需要用 synchronized 或 AtomicInteger

    volatile 不能替代 synchronized，因为它只能保证可见性，不能保证原子性。多个线程同时执行 count++ （复合操作）时，还是需要锁来保证原子性。
```

## DCL(双重检查锁定)
    单例模式（Singleton） ： 一个类在整个 JVM 中只有一个实例，提供一个全局访问点，资源与数据共享。
    DCL 是一种“懒加载 + 线程安全”的单例实现方式。它通过两次检查单例模式的变量 instance == null 来减少锁的竞争。
    所以 DCL 保证了单例模式的正常建立，确保共享资源只被创建一次。
### 两种实现方式
饿汉式 
```
    private static final Singleton INSTANCE = new Singleton();

    优点 ： 简单，线程安全
    缺点 ： 类加载就创建，可能会浪费内存
```
懒汉式（DCL）
```
    双重检查锁定 + volatile

    优点 ： 延迟加载，线程安全
    缺点 ： 写法复杂

    DCL 是“懒加载 + 线程安全”的一种实现方案，适用于“单例对象创建开销大，且只在需要时才初始化”的场景。
```
问：单例模式的双重检查锁定中，为什么要加 volatile？
```
    “instance = new Singleton() 不是原子操作，可能发生指令重排序，导致其他线程拿到未初始化完整的对象。volatile 禁止重排序，保证对象完全初始化后才被其他线程可见"。
```
### DCL的具体实现
DCL = 性能优化（第一次检查） + 线程安全（第二次检查） + 避免半成品（volatile）
```
public class Singleton {
    private static volatile Singleton instance;

    private Singleton() {} // 构造方法初始化，目的是禁止其他类初始化创造对象

    // 只有第一次被调用的时候才会被加载
    public static Singleton getInstance() { //getInstance()是获取类的唯一实例对象，典型用于单例模式，代替 new 类名() 去创建对象。只能调用这个方法获取对象
        if (instance == null) {               // 第一次检查（无锁）避免每次调用都加锁，提高性能
            synchronized (Singleton.class) {
                if (instance == null) {       // 第二次检查（有锁）防止多个线程同时通过第一次检查后重复创建实例
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

## AQS的核心作用（AbstractQueuedSynchronizer 抽象队列同步器）

 AQS 是JUC包中实现锁和同步器的底层框架。ReentrantLock、CountDownLatch、Semaphore 等并发工具都是基于 AQS 构建的。
 JUC包是 ：java.util.concurrent 并发工具包，专门解决并发问题的包，包含原子类，Lock锁接口（重入锁），并发容器。

理解 ： AQS 提供了一个“排队 + 状态管理”的模板，具体锁的实现只需要告诉 AQS“怎么判断能不能获取锁”，剩下的排队和唤醒都由 AQS 完成。

### AQS 的三大部件
    同步状态（state）  一个 volatile int 变量，表示锁是否被占用（0 = 空闲， 1 = 占用）
    CLH 队列           FIFO双向队列，存放等待获取锁的线程
    CAS 操作           原子更新state，保证并发安全

### AQS的两种模式
    独占模式   适用于：只有一个线程可以获得锁   ReentrantLock
    共享模式   适用于：多个线程可以获得锁       CountDownLatch、Semaphore

### AQS的阻塞与唤醒
     AQS 用 CLH 双向队列管理等待线程，获取锁失败时线程加入队列并阻塞，释放锁时唤醒队列中的下一个线程。阻塞和唤醒通过 LockSupport 的 park() 和 unpark() 实现。独占模式用 acquire 获取、release 释放，共享模式用 acquireShared 和 releaseShared。

### AQS 的工作流程
```
        线程调用 lock()
            ↓
        尝试获取同步状态（state）
            ↓
        state == 0 ？ → 是 → 获取锁成功，将 state 设置为 1
            ↓ 否
        加入 CLH 队列尾部，阻塞等待
            ↓
        持有锁的线程调用 unlock()
            ↓
        释放锁，state = 0
            ↓
        唤醒 CLH 队列中的头节点线程
            ↓
        被唤醒的线程尝试获取锁
```
```
总结 : 
    AQS 是 AbstractQueuedSynchronizer 的缩写，是 JUC 包中锁和同步器的基础框架。它通过一个 volatile int 类型的 state 表示同步状态，通过 CLH 双向队列管理等待线程，通过 CAS 保证状态更新的原子性。ReentrantLock、CountDownLatch 等并发工具都是基于 AQS 实现的。
    AQS 的核心 = state（状态）+ CLH 队列（排队）+ LockSupport（阻塞/唤醒）。独占模式用 acquire/release，共享模式用 acquireShared/releaseShared。

**一句话总结 AQS 的具体作用**：AQS 为 JUC 包中的锁和同步器提供了统一的底层模板 —— 锁的"能否获取锁"的判断逻辑交给子类实现，而"获取不到锁时线程如何排队、如何阻塞、如何唤醒"这一套通用流程全部由 AQS 封装完成，避免了每个同步器都要重新实现线程排队与唤醒机制的重复劳动。
AQS 干的事就三样：帮你试拿锁 → 拿不到帮你排队 → 锁释放了帮你叫下一个来拿

个人总结： AQS 就是一个再JUC（Java.util.current）包中的一个关于锁和框架的底层模板，很多工具都是基于这个模板产生的；它通过 volatile int 来同步线程状态，然后通过 CLH 双向队列管理哪些访问锁的线程，如果获取锁就下一个，如果没有获取锁就存在队列中，当锁被释放时，就会唤醒队列中的线程来获取锁。
```

### 并发三大锁

#### ReentrantLock(可重入锁)
    与 synchronized 锁不同的是，ReentrantLock 锁是一个手动操控的锁，相对于说更灵活
特性：
```
    可重入性                    同一个线程可以多次获取同一把锁         不会导致死锁
    公平/非公平锁               公平锁排队等候，非公平锁插队获取       非公平性性能更高，公平性防止饥饿
    底层实现                    基于AQS                             通过内部的 Sync 继承AQS实现 
    与 synchronized 的区别      手动加锁/解锁，可中断超时             synchronized 自动释放，ReentrantLock 手动释放
```


#### ReentrantReadWriteLock（读写锁）
```
    读锁（共享锁）      多个线程可以同时读       多个线程读的时候不阻塞                     
    写锁（排他锁）      只有一个线程能写         写的时候，其他线程不能读也不能写           
    锁降级              写锁 → 读锁             先获取写锁，再获取读锁，然后释放写锁
```
ReentrantReadWriteLock 有两把锁：读锁是共享锁，多线程可以同时读；写锁是排他锁，写的时候独占资源。它还支持锁降级，即写锁可以降级为读锁。

#### Condition（条件队列）
Condition 是 `wait/notify` 的现代化替代方案，解决了传统方式"唤醒精度不够"的问题。

##### wait/notify 的缺陷
传统 `synchronized` 搭配 `wait/notify` 时，每个对象只有**一个隐式等待队列**：
```java
synchronized (lock) {
    while (队列满) wait();  // 生产者等待
    生产();
    notifyAll();            // ❌ 唤醒所有线程，可能叫醒其他生产者（白忙一场）
}
```
`notifyAll()` 不分青红皂白唤醒**所有**等待线程，可能导致"唤醒错人"——叫醒了不该叫的线程，后者发现条件不满足又继续 wait，浪费 CPU。

##### Condition 如何解决
Condition 把"一个大休息室"拆成"多个专门的休息室"，每个 Condition 拥有**独立的等待队列**，实现**精准唤醒**：

| | wait/notify | Condition |
|---|---|---|
| 等待队列 | 每个对象只有 **1 个**隐式队列 | 一个 Lock 可创建 **多个** Condition，各自独立队列 |
| 唤醒精度 | 随机唤醒一个 / 唤醒全部 | **精准唤醒**指定队列中的线程 |

##### 经典案例：仓库（有界缓冲区）
```java
Lock lock = new ReentrantLock();
Condition notFull  = lock.newCondition();   // "不满"休息室
Condition notEmpty = lock.newCondition();   // "不空"休息室

// 生产者线程
lock.lock();
try {
    while (仓库满了) notFull.await();   // → 去"不满"休息室等待
    生产();
    notEmpty.signal();                  // → 只叫醒"不空"休息室的消费者
} finally { lock.unlock(); }

// 消费者线程
lock.lock();
try {
    while (仓库空了) notEmpty.await();  // → 去"不空"休息室等待
    消费();
    notFull.signal();                   // → 只叫醒"不满"休息室的生产者
} finally { lock.unlock(); }
```
关键区别：`notEmpty.signal()` 只唤醒**在 notEmpty 上等待的消费者**，不会误唤醒生产者。做到了"产者叫消者，消者叫产者"，各找各妈，互不干扰。

个人总结：
```
    ReentrantLock 可重入锁就是一个，可以自定义锁的位置，什么时候取锁，什么时候释放锁，并且，可以在同一个线程拥有很多次锁，还可以有效避免死锁的问题，底层还是 AQS ， 而且具有公平与非公平性，可以自定义将某一个特殊的功能类进行特殊照顾，让其发挥最大功能。
    
    ReentrantReadWriteLock 读写锁就是一个具有多线程可读（共享锁），一个线程写（排他锁）功能的锁，一般用于能一起读，不能一起写的过程。

    ConditionLock（条件队列） 是一个用来替代传统 wait/notify 的功能，可以通过建立屏障，精准取出哪些是要先运行，哪些是后运行的线程。
```

##  并发工具类

### CountDownLatch（线程计数门）
核心作用： 一个线程等待N个线程完成任务后再执行   适用于主线程等待子线程完成
```
    核心方法 ：     countDown（减1），await（）等待计数为 0 
    计数不可重置    计数归零后无法再次使用
    底层实现	    共享锁（AQS）
    典型场景        主线程等待多个子任务加载完成后再启动
    CountDownLatch latch = new CountDownLatch(3);
    // 3 个子线程完成任务后分别调用 latch.countDown()
    // 主线程调用 latch.await() 等待计数归零
```

### CyclicBarrier（循环屏障）
核心作用：N 个线程互相等待，全部到达后一起放行  适用于分阶段运行、多线程同步执行
```
    核心方法：     await() 等待其他线程到达，全部到达后自动放行
    计数可重置     调用 reset() 可重置计数，循环使用（与 CountDownLatch 的关键区别）
    底层实现       ReentrantLock + Condition（AQS）
    典型场景       N 个线程分阶段执行，每个阶段都等全部线程完成后再进入下一阶段
    CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("全部到达！"));
    // 每个线程中调用 barrier.await() 等待
    // 当 3 个线程都到达后，屏障打开，所有线程继续执行
```

### Semaphore（信号量）
核心作用：控制同时访问共享资源的线程数    适用于限流、连接池控制
```
    核心方法：     acquire() 获取许可，release() 释放许可
    公平/非公平     构造时可指定是否公平，默认非公平
    底层实现       共享锁（AQS）
    典型场景       数据库连接池限流、接口限流
    Semaphore semaphore = new Semaphore(3);  // 最多允许 3 个线程同时访问
    semaphore.acquire();    // 获取许可，获取不到则阻塞
    // 访问共享资源...
    semaphore.release();    // 释放许可
```
Semaphore 是信号量，用来控制同时访问共享资源的线程数。比如连接池限制最大连接数，或接口限流


### Exchanger（交换器）
核心作用：两个线程互相交换数据  适用于双线程数据交换
```
    核心方法：     exchange(V x) 交换数据，等待另一个线程也到达交换点
    一对一         只能用于两个线程之间的数据交换
    底层实现       CAS + 自旋（AQS 机制）
    典型场景       两个线程之间数据交换，如一个线程生产数据、另一个线程消费数据
    Exchanger<String> exchanger = new Exchanger<>();
    // 线程 A：String data = exchanger.exchange("A的数据");
    // 线程 B：String data = exchanger.exchange("B的数据");
    // 双方同时到达 exchange() 时交换数据，然后继续执行
```

```
    个人总结 ： CountDownLatch（倒计时锁存器） ， 一个计数线程的工具，相当于阀门，当第N个线程结束后，再开始进行当线程，而且当计数完成后，CountDownLatch不能再调用，适用于子线程结束后主线程再启动。

```

## 原子类（Atomic）

原子类是基于 **CAS（乐观锁）** 实现的一套线程安全的数据类型。它们将共享变量的读写操作封装成**不可分割的原子操作**，不需要加 `synchronized` 就能保证线程安全，性能比锁更高。

### 为什么需要原子类？
```java
// ❌ 普通 int 在多线程下不安全
int count = 0;
count++;   // 不是原子操作：读→改→写三步，可能被其他线程打断

// ✅ AtomicInteger 保证原子性
AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet();  // 一步到位，CAS 保证线程安全
```

### 三个重点原子类

#### AtomicInteger —— 原子整数，用于计数器
```java
    核心方法：    incrementAndGet() 自增并返回     getAndIncrement() 返回再自增
                 addAndGet(n) 加 n 并返回         compareAndSet(old, new) 比较并交换
    底层实现：    CAS（Unsafe 类）
    适用场景：    多线程计数器、请求计数、序列号生成

    AtomicInteger count = new AtomicInteger(0);

    // 线程 A：count.incrementAndGet()  → 1
    // 线程 B：count.incrementAndGet()  → 2
    // 即使 100 个线程同时自增，结果也绝对正确，不用加锁
```

#### AtomicReference —— 原子更新对象引用
```java
    核心方法：    compareAndSet(old, new)  如果当前值是 old，就更新为 new
                 getAndSet(new)           设置为新值并返回旧值
    底层实现：    CAS
    适用场景：    多线程共享一个对象的引用，需要原子地替换

    AtomicReference<User> ref = new AtomicReference<>(userA);
    // 线程 1：期望当前是 userA，替换为 userB
    ref.compareAndSet(userA, userB);  // ✅ 成功
    ref.compareAndSet(userA, userC);  // ❌ 失败（已被改为 userB）
```

#### AtomicStampedReference —— 带版本号的原子引用，解决 ABA 问题
```java
    核心方法：    compareAndSet(old, new, oldStamp, newStamp)
                 只有引用值和版本号都匹配时，才更新
    底层实现：    CAS + 版本号（stamp）
    适用场景：    需要避免 ABA 问题的场景，如栈操作、链表操作

    为什么需要版本号？
    // 线程 T1 读到的值是 A（版本号 0）
    // 线程 T2 把 A→B（版本号 1），再把 B→A（版本号 2）
    // 线程 T1 再次检查：值是 A，但版本号已变成 2 ≠ 0 → 发现被改过！阻止更新

    AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);
    int[] stamp = new int[1];
    String value = ref.get(stamp);       // value = "A", stamp[0] = 0
    ref.compareAndSet("A", "B", stamp[0], stamp[0] + 1);  // 版本号 +1
```

### 总结
| 类 | 作用 | 解决什么问题 |
|---|---|---|
| `AtomicInteger` | 原子整数操作 | `i++` 非原子问题 |
| `AtomicReference` | 原子替换对象引用 | 多线程安全地替换共享对象 |
| `AtomicStampedReference` | 带版本号的原子引用 | CAS 的 ABA 问题 |


## 线程池

线程池是一种"复用线程"的管理机制。它预先创建一批线程，任务来了直接分配给空闲线程，避免频繁创建和销毁线程的开销。

### 为什么需要线程池？
```java
// ❌ 来一个任务就 new 一个线程，频繁创建/销毁开销巨大
new Thread(() -> { /* 任务 */ }).start();

// ✅ 线程池复用线程，任务来了直接分配，不用每次新建
executor.execute(() -> { /* 任务 */ });
```

### 七个核心参数（ThreadPoolExecutor 构造方法）

```java
public ThreadPoolExecutor(
    int corePoolSize,       // ① 核心线程数         —— 池中常驻的线程数量
    int maximumPoolSize,    // ② 最大线程数         —— 池中允许的最大线程数量
    long keepAliveTime,     // ③ 空闲线程存活时间    —— 超过核心线程数的线程空闲多久后被回收
    TimeUnit unit,          // ④ 存活时间单位        —— 秒、毫秒等
    BlockingQueue<Runnable> workQueue,  // ⑤ 任务队列 —— 核心线程满时，任务存哪里等待
    ThreadFactory threadFactory,        // ⑥ 线程工厂 —— 如何创建新线程（可自定义名称、优先级等）
    RejectedExecutionHandler handler    // ⑦ 拒绝策略 —— 任务太多无法处理时怎么办
);
```

| 参数 | 含义 | 说明 |
|------|------|------|
| **corePoolSize** | 核心线程数 | 即使空闲也不会被回收的线程数量 |
| **maximumPoolSize** | 最大线程数 | 线程池中最多能有多少个线程 |
| **keepAliveTime** | 空闲存活时间 | 超过核心数的线程空闲多久后被回收 |
| **unit** | 时间单位 | keepAliveTime 的时间单位 |
| **workQueue** | 任务队列 | 阻塞队列，存放等待执行的任务 |
| **threadFactory** | 线程工厂 | 创建新线程的工厂，可设置线程名、优先级等 |
| **handler** | 拒绝策略 | 线程池和队列都满时，如何处理新提交的任务 |

### 线程池的工作流程

```
        提交任务
            ↓
    ① 核心线程是否都在忙？
        ├── 否 → 创建核心线程执行任务
        └── 是 → 进入下一步
            ↓
    ② 任务队列是否已满？
        ├── 否 → 将任务放入队列等待
        └── 是 → 进入下一步
            ↓
    ③ 当前线程数 < maximumPoolSize？
        ├── 是 → 创建新线程（非核心）执行任务
        └── 否 → 执行拒绝策略
```

### 四种拒绝策略

| 策略 | 说明 |
|------|------|
| **AbortPolicy**（默认） | 直接抛出 RejectedExecutionException 异常 |
| **CallerRunsPolicy** | 由提交任务的线程自己去执行该任务（降速） |
| **DiscardPolicy** | 默默丢弃任务，不抛异常 |
| **DiscardOldestPolicy** | 丢弃队列中最旧的任务，然后重新提交新任务 |

### 常见的阻塞队列（workQueue）

| 队列 | 说明 | 特点 |
|------|------|------|
| **ArrayBlockingQueue** | 有界数组队列 | 容量固定，公平性可设置 |
| **LinkedBlockingQueue** | 有界/无界链表队列 | 默认 Integer.MAX_VALUE（近似无界） |
| **SynchronousQueue** | 同步移交队列 | 不存任务，直接交给线程处理 |
| **PriorityBlockingQueue** | 优先级队列 | 任务按优先级执行 |
| **DelayQueue** | 延迟队列 | 延迟时间到了才能被执行 |

### Executors 的四种常见线程池（实际开发不推荐，了解即可）

| 类型 | 核心数 | 最大数 | 队列 | 说明 | 风险 |
|------|--------|--------|------|------|------|
| **newCachedThreadPool** | 0 | Integer.MAX_VALUE | SynchronousQueue | 来任务就建线程 | ⚠️ 无限创建线程会 OOM |
| **newFixedThreadPool** | n | n | LinkedBlockingQueue | 固定线程数 | ⚠️ 队列可能无限堆积，OOM |
| **newSingleThreadExecutor** | 1 | 1 | LinkedBlockingQueue | 单线程顺序执行 | ⚠️ 队列堆积 OOM |
| **newScheduledThreadPool** | n | Integer.MAX_VALUE | DelayQueue | 定时/周期任务 | ⚠️ 无限创建线程 OOM |

> **面试重点**：阿里巴巴 Java 开发手册强制要求**手动创建线程池**（new ThreadPoolExecutor(...)），禁止使用 Executors 的快捷方法，因为它们可能导致 OOM。

### 合理配置线程池大小

```
    CPU 密集型（计算为主）       corePoolSize = CPU 核数 + 1
    IO 密集型（读写为主）        corePoolSize = CPU 核数 × 2（或更多，取决于 IO 等待时间）
    混合型                       拆分 CPU 密集和 IO 密集任务，用不同线程池处理
```

### 线程池的关闭

| 方法 | 说明 |
|------|------|
| **shutdown()** | 不再接收新任务，等待已提交任务执行完后再关闭 |
| **shutdownNow()** | 尝试中断正在执行的任务，返回未执行的任务列表 |
| **isShutdown()** | 判断是否已关闭 |
| **isTerminated()** | 判断是否所有任务都执行完毕 |

### 线程池的状态

```
    RUNNING → SHUTDOWN（调用 shutdown()）
    RUNNING → STOP（调用 shutdownNow()）
    SHUTDOWN → TIDYING（队列为空且执行完）
    STOP → TIDYING（执行完）
    TIDYING → TERMINATED（terminated() 执行完）
```

### 具体使用场景

#### ① 批量异步任务
适用于大批量独立任务，如批量处理文件、批量发送通知邮件。
```java
int core = Runtime.getRuntime().availableProcessors() * 2;
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    core, core, 60L, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1000),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
for (File file : fileList) {
    executor.execute(() -> processFile(file));  // 并发批量处理文件
}
executor.shutdown();
executor.awaitTermination(1, TimeUnit.HOURS);   // 等待所有任务完成
```

#### ② IO 密集型 —— 数据库批量写入
大量 IO 等待（网络 IO、磁盘 IO），线程大部分时间在等待，可配更多线程。
```java
// 数据库批量插入，线程等数据库返回时 CPU 空闲，多设线程提高吞吐
ThreadPoolExecutor ioPool = new ThreadPoolExecutor(
    10, 20, 60L, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(500),
    new ThreadFactoryBuilder().setNameFormat("db-write-%d").build()
);
```

#### ③ 高并发请求 —— 接口限流与隔离
将不同业务用不同线程池隔离，防止一个接口堵死影响其他接口。
```java
// 订单接口专用线程池
ThreadPoolExecutor orderPool = new ThreadPoolExecutor(
    5, 10, 30L, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(100),
    new ThreadPoolExecutor.AbortPolicy()    // 订单超载直接抛异常返回
);

// 查询接口专用线程池（查询量大，允许更多等待）
ThreadPoolExecutor queryPool = new ThreadPoolExecutor(
    10, 20, 30L, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(500),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

#### ④ 定时/周期任务
```java
ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(2);
// 每隔 5 秒执行一次
scheduler.scheduleAtFixedRate(() -> generateReport(), 0, 5, TimeUnit.SECONDS);
// 延迟 10 秒后执行一次
scheduler.schedule(() -> sendReminder(), 10, TimeUnit.SECONDS);
```

#### ⑤ 多线程计算结果汇总
```java
// 拆分大任务 → 多个子线程并行计算 → 汇总结果
List<Future<Integer>> futures = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    int taskId = i;
    futures.add(executor.submit(() -> compute(taskId)));
}
int total = 0;
for (Future<Integer> future : futures) {
    total += future.get();  // 等待每个子任务返回，汇总结果
}
```

### 常见陷阱与避坑

#### ❌ 陷阱一：使用 Executors 创建线程池导致 OOM
```java
// ❌ 错误：LinkedBlockingQueue 默认无界，任务堆积导致 OOM
ExecutorService pool = Executors.newFixedThreadPool(10);  // 队列 Integer.MAX_VALUE

// ✅ 正确：手动指定有界队列，明确拒绝策略
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    10, 20, 60L, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(500),              // 有界队列，防止 OOM
    new ThreadPoolExecutor.AbortPolicy()        // 超载时抛异常，不丢任务
);
```

#### ❌ 陷阱二：任务队列无界导致内存溢出
`LinkedBlockingQueue` 不指定容量时默认 `Integer.MAX_VALUE`，任务积压撑爆内存。
```java
// ❌ 无界队列，任务积压不设上限
new LinkedBlockingQueue<>()          // 容量 = 2^31-1，等同于无界

// ✅ 有界队列，超出就触发拒绝策略
new ArrayBlockingQueue<>(500)        // 最多等 500 个，满了直接拒绝
new LinkedBlockingQueue<>(500)       // 也支持指定容量
```

#### ❌ 陷阱三：线程池中异常被吞没
用 `execute()` 提交任务时，如果任务抛出异常，默认会被线程池吞掉，毫无痕迹。
```java
// ❌ 异常被吞，没有任何日志
executor.execute(() -> {
    int x = 1 / 0;  // 抛出 ArithmeticException → 线程池默默吞掉
});

// ✅ 方案一：用 submit() + Future.get() 捕获异常
Future<?> future = executor.submit(() -> {
    int x = 1 / 0;
});
try {
    future.get();  // 会抛出 ExecutionException
} catch (ExecutionException e) {
    log.error("任务执行异常", e.getCause());  // 能捕获到真正的异常
}

// ✅ 方案二：装饰任务，统一捕获异常
executor.execute(() -> {
    try {
        riskyTask();
    } catch (Exception e) {
        log.error("任务执行失败", e);  // 显式捕获并记录
    }
});

// ✅ 方案三：自定义 ThreadFactory，设置 UncaughtExceptionHandler
new ThreadFactoryBuilder()
    .setNameFormat("my-pool-%d")
    .setUncaughtExceptionHandler((t, e) ->
        log.error("线程 {} 异常", t.getName(), e))  // 捕获所有未捕获异常
    .build();
```

#### ❌ 陷阱四：线程池未正确关闭导致程序不退出
```java
// ❌ 忘记 shutdown，主线程结束了但 JVM 不退出（非守护线程还在运行）
executor.execute(() -> System.out.println("任务"));

// ✅ 正确关闭
try {
    executor.shutdown();                     // 不再接收新任务
    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();              // 超时未结束，强制中断
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            log.error("线程池无法终止");
        }
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}
```

#### ❌ 陷阱五：核心线程数设置过大或过小
```java
// ❌ 核心线程数 = 50，但 CPU 只有 4 核，大量上下文切换浪费 CPU
new ThreadPoolExecutor(50, 50, ...);

// ❌ 核心线程数 = 1，但任务全部是 IO 密集型，吞吐极低
new ThreadPoolExecutor(1, 1, ...);

// ✅ 根据任务性质计算
// CPU 密集型：core = CPU核数 + 1
// IO 密集型：core = CPU核数 × 2 ~ CPU核数 × 10
// 混合型：区分任务类型，拆到不同线程池
```

#### ❌ 陷阱六：线程池隔离不当，一个业务影响所有业务
```java
// ❌ 所有业务共用一个线程池，订单接口故障导致查询接口也瘫痪
ExecutorService globalPool = Executors.newFixedThreadPool(10);

// ✅ 不同业务用不同线程池，相互隔离
ThreadPoolExecutor orderPool = new ThreadPoolExecutor(5, 10, ...);  // 订单
ThreadPoolExecutor queryPool = new ThreadPoolExecutor(10, 20, ...); // 查询
ThreadPoolExecutor logPool   = new ThreadPoolExecutor(2,  5,  ...); // 日志
```

#### ❌ 陷阱七：ThreadLocal 未清理导致内存泄漏
线程池中的线程会复用，如果使用了 ThreadLocal 但没有在任务结束后清除，下个任务会读到脏数据。
```java
// ❌ 线程复用导致 ThreadLocal 数据残留
ThreadLocal<String> context = new ThreadLocal<>();
executor.execute(() -> {
    context.set("用户A");
    // 处理完业务，忘记 remove()
    // context.remove();  // ❌ 忘记了
});
executor.execute(() -> {
    // 拿到的是"用户A"！脏数据
    String user = context.get();  // ⚠️ 错误数据
});

// ✅ 在 finally 中清理 ThreadLocal
executor.execute(() -> {
    try {
        context.set("用户A");
        doBusiness();
    } finally {
        context.remove();  // 保证用完即清理
    }
});
```

### 线程池总结
```
    创建 → 手动 new（禁止 Executors），指定有界队列和拒绝策略
    大小 → CPU 密集 = 核数+1，IO 密集 = 核数×2~10
    隔离 → 不同业务用不同线程池
    关闭 → shutdown + awaitTermination + shutdownNow 三级关闭
    异常 → 用 try-catch 或 Future.get() 捕获，不留隐患
    清理 → ThreadLocal 用完后在 finally 中 remove()
```

## 线程的 6 种状态

Java 的线程在生命周期中有 6 种状态，由 `Thread.State` 枚举定义：

```
                         ┌─────────────────┐
                         │      NEW        │  （新建，还没 start()）
                         └────────┬────────┘
                                  │ start()
                                  ▼
                         ┌─────────────────┐
                   ┌─────│   RUNNABLE      │─────┐        （正在运行 / 等待 CPU 调度）
                   │     └────────┬────────┘     │
                   │              │               │
         等待锁     │     ┌───────┴────────┐      │ 等待 I/O
                   ▼     │                ▼      ▼
           ┌───────────┐ │    ┌──────────────────────┐
           │ BLOCKED   │ │    │      WAITING         │  （wait/join/park）
           │（等待锁）  │ │    └──────────┬───────────┘
           └───────────┘ │               │
                          │    ┌──────────┴───────────┐
                          │    │  TIMED_WAITING        │  （sleep/wait(time)/join(time)/parkNanos）
                          │    └──────────────────────┘
                          │               │
                          ▼               ▼
                         ┌──────────────────────┐
                         │     TERMINATED        │  （执行完毕）
                         └──────────────────────┘
```

| 状态 | 说明 | 进入方式 |
|------|------|---------|
| **NEW** | 新建，还没调用 start() | `new Thread()` |
| **RUNNABLE** | 可运行（正在执行或等待 CPU 调度） | `start()` |
| **BLOCKED** | 阻塞等待锁 | 没拿到 synchronized 锁 |
| **WAITING** | 无限期等待 | `wait()`、`join()`、`park()` |
| **TIMED_WAITING** | 有限期等待 | `sleep(ms)`、`wait(ms)`、`join(ms)`、`parkNanos()` |
| **TERMINATED** | 执行完毕 | 正常结束或异常退出 |

### Thread 核心方法

| 方法 | 作用 | 释放锁？ | 注意事项 |
|------|------|:--------:|---------|
| **start()** | 启动线程，进入 RUNNABLE 状态 | — | 只能调用一次 |
| **sleep(ms)** | 当前线程休眠（TIMED_WAITING） | ❌ 不释放 | 不释放锁，不释放 CPU（时间到继续执行） |
| **yield()** | 让出 CPU 时间片（从运行→就绪） | ❌ 不释放 | 只是建议，不一定生效（依赖于 OS 调度器） |
| **join()** | 等待该线程执行完毕 | — | 主线程等待子线程结束 |
| **interrupt()** | 中断线程 | — | 设置中断标志位，如果线程在 sleep/wait 会抛 InterruptedException |
| **isInterrupted()** | 检查中断标志 | — | 不清除标志 |
| **interrupted()** | 检查并清除中断标志 | — | 静态方法，清除标志 |

```java
// join 示例：主线程等待子线程执行完再继续
Thread t = new Thread(() -> {
    System.out.println("子线程执行中...");
});
t.start();
t.join();  // 主线程阻塞，等 t 执行完
System.out.println("主线程继续");

// interrupt 示例：中断阻塞中的线程
Thread t2 = new Thread(() -> {
    while (!Thread.currentThread().isInterrupted()) {
        // 工作循环
    }
});
t2.start();
t2.interrupt();  // 设置中断标志
```

## ThreadLocal（线程本地变量）

ThreadLocal 让每个线程拥有自己的**独立变量副本**，线程之间互不影响。

### 核心作用
```java
// ❌ 多个线程共享一个变量，需要加锁
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
// 多个线程同时使用 sdf 会有线程安全问题

// ✅ ThreadLocal 让每个线程拥有自己的副本，互不干扰
ThreadLocal<SimpleDateFormat> local = ThreadLocal.withInitial(
    () -> new SimpleDateFormat("yyyy-MM-dd")
);
// 线程 A：local.get() → 自己的 sdf 对象
// 线程 B：local.get() → 自己的 sdf 对象（不同实例）
```

### 常用 API
```java
ThreadLocal<String> tl = new ThreadLocal<>();
tl.set("用户A");    // 在当前线程中存入值
tl.get();            // 在当前线程中取出值
tl.remove();         // 清除当前线程中的值（防止内存泄漏！）
```

### 底层原理
```
    每个 Thread 对象内部有一个 ThreadLocalMap

    Thread ──→ ThreadLocalMap
                    │
        ┌───────────┼───────────┐
        ▼           ▼           ▼
    ThreadLocal①  ThreadLocal②  ThreadLocal③
    ("用户A")      ("tokenX")    ("requestId")
```

- Thread 类内部持有 `ThreadLocalMap`（一个自定义的 HashMap）
- Map 的 key 是 ThreadLocal 对象（**弱引用**），value 是存入的副本值
- 所以同一个 ThreadLocal 对象在不同线程中 get 到的是各自的值

### 内存泄漏问题（面试必问）

```java
// ⚠️ 线程池场景：线程复用，如果不 remove，value 永远不会被回收
ThreadLocal<byte[]> tl = new ThreadLocal<>();
tl.set(new byte[1024 * 1024]);  // 1MB 对象
// ...业务处理...
// 忘记 tl.remove()

// 原因：ThreadLocalMap 的 key 是弱引用，GC 时 key 被回收
//       但 value 是强引用，key 为 null 的 entry 永远不会被清理
//       线程池中的线程长期存活 → value 一直不被回收 → 内存泄漏

// ✅ 正确做法：用完在 finally 中 remove()
try {
    tl.set(data);
    doBusiness();
} finally {
    tl.remove();  // 保证清理
}
```

> **一句话总结**：ThreadLocal 让线程拥有私有变量，底层是 Thread 里的 ThreadLocalMap。使用时务必注意 **finally 中 remove()**，否则线程池场景下会导致内存泄漏。

## LockSupport（线程阻塞工具）

LockSupport 是 AQS 的底层阻塞工具，提供 `park()`（阻塞）和 `unpark(thread)`（唤醒）方法。

### 与 wait/notify 的区别

| | wait/notify | LockSupport |
|---|---|---|
| 依赖锁 | 必须在 synchronized 块内 | **不需要**持有锁 |
| 唤醒方式 | notify/notifyAll（随机/全部） | unpark(指定线程) **精准唤醒** |
| 中断行为 | 抛 InterruptedException | 不抛异常，直接返回（通过检查中断标志） |
| 调用顺序 | wait 必须在 notify 之前 | unpark 可以先于 park **调用**（许可机制） |

### 许可机制（类似信号量）
```java
// unpark 可以先于 park 调用，相当于预先发放一个"许可"
Thread t = new Thread(() -> {
    LockSupport.park();      // ② 有许可，不阻塞，直接继续
    System.out.println("执行");
});
LockSupport.unpark(t);        // ① 提前发放许可
t.start();

// 一个线程最多拥有 1 个许可，重复 unpark 不会累加
```

### 实际应用：AQS 的底层依赖
```java
// AQS 内部就是这么干的
// 获取锁失败时：LockSupport.park(this);     // 阻塞线程
// 释放锁时：    LockSupport.unpark(next);    // 唤醒队列中的下一个线程
```

## CompletableFuture（异步编排）

Java 8 引入的异步编程工具，可以像"流水线"一样编排多个异步任务，解决了 `Future` 的痛点（无法链式调用、无法组合）。

### 创建异步任务

```java
// 无返回值
CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
    System.out.println("执行任务");
});

// 有返回值，默认用 ForkJoinPool.commonPool()
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
    return "结果";
});

// 指定自定义线程池
ExecutorService pool = Executors.newFixedThreadPool(5);
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> {
    return "结果";
}, pool);
```

### 链式调用

```java
CompletableFuture.supplyAsync(() -> "hello")           // 异步执行
    .thenApply(s -> s + " world")                       // 转换结果（同步）
    .thenApplyAsync(s -> s.toUpperCase())               // 转换结果（异步）
    .thenAccept(System.out::println)                    // 消费结果，无返回值
    .exceptionally(e -> {                               // 异常处理
        System.out.println("出错: " + e.getMessage());
        return null;
    });
```

### 任务组合

```java
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");

// 两个都完成，合并结果
f1.thenCombine(f2, (a, b) -> a + b);               // → "AB"

// 两个都完成，消费结果
f1.thenAcceptBoth(f2, (a, b) -> System.out.println(a + b));

// 任意一个完成就继续
f1.applyToEither(f2, s -> "最先完成的是: " + s);

// 等待所有完成
CompletableFuture.allOf(f1, f2).join();

// 等待任意一个完成
CompletableFuture.anyOf(f1, f2).thenAccept(s -> System.out.println(s + " 先完成"));
```

### 注意事项
```java
// ⚠️ 默认使用 ForkJoinPool.commonPool()，全部是 daemon 线程
//    主线程结束后 daemon 线程也会退出，可能导致任务没执行完就退出
// ✅ 建议耗时任务指定自定义线程池
// ⚠️ 异常默认会被吞没，需要用 exceptionally() 或 handle() 捕获
```

## Fork/Join 框架（分治并行计算）

Fork/Join 是 Java 7 引入的并行计算框架，核心思想是**分治法**（大任务拆小任务，并行计算，合并结果）。

### 核心组件
```
    ForkJoinPool            —— 专用于 Fork/Join 的线程池（工作窃取）
    RecursiveTask<V>        —— 有返回值的任务（继承它）
    RecursiveAction         —— 无返回值的任务（继承它）
```

### 工作窃取算法（Work-Stealing）
```
    线程 1 的队列：[任务A] [任务B] [任务C]       ← 线程 1 空闲时
    线程 2 的队列：[任务D] [任务E]               ← 线程 2 的队列

    → 线程 1 从线程 2 队列的尾部"窃取"任务E来执行
    → 充分利用 CPU，减少线程间的竞争
```

### 示例：并行求和
```java
class SumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 1000;  // 拆分阈值
    private long[] array;
    private int start, end;

    public SumTask(long[] array, int start, int end) {
        this.array = array; this.start = start; this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) sum += array[i];
            return sum;
        }
        int mid = (start + end) / 2;
        SumTask left = new SumTask(array, start, mid);
        SumTask right = new SumTask(array, mid, end);
        left.fork();           // 拆分子任务，异步执行
        long rightResult = right.compute();  // 当前线程执行右任务
        long leftResult = left.join();       // 等待左任务结果
        return leftResult + rightResult;
    }
}

// 使用
ForkJoinPool pool = new ForkJoinPool();
long result = pool.invoke(new SumTask(array, 0, array.length));
```

## 并发容器

### ConcurrentHashMap（面试必问）

JDK 7 → JDK 8 的演进：

| | JDK 7（分段锁） | JDK 8（CAS + synchronized） |
|---|---|---|
| 数据结构 | Segment 数组 + HashEntry 数组 | Node 数组 + 链表/红黑树 |
| 并发机制 | 继承 ReentrantLock，锁 Segment | CAS 控制数组，synchronized 锁链表/树头节点 |
| 锁粒度 | 一个 Segment 锁一段数据 | 只锁单个桶的头节点，粒度更细 |
| 扩容 | 每个 Segment 独立扩容 | 多线程协助扩容 |

```java
// JDK 8 put 流程（简化）
// ① 数组为空 → CAS 初始化
// ② 当前桶为空 → CAS 放 Node，无锁
// ③ 当前桶有数据 → synchronized 锁桶头节点，遍历链表/红黑树插入
// ④ 链表长度 ≥ 8 → 转红黑树（TreeBin）
// ⑤ 元素个数超过阈值 → 多线程协助扩容（transfer）

ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("key", 1);        // 线程安全
map.get("key");            // 不加锁，volatile 读
map.computeIfAbsent("key", k -> compute(k));  // 原子操作
```

### CopyOnWriteArrayList（读写分离）

```java
// 适用于"读多写极少"的场景（如白名单、配置缓存）

CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

// 读：不加锁，直接读数组（性能极高）
list.get(0);

// 写：加锁，复制新数组，替换引用
list.add("x");  // = 加锁 → 复制新数组 → 插入 → 替换原数组引用 → 解锁

// ⚠️ 写时复制开销大，不适合频繁写的场景
// ⚠️ 弱一致性：读可能读到旧的数组（写操作还没完成）
```

### 容器对比

| 容器 | 线程安全机制 | 适用场景 |
|------|------------|---------|
| **ConcurrentHashMap** | CAS + synchronized（桶粒度） | 高并发读写 |
| **CopyOnWriteArrayList** | 读写分离，写时复制 | 读多写极少 |
| **ConcurrentLinkedQueue** | CAS（无锁队列） | 高并发队列操作 |
| **BlockingQueue** | ReentrantLock + Condition | 生产者-消费者模式 |

## 死锁

### 四个必要条件

| 条件 | 说明 |
|------|------|
| ① **互斥** | 资源一次只能被一个线程占有 |
| ② **占有并等待** | 线程占有一个资源，同时等待另一个资源 |
| ③ **不可剥夺** | 资源不能被强行抢占，只能由线程主动释放 |
| ④ **循环等待** | 多个线程形成循环：A 等 B 的资源，B 等 A 的资源 |

### 死锁示例
```java
Object lockA = new Object();
Object lockB = new Object();

// 线程 1
synchronized (lockA) {
    Thread.sleep(100);  // 确保线程 2 已拿到 lockB
    synchronized (lockB) {
        System.out.println("线程1完成");
    }
}

// 线程 2
synchronized (lockB) {
    Thread.sleep(100);  // 确保线程 1 已拿到 lockA
    synchronized (lockA) {
        System.out.println("线程2完成");
    }
}
// ⚠️ 线程 1 持有 lockA 等 lockB，线程 2 持有 lockB 等 lockA → 死锁
```

### 如何排查死锁（jstack）
```bash
# 1. 找到 Java 进程的 PID
jps

# 2. 打印线程堆栈
jstack -l <pid>

# 3. 输出中会有专门提示：
# "Found one Java-level deadlock："
# "======================"
# 指出死锁的线程和锁的详细信息
```

### 如何避免死锁
```java
// ✅ 方案一：固定锁顺序（总是先锁 A 再锁 B）
// 破坏"循环等待"
synchronized (lockA) {
    synchronized (lockB) { ... }
}

// ✅ 方案二：使用 tryLock 超时
// 破坏"不可剥夺"，拿不到锁就放弃
ReentrantLock lockA = new ReentrantLock();
ReentrantLock lockB = new ReentrantLock();

if (lockA.tryLock(1, TimeUnit.SECONDS)) {
    try {
        if (lockB.tryLock(1, TimeUnit.SECONDS)) {
            try {
                // 业务逻辑
            } finally { lockB.unlock(); }
        }
    } finally { lockA.unlock(); }
}

// ✅ 方案三：避免嵌套锁，用 ThreadPoolExecutor 一次性分配资源
// ✅ 方案四：使用并发容器代替手动加锁
```

## 附录：Java 并发知识体系总览

```
                    ┌─────────────────────────────────────┐
                    │          Java 并发体系              │
                    └─────────────────────────────────────┘
                                      │
        ┌─────────────────────────────┼─────────────────────────────┐
        ▼                             ▼                             ▼
┌──────────────────┐    ┌────────────────────────┐    ┌──────────────────────┐
│   理论基础        │    │     锁与同步            │    │    并发工具            │
│  · JMM 内存模型   │    │  · synchronized（升级）  │    │  · 线程池（7 参数）    │
│  · 重排序         │    │  · volatile（屏障）     │    │  · CountDownLatch     │
│  · happens-before │    │  · CAS（ABA）           │    │  · CyclicBarrier      │
│  · 顺序一致性      │    │  · AQS（队列同步器）    │    │  · Semaphore           │
└──────────────────┘    │  · LockSupport          │    │  · Exchanger           │
                         │  · ReentrantLock        │    │  · CompletableFuture   │
                         │  · ReadWriteLock        │    │  · Fork/Join           │
                         │  · Condition            │    └──────────────────────┘
                         │  · DCL 单例             │              │
                         │  · 死锁（4 条件）        │    ┌──────────────────────┐
                         └────────────────────────┘    │    数据结构            │
                                      │                 │  · 原子类（CAS）       │
                                      ▼                 │  · ThreadLocal         │
                         ┌────────────────────────┐    │  · 并发容器            │
                         │     线程基础             │    │    ConcurrentHashMap  │
                         │  · 6 种状态             │    │    CopyOnWriteArrayList│
                         │  · sleep/yield/join    │    └──────────────────────┘
                         │  · interrupt            │
                         └────────────────────────┘
```

## 面试考点汇总（按频率排序）

### ⭐⭐⭐⭐⭐ 必考题（几乎每次面试都会问）

**1. synchronized 和 ReentrantLock 的区别？**
```
    synchronized                    ReentrantLock
    ────────────                    ─────────────
    自动加锁/解锁                   手动 lock/unlock（finally 释放）
    非公平锁                        可公平/非公平
    不可中断                        可中断（lockInterruptibly）
    由 JVM 实现                    基于 AQS（Java 代码实现）
    不支持 Condition               支持多个 Condition 精准唤醒
    JDK 6+ 已优化（锁升级）         性能差距可忽略
```

**2. 线程池的核心参数和执行流程？**
- 7 个参数：corePoolSize、maximumPoolSize、keepAliveTime、unit、workQueue、threadFactory、handler
- 流程：核心线程 → 任务队列 → 最大线程 → 拒绝策略
- 为什么禁用 Executors？（OOM 风险）

**3. volatile 关键字的作用和原理？**
- 保证可见性（MESI 缓存一致性协议）
- 禁止指令重排序（内存屏障：写屏障 + 读屏障）
- **不保证原子性**（i++ 不是原子操作）
- 适用场景：状态标记、DCL 单例

**4. sleep() 和 wait() 的区别？**

| | sleep() | wait() |
|---|---|---|
| 所属类 | Thread 的静态方法 | Object 的实例方法 |
| 释放锁 | ❌ 不释放 | ✅ 释放锁 |
| 需要 synchronized？ | ❌ 不需要 | ✅ 必须在同步块内调用 |
| 唤醒方式 | 时间到自动唤醒 | notify/notifyAll 或超时 |
| 用途 | 暂停当前线程 | 线程间通信 |

**5. ConcurrentHashMap 的实现原理（JDK 8）？**
- 数组 + 链表/红黑树
- CAS 初始化数组、CAS 放头节点
- synchronized 锁桶头节点
- 链表 ≥ 8 转红黑树
- 多线程协助扩容（transfer）

### ⭐⭐⭐⭐ 高频题

**6. 什么是 CAS？ABA 问题怎么解决？**
- CAS = Compare And Swap，乐观锁
- 三个参数：内存位置 V、预期值 A、新值 B
- ABA 问题：值被改成 B 又改回 A，CAS 认为没变过
- 解决：AtomicStampedReference（版本号 stamp）

**7. 什么是 AQS？它的核心原理是什么？**
- AbstractQueuedSynchronizer，JUC 锁和同步器的底层框架
- 三大部件：state（volatile int）+ CLH 队列（FIFO）+ CAS
- 两种模式：独占（ReentrantLock）、共享（CountDownLatch/Semaphore）
- 阻塞唤醒通过 LockSupport.park()/unpark()

**8. CountDownLatch 和 CyclicBarrier 的区别？**

| | CountDownLatch | CyclicBarrier |
|---|---|---|
| 核心 | 计数减到 0 放行 | 线程数达到 N 放行 |
| 计数器 | 不可重置（一次性） | 可重置（可复用） |
| 角色 | 一个线程等 N 个线程 | N 个线程互相等待 |
| 底层 | AQS 共享锁 | ReentrantLock + Condition |

**9. 线程池的拒绝策略有哪些？怎么选？**
- AbortPolicy（默认）：抛异常 → 业务允许失败时用
- CallerRunsPolicy：调用者执行 → 降速，保护系统
- DiscardPolicy：丢弃 → 不重要的日志或统计任务
- DiscardOldestPolicy：丢弃最旧 → 适合新鲜度优先的任务

**10. 什么是死锁？如何排查和避免？**
- 四个必要条件：互斥、占有等待、不可剥夺、循环等待
- 排查：jstack -l pid（会提示 "Found one Java-level deadlock"）
- 避免：固定锁顺序、tryLock 超时、避免嵌套锁、使用并发容器

### ⭐⭐⭐ 中频题

**11. 创建线程有哪几种方式？**
```java
// ① 继承 Thread
class MyThread extends Thread { public void run() { ... } }

// ② 实现 Runnable
new Thread(() -> { ... }).start();

// ③ 实现 Callable + FutureTask（有返回值）
FutureTask<Integer> task = new FutureTask<>(() -> { return 1; });
new Thread(task).start();
Integer result = task.get();

// ④ 线程池
ExecutorService pool = Executors.newFixedThreadPool(5);
pool.execute(() -> { ... });
```

**12. ThreadLocal 的原理？内存泄漏问题？**
- 每个 Thread 内部有 ThreadLocalMap，key 是 ThreadLocal（弱引用），value 是副本
- 内存泄漏：key 被 GC 回收后，value 无法被访问也未被清理
- 解决：finally 块中手动 remove()

**13. 乐观锁和悲观锁的区别？**

| | 乐观锁 | 悲观锁 |
|---|---|---|
| 思想 | 假设没人改，先改再验证 | 假设肯定有人改，先锁再操作 |
| 实现 | CAS、版本号 | synchronized、ReentrantLock |
| 场景 | 读多写少（如缓存） | 写多（如转账） |

**14. synchronized 的锁升级过程？**
- JDK 6 之前：直接重量级锁（OS 互斥量）
- JDK 6 优化后：无锁 → 偏向锁（已废弃）→ 轻量级锁（CAS 自旋）→ 重量级锁
- 锁只能升级不能降级
- ⚠️ JDK 15+ 默认禁用偏向锁，JDK 21 彻底移除

**15. 公平锁和非公平锁的区别？**
- 公平锁：按排队顺序获取锁，性能低（上下文切换多）
- 非公平锁：插队获取，性能高，可能导致线程饥饿
- ReentrantLock 默认非公平锁

### ⭐⭐ 了解题

**16. yield() 和 join() 的区别？**
- yield：让出 CPU，从运行→就绪，仍可能被立即调度
- join：等待线程结束，当前线程进入 WAITING

**17. interrupt() 和 stop() 的区别？**
- stop：强制终止线程（已废弃，释放锁导致数据不一致）
- interrupt：设置中断标志，由线程自己检查并响应

**18. LockSupport 和 wait/notify 的区别？**
- 不需要持有锁、精准唤醒指定线程、不抛 InterruptedException、许可机制支持先 unpark 再 park

**19. CopyOnWriteArrayList 的原理和适用场景？**
- 写时复制：读不加锁，写时复制新数组
- 适用：读多写极少的场景（白名单、配置）
- 不适用：频繁写的场景（复制开销大）

**20. Fork/Join 的工作窃取算法是什么？**
- 空闲线程从其他线程队列尾部"偷"任务执行
- 充分利用 CPU，减少线程竞争

### 💡 面试答题技巧

```
    结构化回答：先给结论，再展开细节，最后举例子
    ├── 是什么（一句话定义）
    ├── 怎么用（核心 API / 参数）
    ├── 为什么（底层原理）
    ├── 注意啥（常见坑 / 面试扩展点）
    └── 对比（和相似概念的区别）

    例如面试官问"volatile 关键字"
    → ① volatile 是一个变量修饰符，保证可见性和禁止重排序（是什么）
    → ② 适用场景：状态标记、DCL 单例（怎么用）
    → ③ 底层通过内存屏障 + MESI 协议实现（为什么）
    → ④ 不保证原子性，i++ 需要用 AtomicInteger 或 synchronized（注意啥）
    → ⑤ 和 synchronized 的区别：volatile 只保证可见性，不保证原子性（对比）
```

## 📋 自查清单

按章节逐项检查，能清晰说出每个要点的才算 ✅ 掌握。

### 一、JMM 与理论基础
- [ ] 能说出 JMM 是什么（主内存 vs 工作内存）
- [ ] 能说出三大特性：原子性、可见性、有序性
- [ ] 能解释什么是重排序（编译器/处理器/内存系统三级）
- [ ] 能说出顺序一致性模型及其性能问题
- [ ] 能列举 happens-before 的 6 条规则
- [ ] 能用自己的话解释 happens-before 解决了什么问题

### 二、CAS
- [ ] 能说出 CAS 的三个参数和执行逻辑
- [ ] 能解释 ABA 问题及其解决方案
- [ ] 能说出 CAS 的自旋开销问题及解决办法

### 三、synchronized
- [ ] 能说出 synchronized 做了哪两件事（互斥+可见性）
- [ ] 能解释 monitorenter/monitorexit 的工作细节
- [ ] 能说出什么是可重入性，为什么不会死锁
- [ ] 能画出锁升级路径（无锁→偏向锁→轻量级锁→重量级锁）
- [ ] 能说出偏向锁在 JDK 15+ 的废弃原因
- [ ] 能说出 synchronized 的三种锁对象区别
- [ ] 能对比 synchronized 和 ReentrantLock（至少 3 点）

### 四、volatile
- [ ] 能说出 volatile 的两个核心作用（可见性+禁止重排序）
- [ ] 能说出 volatile 不保证原子性，以及什么场景下出问题
- [ ] 能解释内存屏障（写屏障、读屏障）
- [ ] 能说出 volatile 和 synchronized 的区别

### 五、DCL 单例
- [ ] 能手写 DCL 单例模式
- [ ] 能解释为什么 DCL 要加 volatile

### 六、AQS
- [ ] 能说出 AQS 的三大部件（state、CLH 队列、CAS）
- [ ] 能说出独占模式和共享模式的区别及代表工具
- [ ] 能画出 AQS 的工作流程图
- [ ] 能说出 LockSupport 的 park/unpark 与 wait/notify 的区别

### 七、并发三大锁
- [ ] 能说出 ReentrantLock 的特性及与 synchronized 的区别
- [ ] 能说出 ReadWriteLock 的读锁/写锁/锁降级
- [ ] 能说出 Condition 解决了 wait/notify 的什么问题
- [ ] 能手写生产者-消费者的 Condition 示例

### 八、并发工具类
- [ ] 能说出 CountDownLatch 的核心方法及不可重置特性
- [ ] 能说出 CyclicBarrier 与 CountDownLatch 的区别
- [ ] 能说出 Semaphore 的核心方法及适用场景
- [ ] 能说出 Exchanger 的一对一交换特性

### 九、原子类
- [ ] 能说出 AtomicInteger 的核心方法
- [ ] 能说出 AtomicReference 的用途
- [ ] 能说出 AtomicStampedReference 如何解决 ABA 问题

### 十、线程池
- [ ] 能手写出 ThreadPoolExecutor 的 7 个参数
- [ ] 能画出线程池的执行流程图（核心→队列→最大→拒绝）
- [ ] 能说出 4 种拒绝策略及适用场景
- [ ] 能说出为什么禁止使用 Executors
- [ ] 能说出 CPU 密集型和 IO 密集型的线程数配置公式
- [ ] 能说出 shutdown 和 shutdownNow 的区别
- [ ] 能说出线程池中异常的 3 种处理方式
- [ ] 能说出线程池中 ThreadLocal 的内存泄漏原因及解决办法

### 十一、线程基础
- [ ] 能说出线程的 6 种状态及状态转换图
- [ ] 能说出 sleep、yield、join 的区别
- [ ] 能说出 interrupt 的工作原理（中断标志位）

### 十二、ThreadLocal
- [ ] 能说出 ThreadLocal 的底层原理（ThreadLocalMap）
- [ ] 能说出 key 为什么是弱引用
- [ ] 能说出内存泄漏的原因及如何避免

### 十三、LockSupport
- [ ] 能说出 park/unpark 与 wait/notify 的 4 个区别
- [ ] 能说出锁机制（许可机制，可提前 unpark）

### 十四、CompletableFuture
- [ ] 能说出 supplyAsync 和 runAsync 的区别
- [ ] 能说出 thenApply、thenAccept、thenCombine 的作用
- [ ] 能说出 allOf 和 anyOf 的区别
- [ ] 能说出默认线程池的风险及如何指定自定义线程池

### 十五、Fork/Join
- [ ] 能说出 ForkJoinPool 的工作窃取算法
- [ ] 能说出 RecursiveTask 和 RecursiveAction 的区别

### 十六、并发容器
- [ ] 能说出 ConcurrentHashMap（JDK 8）的 put 流程
- [ ] 能说出 JDK 7 和 JDK 8 的实现区别
- [ ] 能说出 CopyOnWriteArrayList 的适用场景和缺点

### 十七、死锁
- [ ] 能说出死锁的 4 个必要条件
- [ ] 能说出 jstack 排查死锁的步骤
- [ ] 能说出至少 3 种避免死锁的策略

---

**使用方式**：打印出来或每天过一遍，看到某个 [ ] 卡住了就回去翻对应的章节。目标是全部打 ✅。😄

