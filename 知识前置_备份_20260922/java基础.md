## 容器

### hashmap
```
    hashmap就是键值对，底层逻辑是由红黑树，链表与数组组成。

    键储存的是数据的地址（索引，下标），值就是储存的数据

    因为放入数据时，是以键为查询索引，键又是一个占位符，所以不会有重复值

    hashmap是一个动态数组，下标经过hash计算，一个下标会有多个值，所以在我们找数据的时候，一般都是先用hashcode（）找到数组所在的数据桶（就是当前索引所包含的数据集合），再使用equals（）来查明是否是想要的值，这也就是get（）方法

    动态数组就是会自动扩充，初始的默认值为16
    负载因子（默认值为0.75），当元素个数 > 容量 × 负载因子 时，就会按两倍进行扩容，其中负载因子 = 当前存储元素数量 size / 数组容量 capacity
        负载因子比较大时会使得充分利用好内存但是会导致很多hash冲突（大量重复值指向同一个地址）查询变慢
        比较小时会浪费很多内存（扩充速度慢），但是查询会变得快，cpu占用小
        所以默认值为0.75会使得hash冲突变少

    put流程： 先是hashcode（）计算得到hash值定位到数据桶，再通过spread（）扰乱方法进行二次扰动，如果找到相同的就覆盖，没有的就直接插入，再通过equals（）遍历链表或者红黑树，找到相同的key就覆盖，未找到就插入。



    优点：在查询的时候根据键值对查询，速度会很快，当链表长度 ≥ 8 且数组长度 ≥ 64 时会转化为红黑树，大大提升查询效率

    缺点：当数据量比较大时，会出现hash冲突（两个不同的key，经过hash计算得到相同的键，组成的数据桶）效率会大打折扣，在高并发情况下，没有锁会导致查询冲突，性能降低，多个线程同时 put 时，可能会导致数据覆盖，或在 JDK 1.7 中引发扩容死循环。所以高并发下应该用 ConcurrentHashMap

    总结：hashmap适用于简单查询，效率最高，但是在高并发情况下不适合作为查询条件。
```
### ArrayList与LinkedList

    ArrayList 就是一个基于数组之上的动态版本，会自动按1.5倍进行扩容，在内存中是一个线性排列
     优点 ：查询的时间复杂度为 O(1)，只进行尾部插入也很快
     缺点 ：从数组中间插入/删除时较慢，因为会涉及后续元素的移动，效率低

    LinkedList 就是双向链表，每一个节点都存储着数据以及前后节点的引用
     优点 ：在中间插入/删除很方便，只需要修改前后节点的引用即可
     缺点 ：查询只能从头或尾开始遍历，时间复杂度为 O(n)


### 总结：ArrayList vs LinkedList 对比

| 对比维度 | ArrayList | LinkedList |
|---------|----------|------------|
| 底层结构 | 动态数组（连续内存） | 双向链表（离散节点） |
| 内存占用 | 相对紧凑，但扩容可能有闲置容量 | 每个节点额外存储前后指针，占用更大 |
| 查询（随机访问） | **O(1)**，直接通过下标访问 | **O(n)**，需要遍历 |
| 尾部插入/删除 | **O(1)**（均摊，扩容时 O(n)） | **O(1)**（持有尾节点引用） |
| 中间插入/删除 | **O(n)**，需要移动后续元素 | **O(1)**（已知位置时，只需修改指针） |
| 头部插入/删除 | **O(n)**，需要整体移动 | **O(1)** |
| 内存连续性 | 是，CPU 缓存友好 | 否，节点分散，缓存不友好 |

**合理使用场景：**

- **ArrayList 适用：**
  - 需要频繁按下标随机访问（如遍历、二分查找）
  - 主要在尾部进行添加/删除操作
  - 数据量较大且对 CPU 缓存命中率有要求

- **LinkedList 适用：**
  - 需要频繁在头部或中间插入/删除元素（如队列、双端队列）
  - 数据量不大，且操作集中在两端
  - 作为 Queue/Deque 的底层实现（Java 的 `LinkedList` 实现了 `Deque` 接口）

**实际开发建议：** 绝大部分场景首选 `ArrayList`。它的查询快、内存紧凑、CPU 缓存友好。只有当确定要在中间或头部频繁增删时，才考虑 `LinkedList`。



### Set类
  set就是一个基于hashmap的键值对进行修改，只有键没有值，使用的时候都是以键代替为值使用
  是一个不可重复的集合，因为键占位符是唯一的，它的核心功能就是 "去重"。

#### set有三大类
  HashSet： 基于HashMap，插入无顺序，只有键，但是查询速度最快
  LinkedHashSet ：在Hashset的基础上，记录插入顺序
  TreeSet ：自动排序（升序）底层是基于TreeMap（红黑树），所以时间复杂度为O（log n）

---

### 总结：Java容器体系一览

Java 容器（Collection Framework）主要分为两大接口体系：**Collection** 和 **Map**。

| 接口/类 | 底层结构 | 特点 | 适用场景 |
|---------|---------|------|---------|
| **HashMap** | 数组 + 链表 + 红黑树 | 键值对存储，查询快 O(1)；无序；线程不安全 | 绝大多数键值对场景 |
| **ArrayList** | 动态数组（1.5倍扩容） | 随机访问 O(1)，尾部插入快；CPU 缓存友好 | 频繁按下标访问，尾部增删 |
| **LinkedList** | 双向链表 | 头部/中间插入删除 O(1)；随机访问 O(n) | 频繁头部/中间增删，作为 Queue/Deque |
| **HashSet** | 基于 HashMap | 元素唯一，查询快 O(1)；无序 | 去重场景 |
| **LinkedHashSet** | 基于 LinkedHashMap | 元素唯一，**保持插入顺序** | 需要去重且保留顺序 |
| **TreeSet** | 基于 TreeMap（红黑树） | 元素唯一，**自动升序排序** O(log n) | 需要有序不重复集合 |

**核心要点：**

1. **查询优先 → HashMap / ArrayList**
2. **去重优先 → HashSet**
3. **顺序优先 → LinkedHashSet（插入顺序）/ TreeSet（排序）**
4. **高并发 → ConcurrentHashMap（代替 HashMap）**
5. **频繁中间操作 → LinkedList**

选择容器时，先明确需求是 **键值对**、**列表** 还是 **集合去重**，再根据对顺序和性能的要求做出选择。


## @Override 重写
```
  @Override 是编译期校验注解，专门用来标记：当前方法是重写父类 / 实现接口的方法。
  
  重写是指将父类或接口进行重写编写变成一个新的方法，在多个功能都使用上这个接口或类的功能时，但功能不足以完全解决这个问题，
  所以我们就可以直接重写这个类与接口，大大简化我们的流程，使得功能更完善。

  重写的规则：
    1.静态（static）方法不能被重写 —— 静态方法属于类，子类同名静态方法只是"隐藏"父类方法，不是重写

    2.父类或接口中必须存在要重写的方法（即目标方法必须在父类/接口中定义过）

    3.方法名、参数列表必须完全相同

    4.返回值必须兼容（可以是原返回类型的子类型，即"协变返回类型"）

    5.访问权限不能更严格 —— 子类方法的访问修饰符必须大于或等于父类方法的访问权限

    6.final 方法不能重写 —— 父类中声明为 final 的方法，子类不能重写

    7.private 方法不能重写 —— private 方法对子类不可见，因此不存在重写

  注意：重写只是继承方法，但不继承原有的数据，父子类是互相独立的，所以如果不需要重写，仅仅需要里面的数据，可以直接调用目标类
  注意：重写是运行时多态的实现基础，父类引用指向子类对象时，调用重写方法会执行子类的版本
```

## 访问修饰符
```
  访问修饰符用于控制类、方法、变量的可见范围。Java 有四种访问级别，按权限从小到大排列：

  ┌─────────────────────────────────────────────────────────────┐
  │           访问修饰符权限表                                   │
  ├────────────┬───────┬──────────┬───────────┬────────────────┤
  │  修饰符    │ 本类   │ 同包     │ 子类      │ 任何位置       │
  ├────────────┼───────┼──────────┼───────────┼────────────────┤
  │ private    │ ✔     │ ✘       │ ✘        │ ✘             │
  │ 默认(缺省)  │ ✔     │ ✔       │ ✘        │ ✘             │
  │ protected  │ ✔     │ ✔       │ ✔        │ ✘             │
  │ public     │ ✔     │ ✔       │ ✔        │ ✔             │
  └────────────┴───────┴──────────┴───────────┴────────────────┘

  private —— 仅本类可见
    · 只能在同一类内部访问，对外完全封闭
    · 常用于封装内部实现细节（成员变量通常设为 private，通过 getter/setter 暴露）
    · 示例：
        private int age;
        private void helper() { }

  默认（缺省，无修饰符）—— 包级私有（package-private）
    · 同一个包下的所有类都可以访问
    · 不同包下的类无法访问（即使是子类也不行）
    · 示例：
        String name;          // 默认修饰符
        void sayHello() { }   // 默认修饰符

  protected —— 包内 + 子类可见
    · 同包下的所有类可以访问
    · 不同包下的子类也可以访问
    · 常用于父类中希望被子类继承但不对外公开的方法
    · 示例：
        protected int score;
        protected void calculate() { }

  public —— 任何位置可见
    · 对所有类公开，没有访问限制
    · 通常用于对外提供的 API 接口
    · 示例：
        public int id;
        public void run() { }

  访问修饰符使用原则（封装性）：
    1.成员变量 —— 绝大多数情况下用 private，通过 public 的 getter/setter 访问
    2.方法 —— 对外暴露的功能用 public，内部辅助方法用 private
    3.父类子类 —— 希望子类覆写的方法用 protected
    4.类本身 —— 普通类只能用 public 或默认（缺省）；内部类可以使用所有修饰符
```

