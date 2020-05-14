# LiveDataBus
基于Android  LiveData/Lifecycle 实现的事件总线 ，兼容 AndroidX

## 引入依赖
该库已上传至 jcenter

```
 implementation 'com.github.lwjfork:livedata-bus:1.0.0'
```

## 初始化


```
  LiveDataBus
  .get()
  .autoClear(true) // 配置是否自动清理无订阅的LiveData。默认为 true
  .configExecutor(new DefaultTasksExecutor()) // 若无配置，使用默认调度器
```


#### 1 配置是否自动清理无订阅的LiveData。默认为 清理
```
LiveDataBus
  .get()
  .autoClear(true)
  
```

#### 2 配置线程调度

```
 LiveDataBus
  .get()
  .configExecutor(new DefaultTasksExecutor())

```




### 事件订阅与发送
### 1. 普通(非黏性)事件
#### 1.1 生命周期感知订阅
```

// 订阅事件
LiveDataBus.get()
      .with(BusCode.COMMON_CODE)
      .observe(owner,new Observer<Objcet>(){
          @Override
          public void onChanged(Object o) {
                    
          }
      })
 
// 发送事件      
LiveDataBus.get().sentEvent(BusCode.COMMON_CODE)



// 订阅事件 ，接受自定义数据
LiveDataBus.get()
      .with(BusCode.COMMON_CODE,String.class)
      .observe(owner,new Observer<String>(){
          @Override
          public void onChanged(String o) {
                    
          }
      })
 
// 发送事件  ，发送自定义数据    
LiveDataBus.get().sentEvent(BusCode.COMMON_CODE,"testBusEvent")

```
#### 1.2 永久订阅，
* 永久订阅中，可以手动移除 Oberver ，防止内存泄漏

```
// 订阅事件
LiveDataBus.get()
      .with(BusCode.COMMON_CODE)
      .observeForever(new Observer<Objcet>(){
          @Override
          public void onChanged(Object o) {
                    
          }
      })
 
// 发送事件      
LiveDataBus.get().sendEvent(BusCode.COMMON_CODE)



// 订阅事件 ，接受自定义数据
LiveDataBus.get()
      .with(BusCode.COMMON_CODE,String.class)
      .observeForever(new Observer<String>(){
          @Override
          public void onChanged(String o) {
                    
          }
      })
 
// 发送事件  ，发送自定义数据    
LiveDataBus.get().sendEvent(BusCode.COMMON_CODE,"testBusEvent")

```

#### 2. 发送黏性事件
#### 2.1 生命周期感知订阅
```

// 订阅事件
LiveDataBus.get()
      .with(BusCode.COMMON_CODE)
      .observeSticky(owner,new Observer<Objcet>(){
          @Override
          public void onChanged(Object o) {
                    
          }
      })
 
// 发送事件      
LiveDataBus.get().sendStickyEvent(BusCode.COMMON_CODE)
// 发送事件      
// LiveDataBus.get().postStickyEvent(BusCode.COMMON_CODE)



// 订阅事件 ，接受自定义数据
LiveDataBus.get()
      .with(BusCode.COMMON_CODE,String.class)
      .observeSticky(owner,new Observer<String>(){
          @Override
          public void onChanged(String o) {
                    
          }
      })
 
// 发送事件  ，发送自定义数据    
LiveDataBus.get().sendStickyEvent(BusCode.COMMON_CODE,"testBusEvent")
// LiveDataBus.get().postStickyEvent(BusCode.COMMON_CODE,"testBusEvent")

```
#### 2.2 永久订阅，
* 永久订阅中，可以手动移除 Oberver ，防止内存泄漏

```
// 订阅事件
LiveDataBus.get()
      .with(BusCode.COMMON_CODE)
      .observeStickyForever(new Observer<Objcet>(){
          @Override
          public void onChanged(Object o) {
                    
          }
      })
 
// 发送事件      
LiveDataBus.get().sendStickyEvent(BusCode.COMMON_CODE)
// LiveDataBus.get().postStickyEvent(BusCode.COMMON_CODE)



// 订阅事件 ，接受自定义数据
LiveDataBus.get()
      .with(BusCode.COMMON_CODE,String.class)
      .observeStickyForever(new Observer<String>(){
          @Override
          public void onChanged(String o) {
                    
          }
      })
 
// 发送事件  ，发送自定义数据    
LiveDataBus.get().sendStickyEvent(BusCode.COMMON_CODE,"testBusEvent")
// LiveDataBus.get().postStickyEvent(BusCode.COMMON_CODE,"testBusEvent")


```





