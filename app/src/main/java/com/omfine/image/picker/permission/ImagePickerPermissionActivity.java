package com.omfine.image.picker.permission;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.omfine.image.picker.R;
import com.omfine.image.picker.dialog.ImagePickerDialogHelper;
import com.omfine.image.picker.dialog.OnBtnClickListener;
/**
 * 仅做权限处理。
 * 相册和相机两种类型的权限。
 */
public class ImagePickerPermissionActivity extends AppCompatActivity {

    /**
     * 不是相册就是相机权限。
     */
    private boolean requestAlbumPermission = true;
    //相册
    private static final int PERMISSION_WRITE_EXTERNAL_REQUEST_CODE = 0x00000011;
    //相机
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    //记录请求的次数
    private int requestCount = 0;

    /**
     * 相册权限回调
     */
    private final ActivityResultLauncher<String> albumPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
        if (isGranted){
            //同意权限
            Log.e("http_message" , "http_message====albumPermissionResultLauncher=====相册权限已同意,退出当前页面=========");
            finish();
            onPermissionGranted();
            return;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
            //6.0版本以下不需要权限，直接同意权限
            onPermissionGranted();
            finish();
            return;
        }
        Boolean shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES);
        Log.e("http_message" , "http_message=====albumPermissionResultLauncher====相册权限，已拒绝=========shouldShowRequestPermissionRationale: " + shouldShowRequestPermissionRationale);
        //当 shouldShowRequestPermissionRationale == false时，需要弹出窗口，提示用户
        // 告诉用户为啥要申请这个权限
        if (shouldShowRequestPermissionRationale){
            finish();
            onPermissionDenied();
            return;
        }
        //弹出窗口告知用户
        showAlbumPermissionTipDialog();
    });

    /**
     * 相机权限
     */
    private final ActivityResultLauncher<String> cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
        if (isGranted){
            //同意权限
            Log.e("http_message" , "http_message====cameraActivityResultLauncher=====相机权限已同意,退出当前页面=========");
            finish();
            onPermissionGranted();
            return;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M){
            //6.0版本以下不需要权限，直接同意权限
            onPermissionGranted();
            finish();
            return;
        }
        Boolean shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA);
        Log.e("http_message" , "http_message====cameraActivityResultLauncher=====相机权限，已拒绝=========shouldShowRequestPermissionRationale: " + shouldShowRequestPermissionRationale);
        //当 shouldShowRequestPermissionRationale == false时，需要弹出窗口，提示用户
        // 告诉用户为啥要申请这个权限
        if (shouldShowRequestPermissionRationale){
            finish();
            onPermissionDenied();
            return;
        }
        //弹出窗口告知用户
        showCameraPermissionTipDialog();
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker_permission);
        requestCount = 0;
        //0 相册， 其他：相机
        int from = getIntent().getIntExtra("from" , 0);
        requestAlbumPermission = (0 == from);
    }

    /**
     * 请求权限。
     */
    private void requestPermission(){
        if (requestAlbumPermission){
            //相册权限
            if (ImagePickerPermissionCheckHelper.hasAlbumNeededPermission(this)){
                //已有相关权限
                //返回结果，退出当前页面
                Log.e("http_message" , "http_message=========已有相册权限，退出页面===========");
                finish();
                //回调出去
                onPermissionGranted();
                return;
            }
            //没有相册权限，就去请求相册相关的权限
            if (requestCount > 0){
                //返回结果，退出当前页面
                Log.e("http_message" , "http_message=========相册权限，拒绝,退出页面===========请求次数: " + requestCount);
                finish();
                //回调出去
                onPermissionDenied();
                return;
            }
            requestCount ++;
            requestAlbumPermissions();
            return;
        }

        //相机权限
        if (ImagePickerPermissionCheckHelper.hasCameraNeededPermission(this)){
            //已有相机权限
            //返回结果，退出当前页面
            Log.e("http_message" , "http_message=========已有相机权限，退出页面===========");
            finish();
            //回调出去
            onPermissionGranted();
            return;
        }
        //没有相机权限，就去请求相机相关的权限
        if (requestCount > 0){
            //返回结果，退出当前页面
            Log.e("http_message" , "http_message=========相机权限，拒绝,退出页面===========请求次数: " + requestCount);
            finish();
            //回调出去
            onPermissionDenied();
            return;
        }
        requestCount ++;

        requestCameraPermissions();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_REQUEST_CODE){
            Log.e("http_message" , "http_message====相册权限=====权限onRequestPermissionsResult===========permissions: " + permissions.length  + "   grantResults: " + grantResults.length);
            //相册权限
            requestPermission();
            return;
        }
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE){
            Log.e("http_message" , "http_message======相机权限===权限onRequestPermissionsResult===========permissions: " + permissions.length  + "   grantResults: " + grantResults.length);
            //相机权限
            requestPermission();
        }
    }

    /**
     * 请求相册需要的权限。
     */
    private void requestAlbumPermissions(){
        //如果手机系统是android 13 并且 APP的 targetSdk 也是 android 13 或之上版本，使用新的图片权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.TIRAMISU){
            albumPermissionResultLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            return;
        }
        //没有权限，申请权限。
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_REQUEST_CODE);
    }

    /**
     * 请求相机需要的权限。
     */
    private void requestCameraPermissions(){
        //如果手机系统是android 13 并且 APP的 targetSdk 也是 android 13 或之上版本，使用新的图片权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.TIRAMISU){
            //没有权限，申请权限。
            cameraActivityResultLauncher.launch(Manifest.permission.CAMERA);
            return;
        }
        //没有权限，申请权限。
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CAMERA_REQUEST_CODE);
    }

    /**
     * 相册权限被拒绝后且不能再次弹出系统权限窗口时，弹窗提示，去到系统页面开启权限。
     */
    private void showAlbumPermissionTipDialog(){
        ImagePickerDialogHelper.showNativePermissionTipDialog(this ,
                getString(R.string.image_picker_permissions_dialog_title),
                getString(R.string.image_picker_permissions_denied_hint_for_album),
                getString(R.string.image_picker_permissions_dialog_btn_cancel) ,
                getString(R.string.image_picker_permissions_dialog_btn_confirm) ,
                new OnBtnClickListener(){
                    @Override
                    public void onCancel() {
                        //取消
                        finish();
                        //需要回调出去
                        onPermissionDenied();
                    }
                    @Override
                    public void onSure() {
                        //同意
                        startAppSettings();
                    }
                });
    }

    /**
     * 相机权限被拒绝后且不能再次弹出系统权限窗口时，弹窗提示，去到系统页面开启权限。
     */
    private void showCameraPermissionTipDialog(){
        ImagePickerDialogHelper.showNativePermissionTipDialog(this ,
                getString(R.string.image_picker_permissions_dialog_title),
                getString(R.string.image_picker_permissions_denied_hint_for_camera),
                getString(R.string.image_picker_permissions_dialog_btn_cancel) ,
                getString(R.string.image_picker_permissions_dialog_btn_confirm) ,
                new OnBtnClickListener(){
                    @Override
                    public void onCancel() {
                        //取消
                        finish();
                        //需要回调出去
                        onPermissionDenied();
                    }
                    @Override
                    public void onSure() {
                        //同意
                        startAppSettings();
                    }
                });
    }

    /**
     * 权限同意，回调
     */
    private void onPermissionGranted(){
        OnImagePickerPermissionRequestListener onImagePickerPermissionRequestListener = ImagePickerPermissionConfig.getInstance().getOnImagePickerPermissionRequestListener();
        if (null == onImagePickerPermissionRequestListener){
            return;
        }
        onImagePickerPermissionRequestListener.onGranted();
    }

    /**
     * 权限拒绝，回调
     */
    private void onPermissionDenied(){
        OnImagePickerPermissionRequestListener onImagePickerPermissionRequestListener = ImagePickerPermissionConfig.getInstance().getOnImagePickerPermissionRequestListener();
        if (null == onImagePickerPermissionRequestListener){
            return;
        }
        onImagePickerPermissionRequestListener.onDenied();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("http_message" , "http_message=========权限页面=====onStart======");
        requestPermission();
    }


}