## 异常处理

```
  异常（Exception）是 Java 在程序运行过程中出现的不正常情况，它会中断正在执行的指令。
  Java 通过"异常处理机制"来捕获、处理这些异常，保证程序的健壮性。

  ┌─────────────────────────────────────────────────────────────┐
  │                Java 异常体系结构                             │
  │                                                             │
  │                    Throwable                                │
  │                    /      \                                 │
  │                 Error    Exception                          │
  │                (不可处理)  /        \                        │
  │                        Checked   RuntimeException           │
  │                        (编译时)    (运行时)                  │
  └─────────────────────────────────────────────────────────────┘

  Throwable —— 所有异常和错误的根类
    · Error：程序无法处理的严重错误，如 OutOfMemoryError、StackOverflowError
      通常由 JVM 抛出，程序无需捕获，也无力修复
    · Exception：程序可以处理的异常，分为两大类

  一、检查型异常（Checked Exception）—— 编译时异常
    · 编译器强制要求处理，要么 try-catch，要么 throws 抛给上层
    · 常见的有：
        · IOException（文件读写异常）
        · SQLException（数据库操作异常）
        · ClassNotFoundException（类未找到）
        · InterruptedException（线程中断异常）
    · 特点：不处理就无法通过编译

  二、非检查型异常（Unchecked Exception / RuntimeException）—— 运行时异常
    · 编译器不强制处理，通常由程序逻辑错误导致
    · 常见的有：
        · NullPointerException（空指针异常 —— 调用 null 对象的方法/属性）
        · ArrayIndexOutOfBoundsException（数组下标越界）
        · IllegalArgumentException（非法参数）
        · ArithmeticException（算术异常，如除零）
        · ClassCastException（类型转换异常）
    · 特点：编译时不会报错，运行时才暴露

  三、异常处理关键字
    try —— 监控可能发生异常的代码块
    catch —— 捕获并处理指定类型的异常
    finally —— 无论是否发生异常，都会执行的代码块（通常用于释放资源）
    throw —— 手动抛出一个异常对象
    throws —— 声明方法可能抛出的异常，交由调用者处理

  四、基本用法

  ── try-catch 基本结构 ──
    try {
        // 可能发生异常的代码
        int result = 10 / 0;  // 会抛出 ArithmeticException
    } catch (ArithmeticException e) {
        // 捕获并处理异常
        System.out.println("除数不能为零: " + e.getMessage());
    }

  ── 多重 catch ──
    try {
        String str = null;
        System.out.println(str.length());
        int[] arr = new int[3];
        System.out.println(arr[5]);
    } catch (NullPointerException e) {
        System.out.println("空指针异常: " + e.getMessage());
    } catch (ArrayIndexOutOfBoundsException e) {
        System.out.println("下标越界: " + e.getMessage());
    }
    // 注意：子类异常在前，父类异常在后，否则编译报错

  ── try-catch-finally ──
    try {
        // 打开资源
        FileInputStream fis = new FileInputStream("test.txt");
        // 读取操作
    } catch (IOException e) {
        System.out.println("文件读取失败: " + e.getMessage());
    } finally {
        // 无论是否异常，都会执行 —— 用于关闭资源
        System.out.println("资源已释放");
    }
    // 注意：即使 catch 中 return，finally 也会在 return 之前执行

  ── try-with-resources（JDK 7+） ──
    // 自动关闭实现了 AutoCloseable 接口的资源
    try (FileInputStream fis = new FileInputStream("test.txt");
         BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {
        String line = br.readLine();
        System.out.println(line);
    } catch (IOException e) {
        System.out.println("读取异常: " + e.getMessage());
    }
    // 不需要 finally 手动关闭，资源会自动关闭，代码更简洁

  ── throw —— 手动抛出异常 ──
    public void checkAge(int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("年龄不合法: " + age);
        }
        System.out.println("年龄合法: " + age);
    }

  ── throws —— 声明异常 ──
    public void readFile(String path) throws IOException {
        // 该方法不处理异常，交给调用者处理
        FileInputStream fis = new FileInputStream(path);
    }
    // 调用者必须处理（try-catch）或继续 throws

  五、自定义异常
    // 继承 Exception（检查型）或 RuntimeException（非检查型）
    public class BusinessException extends RuntimeException {
        private int code;

        public BusinessException(int code, String message) {
            super(message);
            this.code = code;
        }

        public int getCode() { return code; }
    }

    // 使用自定义异常
    public void transferMoney(double amount) {
        if (amount <= 0) {
            throw new BusinessException(400, "转账金额必须大于0");
        }
        if (amount > 100000) {
            throw new BusinessException(403, "转账金额超过限额");
        }
    }

  六、异常处理最佳实践

    ✅ 尽量使用更具体的异常类型，不要直接用 Exception 捕获所有异常
    ✅ 不要在 catch 中吞掉异常 —— 至少打印日志（e.printStackTrace() 或 logger）
    ✅ 不要用异常控制正常的业务逻辑流程（异常处理性能开销大）
    ✅ 尽量使用 try-with-resources 自动释放资源
    ✅ 自定义异常时，优先继承 RuntimeException（非检查型），减少调用者负担
    ✅ 在 finally 中不要使用 return 语句 —— 会覆盖 try/catch 中的 return
    ✅ 捕获异常后要有实际的处理逻辑，不要让 catch 块为空

  总结：
    异常处理的核心思想是"将异常与正常业务逻辑分离"。
    检查型异常用于"可预见的、可恢复的"场景（如文件不存在）；
    运行时异常用于"程序缺陷"（如空指针、下标越界），应通过代码质量避免。
    合理的异常处理 + 详细的日志记录 = 高质量的 Java 程序。
    最简单的说法就是自定义异常就是额外创建一个类，然后将抛出的异常可以自定义名称，对异常进行解释
```

