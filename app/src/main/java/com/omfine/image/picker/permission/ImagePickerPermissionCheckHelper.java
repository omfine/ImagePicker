package com.omfine.image.picker.permission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.content.ContextCompat;
/**
 * 权限检查。
 * @author E
 */
public class ImagePickerPermissionCheckHelper {


    /**
     * 查看是否有某个功能需要的权限。
     * @param albumPermission 相册，否则相机
     * @param onImagePickerPermissionRequestListener 回调
     */
    public static void checkPermissions(Context context , boolean albumPermission , OnImagePickerPermissionRequestListener onImagePickerPermissionRequestListener){
        //前置检查,确实需要再下一步
        if (albumPermission){
            //相册
            if (hasAlbumNeededPermission(context)){
                onImagePickerPermissionRequestListener.onGranted();
                return;
            }
        }else {
            //相机
            if (hasCameraNeededPermission(context)){
                onImagePickerPermissionRequestListener.onGranted();
                return;
            }
        }
        ImagePickerPermissionConfig.getInstance().setOnImagePickerPermissionRequestListener(onImagePickerPermissionRequestListener);
        context.startActivity(new Intent(context , ImagePickerPermissionActivity.class).putExtra("from" , (albumPermission ? 0 : 1) ));
    }

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
