package com.tb.pdfly.page.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GlobalVM : ViewModel() {

    val showNoPermissionLiveData = MutableLiveData<Boolean>()
    val askPermissionLiveData = MutableLiveData<Boolean>()



}