## 接口interface
```
  接口（Interface）是 Java 中一种引用类型，用于定义抽象方法的集合（契约）。
  它只定义方法签名，不提供方法的具体实现（JDK 8 之前），由实现类来完成具体逻辑。
  
  接口的核心作用是 "定义规范/契约" —— 它告诉外界"能做什么"，而不关心"怎么做"。

  ┌─────────────────────────────────────────────────────────────┐
  │                  Java 接口的核心特性                        │
  ├─────────────────────────────────────────────────────────────┤
  │  1. 使用 interface 关键字定义                               │
  │  2. 方法默认是 public abstract（可省略）                    │
  │  3. 变量默认是 public static final（即常量）                │
  │  4. 类通过 implements 关键字实现接口                        │
  │  5. 一个类可以实现多个接口（弥补 Java 单继承的不足）        │
  │  6. 接口不能实例化（不能 new 接口）                          │
  │  7. 接口没有构造方法                                         │
  └─────────────────────────────────────────────────────────────┘

  一、接口的定义与实现

  ── 基本语法 ──
    // 定义一个接口
    public interface Animal {
        // 抽象方法（JDK 8 之前只能定义抽象方法）
        void eat();               // 默认 public abstract
        void sleep();
        
        // 常量（默认 public static final）
        int MAX_AGE = 100;
        String CATEGORY = "生物";
    }

    // 实现类
    public class Dog implements Animal {
        @Override
        public void eat() {
            System.out.println("狗在吃骨头");
        }

        @Override
        public void sleep() {
            System.out.println("狗在睡觉");
        }
    }

    // 使用
    Animal dog = new Dog();   // 接口引用指向实现类对象（多态）
    dog.eat();                // 输出：狗在吃骨头
    dog.sleep();              // 输出：狗在睡觉
    System.out.println(Animal.MAX_AGE);   // 100（通过接口名访问常量）

  二、为什么使用接口？（接口的优势）

    1. 定义规范（契约）—— 所有实现类都必须遵循接口定义的"约定"
       · 例如：JDBC 定义了 Connection、Statement 等接口，不同数据库厂商提供各自的实现
       · 开发人员只需要面向接口编程，无需关心底层数据库是 MySQL 还是 Oracle

    2. 实现多态 —— 接口引用可以指向任何实现类的对象
       · Animal a = new Dog();  Animal a = new Cat();  // 同一套方法，不同的行为

    3. 弥补单继承的不足 —— Java 类只能继承一个父类，但可以实现多个接口
       · 例如：public class Duck extends Animal implements Flyable, Swimmable { }

    4. 解耦 —— 将"调用方"与"实现方"解耦
       · 调用方只依赖接口，不依赖具体实现类
       · 更换实现类时，调用方代码无需修改

    5. 便于扩展和维护
       · 新增功能时，只需要添加新的实现类，不影响现有代码（开闭原则）

  三、接口的多实现（Multiple Implementation）

    一个类可以实现多个接口，用逗号分隔：

    public interface Flyable {
        void fly();
    }

    public interface Swimmable {
        void swim();
    }

    // 一个类同时实现多个接口
    public class Duck implements Flyable, Swimmable {
        @Override
        public void fly() {
            System.out.println("鸭子在飞");
        }

        @Override
        public void swim() {
            System.out.println("鸭子在游泳");
        }
    }

    // 多接口引用
    Flyable f = new Duck();
    f.fly();               // 鸭子在飞

    Swimmable s = new Duck();
    s.swim();              // 鸭子在游泳

   注意：如果一个类实现了多个接口，而多个接口中有相同的抽象方法，
        实现类只需要实现一个该方法即可（因为是同一个方法的定义）。

  四、接口的继承（Interface extends Interface）

    接口可以继承多个父接口（注意：接口是 extends，且可以多继承）：

    public interface Walkable {
        void walk();
    }

    public interface Runnable extends Walkable {
        void run();
    }

    // 实现类必须实现所有父接口和子接口的抽象方法
    public class Human implements Runnable {
        @Override
        public void walk() {
            System.out.println("人在走路");
        }

        @Override
        public void run() {
            System.out.println("人在跑步");
        }
    }

    多个父接口用逗号分隔：
    public interface A extends B, C { }   // 接口多继承

  五、JDK 8+ 接口的新特性

    ── 5.1 默认方法（default method） ──
        · 使用 default 关键字，在接口中提供方法的默认实现
        · 实现类可以选择不重写，直接继承默认实现
        · 主要用于在不破坏现有实现类的前提下，向接口添加新方法

        public interface Vehicle {
            void drive();    // 抽象方法

            // 默认方法 —— 带有方法体
            default void honk() {
                System.out.println("车辆鸣笛：滴滴！");
            }
        }

        public class Car implements Vehicle {
            @Override
            public void drive() {
                System.out.println("汽车在行驶");
            }
            // 不需要重写 honk()，直接继承默认实现
        }

        // 使用
        Car car = new Car();
        car.drive();    // 汽车在行驶
        car.honk();     // 车辆鸣笛：滴滴！（继承默认方法）

        // 也可以重写默认方法
        public class Truck implements Vehicle {
            @Override
            public void drive() {
                System.out.println("卡车在行驶");
            }

            @Override
            public void honk() {
                System.out.println("卡车鸣笛：叭叭！");
            }
        }

        ── 默认方法的菱形继承问题 ──
        当一个类实现的两个接口有相同签名的默认方法时，必须重写解决冲突：

        public interface A {
            default void sayHi() { System.out.println("A"); }
        }
        public interface B {
            default void sayHi() { System.out.println("B"); }
        }

        public class C implements A, B {
            // 必须重写，否则编译报错
            @Override
            public void sayHi() {
                A.super.sayHi();    // 指定调用 A 的默认方法
                // 或 B.super.sayHi();
                // 或完全自己实现
            }
        }

    ── 5.2 静态方法（static method） ──
        · 接口中也可以定义静态方法，通过 接口名.方法名() 调用
        · 静态方法属于接口，不属于实现类，实现类无法继承
        · 常用于工具方法

        public interface MathUtils {
            static int add(int a, int b) {
                return a + b;
            }
        }

        // 调用
        int sum = MathUtils.add(3, 5);   // 8

    ── 5.3 私有方法（private method，JDK 9+） ──
        · 用于在接口内部提取公共代码，供默认方法或静态方法复用
        · 对外不可见，实现类不能访问

        public interface Logger {
            default void info(String msg) {
                log("INFO", msg);
            }

            default void error(String msg) {
                log("ERROR", msg);
            }

            // 私有方法 —— 抽取公共逻辑
            private void log(String level, String msg) {
                System.out.println("[" + level + "] " + msg);
            }
        }

  六、抽象类 vs 接口

    ┌──────────────────┬──────────────────────────┬──────────────────────────┐
    │   对比维度       │      接口 Interface      │     抽象类 Abstract Class │
    ├──────────────────┼──────────────────────────┼──────────────────────────┤
    │ 关键字           │ interface                │ abstract class           │
    │ 实例化           │ 不能实例化               │ 不能实例化               │
    │ 构造方法         │ 没有构造方法             │ 可以有构造方法           │
    │ 继承/实现数量    │ 类可以实现多个接口       │ 类只能继承一个抽象类     │
    │ 成员变量         │ public static final 常量 │ 可以有各种类型的变量     │
    │ 方法类型         │ 抽象/默认/静态/私有方法   │ 抽象方法 + 普通方法      │
    │ 访问修饰符       │ 方法默认 public          │ 可以使用所有访问修饰符   │
    │ 设计思想         │ "能做什么" —— 行为契约    │ "是什么" —— 层级抽象     │
    │ 适用场景         │ 定义行为规范、能力       │ 抽取公共状态和行为       │
    └──────────────────┴──────────────────────────┴──────────────────────────┘

    **如何选择？**
      · 如果是 "is-a" 关系（如 Dog is-a Animal），且有公共状态/字段 → 用抽象类
      · 如果是 "has-a / can-do" 关系（如 Duck can fly, can swim）→ 用接口
      · 大多数场景优先使用接口 —— 更灵活，支持多实现

  七、函数式接口（Functional Interface）与 Lambda（JDK 8+）

    · 函数式接口是指：有且仅有一个抽象方法的接口
    · 使用 @FunctionalInterface 注解标记（编译器会校验）
    · 函数式接口是 Lambda 表达式和方法引用的基础

    @FunctionalInterface
    public interface Calculator {
        int calculate(int a, int b);   // 唯一抽象方法
    }

    // 传统方式 —— 匿名内部类
    Calculator add = new Calculator() {
        @Override
        public int calculate(int a, int b) {
            return a + b;
        }
    };

    // Lambda 方式 —— 代码简洁很多
    Calculator add = (a, b) -> a + b;
    Calculator multiply = (a, b) -> a * b;

    System.out.println(add.calculate(3, 4));       // 7
    System.out.println(multiply.calculate(3, 4));  // 12

    Java 内置的常用函数式接口（java.util.function 包）：

    | 接口              | 方法签名              | 用途                 |
    |------------------|----------------------|---------------------|
    | Predicate<T>     | boolean test(T t)    | 判断条件             |
    | Consumer<T>      | void accept(T t)     | 消费数据             |
    | Function<T,R>    | R apply(T t)         | 转换数据 T → R       |
    | Supplier<T>      | T get()              | 生产数据             |
    | Comparator<T>    | int compare(T o1,T o2)| 排序比较             |

  八、接口在项目中的实际应用（Service 层设计）

    // 1. 定义接口（契约）
    public interface UserService {
        User findById(Long id);
        List<User> findAll();
        void save(User user);
        void delete(Long id);
    }

    // 2. 实现接口（具体逻辑）
    public class UserServiceImpl implements UserService {
        @Override
        public User findById(Long id) {
            // 查询数据库...
            return user;
        }

        @Override
        public List<User> findAll() {
            // 查询所有用户...
            return userList;
        }

        @Override
        public void save(User user) {
            // 保存用户...
        }

        @Override
        public void delete(Long id) {
            // 删除用户...
        }
    }

    // 3. 面向接口调用（解耦）
    // 在 Controller 中只依赖接口，不依赖具体实现
    // 这就是你提到的 "service 层和 impl 层的联系"

    这样做的好处：
      · 更换实现类时，调用方代码无需改动（比如从 UserServiceImpl 切换到 VipUserServiceImpl）
      · 便于单元测试 —— 可以 mock 接口进行测试
      · 同一接口可以有多种实现（如：本地存储 vs 云存储）

  九、接口设计最佳实践

    ✅ 接口名应体现能力（如：Runnable、Comparable、Serializable）
    ✅ 方法名应清晰表达功能意图
    ✅ 接口应遵循"接口隔离原则"（ISP）—— 接口尽量小而专一
        · 反面：一个庞大的 Animal 接口包含 fly()、swim()、run()、climb()
        · 正面：拆分为 Flyable、Swimmable、Runnable、Climbable 多个小接口
    ✅ 优先面向接口编程，而非面向实现编程
    ✅ 为接口添加 @FunctionalInterface 注解（当符合条件时）
    ✅ 使用 default 方法时注意菱形继承冲突

    ❌ 不要滥用接口 —— 如果只有一个实现类且没有替换需求，先简单，等需要时再提取接口
    ❌ 不要在接口中定义业务常量（应该放在专门的常量类中）
    ❌ 不要为了用接口而用接口，要真的有"多态"或"解耦"需求

  十、总结

    接口是 Java 实现"抽象"和"多态"的核心机制之一。
    它定义了"做什么"的规范，将调用方与实现方解耦，
    配合默认方法、函数式接口和 Lambda 表达式，使 Java 具备了函数式编程的能力。

    核心要点回顾：
      1. 使用 interface 定义，implements 实现
      2. 一个类可以实现多个接口 —— 弥补单继承
      3. 接口不能实例化，没有构造方法
      4. 方法默认 public abstract，变量默认 public static final
      5. JDK 8+ 支持 default、static 方法，JDK 9+ 支持 private 方法
      6. 函数式接口（@FunctionalInterface）支持 Lambda 表达式
      7. 面向接口编程是解耦的核心手段
```

