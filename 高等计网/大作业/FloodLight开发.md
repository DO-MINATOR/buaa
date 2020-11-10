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

- 