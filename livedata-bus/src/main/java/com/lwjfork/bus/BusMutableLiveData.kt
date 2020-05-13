package com.lwjfork.bus

/**
 * Created by lwj on 2020/4/30
 * lwjfork@gmail.com
 */
internal class BusMutableLiveData<T : Any?> : BusLiveData<T> {

    constructor(tag: Int) : super(tag)
    constructor(tag: Int, value: T) : super(tag, value)


    public override fun postValue(value: T) {
        super.postValue(value)
    }

    public override fun setValue(value: T) {
        super.setValue(value)
    }
}