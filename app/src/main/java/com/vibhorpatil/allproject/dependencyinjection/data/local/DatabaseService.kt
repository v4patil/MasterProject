package com.vibhorpatil.allproject.dependencyinjection.data.local

import android.content.Context

class DatabaseService (
     private val context: Context,
     private val databaseName: String,
     private val version: Int
) {

    fun getDummyData(): String {
        return "DATABASE_DUMMY_DATA"
    }
}