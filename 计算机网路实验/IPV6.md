### IPV6地址

128bit

- 首选格式，也称冒号16进制，

![image-20201024144143433](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201024144143974.png)

- 压缩格式，连续的0组划用：：代替，只能出现一次

![image-20201024144221502](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201024144221502.png)

- 内嵌IPV4

![image-20201024144300550](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201024144300550.png)

同样存在网络前缀的概念

![image-20201024144335862](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201024144335862.png)

未指定地址：::

环回地址：::1

被请求节点组播地址：

![image-20201024144731243](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201024144731243.png)

### IPV6结构

报头40字节，扩展报头无限制，无分段（分段处理都在源和目的地），去掉检验和

### 扩展报头

- 逐跳选项报头：路由器必须查看的报头，超大数据报的长度也在这里
- 目标选项报头：接收主机查看
- 路由报头：选择中间路径的
- 分段报头：源、目的地进行处理

![image-20201024170045703](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201024170045703.png)

下一个扩展报头的类型、本扩展报头长度，option由TLV方式表达。特殊的，pad选项用于填充报头，以符合边界定义

### ICMPv6

传统的ICMPv4用于向源节点发送有关目的地址的信息，包括目的不可达、数据包过长、超时等，v6版本在此基础上实现了更多的功能。ICMP也属于IP协议，并不属于传输层协议

- 差错报文：目标不可达、数据报超长、超时、参数问题。8bit类型字段首位为0
- 信息报文：多播侦听发现MLD、邻接点发现ND、回送请求、回送应答。8bit类型字段首位为1

### 邻居发现协议ND

#### 功能：

- 地址自动配置
- 路由器发现
- 前缀发现
- 重复地址检测
- 地址解析

#### 报文：

- 路由器请求报文router solicitation：不等待路由器的周期性通告，而是2立即向路由器发送请求报文，就可以立刻得到路由器配置的链路参数
  - hop limit，限制其他链路的请求报文占用本链路的带宽
  - 链路本地地址后24位与mac地址后24位相同
- 路由器通告报文router advertisement：路由器周期性的通告链路状态、网络参数。
  -  cur hop limit：主机发送报文使用的默认跳数限制
  - router lifetime：作为缺省路由器的生命周期
  - reachable time：可达保有时间
  - retrans timer：重传时间
  - Options：
    - 链路层地址
    - MTU选项
    - 前缀信息选项
- 邻居请求报文Neighbor Solicitation：请求邻居节点的链路层地址
- 邻居通告报文Neighbor Advertisement：
- 重定向报文Redirect

### 组播侦听发现协议MLD

![image-20201024174738965](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201024174738965.png)