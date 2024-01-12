package com.omfine.image.picker.dialog;

import android.app.AlertDialog;
import android.content.Context;
/**
 * 弹窗类。
 */
public class ImagePickerDialogHelper {

    /**
     * 弹出系统窗口。
     * @param context 下下文
     * @param title 标题
     * @param content 内容
     * @param negativeButtonText 拒绝按钮文本
     * @param PositiveButtonText 同意按钮文本
     * @param onBtnClickListener 回调
     */
    public static void showNativePermissionTipDialog(Context context , String title , String content , String negativeButtonText , String PositiveButtonText , OnBtnClickListener onBtnClickListener){
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(content)
                .setNegativeButton(negativeButtonText, (dialog, which) -> {
                    dialog.cancel();
                    if (null != onBtnClickListener){
                        onBtnClickListener.onCancel();
                    }
                }).setPositiveButton(PositiveButtonText, (dialog, which) -> {
                    dialog.cancel();
                    if (null != onBtnClickListener){
                        onBtnClickListener.onSure();
                    }
                }).show();
    }


}
