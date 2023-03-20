# ImagePicker
image picker for android developer


使用方法
1,打开相册(选择图片)\n
        ImageSelector.builder()
            .useCamera(true) //设置是否使用拍照
            .setSingle(true) //设置是否单选
            .setCrop(cameraCrop == 0) //裁切
            .canPreview(true) //是否点击放大图片查看,，默认为true
            .start(this, 123) // 打开相

2,打开相机(拍照)
        ImageSelector.builder()
            .onlyTakePhoto(true) // 仅拍照，不打开相册
            .setCrop(cameraCrop == 0)
            .start(this, 123)
