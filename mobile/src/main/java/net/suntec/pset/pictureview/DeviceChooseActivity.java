package net.suntec.pset.pictureview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class DeviceChooseActivity extends AppCompatActivity {

    private LinearLayout root;
    private TextView mTextView;

    // key 文件夹， value  图片列表
    private HashMap<String, List<String>> mStringListHashMap = new HashMap<>();

    private Storage[] mStorages = new Storage[3];
    private int currentIndex = 0;
    private boolean sdCardExist;
    private boolean usb1Exist;
    private boolean usb2Exist;
    final private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);

        root = (LinearLayout) findViewById(R.id.activity_main);
        mTextView = (TextView) findViewById(R.id.textView);

        /* 请求权限， 用户允许后，则不用再请求 */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) //没有授权
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an0
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
        }
        else {
            // 检测是否存在storage
            isStorageExist();
            // 动态添加设备选择button
            InsertBtn();
        }
    }

    public void InsertBtn() {

        if(mStorages.length > 0) {
            mTextView.setVisibility(View.GONE);
        }

        int width = DensityUtil.dip2px(this, 200f);
        int height = DensityUtil.dip2px(this, 200f); // dp --> px
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);

        for(int i=0; i<mStorages.length; i++) {

            ImageButton btn = new ImageButton(this);
            btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
            btn.setLayoutParams(layoutParams);
            //btn.setImageResource(mStorages[i].getType());
            btn.setBackgroundResource(mStorages[i].getBackgroundResId());
            btn.setId(mStorages[i].mId);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*  prepear 界面， 读取设备中的数据 */
                    Intent intent = new Intent(DeviceChooseActivity.this, PreparingActivity.class);
                    intent.putExtra("deviceID", v.getId());  // 传入设备id
                    startActivity(intent);
                }
            });
            root.addView(btn);
        }
    }

    public enum DEVICE_TYPE{
        DEVICE_TYPE_SD,
        DEVICE_TYPE_USB1,
        DEVICE_TYPE_USB2
    }

    public class Storage {
        private boolean mIsExist;
        private int mType;
        private int mBackgroundResId;

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            this.mId = id;
        }

        private int mId;


        public Storage(boolean isExist, int type, int backgroundResId, int id) {
            mIsExist = isExist;
            mType = type;
            mBackgroundResId = backgroundResId;
            this.mId = id;
        }

        public int getBackgroundResId() {
            return mBackgroundResId;
        }

        public void setBackgroundResId(int backgroundResId) {
            mBackgroundResId = backgroundResId;
        }

        public boolean isExist() {
            return mIsExist;
        }

        public void setExist(boolean exist) {
            mIsExist = exist;
        }

        public int getType() {
            return mType;
        }

        public void setType(int type) {
            mType = type;
        }
    }

    /**
     * 判断storage是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public void isStorageExist() {
        boolean isExist =  Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if(isExist) {
            mStorages[currentIndex] = new Storage(true, R.drawable.sd6,
                    R.drawable.btn_device_choose_style_sd, 0);
            mStorages[currentIndex].setType(0);
            currentIndex++;
        }

        isExist = true; // usb1待定
        if(isExist) {
            mStorages[currentIndex] = new Storage(true, R.drawable.usb1,
                    R.drawable.btn_device_choose_style_usb1, 1);
            currentIndex++;
        }

        isExist = true; // usb2待定
        if(isExist) {
            mStorages[currentIndex] = new Storage(true, R.drawable.usb2,
                    R.drawable.btn_device_choose_style_usb2, 2);
            currentIndex++;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    isStorageExist();
                    InsertBtn();

                } else {
                    Toast.makeText(DeviceChooseActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            default:
                Toast.makeText(DeviceChooseActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

}
