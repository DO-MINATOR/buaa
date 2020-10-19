所用工具：

mininet：组网软件

sFlow：监测交换机信息（统计信息、数据信息）

OpenFlow：通信协议（用于控制网络设备的转发平面），下发FlowTable

主动模式、被动模式



OpenFlow在[网络管理](https://baike.baidu.com/item/网络管理)和安全控制中的应用。如果网络是基于OpenFlow技术实现的，则经过OpenFlow[交换机](https://baike.baidu.com/item/交换机)的每个新的[数据流](https://baike.baidu.com/item/数据流)都必须由控制器来做出转发决定。在控制器中可以对这些流按照预先制定的规则进行检查，然后由控制器指定[数据流](https://baike.baidu.com/item/数据流)的传输路径以及流的处理策略，从而更好的控制网络。更为重要的是，在内部网络和外网的连接处应用OpenFlow[交换机](https://baike.baidu.com/item/交换机)可以通过更改[数据流](https://baike.baidu.com/item/数据流)的路径以及拒绝某些数据流来增强企业内网的安全性。