package com.lwjfork.bus

import androidx.lifecycle.*

/**
 * Created by lwj on 2020/4/30
 * lwjfork@gmail.com
 */

abstract class BusLiveData<T> : LiveData<T> {

    internal var version: Int = Int.MIN_VALUE
    internal var tag: Int = -1

    //  注册次数
    internal var registerCount = 0;

    constructor(tag: Int) : super() {
        this.tag = tag
    }

    constructor(tag: Int, value: T) : super(value) {
        this.tag = tag
    }


    override fun setValue(value: T) {
        version = version.plus(1)
        super.setValue(value)
    }

    override fun postValue(value: T) {
        version = version.plus(1)
        super.postValue(value)
    }


    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observe(owner, observer, true)
    }

    fun observe(owner: LifecycleOwner, observer: Observer<in T>, uiThread: Boolean) {
        observe(owner, observer, sticky = false, uiThread = uiThread)
    }

    override fun observeForever(observer: Observer<in T>) {
        observeForever(observer, uiThread = true)
    }

    fun observeForever(observer: Observer<in T>, uiThread: Boolean) {
        observeForever(observer, sticky = false, uiThread = uiThread)
    }


    override fun removeObserver(observer: Observer<in T>) {
        super.removeObserver(observer)
        LiveDataBus.get().remove(observer)
        registerCount--
    }

    private fun observeForever(observer: Observer<in T>, sticky: Boolean, uiThread: Boolean) {
        LiveDataBus.get().runOnUI(Runnable {
            super.observeForever(BusObserverWrapper(this, observer, version, sticky = sticky, uiThread = uiThread))
        })
    }

    private fun observe(owner: LifecycleOwner, observer: Observer<in T>, sticky: Boolean, uiThread: Boolean = true) {
        LiveDataBus.get().runOnUI(Runnable {
            super.observe(owner, BusObserverWrapper(this, observer, version, sticky, uiThread))
            registerCount++
            owner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        LiveDataBus.get().remove(observer)
                        registerCount--
                        LiveDataBus.get().clearLiveDataByTag(tag, sticky)
                    }
                }
            })
        })
    }

    // 观察粘性事件
    fun observeSticky(owner: LifecycleOwner, observer: Observer<in T>, uiThread: Boolean = true) {
        observe(owner, observer, sticky = true, uiThread = uiThread)
        // 黏性事件，则可能需要提高级别
        LiveDataBus.get().improveLiveDataLevel(tag)
    }

    // 观察粘性事件
    fun observeStickyForever(observer: Observer<in T>, uiThread: Boolean = true) {
        observeForever(observer, sticky = true, uiThread = uiThread)
        // 黏性事件，则可能需要提高级别
        LiveDataBus.get().improveLiveDataLevel(tag)
    }


}