package com.lwjfork.bus

import androidx.lifecycle.Observer

/**
 * Created by lwj on 2020/4/30
 * lwjfork@gmail.com
 */
class BusObserverWrapper<T>(
        private var liveData: BusLiveData<T>,
        private var realObserver: Observer<in T>,
        private var version: Int, // Observer 的版本号
        private var sticky: Boolean,// 是否要进行粘性监听
        private var uiThread: Boolean = true // 是否在主线程进行回调
) : Observer<T> {

    init {
        LiveDataBus.get().addObserver(realObserver, this)
    }

    override fun onChanged(t: T) {
        LiveDataBus.get().execute(Runnable { changed(t) }, uiThread)
    }
    private fun changed(t: T) {
        if (sticky) {
            // 粘性监听，
            realObserver.onChanged(t)
        } else {
            // 非粘性监听，比较 Observer 和 BusLiveData 的版本号
            if (liveData.version > version) {
                realObserver.onChanged(t)
            }
        }
    }


}