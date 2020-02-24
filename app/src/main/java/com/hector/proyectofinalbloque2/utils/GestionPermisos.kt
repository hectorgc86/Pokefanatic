package com.hector.proyectofinalbloque2.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GestionPermisos (
    private val activity: Activity,
    private val permiso: String,
    private val code: Int)
{
    fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(activity, permiso) != PackageManager.PERMISSION_GRANTED){
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permiso)) {
                ActivityCompat.requestPermissions(activity, arrayOf(permiso), code)
            }
        }
        return ContextCompat.checkSelfPermission(activity, permiso) == PackageManager.PERMISSION_GRANTED
    }
}