## IO流
```
  IO 流（Input/Output Stream）是 Java 用于处理数据输入输出的一套抽象机制。
  它屏蔽了底层设备（文件、网络、键盘等）的差异，让开发者用统一的方式来读写数据。

  ┌─────────────────────────────────────────────────────────────┐
  │                   Java IO 流体系概述                       │
  │                                                             │
  │           字节流（8bit）             字符流（16bit）         │
  │          ┌────────────┐           ┌────────────┐           │
  │  输入流   │ InputStream │           │  Reader    │           │
  │  输出流   │OutputStream│           │  Writer    │           │
  │          └────────────┘           └────────────┘           │
  │                                                             │
  │  字节流 → 处理所有类型文件（图片、视频、音频等）            │
  │  字符流 → 专门处理文本文件（.txt、.java、.md 等）          │
  └─────────────────────────────────────────────────────────────┘

  一、IO 流的分类

    Java 的 IO 流可以按三个维度分类：

    ┌─────────────┬────────────────────┬────────────────────────┐
    │  分类维度   │      类别          │         说明           │
    ├─────────────┼────────────────────┼────────────────────────┤
    │ 数据方向    │ 输入流（读）       │ 从数据源读取到程序      │
    │             │ 输出流（写）       │ 从程序写出到目标        │
    ├─────────────┼────────────────────┼────────────────────────┤
    │ 数据单位    │ 字节流（8bit）     │ 处理二进制数据          │
    │             │ 字符流（16bit）    │ 处理文本数据            │
    ├─────────────┼────────────────────┼────────────────────────┤
    │ 功能角色    │ 节点流             │ 直接连接数据源          │
    │             │ 处理流（装饰器）   │ 包装节点流，增强功能    │
    └─────────────┴────────────────────┴────────────────────────┘

  二、核心抽象类

    ── 字节流 ──
      · InputStream  —— 字节输入流的根类
      · OutputStream —— 字节输出流的根类

    ── 字符流 ──
      · Reader —— 字符输入流的根类
      · Writer —— 字符输出流的根类

  三、常用实现类

    ── 文件操作 ──
      · FileInputStream  —— 从文件读取字节数据
      · FileOutputStream —— 向文件写入字节数据
      · FileReader       —— 从文件读取字符数据（默认使用系统编码）
      · FileWriter       —— 向文件写入字符数据

    ── 缓冲流（提高性能） ──
      · BufferedInputStream   —— 字节输入缓冲流
      · BufferedOutputStream  —— 字节输出缓冲流
      · BufferedReader        —— 字符输入缓冲流（提供 readLine() 按行读取）
      · BufferedWriter        —— 字符输出缓冲流

    ── 转换流（字节 ↔ 字符桥梁） ──
      · InputStreamReader    —— 字节输入流 → 字符输入流（可指定编码）
      · OutputStreamWriter   —— 字节输出流 → 字符输出流（可指定编码）

    ── 数据流（读写基本数据类型） ──
      · DataInputStream   —— 读取 Java 基本数据类型（int、double 等）
      · DataOutputStream  —— 写入 Java 基本数据类型

    ── 对象流（序列化/反序列化） ──
      · ObjectInputStream  —— 读取对象（反序列化）
      · ObjectOutputStream —— 写入对象（序列化）

    ── 标准输入输出 ──
      · System.in   —— 标准输入流（通常是键盘）
      · System.out  —— 标准输出流（通常是控制台）
      · System.err  —— 标准错误输出流

  四、基本用法示例

    ── 4.1 文件复制（字节流） ──
      try (FileInputStream fis = new FileInputStream("source.jpg");
           FileOutputStream fos = new FileOutputStream("dest.jpg")) {

          byte[] buffer = new byte[8192];  // 8KB 缓冲区
          int len;
          while ((len = fis.read(buffer)) != -1) {
              fos.write(buffer, 0, len);
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
      // 使用 try-with-resources 自动关闭流

    ── 4.2 读取文本文件（字符缓冲流） ──
      try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
          String line;
          while ((line = br.readLine()) != null) {
              System.out.println(line);
          }
      } catch (IOException e) {
          e.printStackTrace();
      }

    ── 4.3 写入文本文件 ──
      try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
          bw.write("第一行内容");
          bw.newLine();              // 换行
          bw.write("第二行内容");
      } catch (IOException e) {
          e.printStackTrace();
      }

    ── 4.4 使用转换流指定编码 ──
      // 读取 GBK 编码的文件
      try (BufferedReader br = new BufferedReader(
               new InputStreamReader(new FileInputStream("gbk.txt"), "GBK"))) {
          String line;
          while ((line = br.readLine()) != null) {
              System.out.println(line);
          }
      } catch (IOException e) {
          e.printStackTrace();
      }

      // 写入 UTF-8 编码的文件
      try (BufferedWriter bw = new BufferedWriter(
               new OutputStreamWriter(new FileOutputStream("utf8.txt"), "UTF-8"))) {
          bw.write("你好，世界");
      } catch (IOException e) {
          e.printStackTrace();
      }

    ── 4.5 数据传输（DataStream） ──
      // 写入
      try (DataOutputStream dos = new DataOutputStream(
               new FileOutputStream("data.dat"))) {
          dos.writeInt(42);
          dos.writeDouble(3.14);
          dos.writeUTF("Hello");
      } catch (IOException e) {
          e.printStackTrace();
      }

      // 读取（必须按写入顺序读取）
      try (DataInputStream dis = new DataInputStream(
               new FileInputStream("data.dat"))) {
          int i = dis.readInt();        // 42
          double d = dis.readDouble();  // 3.14
          String s = dis.readUTF();     // "Hello"
      } catch (IOException e) {
          e.printStackTrace();
      }

    ── 4.6 对象序列化 ──
      // 让对象可序列化（必须实现 Serializable 接口）
      public class Student implements Serializable {
          private static final long serialVersionUID = 1L;  // 序列化版本号
          private String name;
          private int age;
          // transient 关键字 —— 该字段不会被序列化
          private transient String password;

          // 构造方法、getter/setter 省略...
      }

      // 序列化 —— 将对象写入文件
      try (ObjectOutputStream oos = new ObjectOutputStream(
               new FileOutputStream("student.dat"))) {
          Student s = new Student("张三", 20, "123456");
          oos.writeObject(s);
      } catch (IOException e) {
          e.printStackTrace();
      }

      // 反序列化 —— 从文件读取对象
      try (ObjectInputStream ois = new ObjectInputStream(
               new FileInputStream("student.dat"))) {
          Student s = (Student) ois.readObject();
          System.out.println(s.getName());  // 张三
      } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
      }

      序列化注意事项：
        · 需要序列化的类必须实现 Serializable 接口（标记接口）
        · 使用 serialVersionUID 标识版本号，反序列化时校验
        · static 和 transient 修饰的字段不会被序列化
        · 如果父类实现了 Serializable，子类自动可序列化
        · 如果子类可序列化但父类不可序列化，父类的字段需要无参构造

  五、文件操作（File 类）

    java.io.File 类用于表示文件或目录的路径，提供创建、删除、重命名、遍历等操作。

    // 创建 File 对象
    File file = new File("C:\\test\\note.txt");
    File dir  = new File("C:\\test");

    // 常用方法
    file.exists()           // 文件/目录是否存在
    file.isFile()           // 是否是文件
    file.isDirectory()      // 是否是目录
    file.getName()          // 获取文件名（note.txt）
    file.getPath()          // 获取路径
    file.getAbsolutePath()  // 获取绝对路径
    file.length()           // 文件大小（字节）
    file.lastModified()     // 最后修改时间
    file.createNewFile()    // 创建新文件（返回 boolean）
    file.mkdir()            // 创建单级目录
    file.mkdirs()           // 创建多级目录
    file.delete()           // 删除文件/空目录
    file.list()             // 返回目录下所有文件名（String[]）
    file.listFiles()        // 返回目录下所有文件（File[]）
    file.renameTo(File dest)// 重命名/移动

    // 遍历目录示例
    File dir = new File("C:\\myFolder");
    if (dir.isDirectory()) {
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                System.out.println("文件: " + f.getName());
            } else if (f.isDirectory()) {
                System.out.println("目录: " + f.getName());
            }
        }
    }

   注意：File 类只能操作文件和目录的元数据，不能读写文件内容！
         读写内容必须使用 IO 流。

  六、装饰器模式

    Java IO 流大量使用了装饰器模式（Decorator Pattern）。
    核心思想：用处理流包装节点流，动态增强功能。

    例如：
      BufferedReader br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream("test.txt"), "UTF-8"
          )
      );

    各层的增强：
      FileInputStream      —— 节点流，读取文件原始字节
      InputStreamReader    —— 处理流，将字节解码为字符（可指定编码）
      BufferedReader       —— 处理流，提供缓冲和 readLine() 按行读取

    常见处理流组合：
      · BufferedInputStream  + FileInputStream      → 缓冲读文件
      · BufferedReader       + FileReader            → 按行读文本
      · DataInputStream      + FileInputStream       → 读基本类型数据
      · ObjectInputStream    + FileInputStream       → 读对象
      · InputStreamReader    + FileInputStream       → 指定编码读文本

  七、Java NIO（JDK 1.4+）

    传统 IO（BIO）是阻塞的、面向流的。JDK 1.4 引入了 NIO（New IO 或 Non-blocking IO），
    核心区别：

    ┌──────────┬──────────────────────┬──────────────────────────┐
    │  对比    │    传统 IO (BIO)     │      NIO                 │
    ├──────────┼──────────────────────┼──────────────────────────┤
    │ 面向     │ 面向流（Stream）     │ 面向通道（Channel）      │
    │ 缓冲区   │ 无（需手动 byte[]）  │ 内置 Buffer（ByteBuffer）│
    │ 阻塞     │ 阻塞模式             │ 支持非阻塞模式           │
    │ 选择器   │ 无                   │ Selector 多路复用        │
    │ 适用场景 │ 低并发、小文件       │ 高并发、大文件、网络编程 │
    └──────────┴──────────────────────┴──────────────────────────┘

    NIO 三大核心组件：
      1. Channel（通道）—— 连接数据源的通道，可读写（FileChannel、SocketChannel）
      2. Buffer（缓冲区）—— 数据存放的位置（ByteBuffer、CharBuffer 等）
      3. Selector（选择器）—— 监控多个 Channel 的事件（用于网络编程）

    // NIO 读取文件示例
    try (FileChannel channel = FileChannel.open(Paths.get("test.txt"),
             StandardOpenOption.READ)) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(buffer);
        while (bytesRead != -1) {
            buffer.flip();                // 切换为读模式
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
            buffer.clear();               // 清空，准备下次写入
            bytesRead = channel.read(buffer);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

   注意：日常文件读写大多数场景用传统 IO 即可；NIO 主要在高并发网络编程中使用。

  八、IO 模型概述

    ┌──────────┬──────────────┬────────────────────────┬────────────┐
    │ IO 模型  │   阻塞？     │        描述            │ Java 支持  │
    ├──────────┼──────────────┼────────────────────────┼────────────┤
    │ BIO      │ 阻塞         │ 传统 java.io，一连接一线程   │ ✔       │
    │ NIO      │ 非阻塞       │ java.nio，Selector 多路复用  │ ✔       │
    │ AIO      │ 异步非阻塞   │ JDK 7+，AsynchronousChannel │ ✔       │
    └──────────┴──────────────┴────────────────────────┴────────────┘

    · BIO（Blocking IO）：简单，但线程开销大，不适合高并发
    · NIO（Non-blocking IO）：通过 Selector 一个线程管理多个连接，适合高并发
    · AIO（Asynchronous IO）：真正的异步 IO，回调通知，但实际使用较少

  九、最佳实践

    ✅ 尽量使用 try-with-resources 自动关闭流（JDK 7+）
    ✅ 读取文本文件优先使用 BufferedReader（有 readLine()）
    ✅ 处理二进制文件（图片、视频）用 BufferedInputStream/OutputStream
    ✅ 指定编码时使用 InputStreamReader/OutputStreamWriter
    ✅ 使用缓冲流包装节点流提高性能（缓冲区大小 8KB 常见）
    ✅ 文件复制使用 8KB 缓冲区，不要逐字节读取

    ❌ 不要忘记关闭流（或用 try-with-resources）
    ❌ 不要用 FileInputStream 读取文本（字节流不能正确处理字符编码）
    ❌ 不要在 read() 返回值判断上出错 —— read() 返回 int（0~255），不是 byte
    ❌ 不要忽略序列化版本号（serialVersionUID），否则反序列化可能失败

  十、总结

    Java IO 流是一套分层设计的输入输出抽象，核心是四个基类（InputStream、
    OutputStream、Reader、Writer），通过装饰器模式组合增强功能。

    核心要点回顾：
      1. 字节流（XxxStream）处理二进制数据；字符流（XxxReader/XxxWriter）处理文本
      2. 缓冲流（BufferedXxx）提高读写性能
      3. 转换流（InputStreamReader/OutputStreamWriter）连接字节流和字符流
      4. 对象流（ObjectXxxStream）实现序列化/反序列化
      5. 使用 try-with-resources 自动释放资源
      6. File 类操作文件和目录元数据，不读写内容
      7. NIO 面向通道 + 缓冲区，支持非阻塞模式，用于高并发场景
```

