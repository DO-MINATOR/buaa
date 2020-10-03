## COPYIN子句

针对threadprivate声明的线程持续私有变量，使用该子句可以将变量值赋值给并行区域各线程，而不仅局限于0号线程。

```c
#include <iostream>
#include <omp.h>

using namespace std;
int g = 0;//g为全局变量
#pragma omp threadprivate(g)//声明其为线程持续私有变量

int main() {

#pragma omp parallel for num_threads(4)
    for (int i = 0; i < 5; i++) {
        g++;
        cout << g << " in thread " << omp_get_thread_num() << endl;
    }
    cout << endl;
#pragma omp parallel for num_threads(4) copyin(g)
    for (int i = 0; i < 4; i++) {
        cout << g << " in thread " << omp_get_thread_num() << endl;
    }
}
```

执行上述代码，得到结果如下：

![image-20200718132547292](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20200718132547292.png)

## COPYPRIVATE子句

copyprivate子句只能用在single指令后，具体作用是将执行线程的变量值复制给其余线程同名变量，由于执行single程序段的线程是随机的，因此执行结果即复制的值有可能不同。

## REDUCTION子句

用于将并行区域计算后的结果进行规约，需要注意，reduction默认已经对变量声明了private构造。

```c
int main() {
    int sum = 0;
#pragma omp parallel for num_threads(4) reduction(+: sum)
    for (int i = 0; i < 5; i++) {
        sum += i;
        cout << sum << " in thread " << omp_get_thread_num() << endl;
    }
    cout << sum << endl;
}
```

另外，reduction会对变量默认复制，具体如下

| 运算符 | 初始值 |
| -------- | ------ |
| + | 0 |
| - | 0 |
| * | 1 |
| and | true |
| or | false |
| eqv | true |
| neqv | false |
| min | 极大值 |
| max | 极小值 |
