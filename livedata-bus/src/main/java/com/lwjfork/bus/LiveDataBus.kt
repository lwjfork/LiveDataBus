package com.lwjfork.bus

import androidx.lifecycle.Observer
import com.lwjfork.bus.executor.DefaultTasksExecutor
import com.lwjfork.bus.executor.TasksExecutor


/**
 * Created by lwj on 2020/4/29
 * lwjfork@gmail.com
 * 替代 RxBus
 */

class LiveDataBus private constructor() {

    private val observers: MutableMap<Observer<*>, BusObserverWrapper<*>> = mutableMapOf()

    private val cacheMap: MutableMap<Int, BusMutableLiveData<Any>> = mutableMapOf()
    private val cacheStickyMap: MutableMap<Int, BusMutableLiveData<Any>> = mutableMapOf()

    private var executor: TasksExecutor = DefaultTasksExecutor()
    private var autoClear: Boolean = false

    companion object {
        private val instance: LiveDataBus by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LiveDataBus()
        }

        @JvmStatic
        fun get(): LiveDataBus {
            return instance
        }
    }

    fun autoClear(autoClear: Boolean = true): LiveDataBus {
        this.autoClear = autoClear
        return this
    }

    fun configExecutor(executor: TasksExecutor): LiveDataBus {
        this.executor = executor
        return this
    }


    @Suppress("UNCHECKED_CAST")
    @Synchronized
    fun <T : Any> with(code: Int, clazzType: Class<T>): BusLiveData<T> {
        val liveData: BusMutableLiveData<Any>? = cacheMap[code]
        val stickyLiveData: BusMutableLiveData<Any>? = cacheStickyMap[code]
        if (liveData != null) {
            return liveData as BusLiveData<T>
        }
        if (stickyLiveData != null) {
            return stickyLiveData as BusLiveData<T>
        }
        val createdLiveData = BusMutableLiveData<Any>(code)
        cacheMap[code] = createdLiveData
        return createdLiveData as BusLiveData<T>
    }


    @Suppress("UNCHECKED_CAST")
    @Synchronized
    fun with(code: Int): BusLiveData<Object> {
        return with(code, Object::class.java)
    }

    /**
     * 支持子线程发送事件，并且该事件发送为非黏性事件
     * @param code  事件的唯一标记
     * @param value  发送数据
     * @return
     */
    fun <T : Any> postEvent(code: Int, value: T) {
        sendEvent(code, value, isPost = true, isSticky = false)
    }

    /**
     * 支持子线程发送事件，并且该事件发送为非黏性事件
     * @param code  事件的唯一标记
     * @return
     */
    fun postEvent(code: Int) {
        postEvent(code, Object())
    }

    /**
     * 支持子线程发送事件，并且该事件发送为黏性事件
     * @param code  事件的唯一标记
     * @param value  发送数据
     * @return
     */
    fun <T : Any> postStickyEvent(code: Int, value: T) {
        sendEvent(code, value, isPost = true, isSticky = true)
    }

    /**
     * 支持子线程发送事件，并且该事件发送为黏性事件
     * @param code  事件的唯一标记
     * @return
     */
    fun postStickyEvent(code: Int) {
        postStickyEvent(code, Object())
    }


    /**
     * 只支持主线程发送事件，并且该事件发送为非黏性事件
     * @param code  事件的唯一标记
     * @param value  发送数据
     * @return
     */
    fun <T : Any> sendEvent(code: Int, value: T) {
        sendEvent(code, value, isPost = false, isSticky = false)
    }

    /**
     * 只支持主线程发送事件，并且该事件发送为非黏性事件
     * @param code  事件的唯一标记
     * @return
     */
    fun sendEvent(code: Int) {
        sendEvent(code, Object())
    }


    /**
     * 只支持主线程发送事件，并且该事件发送为黏性事件
     * @param code  事件的唯一标记
     * @param value  发送数据
     * @return
     */
    fun <T : Any> sendStickyEvent(code: Int, value: T) {
        sendEvent(code, value, isPost = false, isSticky = true)
    }

    /**
     * 只支持主线程发送事件，并且该事件发送为黏性事件
     * @param code  事件的唯一标记
     * @return
     */
    fun sendStickyEvent(code: Int) {
        sendStickyEvent(code, Object())
    }

    /**
     * 1.如果发送黏性事件，则会从 cacheStickyMap  集合中查找 LiveData
     *   1.1  查找到：直接发送事件
     *   1.2  没有查找到，则会从 cacheMap 查找，若有，则将其提高级别并放入 cacheStickyMap，若无，则创建 LiveData 并放入 cacheStickyMap
     *
     * 2.如果发送非黏性事件，则首先会从 cacheMap 集合中查找 LiveData , 若无则从 cacheStickyMap 中查找
     *   1.1  查找到：直接发送事件
     *   1.2  若无，则创建 LiveData 并放入 cacheMap
     *
     * @param code  事件的唯一标记
     * @param value  发送的事件-数据
     * @param isPost 是否可以在子线程发送事件
     * @param isSticky 是否是黏性事件
     * @return
     */
    private fun <T : Any> sendEvent(code: Int, value: T?, isPost: Boolean, isSticky: Boolean) {
        var mutableLiveData: BusMutableLiveData<Any>? = null
        if (isSticky) {
            var stickyLiveData: BusMutableLiveData<Any>? = cacheStickyMap[code]
            val liveData: BusMutableLiveData<Any>? = cacheMap[code]
            if (stickyLiveData == null && liveData == null) {
                stickyLiveData = BusMutableLiveData<Any>(code)
                cacheStickyMap[code] = stickyLiveData
            } else if (liveData != null) {
                cacheMap.remove(code)
                cacheStickyMap[code] = liveData
            } else if (stickyLiveData != null) {
                mutableLiveData = stickyLiveData
            }
        } else {
            val stickyLiveData: BusMutableLiveData<Any>? = cacheStickyMap[code]
            var liveData: BusMutableLiveData<Any>? = cacheMap[code]
            if (stickyLiveData == null && liveData == null) {
                liveData = BusMutableLiveData<Any>(code)
                cacheMap[code] = liveData
            } else if (stickyLiveData != null) {
                mutableLiveData = stickyLiveData
            } else if (liveData != null) {
                mutableLiveData = liveData
            }
        }
        if (isPost) {
            if (value != null) {
                mutableLiveData?.postValue(value)
            } else {
                mutableLiveData?.postValue(Object())
            }
        } else {
            execute(Runnable {
                if (value != null) {
                    mutableLiveData?.setValue(value)
                } else {
                    mutableLiveData?.setValue(Object())
                }
            })
        }
    }


    internal fun <T> addObserver(
        realObserver: Observer<in T>,
        observerWrapper: BusObserverWrapper<T>
    ) {
        observers[realObserver] = observerWrapper
    }

    internal fun <T> remove(realObserver: Observer<T>) {
        observers.remove(realObserver)
    }

    internal fun improveLiveDataLevel(code: Int) {
        if (cacheStickyMap.containsKey(code)) {
            return
        }
        if (cacheMap.containsKey(code)) {
            val liveData: BusMutableLiveData<Any> = cacheMap[code]!!
            cacheStickyMap[code] = liveData
        }
    }

    /**
     * 如果是 非黏性事件，则应该及时清除 LiveData，降低内存消耗
     */
    internal fun clearLiveDataByTag(tag: Int, sticky: Boolean) {
        if (autoClear) {
            val liveData: BusMutableLiveData<Any>? = cacheMap[tag]
            if (liveData != null) {
                val registerCount: Int = liveData.registerCount
                if (registerCount == 0) {
                    cacheMap.remove(tag)
                }
            }
        }
    }


    internal fun execute(runnable: Runnable, isMainThread: Boolean = true) {
        executor.execute(runnable, isMainThread)
    }

    internal fun runOnUI(runnable: Runnable) {
        executor.execute(runnable, isMainThread = true)
    }

    internal fun runOnIO(runnable: Runnable) {
        executor.execute(runnable, isMainThread = false)
    }
}