## 泛型
```
  泛型（Generics）是 JDK 5 引入的重要特性，用于在编译阶段进行类型检查，
  将"类型"作为参数传递给类、接口或方法，使得代码可以操作指定类型的数据。

  泛型的核心作用："让代码可以复用，同时保证类型安全"。

  ┌─────────────────────────────────────────────────────────────┐
  │              泛型的核心设计思想                              │
  │                                                             │
  │  没有泛型之前：                                             │
  │    List list = new ArrayList();                             │
  │    list.add("Hello");                                       │
  │    String s = (String) list.get(0);  // 需要手动强转       │
  │    list.add(123);                    // 可以混入其他类型    │
  │    String s2 = (String) list.get(1); // 运行时报 ClassCastException  │
  │                                                             │
  │  有了泛型之后：                                             │
  │    List<String> list = new ArrayList<>();                   │
  │    list.add("Hello");                                       │
  │    String s = list.get(0);         // 不需要强转            │
  │    list.add(123);                  // 编译报错，类型安全     │
  └─────────────────────────────────────────────────────────────┘

  一、为什么需要泛型？

    1. 编译时类型检查 —— 把运行时的 ClassCastException 提前到编译时报错
    2. 消除类型转换 —— 不再需要手动向下转型（强转）
    3. 代码复用 —— 一套代码可以处理多种类型（对比 Object 方式，更安全）
    4. 实现通用算法 —— 如排序、搜索、集合操作等

  二、泛型的基本使用

    ── 2.1 泛型类 ──
      // T 是类型参数（Type Parameter），可以是任意标识符
      public class Box<T> {
          private T item;

          public void set(T item) {
              this.item = item;
          }

          public T get() {
              return item;
          }
      }

      // 使用
      Box<String> stringBox = new Box<>();
      stringBox.set("Hello");
      String s = stringBox.get();   // 自动返回 String 类型

      Box<Integer> intBox = new Box<>();
      intBox.set(123);
      Integer num = intBox.get();   // 自动返回 Integer 类型

    ── 2.2 泛型接口 ──
      public interface Repository<T> {
          void save(T item);
          T findById(Long id);
          List<T> findAll();
      }

      // 实现泛型接口
      public class UserRepository implements Repository<User> {
          @Override
          public void save(User user) { /* ... */ }

          @Override
          public User findById(Long id) { /* ... */ }

          @Override
          public List<User> findAll() { /* ... */ }
      }

    ── 2.3 泛型方法 ──
      // 泛型方法：在返回类型前声明类型参数
      public class Utils {
          // 静态泛型方法
          public static <T> T getLast(List<T> list) {
              return list.get(list.size() - 1);
          }

          // 非静态泛型方法
          public <T> void printArray(T[] array) {
              for (T item : array) {
                  System.out.println(item);
              }
          }
      }

      // 使用
      List<String> list = Arrays.asList("A", "B", "C");
      String last = Utils.getLast(list);  // "C"

      Integer[] nums = {1, 2, 3};
      Utils utils = new Utils();
      utils.printArray(nums);  // 自动推断 T 为 Integer

    ── 2.4 泛型构造方法 ──
      public class Pair<K, V> {
          private K key;
          private V value;

          // 泛型构造方法
          public <A, B> Pair(A key, B value) {
              // 注意：构造方法中的类型参数可以和类声明不同
          }
      }

  三、类型参数命名约定

    · T —— Type（类型）
    · E —— Element（元素，常用于集合）
    · K —— Key（键）
    · V —— Value（值）
    · N —— Number（数字）
    · R —— Result（结果）
    · ? —— 通配符（未知类型）

  四、通配符（Wildcard）

    通配符 ? 表示"未知类型"，用于在方法参数中灵活处理泛型类型。

    ── 4.1 无界通配符 ? ──
      // 可以接收任意类型的 List
      public static void printList(List<?> list) {
          for (Object obj : list) {
              System.out.println(obj);
          }
      }

      // 可以传入任何类型的 List
      printList(Arrays.asList(1, 2, 3));          // List<Integer>
      printList(Arrays.asList("A", "B", "C"));    // List<String>

      注意：List<?> 只能读取，不能写入（除了 null），因为类型未知

    ── 4.2 上界通配符 ? extends T ──
      // 表示接收 T 或 T 的子类型
      public static double sumOfList(List<? extends Number> list) {
          double sum = 0;
          for (Number num : list) {
              sum += num.doubleValue();
          }
          return sum;
      }

      // 可以传入 Number 及其子类的 List
      sumOfList(Arrays.asList(1, 2, 3));                // List<Integer>
      sumOfList(Arrays.asList(1.5, 2.7, 3.2));          // List<Double>
      sumOfList(Arrays.asList(1L, 2L, 3L));             // List<Long>

      特点：可以读取（读到 T 类型），但不能写入（无法确定具体子类型）

    ── 4.3 下界通配符 ? super T ──
      // 表示接收 T 或 T 的父类型
      public static void addNumbers(List<? super Integer> list) {
          list.add(1);
          list.add(2);
          list.add(3);
      }

      // 可以传入 Integer 及其父类的 List
      List<Number> numList = new ArrayList<>();
      addNumbers(numList);        // 可以
      List<Object> objList = new ArrayList<>();
      addNumbers(objList);        // 可以
      List<Integer> intList = new ArrayList<>();
      addNumbers(intList);        // 可以

      特点：可以写入 T 类型或其子类型，读取只能读到 Object

    ── 通配符使用原则（PECS） ──
      PECS（Producer Extends, Consumer Super）：
        · 如果要从集合中"读取"数据（生产者）→ ? extends T
        · 如果要向集合中"写入"数据（消费者）→ ? super T
        · 如果既要读又要写 → 不使用通配符，直接用 T

  五、类型擦除（Type Erasure）

    泛型是 JDK 5 才引入的，为了兼容旧代码，Java 采用"类型擦除"实现泛型。

    · 编译时：编译器会检查泛型类型，确保类型安全
    · 运行时：泛型类型信息被擦除，替换为边界类型（如 Object 或限定类型）

    // 源代码
    List<String> list = new ArrayList<>();
    list.add("Hello");
    String s = list.get(0);

    // 编译后（类型擦除）
    List list = new ArrayList();
    list.add("Hello");
    String s = (String) list.get(0);  // 编译器自动插入强转

    类型擦除带来的限制：
      1. 不能使用基本类型作为类型参数 —— 必须用包装类（Integer、Double 等）
         · List<int> ❌    List<Integer> ✔
      2. 不能创建泛型数组 —— new T[] 是不允许的
         · T[] arr = new T[10]; ❌
         · 可以通过 (T[]) new Object[10] 绕过（有警告）
      3. 不能实例化类型变量 —— new T() 是不允许的
      4. 不能使用 instanceof 检查泛型类型 —— if (obj instanceof List<String>) ❌
      5. 静态上下文中不能使用类的类型参数（因为静态成员属于类，不依赖具体实例）
         · public static T value; ❌
      6. 泛型类不能继承 Throwable —— 不能抛泛型异常

  六、泛型与继承

    注意：泛型类型之间没有继承关系！

    List<Number> list = new ArrayList<Integer>();  // ❌ 编译错误

    虽然 Integer 是 Number 的子类，但 List<Integer> 不是 List<Number> 的子类型。
    因为 List<Integer> 和 List<Number> 在运行时都是 List 类型（类型擦除），
    它们之间没有继承关系。

    正确做法：用通配符
    List<? extends Number> list = new ArrayList<Integer>();  // ✔

  七、多重边界（Multiple Bounds）

    类型参数可以指定多个上界，用 & 分隔：

    // T 必须同时是 Comparable 和 Serializable 的子类型
    public class MultiBound<T extends Comparable<T> & Serializable> {
        // ...
    }

    注意：最多只能有一个类边界，且必须是第一个；接口边界可以有多个。

    public class A { }
    public interface B { }
    public interface C { }

    // ✔ 正确：类边界在第一个，接口跟后面
    public class Test<T extends A & B & C> { }

    // ❌ 错误：接口在前，类在后
    // public class Test<T extends B & A & C> { }

  八、泛型在实际项目中的应用

    ── 8.1 集合框架（最广泛的应用） ──
      List<String> list = new ArrayList<>();
      Map<String, Integer> map = new HashMap<>();
      Set<Long> set = new HashSet<>();

      // 集合排序
      List<Student> students = getStudents();
      Collections.sort(students, (s1, s2) -> s1.getAge() - s2.getAge());

    ── 8.2 泛型 DAO / Repository（通用数据访问） ──
      public interface BaseRepository<T, ID> {
          T findById(ID id);
          List<T> findAll();
          T save(T entity);
          void deleteById(ID id);
      }

      public class BaseRepositoryImpl<T, ID> implements BaseRepository<T, ID> {
          @Override
          public T findById(ID id) {
              // 通用查询逻辑
              return null;
          }
          // ... 其他方法的通用实现
      }

      // 具体的 Repository 继承通用实现
      @Repository
      public class UserRepository extends BaseRepositoryImpl<User, Long> {
          // 可以添加 User 特有的查询方法
      }

    ── 8.3 泛型工具类 ──
      public class CollectionUtils {
          // 将一个 List 转换成另一个类型的 List
          public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
              List<R> result = new ArrayList<>();
              for (T item : list) {
                  result.add(mapper.apply(item));
              }
              return result;
          }
      }

      // 使用
      List<String> names = Arrays.asList("Alice", "Bob");
      List<Integer> lengths = CollectionUtils.map(names, String::length);
      // lengths → [5, 3]

    ── 8.4 泛型与 Optional/Stream ──
      // JDK 8+ 的 Optional 和 Stream 大量使用泛型
      Optional<String> optional = Optional.of("Hello");
      Stream<Integer> stream = Stream.of(1, 2, 3)
          .filter(n -> n > 1)
          .map(n -> n * 2);

  九、泛型最佳实践

    ✅ 命名清晰 —— 使用有意义的类型参数名（T、E、K、V 是惯例）
    ✅ 尽量让编译器自动推断类型 —— 使用菱形运算符 <>
        · Map<String, List<String>> map = new HashMap<>();  // JDK 7+
    ✅ 使用通配符让 API 更灵活 —— 遵循 PECS 原则
    ✅ 优先使用泛型集合，避免 raw type（原始类型）
        · List<String> ✔   · List ❌（编译器会警告）

    ❌ 不要使用 raw type —— List 而不是 List<String>
    ❌ 不要在泛型中使用基本类型 —— 用包装类代替
    ❌ 不要创建泛型数组 —— 用 ArrayList<T> 代替
    ❌ 不要用 instanceof 检查具体泛型类型（如 list instanceof List<String>）
    ❌ 不要在静态变量中使用类的类型参数
    ❌ 不要将泛型和数组混用（List<String>[] 虽然可以创建，但不安全）

  十、总结

    泛型是 Java 类型系统的核心扩展，它让代码更安全、更复用、更清晰。

    核心要点回顾：
      1. 泛型类/接口/方法 —— 将类型作为参数
      2. 通配符 ? —— 处理未知类型（? extends / ? super）
      3. PECS 原则 —— Producer Extends, Consumer Super
      4. 类型擦除 —— 编译时检查，运行时擦除
      5. 泛型集合 —— 最广泛的应用场景
      6. 不要使用 raw type —— 编译器会警告
      7. 菱形运算符 <> —— JDK 7+ 自动推断类型
```

