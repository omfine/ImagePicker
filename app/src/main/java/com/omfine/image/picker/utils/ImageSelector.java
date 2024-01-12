package com.omfine.image.picker.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.omfine.image.picker.ClipImageActivity;
import com.omfine.image.picker.ImageSelectorActivity;
import com.omfine.image.picker.entry.RequestConfig;
import com.omfine.image.picker.listener.OnImagePickerResultListener;
import com.omfine.image.picker.model.ImageModel;
import com.omfine.image.picker.permission.ImagePickerPermissionCheckHelper;
import com.omfine.image.picker.permission.OnImagePickerPermissionRequestListener;
import java.util.ArrayList;
/**
 *
 */
public class ImageSelector {

    /**
     * 图片选择的结果
     */
    public static final String SELECT_RESULT = "select_result";

    /**
     * 是否是来自于相机拍照的图片，
     * 只有本次调用相机拍出来的照片，返回时才为true。
     * 当为true时，图片返回当结果有且只有一张图片。
     */
    public static final String IS_CAMERA_IMAGE = "is_camera_image";

    public static final String KEY_CONFIG = "key_config";

    //最大的图片选择数
    public static final String MAX_SELECT_COUNT = "max_select_count";
    //是否单选
    public static final String IS_SINGLE = "is_single";
    //初始位置
    public static final String POSITION = "position";

    public static final String IS_CONFIRM = "is_confirm";

    public static final int RESULT_CODE = 0x00000012;

    /**
     * 预加载图片
     *
     * @param context
     */
    public static void preload(Context context) {
        ImageModel.preloadAndRegisterContentObserver(context);
    }

    /**
     * 清空缓存
     */
    public static void clearCache(Context context) {
        ImageModel.clearCache(context);
    }

    public static ImageSelectorBuilder builder() {
        return new ImageSelectorBuilder();
    }

    public static class ImageSelectorBuilder {

        private RequestConfig config;

        private ImageSelectorBuilder() {
            config = new RequestConfig();
        }

        /**
         * 是否使用图片剪切功能。默认false。如果使用了图片剪切功能，相册只能单选。
         *
         * @param isCrop
         * @return
         */
        public ImageSelectorBuilder setCrop(boolean isCrop) {
            config.isCrop = isCrop;
            return this;
        }

        /**
         * 图片剪切的宽高比，宽固定为手机屏幕的宽。
         *
         * @param ratio
         * @return
         */
        public ImageSelectorBuilder setCropRatio(float ratio) {
            config.cropRatio = ratio;
            return this;
        }

        /**
         * 是否单选
         *
         * @param isSingle
         * @return
         */
        public ImageSelectorBuilder setSingle(boolean isSingle) {
            config.isSingle = isSingle;
            return this;
        }

        /**
         * 是否可以点击放大图片查看，默认为true
         *
         * @param isViewImage
         * @return
         * @deprecated 请使用canPreview(boolean canPreview);
         */
        @Deprecated
        public ImageSelectorBuilder setViewImage(boolean isViewImage) {
            config.canPreview = isViewImage;
            return this;
        }

        /**
         * 是否可以点击预览，默认为true
         *
         * @param canPreview
         * @return
         */
        public ImageSelectorBuilder canPreview(boolean canPreview) {
            config.canPreview = canPreview;
            return this;
        }

        /**
         * 是否使用拍照功能。
         *
         * @param useCamera 默认为true
         * @return
         */
        public ImageSelectorBuilder useCamera(boolean useCamera) {
            config.useCamera = useCamera;
            return this;
        }

        public ImageSelectorBuilder onlyTakePhoto(boolean onlyTakePhoto) {
            config.onlyTakePhoto = onlyTakePhoto;
            return this;
        }

        /**
         * 图片的最大选择数量，小于等于0时，不限数量，isSingle为false时才有用。
         *
         * @param maxSelectCount
         * @return
         */
        public ImageSelectorBuilder setMaxSelectCount(int maxSelectCount) {
            config.maxSelectCount = maxSelectCount;
            return this;
        }

        /**
         * 接收从外面传进来的已选择的图片列表。当用户原来已经有选择过图片，现在重新打开
         * 选择器，允许用户把先前选过的图片传进来，并把这些图片默认为选中状态。
         *
         * @param selected
         * @return
         */
        public ImageSelectorBuilder setSelected(ArrayList<String> selected) {
            config.selected = selected;
            return this;
        }

/*        *//**
         * 打开相册
         *
         * @param activity
         * @param requestCode
         *//*
        public void start(Activity activity, int requestCode) {
            config.requestCode = requestCode;
            // 仅拍照，useCamera必须为true
            if (config.onlyTakePhoto) {
                config.useCamera = true;
            }
            if (config.isCrop) {
                ClipImageActivity.openActivity(activity, requestCode, config);
            } else {
                ImageSelectorActivity.openActivity(activity, requestCode, config);
            }
        }*/


