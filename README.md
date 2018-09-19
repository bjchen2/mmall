# mmall
慕课网—Java从零到企业级电商

**注意：**本项目与视频源码大致实现功能，思想相同，但代码结构上有很多区别，请谨慎使用，如：

- 本项目不在Service层构造封装数据，Service层只返回Data值，由Controller层使用封装好的ResultUtil生成封装数据
- 本项目将管理员身份验证和用户登录信息验证使用AOP切面拦截并判断，不再重复在Controller层判断
- 枚举类不在通过自身实现codeOf方法获取适应值的枚举，而是使枚举类继承一个公共接口，并通过EnumUtil封装的方法获取
- **支付宝接入只引入了官方pom文件，没有使用demo提供的jar包**，所以在接入上有很大的区别，比如，自己封装AlipayConfig,ZxingUtil等
- 多数for循环访问数据库改为一次性从数据库获取数据再进行处理，**因为数据库访问是一个极慢的操作**
- 抛出异常用handler捕获并处理，抛出异常的地方使用log记录了日志
- ......

**特点** ：

1. 类目表使用了无限级分类表
2. 使用mybatis三剑客（mybatis-plugin、mybatis-generator、mybatis-pagehelper）
3. 接入 支付宝当面付 支付
4. 封装好 FTPUtil 用于上传图片到 FTP 服务器上
5. ......