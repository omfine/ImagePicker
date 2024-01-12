package com.omfine.image.picker.permission;

public class ImagePickerPermissionConfig {

    private static ImagePickerPermissionConfig imagePickerPermissionConfig;

    private ImagePickerPermissionConfig(){}

    private final static Object object = new Object();

    public static ImagePickerPermissionConfig getInstance(){
        synchronized (object){
            if (null == imagePickerPermissionConfig){
                imagePickerPermissionConfig = new ImagePickerPermissionConfig();
            }
            return imagePickerPermissionConfig;
        }
    }

    private OnImagePickerPermissionRequestListener onImagePickerPermissionRequestListener;

    public OnImagePickerPermissionRequestListener getOnImagePickerPermissionRequestListener() {
        return onImagePickerPermissionRequestListener;
    }

    public void setOnImagePickerPermissionRequestListener(OnImagePickerPermissionRequestListener onImagePickerPermissionRequestListener) {
        this.onImagePickerPermissionRequestListener = onImagePickerPermissionRequestListener;
    }
}
