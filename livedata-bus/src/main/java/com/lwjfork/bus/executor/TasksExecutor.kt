package com.lwjfork.bus.executor

/**
 * Created by lwj on 2020/5/7
 * lwjfork@gmail.com
 */
abstract class TasksExecutor {


    // 执行在 io 线程
    abstract fun runOnIO(runnable: Runnable)

    // 执行在 UI 线程
    abstract fun runOnUI(runnable: Runnable)

    /**
     * 执行任务
     * @param runnable 任务
     * @param isMainThread 是否执行在主线程，默认是执行在主线程
     */
    fun execute(runnable: Runnable, isMainThread: Boolean = true) {
        if (isMainThread) {
            runOnUI(runnable)
        } else {
            runOnIO(runnable)
        }
    }

    // 当前线程是否是主线程
    abstract fun isUIThread(): Boolean


}