package com.omfine.image.picker.utils;

import com.omfine.image.picker.listener.OnImagePickerResultListener;

public class ImagePickerTempConfig {

    private static ImagePickerTempConfig imagePickerTempConfig;

    private ImagePickerTempConfig(){}

    private final static Object object = new Object();

    public static ImagePickerTempConfig getInstance(){
        synchronized (object){
            if (null == imagePickerTempConfig){
                imagePickerTempConfig = new ImagePickerTempConfig();
            }
            return imagePickerTempConfig;
        }
    }

    private OnImagePickerResultListener onImagePickerResultListener;

    public OnImagePickerResultListener getOnImagePickerResultListener() {
        return onImagePickerResultListener;
    }

    public void setOnImagePickerResultListener(OnImagePickerResultListener onImagePickerResultListener) {
        this.onImagePickerResultListener = onImagePickerResultListener;
    }
}
