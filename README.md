llRPC支持：

​	1.通过注解远程调用，隐藏实现细节。

​    2.支持多对多调用，1个客户端可以连接多个服务端（可以是不同项目），服务端可以与多个客户端连接

​	3.支持配置修改同步异步调用，也可通过注解修改调用方式

​	4.支持消息确认机制，防止丢失消息

​	5.重写线程池已提升服务端调用方法的效率

​	6.支持连接重连与连接移除策略

​	7.支持路由扩展



使用：

client的maven引用

```markup
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>
<dependencys>
	<dependency>
	    <groupId>com.github.861346957.llRPC</groupId>
	    <artifactId>llClient</artifactId>
	    <version>1.0.3-SNAPSHOT</version>
	</dependency>
</dependencys>
```

serve的maven引用

```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
</repositories>
<dependencys>
	<dependency>
	    <groupId>com.github.861346957.llRPC</groupId>
	    <artifactId>llServe</artifactId>
	    <version>1.0.3-SNAPSHOT</version>
	</dependency>
</dependencys>
```



配置参数

client

```
llc.hosts(serve的地址)
项目名-地址:端口,地址:端口;项目名-地址:端口,地址:端口；....(一个项目时项目名可以省略，端口可以省略，默认7272)

llc.route(路由class)
可以不填，默认是循环路由，使用者也可以实现Route，填写全限定类名

llc.isConfirm（确认机制）
默认0不开启确认机制，1为开启确认机制，发送失败或者丢失的消息会重试

llc.confirmOverTime（确认超时时间，单位秒）
默认90秒，开启确认机制后，该时间范围内没发送成功的消息删除
```

serve

```
lls.monitorPort(监听端口)
默认7272

lls.coreSize(处理消息的线程数)
默认核心数

lls.maxCoreSize(处理消息的最大线程数)
默认核心数*2-1

lls.threadExpireTime(线程超时时间，单位毫秒)
默认10秒，当线程数超过处理消息的线程数时，线程空闲超过超时时间时，关闭超过

lls.threshold(任务阈值，当堆积的任务数超过该阈值时开启线程，直到线程数到最大线程数)
默认100

lls.readAndWriteTime(读写超时时间，单位秒)
默认60秒,超过该时间移除连接(客户端默认开启心跳的)，连续3次超过，移除该通道的消息缓存

lls.isConfirm（确认机制）
默认0不开启确认机制，1为开启确认机制，发送失败或者丢失的消息会重试

lls.confirmOverTime（确认超时时间，单位秒）
默认90秒，开启确认机制后，该时间范围内没发送成功的消息删除
```



demo参考

client

```
@RestController
@RequestMapping("/demo")
public class TestController {
    @Autowired
    private DemoService demoService;
    private AtomicInteger integer=new AtomicInteger(1);
    @RequestMapping("demo1")
    public String getRpc(){
        return demoService.getDemo(integer.getAndIncrement()+"");
    }
    @RequestMapping("demo2")
    public Map<String, String> getRpc1(){
        AsyncResult<Map<String, String>> result = demoService.getMap(integer.getAndIncrement() + "");
        return result.getResult();
    }
}


@IClient
public interface DemoService {

    String getDemo(String ip);
    @IAsync
    AsyncResult<Map<String,String>> getMap(String id);
}
```

serve

```
@Service
public class DemoService {
    public String getDemo(String ip){
        return ip+":rpc调用成功";
    }
    public Map<String,String> getMap(String id){
        Map<String,String> map=new HashMap<>();
        map.put("id",id);
        map.put("name","getMap");
        return map;
    }
}

```

有问题，可以通过邮箱1207123678@qq.com联系我

