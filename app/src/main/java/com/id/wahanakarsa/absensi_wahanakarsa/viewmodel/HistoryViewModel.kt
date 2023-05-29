package com.id.wahanakarsa.absensi_wahanakarsa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.id.wahanakarsa.absensi_wahanakarsa.database.DatabaseClient.Companion.getInstance
import com.id.wahanakarsa.absensi_wahanakarsa.database.dao.DatabaseDao
import com.id.wahanakarsa.absensi_wahanakarsa.model.ModelDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    var dataLaporan: LiveData<List<ModelDatabase>>
    var databaseDao: DatabaseDao? = getInstance(application)?.appDatabase?.databaseDao()

    fun deleteDataById(uid: Int) {
        Completable.fromAction {
            databaseDao?.deleteHistoryById(uid)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    init {
        dataLaporan = databaseDao!!.getAllHistory()
    }
}