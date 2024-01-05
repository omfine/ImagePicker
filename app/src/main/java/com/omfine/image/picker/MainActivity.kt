package com.omfine.image.picker

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.omfine.image.picker.utils.ImageSelector

class MainActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.M)
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // 同意
            Log.e("http_message" , "http_message==========同意==========VERSION: ${Build.VERSION.SDK_INT}    targetSdkVersion: ${applicationInfo.targetSdkVersion}")
        } else {
            // 拒绝
            val boolean = shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)
            //当boolean == false时，需要弹出窗口，提示用户
            // 告诉用户为啥要申请这个权限
            Log.e("http_message" , "http_message==========拒绝===${boolean}=======VERSION: ${Build.VERSION.SDK_INT}    targetSdkVersion: ${applicationInfo.targetSdkVersion}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.permissionBtn).setOnClickListener {

            if (Build.VERSION.SDK_INT >= 33 && applicationInfo.targetSdkVersion >= 33){
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }else{

                ActivityCompat.requestPermissions(this , arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE) , 200)
            }


        }

        listeners()
    }

    private fun listeners(){
/*        findViewById<Button>(R.id.permissionBtn).setOnClickListener {
            requestPermission()
        }*/
        findViewById<Button>(R.id.openAlbumBtn).setOnClickListener {
            a()
        }

        findViewById<Button>(R.id.openCameraBtn).setOnClickListener {
            b()
        }
    }

    private fun requestPermission(){

    }

    private fun a(){
        ImageSelector.builder()
            .useCamera(true) //设置是否使用拍照
            .setSingle(true) //设置是否单选
            .setCrop(true) //裁切
            .canPreview(true) //是否点击放大图片查看,，默认为true
            .start(this, 20) // 打开相
    }

    private fun b(){
        ImageSelector.builder()
            .onlyTakePhoto(true) // 仅拍照，不打开相册
            .setCrop(true)
            .start(this, 20)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (null == data){
            return
        }
        val list = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
        if (list.isNullOrEmpty()){
            return
        }
        Log.e("http_message", "========图片处理=====图片数量===:: ${list.size}")

        val imagePath = list[0]
        Log.e("http_message", "========图片处理=====图片最终地址=====imagePath:: $imagePath")

        val b = BitmapFactory.decodeFile(imagePath)
        if (null != b){
            Log.e("http_message", "========图片处理=====b:: ${b.byteCount}  w: ${b.width}  h: ${b.height}")
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.e("http_message" , "http_message==========onRequestPermissionsResult==========VERSION: ${Build.VERSION.SDK_INT}    targetSdkVersion: ${applicationInfo.targetSdkVersion}")
    }

}