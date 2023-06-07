package com.youthworkhub.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val switchFragment = MutableLiveData<String>()

    fun switchFragment(fragment: String) {
        switchFragment.postValue(fragment)
    }

}