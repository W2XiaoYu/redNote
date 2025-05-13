package com.example.rednote.layout

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainLayoutViewModel : ViewModel() {
    private val _initIndex = MutableStateFlow(0)
    val initIndex: StateFlow<Int> = _initIndex
    fun setInitIndex(index: Int) {
        _initIndex.value = index
    }
}