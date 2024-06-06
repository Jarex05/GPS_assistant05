package com.mikhail_R_gps_tracker.gpsassistant.utils

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mikhail_R_gps_tracker.gpsassistant.R

fun Fragment.openFragmentChet(f: Fragment){
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolderChet, f).commit()
}

fun AppCompatActivity.openFragmentChet(f: Fragment){
    if (supportFragmentManager.fragments.isNotEmpty()){
        if (supportFragmentManager.fragments[0].javaClass == f.javaClass) return
    }
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolderChet, f).commit()
}

fun Fragment.openFragmentNechet(f: Fragment){
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolderNechet, f).commit()
}

fun AppCompatActivity.openFragmentNechet(f: Fragment){
    if (supportFragmentManager.fragments.isNotEmpty()){
        if (supportFragmentManager.fragments[0].javaClass == f.javaClass) return
    }
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.placeHolderNechet, f).commit()
}

fun Fragment.showToast(s: String){
    Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showToast(s: String){
    Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}

fun Fragment.checkPermissionChet(p: String): Boolean{
    return when(PackageManager.PERMISSION_GRANTED){
        ContextCompat.checkSelfPermission(activity as AppCompatActivity, p) -> true
        else -> false
    }
}

fun Fragment.checkPermissionNechet(p: String): Boolean{
    return when(PackageManager.PERMISSION_GRANTED){
        ContextCompat.checkSelfPermission(activity as AppCompatActivity, p) -> true
        else -> false
    }
}