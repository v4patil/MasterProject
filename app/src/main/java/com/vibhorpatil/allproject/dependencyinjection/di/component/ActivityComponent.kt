package com.vibhorpatil.allproject.dependencyinjection.di.component

import com.vibhorpatil.allproject.dependencyinjection.di.ActivityScope
import com.vibhorpatil.allproject.dependencyinjection.di.module.ActivityModule
import com.vibhorpatil.allproject.dependencyinjection.ui.home.HomeActivity
import com.vibhorpatil.allproject.dependencyinjection.ui.login.LoginActivity
import com.vibhorpatil.allproject.learnkotlincoroutine.CoroutineActivity
import dagger.Component


@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(context: LoginActivity)

    fun inject(context: HomeActivity)

    fun inject(context: CoroutineActivity)
}