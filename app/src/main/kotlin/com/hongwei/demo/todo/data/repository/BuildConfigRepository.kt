package com.hongwei.demo.todo.data.repository

import com.hongwei.demo.todo.BuildConfig
import javax.inject.Inject

class BuildConfigRepository @Inject constructor() {

    fun test() {
        val dataStorageFlavor = BuildConfig.DATA_STORAGE

    }
}