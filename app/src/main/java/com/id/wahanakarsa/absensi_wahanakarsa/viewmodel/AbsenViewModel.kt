package com.id.wahanakarsa.absensi_wahanakarsa.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.id.wahanakarsa.absensi_wahanakarsa.database.DatabaseClient.Companion.getInstance
import com.id.wahanakarsa.absensi_wahanakarsa.database.dao.DatabaseDao
import com.id.wahanakarsa.absensi_wahanakarsa.model.ModelDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class AbsenViewModel(application: Application) : AndroidViewModel(application) {
    var databaseDao: DatabaseDao? = getInstance(application)?.appDatabase?.databaseDao()

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDataAbsen(
        nama: String,
        tanggal: String, lokasi: String, keterangan: String) {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH)
        val dateTime = LocalDateTime.parse(tanggal, dateTimeFormatter)
        val timeString = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        val dateTimeFormatter2 = DateTimeFormatter.ofPattern("HH:mm")
        val time = LocalTime.parse(timeString, dateTimeFormatter2)

        val cutOffTime = LocalTime.of(7, 0) // Set the cutoff time to 7:00 AM
        val status = if (time.isAfter(cutOffTime)) "TELAT" else "TIDAK TELAT"
        Completable.fromAction {
            val modelDatabase = ModelDatabase()
            modelDatabase.status = status
            modelDatabase.nama = nama
            modelDatabase.tanggal = tanggal
            modelDatabase.lokasi = lokasi
            modelDatabase.keterangan = keterangan
            databaseDao?.insertData(modelDatabase)
        }

            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()

        val db = Firebase.firestore
        val user = hashMapOf(
            "nama" to nama,
            "tanggal" to  tanggal,
            "lokasi" to lokasi,
            "keterangan" to keterangan,
            "status" to status
        )
        val path = when(keterangan){
            "Absen Masuk"->"Absen Masuk"
            "Absen Keluar"-> "Absen Keluar"

            else -> "Izin"
        }
        db.collection(path)
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }


}