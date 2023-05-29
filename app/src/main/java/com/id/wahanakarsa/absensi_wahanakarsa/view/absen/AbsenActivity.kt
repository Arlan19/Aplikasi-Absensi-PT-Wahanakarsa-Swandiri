package com.id.wahanakarsa.absensi_wahanakarsa.view.absen

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModelProvider
import com.id.wahanakarsa.absensi_wahanakarsa.R
import com.id.wahanakarsa.absensi_wahanakarsa.viewmodel.AbsenViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.BuildConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_absen.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AbsenActivity : AppCompatActivity() {

    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0

    var strLatitude = "0"
    var strLongitude = "0"



    lateinit var strCurrentLocation: String
    lateinit var strTitle: String

    lateinit var absenViewModel: AbsenViewModel
    lateinit var progressDialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absen)

        setInitLayout()
        setCurrentLocation()
        setUploadData()
    }

    private fun setCurrentLocation() {
        progressDialog.show()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener(this) { location ->
                progressDialog.dismiss()
                if (location != null) {
                    strCurrentLatitude = location.latitude
                    strCurrentLongitude = location.longitude
                    val geocoder = Geocoder(this@AbsenActivity, Locale.getDefault())
                    try {
                        val addressList =
                            geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
                        if (addressList != null && addressList.size > 0) {
                            strCurrentLocation = addressList[0].getAddressLine(0)
                            inputLokasi.setText(strCurrentLocation)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this@AbsenActivity,
                        "Ups, gagal mendapatkan lokasi. Silahkan periksa GPS atau koneksi internet Anda!",
                        Toast.LENGTH_SHORT).show()
                    strLatitude = "0"
                    strLongitude = "0"
                }
            }
    }

    private fun setInitLayout() {
        progressDialog = ProgressDialog(this)
        strTitle = intent.extras?.getString(DATA_TITLE).toString()

        if (strTitle != null) {
            tvTitle.text = strTitle
        }

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        absenViewModel = ViewModelProvider(this, (ViewModelProvider.AndroidViewModelFactory
            .getInstance(this.application) as ViewModelProvider.Factory)).get(AbsenViewModel::class.java)

        inputTanggal.setOnClickListener {
            val tanggalAbsen = Calendar.getInstance()
            val date =
                OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    tanggalAbsen[Calendar.YEAR] = year
                    tanggalAbsen[Calendar.MONTH] = monthOfYear
                    tanggalAbsen[Calendar.DAY_OF_MONTH] = dayOfMonth
                    val strFormatDefault = "dd MMMM yyyy HH:mm"
                    val simpleDateFormat = SimpleDateFormat(strFormatDefault, Locale.getDefault())
                    inputTanggal.setText(simpleDateFormat.format(tanggalAbsen.time))
                }
            DatePickerDialog(
                this@AbsenActivity, date,
                tanggalAbsen[Calendar.YEAR],
                tanggalAbsen[Calendar.MONTH],
                tanggalAbsen[Calendar.DAY_OF_MONTH]
            ).show()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUploadData() {
        btnAbsen.setOnClickListener {
            val strNama = inputNama.text.toString()
            val strTanggal = inputTanggal.text.toString()
            val strKeterangan = strTitle
            if ( strNama.isEmpty() || strCurrentLocation.isEmpty()
                || strTanggal.isEmpty() || strKeterangan.isEmpty()) {
                Toast.makeText(this@AbsenActivity,
                    "Data tidak boleh ada yang kosong!", Toast.LENGTH_SHORT).show()
            } else {
                absenViewModel.addDataAbsen(

                    strNama,
                    strTanggal,
                    strCurrentLocation,
                    strKeterangan)
                Toast.makeText(this@AbsenActivity,
                    "Laporan Anda terkirim, tunggu info selanjutnya ya!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DATA_TITLE = "TITLE"
    }
}