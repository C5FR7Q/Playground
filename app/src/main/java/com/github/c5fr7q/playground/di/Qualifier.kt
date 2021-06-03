package com.github.c5fr7q.playground.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SygicClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnsplashClient