package com.vibhorpatil.allproject.learnkotlinflow

sealed interface UIState<out T> {

    data class Success<T>(val data: T) : UIState<T>

    data class Error(val error: String) : UIState<Nothing>

    object Loading : UIState<Nothing>

}