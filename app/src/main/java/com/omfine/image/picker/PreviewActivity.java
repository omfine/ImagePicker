package com.omfine.image.picker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.omfine.image.picker.adapter.ImagePagerAdapter;
import com.omfine.image.picker.entry.Image;
import com.omfine.image.picker.utils.ImageSelector;
import com.omfine.image.picker.utils.VersionUtils;
import com.omfine.image.picker.view.MyViewPager;
import java.util.ArrayList;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;

public class PreviewActivity extends AppCompatActivity {

    private MyViewPager myViewPager;
    private TextView tvIndicator;
    private TextView tvConfirm;
    private FrameLayout btnConfirm;
    private TextView tvSelect;
    private RelativeLayout rlTopBar;
    private RelativeLayout rlBottomBar;

    //tempImages和tempSelectImages用于图片列表数据的页面传输。
    //之所以不要Intent传输这两个图片列表，因为要保证两位页面操作的是同一个列表数据，同时可以避免数据量大时，
    // 用Intent传输发生的错误问题。
    private static ArrayList<Image> tempImages;
    private static ArrayList<Image> tempSelectImages;

    private ArrayList<Image> mImages = new ArrayList<>();
    private ArrayList<Image> mSelectImages = new ArrayList<>();
    private boolean isShowBar = true;
    private boolean isConfirm = false;
    private boolean isSingle;
    private int mMaxCount;

    private BitmapDrawable mSelectDrawable;
    private BitmapDrawable mUnSelectDrawable;

    public static void openActivity(Activity activity, ArrayList<Image> images,
                                    ArrayList<Image> selectImages, boolean isSingle,
                                    int maxSelectCount, int position) {
        tempImages = images;
        tempSelectImages = selectImages;
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra(ImageSelector.MAX_SELECT_COUNT, maxSelectCount);
        intent.putExtra(ImageSelector.IS_SINGLE, isSingle);
        intent.putExtra(ImageSelector.POSITION, position);

        //这个方法，在内存不足，或数量过大时，会引起android.os.TransactionTooLargeException: data parcel size 2851820* bytes异常
//        intent.putParcelableArrayListExtra("images" , images);
//        intent.putParcelableArrayListExtra("selectImages" , selectImages);

        activity.startActivityForResult(intent, ImageSelector.RESULT_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        if (VersionUtils.isAndroidP()) {
            //设置页面全屏显示
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            //设置页面延伸到刘海区显示
            getWindow().setAttributes(lp);
        }

        setStatusBarVisible(true);
//        mImages = tempImages;
//        tempImages = null;
//        mSelectImages = tempSelectImages;
//        tempSelectImages = null;

        Intent intent = getIntent();

        mImages.clear();
        mSelectImages.clear();
        if (null != tempImages && !tempImages.isEmpty()){
            mImages.addAll(tempImages);
            mSelectImages.addAll(tempSelectImages);
        }
        //如果图片没有包括 所有选择的图片，则需要将未包括选择的图片添加到图片列表前面，这是因为切换文件夹，再来预览，才会出现这个问题
        int selectedSize = mSelectImages.size();
        List<Image> unContainList = new ArrayList<>();
        for (int i = 0; i < selectedSize; i++) {
            Image image = mSelectImages.get(i);
            if (!mImages.contains(image)){
                unContainList.add(image);
            }
        }
        //重新定义position
        int position = intent.getIntExtra(ImageSelector.POSITION, 0);
        //未包括的图片数量
        int unContainSize = unContainList.size();
        if (unContainSize > 0){
            mImages.addAll(0 , unContainList);
            position = position + unContainSize;
            int imageSize = mImages.size();
            position = position >= imageSize ?  (imageSize - 1) : position;
            position = position < 0 ? 0 : position;
        }

        //这个方法不能再用了，如果图片列表过大，则会引起android.os.TransactionTooLargeException: data parcel size 2851820* bytes异常
        //所以不能用intent.putParcelableArrayListExtra()方法来传递大的图片列表。
/*        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ArrayList<Image> images = intent.getParcelableArrayListExtra("images" , Image.class);
            ArrayList<Image> selectImages = intent.getParcelableArrayListExtra("selectImages" , Image.class);
            if (null != images){
                mImages.addAll(images);
            }
            if (null != selectImages){
                mSelectImages.addAll(selectImages);
            }
        }else {
            ArrayList<Image> images = intent.getParcelableExtra("images");
            ArrayList<Image> selectImages = intent.getParcelableExtra("selectImages");
            if (null != images){
                mImages.addAll(images);
            }
            if (null != selectImages){
                mSelectImages.addAll(selectImages);
            }
        }*/

        if (mImages.isEmpty()){
            finish();
            return;
        }

        mMaxCount = intent.getIntExtra(ImageSelector.MAX_SELECT_COUNT, 0);
        isSingle = intent.getBooleanExtra(ImageSelector.IS_SINGLE, false);

        Resources resources = getResources();
        Bitmap selectBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_image_select);
        mSelectDrawable = new BitmapDrawable(resources, selectBitmap);
        mSelectDrawable.setBounds(0, 0, selectBitmap.getWidth(), selectBitmap.getHeight());

        Bitmap unSelectBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_image_un_select);
        mUnSelectDrawable = new BitmapDrawable(resources, unSelectBitmap);
        mUnSelectDrawable.setBounds(0, 0, unSelectBitmap.getWidth(), unSelectBitmap.getHeight());

