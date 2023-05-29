package com.id.wahanakarsa.absensi_wahanakarsa.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.id.wahanakarsa.absensi_wahanakarsa.database.dao.DatabaseDao
import com.id.wahanakarsa.absensi_wahanakarsa.model.ModelDatabase

@Database(entities = [ModelDatabase::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao?
}