---

## 🎯 面试高频考点速查

### 1. ArrayList vs LinkedList 区别（几乎是必问题）

| 对比维度 | ArrayList | LinkedList |
|---------|----------|------------|
| **底层结构** | 动态数组（连续内存） | 双向链表（离散节点） |
| **随机访问** | <span style="color:#e74c3c;font-weight:bold">**O(1)**</span> — 直接下标 | <span style="color:#e74c3c;font-weight:bold">**O(n)**</span> — 需要遍历 |
| **尾部插入** | O(1) 均摊 | O(1) 持有尾引用 |
| **头部/中间插入** | O(n) 需移动元素 | O(1) 改指针即可 |
| **内存占用** | 紧凑，CPU 缓存友好 | 每个节点多存前后指针，较大 |
| **扩容机制** | <span style="color:#e67e22;font-weight:bold">1.5 倍扩容</span> | 无扩容，随用随分配 |

<span style="color:#2980b9;font-weight:bold">**💡 面试回答技巧：**</span>
- <span style="color:#27ae60;font-weight:bold">**绝大部分场景用 ArrayList**</span>，查询快、内存紧凑、缓存友好
- 只有**频繁在头部或中间增删**才考虑 LinkedList
- LinkedList 同时实现了 Deque 接口，可用作队列/双端队列         
  Deque是双端队列，可同时在头尾进行操作，适合既需要FIFO又需要栈的操作，Queue队列是只能在尾部插入，头部删除，适合先进先出（FIFO）

---

### 2. HashMap 的 put 流程（高频手撕题）

```
① 计算 key.hashCode() 得到哈希值
② 通过 spread() 二次扰动，降低碰撞概率
③ 定位到对应的数据桶（数组下标）
④ 判断桶内情况：
   ├─ 空桶 → 直接插入新节点
   ├─ 链表 → 用 equals() 逐个比较 key
   │    ├─ 找到相同 key → 覆盖 value
   │    └─ 没找到 → 尾插（JDK 8 后）新节点
   │        └─ 链表长度 ≥ 8 且数组长度 ≥ 64 → 转红黑树
   └─ 红黑树 → 用 equals() 在树中查找
        ├─ 找到相同 key → 覆盖
        └─ 没找到 → 插入树节点
⑤ 检查是否需要扩容：size > capacity × 0.75 → 2 倍扩容
```

**关键数字：**
- 初始容量：<span style="color:#e74c3c;font-weight:bold">**16**</span>
- 负载因子：<span style="color:#e74c3c;font-weight:bold">**0.75**</span>
- 树化阈值：链表长度 <span style="color:#e74c3c;font-weight:bold">**≥ 8**</span> 且数组长度 <span style="color:#e74c3c;font-weight:bold">**≥ 64**</span>
- 扩容倍数：<span style="color:#e74c3c;font-weight:bold">**2 倍**</span>

**为什么负载因子是 0.75？**
- 权衡时间与空间：太大 → 哈希冲突多，查询慢；太小 → 浪费内存
- 0.75 是经过大量实践得出的平衡点
  
- 大于8转化为红黑树是因为这是时间和空间的平衡点，提升查询效率
---

### 3. HashSet 的去重原理<span style="color:#e67e22;font-weight:bold">（一句话说清楚）</span>

> <span style="background-color:#fff3cd;padding:0 4px;border-radius:3px">**HashSet 底层就是 HashMap，value 固定为一个常量对象（PRESENT），利用 HashMap 的 key 不可重复特性来实现去重。**</span>

```
HashSet.add(e) 实际调用 → HashMap.put(e, PRESENT)
                              └─ key = e, value = 常量对象
                                 └─ HashMap 的 key 唯一 → 自然去重
```

**去重判断流程：**
1. 先调用 `hashCode()` 定位桶
2. 再调用 `equals()` 判断是否相等
3. **只有 hashCode 和 equals 都相同**，才认为是重复元素

**所以：** 自定义对象放入 HashSet 时，<span style="color:#27ae60;font-weight:bold">**必须同时重写 hashCode() 和 equals()**</span>，否则去重会失效。

---

### 4. List / Map / Set 的选用指南（场景题）

| 需求 | 选哪个 | 理由 |
|------|--------|------|
| 存一组数据，要按下标随机访问 | **ArrayList** | 随机访问 O(1) |
| 存一组数据，频繁头尾增删 | **LinkedList** | 头尾操作 O(1) |
| 键值对存储，快速查询 | **HashMap** | 查询 O(1) |
| 键值对，线程安全高并发 | **ConcurrentHashMap** | 分段锁 / CAS |
| 元素去重，不关心顺序 | **HashSet** | 基于 HashMap，O(1) |
| 元素去重，保留插入顺序 | **LinkedHashSet** | 在 HashSet 上维护了双向链表 |
| 元素去重，自动排序 | **TreeSet** | 基于红黑树，O(log n) |
| 保持插入顺序的键值对 | **LinkedHashMap** | HashMap + 双向链表 |
| 按键排序的键值对 | **TreeMap** | 红黑树 |

CAS就是一个无锁的原子操作，通过比较当前值与预期值是否一致来判断是否更新,一致就更新，否则就重试
在高迸发情况下代替加锁操作              
优点 ： 性能好，无死锁          
缺点 ： 生产出ABA问题，和自旋消耗CPU        
ConcurrentHashMap高效的原因是：锁桶 + CAS操作，当桶为空时，直接用CAS进行无锁插入，当不为空时，只锁住当前桶的头节点，其他的桶的操作不受影响，读操作不加锁，通过volatile保证可见性。相比Hashtable就是将整个表锁住，即使操作不同的表也会遭到阻塞，并发性能极差。

