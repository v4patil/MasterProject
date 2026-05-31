package com.vibhorpatil.allproject.dependencyinjection.di

import javax.inject.Scope


@Scope
@Retention(AnnotationRetention.BINARY)
annotation class ActivityScope

@Scope
@Retention(AnnotationRetention.BINARY)
annotation class FragmentScope