        setStatusBarColor();
        initView();
        initListener();
        initViewPager();

        tvIndicator.setText(1 + "/" + mImages.size());
        changeSelect(mImages.get(0));
        myViewPager.setCurrentItem(position);
    }

    private void initView() {
        myViewPager = findViewById(R.id.myImagePickerViewPager);
        tvIndicator = findViewById(R.id.tv_indicator);
        tvConfirm = findViewById(R.id.tv_confirm);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvSelect = findViewById(R.id.tv_select);
        rlTopBar = findViewById(R.id.rl_top_bar);
        rlBottomBar = findViewById(R.id.rl_bottom_bar);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlTopBar.getLayoutParams();
        lp.topMargin = getStatusBarHeight(this);
        rlTopBar.setLayoutParams(lp);
    }

    private void initListener() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirm = true;
                finish();
            }
        });
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSelect();
            }
        });
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, mImages);
        myViewPager.setAdapter(adapter);
        adapter.setOnItemClickListener(new ImagePagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Image image) {
                if (isShowBar) {
                    hideBar();
                } else {
                    showBar();
                }
            }
        });
        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tvIndicator.setText(position + 1 + "/" + mImages.size());
                changeSelect(mImages.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        if (VersionUtils.isAndroidL()) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#373c3d"));
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 显示和隐藏状态栏
     *
     * @param show
     */
    private void setStatusBarVisible(boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (show) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

            }
        }
    }

    /**
     * 显示头部和尾部栏
     */
    private void showBar() {
        isShowBar = true;
        setStatusBarVisible(true);
        //添加延时，保证StatusBar完全显示后再进行动画。
        rlTopBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rlTopBar != null) {
                    ObjectAnimator animator = ofFloat(rlTopBar, "translationY",
                            rlTopBar.getTranslationY(), 0).setDuration(300);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            if (rlTopBar != null) {
                                rlTopBar.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    animator.start();
                    ofFloat(rlBottomBar, "translationY", rlBottomBar.getTranslationY(), 0)
                            .setDuration(300).start();
                }
            }
        }, 100);
    }

    /**
     * 隐藏头部和尾部栏
     */
    private void hideBar() {
        isShowBar = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(rlTopBar, "translationY",
                0, -rlTopBar.getHeight()).setDuration(300);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (rlTopBar != null) {
                    rlTopBar.setVisibility(View.GONE);
                    //添加延时，保证rlTopBar完全隐藏后再隐藏StatusBar。
                    rlTopBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setStatusBarVisible(false);
                        }
                    }, 5);
                }
            }
        });
        animator.start();
        ofFloat(rlBottomBar, "translationY", 0, rlBottomBar.getHeight())
                .setDuration(300).start();
    }

    private void clickSelect() {
        int position = myViewPager.getCurrentItem();
        if (mImages != null && mImages.size() > position) {
            Image image = mImages.get(position);
            if (mSelectImages.contains(image)) {
                mSelectImages.remove(image);
            } else if (isSingle) {
                mSelectImages.clear();
                mSelectImages.add(image);
            } else if (mMaxCount <= 0 || mSelectImages.size() < mMaxCount) {
                mSelectImages.add(image);
            }
            changeSelect(image);
        }
    }

    private void changeSelect(Image image) {
        tvSelect.setCompoundDrawables(mSelectImages.contains(image) ?
                mSelectDrawable : mUnSelectDrawable, null, null, null);
        setSelectImageCount(mSelectImages.size());
    }

    private void setSelectImageCount(int count) {
        if (count == 0) {
            btnConfirm.setEnabled(false);
            tvConfirm.setText(R.string.selector_send);
        } else {
            btnConfirm.setEnabled(true);
            if (isSingle) {
                tvConfirm.setText(R.string.selector_send);
            } else if (mMaxCount > 0) {
                tvConfirm.setText(getString(R.string.selector_send) + "(" + count + "/" + mMaxCount + ")");
            } else {
                tvConfirm.setText(getString(R.string.selector_send) + "(" + count + ")");
            }
        }
    }

    @Override
    public void finish() {
        //Activity关闭时，通过Intent把用户的操作(确定/返回)传给ImageSelectActivity。
        Intent intent = new Intent();
//        intent.putParcelableArrayListExtra("images" , mImages);
        intent.putParcelableArrayListExtra("selectImages" , mSelectImages);

        intent.putExtra(ImageSelector.IS_CONFIRM, isConfirm);
        setResult(ImageSelector.RESULT_CODE, intent);
        super.finish();
    }
}
