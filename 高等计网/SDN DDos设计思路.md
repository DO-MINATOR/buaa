DDos防御机制：OpenFlow在网络管理和安全控制中的应用。如果网络是基于OpenFlow技术实现的，则经过OpenFlow交换机的每个新的数据流都必须由控制器来做出转发决定。在控制器中可以对这些流按照预先制定的规则进行检查，然后由控制器指定数据流的传输路径以及流的处理策略，从而更好的控制网络。更为重要的是，在内部网络和外网的连接处应用OpenFlow交换机可以通过更改数据流的路径以及拒绝某些数据流来增强企业内网的安全性。

流表下发：流表是交换机进行转发策略控制的核心数据结构。交换机芯片通过查找流表项来决策进入交换机网络的数据包执行适当的处理动作。下发流表就好比向交换机发送一条指令。

### 所用工具：

- mininet：组网软件

- sFlow：监测交换机信息（统计信息、数据信息）

- OpenFlow：通信协议（用于控制网络设备的转发平面），下发FlowTable
- Floodlight：实现了OF通信协议的控制器

### 参考博客：

- [DDoS 攻击与防御：从原理到实践](https://blog.csdn.net/wangyiyungw/article/details/80537891)
- [Mininet 安装及可视化操作](https://blog.csdn.net/AsNeverBefore/article/details/78916645)
- [常用SDN控制器安装部署之Floodlight篇](https://www.sdnlab.com/2909.html)
- [SDN-LAB实验平台](https://www.sdnlab.com/experimental-platform/)
- [sFlow流量监控之DDoS防御](https://www.sdnlab.com/sflow-ddos/)