<span style="color:#8e44ad;font-weight:bold">**🎯 面试官最爱问的变体：**</span>
- *"有一段文本，统计每个单词出现的次数？"* → **HashMap\<String, Integer\>**
- *"有一个用户列表，要去掉重名的用户？"* → 重写 `equals()/hashCode()` → **HashSet**
- *"LRU 缓存怎么实现？"* → **LinkedHashMap**（accessOrder = true，重写 removeEldestEntry）          
（LRU缓存就是一种缓存机制，策略就是：当缓存满了，优先淘汰最久没有被访问的缓存,least Recently Used）
---

### 5. 快速记忆口诀


```
查得快  → ArrayList / HashMap
去重    → HashSet
有序    → LinkedHashSet / LinkedHashMap
排序    → TreeSet / TreeMap
高并发  → ConcurrentHashMap
头尾操  → LinkedList
```
</span>

---

## 🔥 更多面试必考话题（你的笔记暂未覆盖）

### 6. 面向对象三大特性

| 特性 | 核心概念 | 面试关键词 |
|------|---------|-----------|
| **封装** | 隐藏内部实现，暴露公共方法 | private/getter/setter，高内聚低耦合 |
| **继承** | 子类复用父类属性和方法 | extends，super，方法重写 |
| **多态** | 同一接口，不同实现 | 重写+父类引用指向子类对象，编译看左边运行看右边 |

<span style="color:#2980b9;font-weight:bold">**❓ 面试高频追问：**</span>
- *"多态的实现条件？"* → 继承 + 重写 + 父类引用指向子类对象,解决的是行为扩展问题
- *"重写和重载的区别？"* → 重写(运行时多态，方法签名相同) / 重载(编译时多态，同类中方法名相同参数不同)
- *"构造方法能不能重写？"* → 不能，构造方法不能被继承
- *"多态与接口的区别？"* →继承让子类能直接使用父类的方法；多态则是用父类（或接口）类型的引用指向子类对象（即引入父类所包含的子类方法），在运行时调用子类重写后的方法。
---

### 7. String / StringBuilder / StringBuffer（极高频率）

| 对比 | String | StringBuilder | StringBuffer |
|------|--------|--------------|-------------|
| **可变性** | **不可变**（final char[]） | 可变 | 可变 |
| **线程安全** | 安全（不可变天然安全） | <span style="color:#e74c3c;font-weight:bold">**不安全**</span> | <span style="color:#27ae60;font-weight:bold">**安全**</span>（synchronized） |
| **性能** | 拼接时产生大量对象 | <span style="color:#27ae60;font-weight:bold">**最快**</span> | 较慢（加锁开销） |
| **适用场景** | 不变的字符串 | **单线程大量拼接** | 多线程下字符串操作 |

<span style="color:#2980b9;font-weight:bold">**💡 面试回答黄金模板：**</span>
```
String 不可变 → 每次拼接都 new 新对象，循环拼接性能极差（每一次变化都会创造一个新的内存区域，旧的等待删除）
StringBuilder 可变无锁 → 单线程字符串拼接首选
StringBuffer 可变有锁 → 多线程环境使用
日常开发 StringBuilder 用得最多

个人总结：普通String类型的对象是直接赋值创建的，底层就是在字符串常量池（一个存放字符串地址的特殊区域），里面的存放的地址就有一份，调用时就会存储字符串对象的地址，所以String本身是不可变的，变化时时创建一个新的区域来存放，旧的区域会被淘汰，所以在高并发的情况下性能会很慢，又因为其不变的特性，所以String本身就是安全的；StringBuilder和StringBuffer都是一个new 出一个对象，而底层中专门有一个堆区来存放new的对象，所以当对象被改变时是直接在堆区中进行改变，效率较高，但是我们日常开发中一般使用StringBuffer，因为其在操作的时候会加上锁，来保持高并发情况下安全有序的进行，但又因为这个锁的消耗，使得StringBuder是其中效率最高的容器，所以在单并发场景下这个是最优解。 
```

<span style="color:#2980b9;font-weight:bold">**📝 常考题：**</span>
```java
String s1 = "hello";
String s2 = "hello";
System.out.println(s1 == s2);        // true（字符串常量池）
String s3 = new String("hello");
System.out.println(s1 == s3);        // false（堆上新对象）
System.out.println(s1.equals(s3));   // true（比较内容）
```
堆区：是一个专门存放new对象的一个内存区域，每一个对象都是独一份
字符串常量池：是一个专门存放字符串地址的特殊区域，其中相同的内容只会存一份，调用的时候复用地址
---

### 8. == 与 equals() 与 hashCode()（必问组合拳）

| 比较方式 | 作用 |
|---------|------|
| **==** | 比较**内存地址**（基本类型比较值） |
| **equals()** | Object 默认也是 ==，但 **String、Integer 等已重写为比较内容** |
| **hashCode()** | 返回对象的哈希值，用于 HashMap/HashSet 定位桶 |

<span style="color:#e74c3c;font-weight:bold">**⚠️ equals 和 hashCode 的约定（重要！）：**</span>
1. 两个对象 equals 相等 → **hashCode 必须相等** ✅
2. 两个对象 hashCode 相等 → **equals 不一定相等**（哈希碰撞）
3. **重写 equals 必须同时重写 hashCode**，否则 HashSet/HashMap 会出 Bug

<span style="color:#2980b9;font-weight:bold">**❓ 面试连环问：**</span>
- *"两个对象 hashCode 相同，equals 一定相同吗？"* → 不一定，可能哈希碰撞（就比如账号密码，密码可以相同，但是账号是不同的，所以不是同一个用户）
- *"HashMap 为什么用 hashCode 和 equals 两个方法？"* → hashCode 快速定位桶，equals 精确查找
- *"不重写 hashCode 会怎样？"* → HashSet 去重会失效，相同内容的对象会重复存入

---

### 9. static 关键字（一网打尽）

| 用法 | 特点 |
|------|------|
| **静态变量** | 属于类，所有实例共享一份，类加载时初始化 |
| **静态方法** | 属于类，只能访问静态成员，不能使用 super/this |
| **静态代码块** | 类加载时执行一次，用于初始化静态资源 |
| **静态内部类** | 不依赖外部类实例，不能访问外部类非静态成员 |
| **静态导入** | `import static` 直接使用静态方法/常量 |

<span style="color:#2980b9;font-weight:bold">**❓ 面试高频题：**</span>
- *"静态方法能不能被重写？"* → <span style="color:#e74c3c;font-weight:bold">**不能**</span>，静态方法属于类，子类同名方法只是"隐藏"
- *"静态变量存在哪里？"* → **方法区（JDK 8 元空间）**，不是堆
- *"main 方法为什么是 static？"* → JVM 无需创建对象即可调用入口方法
```
静态方法（main）中不能直接调用非静态方法，因为非静态方法属于对象，必须先创建对象。
    所以我们可以创建一个类的实例对象，然后再通过实例对象调用非静态方法                  
    静态方法调用非静态方法的方式：                  
        方式一：把非静态方法改成静态方法
            public static void updateMap(...) { ... }
        方式二：在静态方法中创建对象，再调用
            exaction ex = new exaction();
            ex.updateMap(...);      
```
---

### 10. final 关键字

| 修饰位置 | 含义 |
|---------|------|
| **final 变量** | 值不可变（基本类型值不变，引用类型指向不变） |
| **final 方法** | 不能被子类重写 |
| **final 类** | 不能被继承（如 String、Integer 都是 final 类） |

<span style="color:#2980b9;font-weight:bold">**📝 常考题：**</span>
- *"final 和 finally 和 finalize 的区别？"* → final 关键字 / finally 异常处理 / finalize GC 回调（已弃用）
- *"final 修饰的引用类型，内容能变吗？"* → 引用不能变，但对象内容可以变 `final StringBuilder sb = new StringBuilder("a"); sb.append("b");` ✔

---

### 11. 反射（Reflection）（中高阶必问）

<span style="color:#e67e22;font-weight:bold">**💡 一句话定义：**</span> 在运行时动态获取类的信息（构造方法、字段、方法）并操作对象。

```java
// 获取 Class 对象的三种方式
Class<?> c1 = Class.forName("com.example.User");  // 最常用
Class<?> c2 = User.class;
Class<?> c3 = new User().getClass();

// 创建实例
Object obj = c1.getDeclaredConstructor().newInstance();

// 调用方法
Method method = c1.getMethod("setName", String.class);
method.invoke(obj, "张三");

// 访问私有字段
Field field = c1.getDeclaredField("name");
field.setAccessible(true);    // 暴力反射
field.set(obj, "李四");
```

**应用场景：**
- Spring IoC 容器（创建和管理 Bean）
- 注解解析
- JDBC 加载驱动
- 动态代理
- 一般要配合异常使用
  
**缺点：** 性能开销大，破坏封装，存在安全隐患



---

### 12. 多线程基础（高频必考）

#### 12.1 创建线程的两种方式

```java
// 方式一：继承 Thread
class MyThread extends Thread {
    @Override
    public void run() { System.out.println("线程运行"); }
}
new MyThread().start();

// 方式二：实现 Runnable（推荐，Java 单继承限制）
class MyTask implements Runnable {
    @Override
    public void run() { System.out.println("任务运行"); }
}
new Thread(new MyTask()).start();
```

#### 12.2 synchronized 关键字

| 用法 | 锁对象 |
|------|--------|
| `synchronized void method()` | 锁当前实例（this） |
| `synchronized static void method()` | 锁 Class 对象 |
| `synchronized(this) { }` | 锁当前实例 |
| `synchronized(XXX.class) { }` | 锁 Class 对象 |

<span style="color:#8e44ad;font-weight:bold">**⚡ 核心要点：**</span> synchronized 保证 **原子性、可见性、有序性**，是可重入锁、悲观锁。

#### 12.3 volatile 关键字

- 保证**可见性**：一个线程修改，其他线程立即可见
- 禁止**指令重排序**
- **不保证原子性**（i++ 操作仍需加锁）

