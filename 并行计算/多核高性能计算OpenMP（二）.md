## OpenMP并行执行模式

OpenMP采用串行-并行-串行执行模式，其特点如下：

![img](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/fork_join2.gif)

1. 程序的串行区，由线程0开始执行，且始终存在。
2. 在parallel并行区中，根据指定线程数量，由0号线程和派生出来的线程一同执行，在并行区域最后，需要等待所有线程执行完毕后，0号线程才能继续执行。

## OpenMP组成部分

### 编译指导语句

其组织为：

|           标识符           |  指令名  |          [子句列表]          |
| :------------------------: | :------: | :--------------------------: |
| C语言采用#pragma omp为开头 | 位于中间 | 可选项，可以按照任意顺序插入 |

如下所示是编译指导语句的一个例子。

```c
#pragma omp parallel private(a) num_threads(8)
    {
        cout << "hello world";
    };
```

标识符必须存在，且固定写法为#pragma omp。

指令名，用于指导多个CPU执行任务，常用指令如下：

| 指令          | 描述                                                         |
| :------------ | :----------------------------------------------------------- |
| parallel      | 用在一个结构块之前，表示这段代码将被多个线程并行执行         |
| parallel for  | 用于for循环语句之前，表示将循环计算任务分配到多个线程中并行执行，以实现任务分担，必须由编程人员自己保证每次循环之间无数据相关性 |
| sections      | 用在可被并行执行的代码段之前，用于实现多个结构块语句的任务分担，需配合section区分各自执行的代码段。 |
| single        | 用在并行域内，表示一段只被单个线程执行的代码                 |
| critical      | 用在一段代码临界区之前，保证每次只有一个线程进入             |
| flush         | 保证各个线程的数据可见性                                     |
| barrier       | 用于并行域内代码的线程同步，线程执行到barrier时要停下等待，直到所有线程都执行到barrier时才继续往下执行 |
| atomic        | 用于指定一个数据操作需要原子性地完成                         |
| threadprivate | 指定变量为线程私有变量，注意与一般private子句的区别          |

一些指令后需要使用子句才能实现相应功能，常用子句如下：

| 子句         | 描述                                                         |
| ------------ | ------------------------------------------------------------ |
| private      | 指定一个或多个变量在每个线程中都有它自己的私有副本           |
| firstprivate | 指定一个或多个变量在每个线程都有它自己的私有副本，并且私有变量要在进入并行域或任务分担域时，继承主线程中的同名变量的值作为初值 |
| lastprivate  | 是用来指定将线程中的一个或多个私有变量的值在并行处理结束后复制到主线程中的同名变量中，负责拷贝的线程是for或sections任务分担中的最后一个线程 |
| reduction    | 用来指定一个或多个变量是私有的，并且在并行处理结束后这些变量要执行指定的归约运算，并将结果返回给主线程同名变量 |
| num_threads  | 指定并行域内的线程的数目                                     |

### 库函数

若要使用OpenMP提供的库函数，需要引入头文件，

`include <omp.h>`

 常用库函数如下：

| 函数名              | 描述                         |
| ------------------- | ---------------------------- |
| omp_set_num_threads | 设置后续并行区域的并行线程数 |
| omp_get_num_procs   | 返回当前处理器核心数量       |
| omp_get_num_threads | 返回当前区域并行线程数量     |
| omp_get_thread_num  | 返回当前线程id               |
| omp_get_dynamic     | 返回是否支持动态改变线程数量 |
| omp_set_dynamic     | 设置是否改变线程数量         |
| omp_get_wtime       | 返回双精度实数，秒           |

### 环境变量

| 变量名          | 描述                                                   |
| --------------- | ------------------------------------------------------ |
| OMP_SCHEDULE    | 用于for循环并行化后的调度，它的值就是循环调度的类型    |
| OMP_NUM_THREADS | 用于设置并行域中的线程数                               |
| OMP_DYNAMIC     | 通过设定变量值，来确定是否允许动态设定并行域内的线程数 |
| OMP_NESTED      | 指出是否可以并行嵌套                                   |

## 简单示例

采用Clion作为开发工具，关联C/C++compiler，通过CMakeList编译程序，一般的C程序无需改动CMakeList，而如果要支持OpenMP，需要在CMakeList中添加如下一行：

`set(CMAKE_CXX_FLAGS "-fopenmp")`，即编译选项增加对OpenMP的支持。

如下是通过设置不同的num_threads来比较并行计算带来的效率提升。

```c
#include<iostream>
#include"omp.h"

using namespace std;

void test() {
    for (int i = 0; i < 80000; i++);
}

int main() {

    double startTime = omp_get_wtime();
#pragma omp parallel for num_threads(8)
    for (int i = 0; i < 80000; i++) {
        test();
    }
    double endTime = omp_get_wtime();
    printf("指定 8 个线程，执行时间: %f\n", endTime - startTime);

    startTime = omp_get_wtime();
#pragma omp parallel for num_threads(4)
    for (int i = 0; i < 80000; i++) {
        test();
    }
    endTime = omp_get_wtime();
    printf("指定 4 个线程，执行时间: %f\n", endTime - startTime);

    startTime = omp_get_wtime();
#pragma omp parallel for num_threads(2)
    for (int i = 0; i < 80000; i++) {
        test();
    }
    endTime = omp_get_wtime();
    printf("指定 2 个线程，执行时间: %f\n", endTime - startTime);

    startTime = omp_get_wtime();
#pragma omp parallel for num_threads(1)
    for (int i = 0; i < 80000; i++) {
        test();
    }
    endTime = omp_get_wtime();
    printf("指定 1 个线程，执行时间: %f\n", endTime - startTime);

    startTime = omp_get_wtime();
}
```

执行结果如图所示，从结果中可以看到，执行时间与线程数量约为倍数线性关系，本机核心数量为8，因此线程数量超过8时提升的意义就不大了。反而会由于线程切换导致执行时间加长。

![image-20200624174324646](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20200624174324646.png)