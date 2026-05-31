package com.vibhorpatil.allproject.dependencyinjection.di.module

import android.app.Application
import com.vibhorpatil.allproject.dependencyinjection.data.local.DatabaseService
import com.vibhorpatil.allproject.dependencyinjection.data.remote.HttpClient
import com.vibhorpatil.allproject.dependencyinjection.di.ApplicationContext
import com.vibhorpatil.allproject.dependencyinjection.di.BaseURL
import com.vibhorpatil.allproject.dependencyinjection.di.DatabaseName
import com.vibhorpatil.allproject.dependencyinjection.di.DatabaseVersion
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(val application: Application) {


    @Provides
    @ApplicationContext
    fun provideContext(): Application {
        return application
    }

    @Singleton
    @Provides
    fun provideDatabaseService(
        @DatabaseName databaseName: String,
        @DatabaseVersion databaseVersion: Int
    ): DatabaseService {
        return DatabaseService(application, databaseName, databaseVersion)
    }

    @Provides
    @DatabaseName
    fun provideDatabaseName(): String = "DATABASE_NAME"

    @Provides
    @DatabaseVersion
    fun provideDatabaseVersion(): Int = 1

    @Provides
    @BaseURL
    fun provideBaseUrl(): String = "BASE_URL"

    @Provides
    @Singleton
    fun provideHttpClient() : HttpClient = HttpClient()


}