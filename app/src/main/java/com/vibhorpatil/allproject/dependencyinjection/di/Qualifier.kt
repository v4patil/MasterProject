package com.vibhorpatil.allproject.dependencyinjection.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationContext

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ActivityContext

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseURL

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseVersion


