package com.banxa.nativepaymentssdk.di

object ServiceLocator {

    lateinit var container: AppContainer

    fun init() {
        container = AppContainer()
    }
}