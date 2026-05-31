package com.vibhorpatil.allproject.dependencyinjection.data.local

import android.app.Application
import com.vibhorpatil.allproject.dependencyinjection.di.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FileStorageService @Inject constructor(
    @ApplicationContext application: Application,
    databaseService: DatabaseService
    ) {
}