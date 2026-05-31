package com.vibhorpatil.allproject.dependencyinjection.di.module

import android.content.Context
import com.vibhorpatil.allproject.dependencyinjection.di.ActivityContext
import com.vibhorpatil.allproject.dependencyinjection.ui.home.HomeViewModel
import com.vibhorpatil.allproject.dependencyinjection.ui.login.LoginViewModel
import dagger.Module
import dagger.Provides


@Module
class ActivityModule(val context: Context) {

    @ActivityContext
    @Provides
    fun provideContext(): Context {
        return context
    }

    @Provides
    fun provideLoginViewModel() :  LoginViewModel{
        return LoginViewModel()
    }

    @Provides
    fun provideHomeViewModel() : HomeViewModel {
        return HomeViewModel()
    }
}