**经典用法：** DCL 单例模式中的 `private static volatile Singleton instance;`

- volatile是一个解决可见性问题，而synchronized是解决可见性 + 原子性的问题
原子性是 要么一下子做完，要么什么都不做，中间不能被其他线程打断

#### 12.4 线程池（极其高频）

```java
// 推荐方式：ThreadPoolExecutor（明确参数含义）
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    2,                // corePoolSize 核心线程数
    5,                // maximumPoolSize 最大线程数
    60L,              // keepAliveTime 空闲线程存活时间
    TimeUnit.SECONDS, // 时间单位
    new LinkedBlockingQueue<>(10), // 阻塞队列
    new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
);
```
 线程就是程序执行任务的基本单位，负责执行具体的代码逻辑         
 线程池是管理线程的结构，控制线程数量、复用线程、任务排队，避免频繁创建销毁线程带来的性能开销。

<span style="color:#e74c3c;font-weight:bold">**📚 七大参数（必背）：**</span>
1. `corePoolSize` — 核心线程数
2. `maximumPoolSize` — 最大线程数
3. `keepAliveTime` — 空闲线程存活时间
4. `unit` — 时间单位
5. `workQueue` — 阻塞队列
6. `threadFactory` — 线程工厂
7. `handler` — 拒绝策略

<span style="color:#2980b9;font-weight:bold">**📋 四种拒绝策略：**</span>
- `AbortPolicy` — 抛异常（默认）
- `CallerRunsPolicy` — 调用者线程执行
- `DiscardOldestPolicy` — 丢弃最老任务
- `DiscardPolicy` — 直接丢弃

<span style="color:#e74c3c;font-weight:bold">**📚 线程池处理流程（必背）：**</span>
```
① corePoolSize 满了 → 进阻塞队列
② 队列满了 → 创建新线程到 maximumPoolSize
③ maximumPoolSize 满了 → 执行拒绝策略
④ 任务完成后空闲线程超过 keepAliveTime → 回收至 corePoolSize
```

---

### 13. JVM 内存模型（基础必知）

```
┌─────────────────────────────────────────┐
│              JVM 运行时数据区             │
├───────────────────┬─────────────────────┤
│  线程私有          │  线程共享            │
├───────────────────┼─────────────────────┤
│  程序计数器        │  堆（Heap）          │
│  虚拟机栈          │  方法区（元空间）     │
│  本地方法栈        │                     │
└───────────────────┴─────────────────────┘
```
    JVM是java虚拟机，也是内存怎么划分、对象存哪、垃圾回收 GC、类加载、字节码执行，是一个运行环境
| 区域 | 作用 | 是否线程共享 |
|------|------|------------|
| **程序计数器** | 当前线程执行的字节码行号 | 否 |
| **虚拟机栈** | 存储局部变量表、操作数栈、方法出口 | 否 |
| **本地方法栈** | 为 native 方法服务 | 否 |
| **堆** | 存放对象实例（GC 主要区域） | **是** |
| **方法区（元空间）** | 存储类信息、常量、静态变量 | **是** |

---

### 14. 类加载机制

<span style="color:#2980b9;font-weight:bold">**🔄 生命周期：**</span> 加载 → 验证 → 准备 → 解析 → 初始化 → 使用 → 卸载
加载：把.class文件读取到内存，生成class对象
验证：检查字节是否符合Java规范
准备：为静态变量分配内存，赋默认值
解析：将符号引用转化为直接引用（类名，方法名转化为特殊对象池）
初始化：执行静态代码块和静态变量赋值
使用：调用类的方法，创建对象
卸载：类被GC回收（垃圾回收）

<span style="color:#e74c3c;font-weight:bold">**📚 双亲委派模型（必问）：**</span>
```
                    Bootstrap ClassLoader（启动类加载器）
                           ↑
                    Platform ClassLoader (扩展类加载器)
                           ↑
                    Application ClassLoader（应用程序类加载器）
                           ↑
                    自定义 ClassLoader（自定义类加载器）
```
双亲委派模型：当一个类加载器收到类加载请求时，它不会自己先去加载，而是把请求交给父类加载器去完成，逐层向上传递。如果父类加载器加载失败，再由当前加载器自己尝试加载。

**双亲委派工作流程：**
1. 收到类加载请求，先委托给父加载器去加载
2. 父加载器无法加载时，才由子加载器自行加载

**优点：** 防止核心 API 被篡改（如自己写的 `java.lang.String` 不会被加载，因为 Bootstrap 已经加载过了），避免重复加载

---

### 15. 快速自检清单

面试前对照这个清单自查，看看哪些还不会：

- [ ] 面向对象三大特性能否说清楚？      
     -- 面向对象的三大特性：封装、继承、多态
- [ ] String、StringBuilder、StringBuffer 区别？        
     -- String不可变，每次修改都会在字符串常量池或堆中创建新对象（String s = "xxx"走常量池，new String()走堆）；StringBuilder和StringBuffer是在堆区中new一个可变对象，修改时直接在原有对象上进行，不会创建新对象。StringBuilder是不加锁的，性能最高，但线程不安全；StringBuffer是通过加锁（synchronized）来保证线程安全。        
- [ ] == 和 equals 区别？       
     -- == 对于基本类型（int、double等）比较的是值，对于引用类型才比较内存地址；equals() 在 Object 中默认也是比较地址，但 String、Integer 等类已重写为比较内容值。所以使用 equals() 前要看该类是否重写过。
- [ ] HashMap put 流程和扩容机制？      
     -- put流程：先计算 key.hashCode()，再通过 spread() 二次扰动降低碰撞概率，然后定位到数据桶。判断桶内情况：空桶直接插入；链表则用 equals() 遍历比较 key，找到相同就覆盖，没找到就尾插（JDK 8+），链表长度 ≥ 8 且数组 ≥ 64 转红黑树；红黑树则在树中查找覆盖或插入。扩容：当 size > capacity × 0.75 时按 2 倍扩容，红黑树节点 < 6 时退化为链表。           
- [ ] ArrayList 和 LinkedList 区别？        
     -- ArrayList就是一个动态数组，会自动扩容，在数组中间插入元素的时候会挪动后面的元素，效率较低，但是其查询的时候效率很高时间复杂度为O（1）,LinkedList就是一个链表，每一个节点都储存着值与上下节点的地址，所以在中间插入会很方便，只需要修改地址就好，但是其在查询的时候只能一个一个节点去寻找，查询效率很慢              
- [ ] HashSet 去重原理？        
     -- HashSet 底层就是 HashMap，value 固定为一个常量对象（PRESENT），利用 HashMap 的 key 不可重复（唯一性）特性来实现去重。去重时先通过 hashCode() 定位桶，再用 equals() 判断是否相等，两者都相同才认为是重复元素。           
- [ ] 接口和抽象类区别？        
     -- 接口（interface）是一种引用类型，定义行为契约（能做什么），方法默认 public abstract，变量默认 public static final，类通过 implements 实现，可多实现，没有构造方法，不能实例化。抽象类（abstract class）是一个类，可以拥有构造方法、成员变量、普通方法和抽象方法，通过 extends 继承，只能单继承，用于抽取公共状态和行为（是什么）。
       选择原则：有 is-a 关系且需要共享字段/代码 → 抽象类；有 can-do 能力契约 → 接口；大多数场景优先使用接口（更灵活）。         
- [ ] 异常体系（Checked / Unchecked）？         
     -- 异常体系分为不可处理的错误（Error）和可处理的异常（Exception）。Exception 又分为 Checked Exception（编译时异常，编译器强制要求 try-catch 或 throws，如 IOException）和 RuntimeException（运行时异常，编译器不强制处理，如 NullPointerException）。      
- [ ] 反射的原理和应用场景？        
     -- 反射就是通过动态的获取与操作类的信息来返还给客户端需求，主要应用在SpringIoC（bean对象的创建与管理），注解的分析，JDBC路径分析等  
- [ ] 线程池七大参数和处理流程？
     -- 七大参数为：corePoolSize（核心线程数，一直保持在线的线程，即使空闲也不会回收），maximumPoolSize（最大线程数），keepAliveTime（空闲线程最大存活时间），unit（时间单位），workQueue（阻塞队列），threadFactory（线程工厂，创建新的线程，还可以给线程命名），handler（拒绝策略）；
       处理流程：核心线程（corePoolSize）满了 → 进入阻塞队列（workQueue）；队列满了 → 创建新线程直到最大线程数（maximumPoolSize）；也满了 → 执行拒绝策略（handler）；任务完成后空闲线程超过 keepAliveTime → 回收至 corePoolSize。
- [ ] synchronized 和 volatile 区别？
     -- synchronized 解决可见性和原子性问题，适合多个线程同时修改共享变量的场景；而 volatile 只解决可见性问题，不能保证原子性，但保证每次读取都从主内存读取，确保数据最新，适合单线程写、多线程读的场景
- [ ] JVM 运行时数据区？
     -- JVM数据区包括，堆区，方法区，虚拟机栈，本地方法栈和程序计数器。其中堆区和方法区是线程共享的，虚拟机栈、本地方法栈和程序计数器是线程私有的。     
总的来说就是： 堆存对象、栈存方法调用、方法区存类信息
- [ ] 双亲委派模型？    
     -- 就是想让父类加载器先去查找，一层一层往上，如果父类找不到，就会转为自己找。
- [ ] 类加载过程？              
     -- 加载  验证  准备  解析  初始化  使用  卸载  
- [ ] 异步是什么？
     -- 异步就是在主线程中执行任务，但是不一定同主线程返回，在主线程提交任务后直接运行返回，如果任务出现错误也不会影响主线程的进行，两者递交互不阻塞。
---

