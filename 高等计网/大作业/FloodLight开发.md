### FloodLight大致执行流程及组件关系

![Floodlight启动流程图](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/20170330152748238)

Main：

- new CmdLineParser：用于获取用户命令行配置参数类
- new loodlightModuleLoader：创建模块加载器类
- loadModulesFromConfig()：调用加载方法，传入配置文件，并加载模块，返回模块上下文环境
- getServiceImpl()：从上下文环境启动restApi，对外暴露接口
- runmodules()：启动controller及其模块

loadModulesFromConfig：

- new Properties：解析配置文档中非模块配置的属性
- new Collection<String//> configMods：存放待配置的模块
- mergeProperties()：将配置文件解析到prop中

loadModulesFromList：

- findAllModules(configMods)：将已配置好的模块解析到三个成员变量中
- parseConfigParameters()：将配置属性解析并放入到模块的上下文环境中
- initModules()：初始化模块

findAllModules：

1. serviceMap：将模块所提供的服务与模块本身相关联，如将MemoryStorageSource模块与IStorageSourceService接口服务相关联，该接口同时需实现FloodService方法。
2. moduleServiceMap：与service相反，实现模块服务与模块间的映射
3. moduleNameMap：模块名称和模块实例间的映射

initModules：

- getServiceImpls()：获取模块提供服务的实例
- addService()：将服务加入到模块上下文环境中
- init()：遍历模块服务列表，调用其init方法。

runModules：

- new List<RunMethod//>：创建待执行模块集合
- getAnnotation(run.class)：获取带有@Run注解的方法
- run()：执行方法

### Controller运行机制

```java
public class Controller implements IFloodlightProviderService, IStorageSourceListener, IInfoProvider {
    //定义了一个模块队列，所有待执行的模块都将添加到这个队列中
    protected static BlockingQueue<IUpdate> updates;
    //待执行模块须实现dispatch方法
    public interface IUpdate {
        /**
         * Calls the appropriate listeners
         */
        public void dispatch();
    }

    //向队列添加一个update
    @Override
    public void addUpdateToQueue(IUpdate update) {
        try {
            updates.put(update);
        } catch (InterruptedException e) {
            // This should never happen
            log.error("Failure adding update {} to queue.", update);
        }
    }
    //run方法中是从队列中取出IUpdate实现并调用dispatch方法
    @Override
    public void run() {
        moduleLoaderState = ModuleLoaderState.COMPLETE;

        if (log.isDebugEnabled()) {
            logListeners();
        }

        while (true) {
            try {
                IUpdate update = updates.take();
                update.dispatch();
            } catch (InterruptedException e) {
                log.error("Received interrupted exception in updates loop;" +
                          "terminating process");
                log.info("Calling System.exit");
                System.exit(1);
            } catch (StorageException e) {
                log.error("Storage exception in controller " +
                          "updates loop; terminating process", e);
                log.info("Calling System.exit");
                System.exit(1);
            } catch (Exception e) {
                log.error("Exception in controller updates loop", e);
            }
        }
    }
}
```

### IUpdate实现类

#### SwitchUpdate

​	处理交换机事件（添加交换机、删除交换机、端口改变等）

#### HAControllerNodeIPUpdate

#### HARoleUpdate

OFChannelHandler负责交换机的目标发现，状态识别，通信配置等，主要收发Hello、Feature、Echo报文，并进行链接，状态转移图如下

![这里写图片描述](https://imagebag.oss-cn-chengdu.aliyuncs.com/img/20170412160614120)

交予上层的OFSwitchHandshakeHandler进行处理，实现报文的管理和分发。如果是master manger，则可以对交换机进行读写操作，如果是slave manger，则只能进行读操作。另外，master manger还负责采集交换机的packet-in报文，将packet-in报文收集后，交予controller，并由controller下的相应模块来处理

### LinkDiscoveryManager

通过LLDP和BDDP进行数据链路的发现，发送LLDP，如果从packet-in报文中收到相同id的LLDP，则认为下方具有支持OF协议的交换机，如果没有收到LLDP，则发送BDDP，如果收到相同id的BDDP，则认为有非OF协议支持的交换机，如果都没有收到，则处于网络边缘。

维护了四个队列

```java
protected LinkedBlockingQueue<NodePortTuple> quarantineQueue;//发送BDDP的端口集合
protected LinkedBlockingQueue<NodePortTuple> maintenanceQueue;//发送LDDP的端口集合
protected LinkedBlockingQueue<NodePortTuple> toRemoveFromQuarantineQueue;//quarantineQueue移出端口集合
protected LinkedBlockingQueue<NodePortTuple> toRemoveFromMaintenanceQueue;//maintenanceQueue移出端口集合
```

用于控制发送LLDP与BDDP

LLDP发送分为两个方面，一是链路发现模块启动后，周期性的向所有交换机端口发送LLDP报文，二是在新加入交换机或交换机端口改变时进行发送。在发送LLDP后，会将该端口信息放入到maintenanceQueue中。由于实现了IOFMessageListener，所以可以处理进入控制器的packet-in报文，将其解包后，获得LLDP消息，通过比较发送、接收的LLDP id信息，如果二者相同，则将该端口移除maintenanceQueue队列中，并加入到toRemoveFromQuarantineQueue和toRemoveFromMaintenanceQueue队列中，这样就不会再向其发送LLDP和BDDP报文了。

BDDP发送，则是通过取出quarantineQueue队列中的端口信息，并在toRemoveFromQuarantineQueue和toRemoveFromMaintenanceQueue队列中查看有无此端口信息，如果没有，则发送BDDP报文，否则不发送。

### TopologyManager

处理交换机事件的SwitchUpdate，当检测到链路发生变化时（新增、减少、端口变化）则会进行controller调度dispatch方法，进而执行LinkDiscoveryManager的sendDiscoveryMessage方法，发送LLDP和BDDP消息，进行链路设备的发现，根据获取的信息生成相应的拓扑结构

### Forwarding

该模块负责处理计算源和目的地设备间的最短路径，并通过下发流表的方式实现数据包转发，receive函数接收pack-in报文，通常只处理新加入的数据源，对于已有的数据源，因为已经计算过了转发规则，并已下发流表，所以交换机不会再次请求控制器为其计算路径。