        /**
         * 打开相册
         * 在这一步，加上权限检测步骤
         * @param activity
         * @param requestCode
         */
        public void start(Activity activity, int requestCode) {
            config.requestCode = requestCode;
            // 仅拍照，useCamera必须为true
            if (config.onlyTakePhoto) {
                config.useCamera = true;
            }
            //提前加权限检测
            ImagePickerPermissionCheckHelper.checkPermissions(activity , !config.onlyTakePhoto , new OnImagePickerPermissionRequestListener(){
                @Override
                public void onDenied() {
                    //权限拒绝
                    Log.e("http_message" , "http_message========ImageSelector=权限拒绝===========: " + (config.onlyTakePhoto ? "相机" : "相册"));
                }
                @Override
                public void onGranted() {
                    //有权限
                    Log.e("http_message" , "http_message========ImageSelector=有权限===下一步========: " + (config.onlyTakePhoto ? "相机" : "相册"));

                    if (config.isCrop) {
                        ClipImageActivity.openActivity(activity, requestCode, config);
                    } else {
                        ImageSelectorActivity.openActivity(activity, requestCode, config);
                    }

                }
            });

        }


        /**
         * 打开相册
         * 在这一步，加上权限检测步骤
         * @param activity
         * @param requestCode
         */
        public void start(Activity activity, int requestCode , OnImagePickerResultListener onImagePickerResultListener) {
            config.requestCode = requestCode;
            // 仅拍照，useCamera必须为true
            if (config.onlyTakePhoto) {
                config.useCamera = true;
            }
            //提前加权限检测
            ImagePickerPermissionCheckHelper.checkPermissions(activity , !config.onlyTakePhoto , new OnImagePickerPermissionRequestListener(){
                @Override
                public void onDenied() {
                    //权限拒绝
                    Log.e("http_message" , "http_message========ImageSelector=权限拒绝===========: " + (config.onlyTakePhoto ? "相机" : "相册"));
                    if (null != onImagePickerResultListener){
                        onImagePickerResultListener.onPermissionDenied();
                    }
                }
                @Override
                public void onGranted() {
                    //有权限
                    Log.e("http_message" , "http_message========ImageSelector=有权限===下一步========: " + (config.onlyTakePhoto ? "相机" : "相册"));
                    ImagePickerTempConfig.getInstance().setOnImagePickerResultListener(onImagePickerResultListener);

                    if (config.isCrop) {
                        ClipImageActivity.openActivity(activity, requestCode, config);
                    } else {
                        ImageSelectorActivity.openActivity(activity, requestCode, config);
                    }

                }
            });

        }

        /**
         * 打开相册或相机
         * @param activity activity
         * @param onImagePickerResultListener 图片回调
         */
        public void start(Activity activity , OnImagePickerResultListener onImagePickerResultListener){
            start(activity , 5060 , onImagePickerResultListener);
        }

        /**
         * 打开相册
         *
         * @param fragment
         * @param requestCode
         */
        public void start(Fragment fragment, int requestCode) {
            config.requestCode = requestCode;
            // 仅拍照，useCamera必须为true
            if (config.onlyTakePhoto) {
                config.useCamera = true;
            }
            //提前加权限检测
            ImagePickerPermissionCheckHelper.checkPermissions(fragment.getActivity() , !config.onlyTakePhoto , new OnImagePickerPermissionRequestListener(){
                @Override
                public void onDenied() {
                    //权限拒绝
                    Log.e("http_message" , "http_message=======fragment=ImageSelector=权限拒绝===========: " + (config.onlyTakePhoto ? "相机" : "相册"));
                }
                @Override
                public void onGranted() {
                    //有权限
                    Log.e("http_message" , "http_message=======fragment=ImageSelector=有权限===下一步========: " + (config.onlyTakePhoto ? "相机" : "相册"));

                    if (config.isCrop) {
                        ClipImageActivity.openActivity(fragment, requestCode, config);
                    } else {
                        ImageSelectorActivity.openActivity(fragment, requestCode, config);
                    }

                }
            });



        }

        /**
         * 打开相册
         *
         * @param fragment
         * @param requestCode
         */
        public void start(android.app.Fragment fragment, int requestCode) {
            config.requestCode = requestCode;
            // 仅拍照，useCamera必须为true
            if (config.onlyTakePhoto) {
                config.useCamera = true;
            }
            //提前加权限检测
            ImagePickerPermissionCheckHelper.checkPermissions(fragment.getActivity() , !config.onlyTakePhoto , new OnImagePickerPermissionRequestListener(){
                @Override
                public void onDenied() {
                    //权限拒绝
                    Log.e("http_message" , "http_message=======fragment=ImageSelector=权限拒绝===========: " + (config.onlyTakePhoto ? "相机" : "相册"));
                }
                @Override
                public void onGranted() {
                    //有权限
                    Log.e("http_message" , "http_message=======fragment=ImageSelector=有权限===下一步========: " + (config.onlyTakePhoto ? "相机" : "相册"));

                    if (config.isCrop) {
                        ClipImageActivity.openActivity(fragment, requestCode, config);
                    } else {
                        ImageSelectorActivity.openActivity(fragment, requestCode, config);
                    }

                }
            });

        }
    }

}
