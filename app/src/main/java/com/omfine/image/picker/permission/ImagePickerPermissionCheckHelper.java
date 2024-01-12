package com.omfine.image.picker.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class ImagePickerPermissionCheckHelper {


    /**
     * 相册需要的权限
     * @return
     */
    public static boolean hasAlbumNeededPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        //如果手机系统是android 13 并且 APP的 targetSdk 也是 android 13 或之上版本，使用新的图片权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.TIRAMISU){
            return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context , Manifest.permission.READ_MEDIA_IMAGES);
        }
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context , Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 相机需要的权限
     * @return
     */
    public static boolean hasCameraNeededPermission(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        //如果手机系统是android 13 并且 APP的 targetSdk 也是 android 13 或之上版本，使用新的图片权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.TIRAMISU){
            //没有权限，申请权限。
            return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context , Manifest.permission.CAMERA);
        }
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context , Manifest.permission.CAMERA)
                && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context , Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


}
