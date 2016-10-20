package net.suntec.pset.pictureview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;

public class PVSetWallpaper extends AppCompatActivity {

    private Uri imageUri;
    private ImageView mImageView;
    private String path;
    private Button mPreviewBtn;
    private Button mCancelBtn;
    private Button mInitializeBtn;
    private Button mFinishBtn;

    private File image;
    private RelativeLayout mRootLayout;
    private FrameLayout mFrameLayout;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mFrameLayout.setVisibility(View.INVISIBLE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvset_wallpaper);
        path = getIntent().getStringExtra("path");
        image = new File(path);
        imageUri = Uri.fromFile(image);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageURI(imageUri);
        mCancelBtn = (Button) findViewById(R.id.cancel);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mPreviewBtn = (Button) findViewById(R.id.preview);
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file=new File(Environment.getExternalStorageDirectory(),
                        "/temp/"+System.currentTimeMillis() + ".jpg");
//                if (!file.getParentFile().exists())
//                    file.getParentFile().mkdirs();
                Uri outputUri = FileProvider.getUriForFile(v.getContext(),
                        "net.suntec.pset.fileprovider",image);
                Uri inputUri=FileProvider.getUriForFile(v.getContext(),
                        "net.suntec.pset.fileprovider",
                        image);
                //通过FileProvider创建一个content类型的Uri

                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setAction("com.android.camera.action.CROP");
                intent.setDataAndType(inputUri, "image/*");// mUri是已经选择的图片Uri
                intent.putExtra("data", "true");
                intent.putExtra("aspectX", 1);// 裁剪框比例
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
                intent.putExtra("outputFormat",
                        Bitmap.CompressFormat.JPEG.toString());
//                intent.putExtra("outputX", 150);// 输出图片大小
//                intent.putExtra("outputY", 150);
                intent.putExtra("return-data", true);
                PVSetWallpaper.this.startActivityForResult(intent, 200);
            }
        });

        mRootLayout = (RelativeLayout) findViewById(R.id.pvset_wallpaper);
        mFrameLayout = (FrameLayout) findViewById(R.id.framelayout);
        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFrameLayout.getVisibility()!=View.VISIBLE) {
                    mFrameLayout.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(mRunnable, 4000);
                }
                else {
                    mFrameLayout.setVisibility(View.INVISIBLE);
                    mHandler.removeCallbacks(mRunnable);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (200 == requestCode) {

            if (resultCode == RESULT_OK) {
                // 拿到剪切数据
                Bitmap bmap = data.getParcelableExtra("data");

                // 显示剪切的图像
                final ImageView imageview = (ImageView) this.findViewById(R.id.imageView);
                imageview.setImageBitmap(bmap);
                imageview.setScaleType(ImageView.ScaleType.FIT_XY);
                imageview.setVisibility(View.VISIBLE);

                imageview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageview.setVisibility(View.GONE);
                    }
                });

                // 图像保存到文件中
                FileOutputStream foutput = null;
                try {
                    foutput = new FileOutputStream("/root/home/wangzhanfei/image1.png");
                    bmap.compress(Bitmap.CompressFormat.PNG, 100, foutput);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    if (null != foutput) {
                        try {
                            foutput.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
