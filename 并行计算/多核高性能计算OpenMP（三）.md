## private子句

对于一般的局部变量，使用private子句构造各线程的私有变量。

- private，如下所示，各线程私有k、l变量，且该变量和串行区同名变量无关联。

```c
  #pragma omp parallel private(k,l)
      {
          ...
      }
```

- firstprivate，如下所示，相比private只是多了一个初始值传递，即各线程私有变量值与串行区同名变量值相同。

```c
#pragma omp parallel firstprivate(k,l)
    {
        ...
    }
```

- lastprivate，如下所示，必须配合for循环，并行结束后，将值传回串行区同名变量，且被传递对象为逻辑执行的最后一次循环，而不是最后一个运行完的线程。

```c
#pragma omp parallel for lastprivate(A)
    for (int i = 0; i < 10; i++) {
        A=i;
    }
```

## shared子句

并行区共享串行区的变量，如果不加以说明，shared是默认选择的。需要注意对共享变量的访问，要注意访问控制，如下所示，在4线程并行执行时，A结果一般不为10000。

```c
int A = 0;

#pragma omp parallel for num_threads(4)
    for (int i = 0; i < 10000; i++) {
        A++;
    }
c
    printf("%d\n", A);   // #2
```

## threadprivate指令

注意，它不是子句，虽然看上去和private/firstprivate很像。只能针对全局变量使用，如下所示：

```c
#include <iostream>
#include <omp.h>

using namespace std;
int g = 0;//g为全局变量
#pragma omp threadprivate(g)//声明其为线程持续私有变量

int main() {
    int A = 0;

#pragma omp parallel
    {
        g = omp_get_thread_num();//8个线程，各自为自己的g私有变量赋值
    };
    cout << g << endl;
#pragma omp parallel
    {
        cout << g << " in thread-" << omp_get_thread_num() << endl;
        //由于g为持续私有变量，因此第二个并行区g变量的值仍然保持
    };
    cout << g << " in thread-" << omp_get_thread_num() << endl;
    //主线程id也是0，因此并行区0号线程和串行区线程持续持有该私有变量
    return 0;
}
```

