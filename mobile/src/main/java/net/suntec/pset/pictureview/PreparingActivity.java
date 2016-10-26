package net.suntec.pset.pictureview;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class PreparingActivity extends AppCompatActivity {

    //初始化变量,帧布局
    FrameLayout relative = null;

    private HashMap<String, List<String>> mGruopMap = new HashMap<>();
    private final static int SCAN_OK = 0x01;
    private final static int MOVE_DRAW = 0x02;
    //自定义一个用于定时更新UI界面的handler类对象
    Handler mHandler = new Handler()
    {
        int i = 0;
        @Override
        public void handleMessage(Message msg) {
            //判断信息是否为本应用发出的
            switch (msg.what) {
                case MOVE_DRAW: i++; move(i % 12); break;
                case SCAN_OK:
                    /* start folder list */
                    if(mGruopMap.size()!=0) {

                        // Folder List UI
                        Intent intent = new Intent(PreparingActivity.this, FolderListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("HashMap", mGruopMap);
                        intent.putExtra("data", bundle);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // No Picture UI
                        Intent intent = new Intent(PreparingActivity.this, NoPictureActivity.class);
                        startActivity(intent);
//                        finish();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //定义切换进度图的方法
    public void move(int i)
    {
        Drawable a = getResources().getDrawable(R.drawable.a103, null);
        Drawable b = getResources().getDrawable(R.drawable.a104, null);
        Drawable c = getResources().getDrawable(R.drawable.a105, null);
        Drawable d = getResources().getDrawable(R.drawable.a106, null);
        Drawable e = getResources().getDrawable(R.drawable.a107, null);
        Drawable f = getResources().getDrawable(R.drawable.a108, null);
        Drawable g = getResources().getDrawable(R.drawable.a109, null);
        Drawable h = getResources().getDrawable(R.drawable.a110, null);
        Drawable I = getResources().getDrawable(R.drawable.a111, null);
        Drawable j = getResources().getDrawable(R.drawable.a112, null);
        Drawable k = getResources().getDrawable(R.drawable.a113, null);
        Drawable l = getResources().getDrawable(R.drawable.a114, null);
        //通过setForeground来设置前景图像
        switch(i)
        {
            case 0:
                relative.setForeground(a);
                break;
            case 1:
                relative.setForeground(b);
                break;
            case 2:
                relative.setForeground(c);
                break;
            case 3:
                relative.setForeground(d);
                break;
            case 4:
                relative.setForeground(e);
                break;
            case 5:
                relative.setForeground(f);
                break;
            case 6:
                relative.setForeground(g);
                break;
            case 7:
                relative.setForeground(h);
                break;
            case 8:
                relative.setForeground(I);
                break;
            case 9:
                relative.setForeground(j);
                break;
            case 10:
                relative.setForeground(k);
                break;
            case 11:
                relative.setForeground(l);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparing);
        //定义一个定时器对象,定时发送信息给handler
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                //发送一条空信息来通知系统改变前景图片
                mHandler.sendEmptyMessage(MOVE_DRAW);
            }
        }, 0,170);

        relative = (FrameLayout) findViewById(R.id.my_frame);

    }

    @Override
    protected void onStart() {
        super.onStart();
        int deviceId = getIntent().getIntExtra("deviceID", 0);
        switch (deviceId) {
            case 0: getImagesOfSD(); break;
            case 1: getImagesOfSD(); break;
            case 2: getImagesOfSD(); break;
        }
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getImagesOfSD() {
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }

        //显示进度条
        // mProgressDialog = ProgressDialog.show(FolderListActivity.this, null, "正在加载...");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e){
                    ;
                }

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = PreparingActivity.this.getContentResolver();

                //只查询jpeg和png,bmp的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "+ MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png", "image/bmp" }, MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
                    //获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    //获取该图片的父路径名,即图片所在的文件夹
                    String parentName = new File(path).getParentFile().getName();


                    //根据父路径名放入到mGruopMap, 包含的图片路径
                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }

                mCursor.close();
                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();
    }
}
