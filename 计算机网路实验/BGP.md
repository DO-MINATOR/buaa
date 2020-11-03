自治系统内部使用相同的IGP，如OSPF、RIP；自治系统外部使用EGP，如BGP。通过外部路由协议和自治系统编号，就可以确定到达目的AS的路由，改编号由组织分配，1-65411是注册的英特网编号，65412-65535是专用编号。

BGP支持无类别域间路由选择CIDR，可以减少路由表无限制的扩大，同时借助编号信息，来消除路由环路。

#### 通告原则：

- 将收到的EBGP传递给它的BGP对等体（包括EBGP和IBGP）
- 收到的IBGP不会发送到其他IBGP，但是否传递到EBGP，要视IGP和BGP的同步情况来决定

#### 注入方式：

- 动态注入：将从IGP获取的内部路由信息直接封装在BGP中，配置简单，但没有过滤
- 半动态注入：有选择的将IGP信息注入到BGP
- 静态注入：将手工指定的某条路由信息加入到BGP

#### 报文类型：

- OPEN报文：交换各自的版本、自治系统编号、保持时间等
- UPDATE报文： 路由更新信息，包括撤销路由和新的可达路由，路径属性
- NOTIFICATION：检测到差错时，发送该报文，用于关闭对等体的连接
- KEEPALIVE：对等体间周期性的发送，确保连接有效

#### 状态转换：

1. Idle
2. Connect
3. Active
4. Open-sent
5. Open-confirm
6. Established

#### 路由聚合

将as内部路由聚合一起再发送给BGP，可以减小路由传输量，同时隔离了部分网络拓扑变化，保持了网络稳定。

#### 常见属性

- Origin：IGP（network配置、路由聚合）、EGP（其他AS传递过来的）、Incomplete（IGP传输过来的）
- As-path：经过的AS路由号，保证了无环路
- Next-hop：EBGP传输时，next-hop为EBGP路由地址，在向IBGP传输时，不改变路由地址
- med

![image-20201006124512598](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201006124512598.png)

- local-preference

![image-20201006124537803](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/image-20201006124537803.png)

#### 路由策略

ACL：

路由策略：条件匹配后，进行属性设置