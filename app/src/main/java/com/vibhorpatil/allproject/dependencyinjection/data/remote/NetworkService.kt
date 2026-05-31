package com.vibhorpatil.allproject.dependencyinjection.data.remote

import com.vibhorpatil.allproject.dependencyinjection.di.BaseURL
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkService @Inject constructor(
    httpClient: HttpClient,
    @BaseURL baseUrl : String
)  {
}