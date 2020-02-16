package com.nozariv2.database.viewmodelfactories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class PageViewModelFactory(application: Application, extra: Int):ViewModelProvider.Factory {

    private var mApplication= application
    private var mExtra = extra

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PageViewModelFactory(mApplication, mExtra) as T
    }

}