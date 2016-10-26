package net.suntec.pset.pictureview;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;

public class PVSetWallpaper extends Activity {

    private Uri imageUri;
    private CropImageView mCropImageView;
    private ImageView mCroppedImageView;
    private String path;
    private Button mPreviewBtn;
    private Button mCancelBtn;
    private Button mInitializeBtn;
    private Button mFinishBtn;

    private File image;
    private FrameLayout mRootLayout;
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
        Bitmap bm = BitmapFactory.decodeFile(path);
        mCroppedImageView = (ImageView) findViewById(R.id.croppedImageView);
        mCroppedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCroppedImageView.setVisibility(View.INVISIBLE);
            }
        });

        mCropImageView = (CropImageView) findViewById(R.id.imageView);
        mCropImageView.setOnClickListener(new View.OnClickListener() {
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
        mCropImageView.setImageBitmap(bm);

        mCancelBtn = (Button) findViewById(R.id.cancel);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                Intent intent = new Intent(PVSetWallpaper.this, PictureListActivity.class);
//                startActivity(intent);
            }
        });
        mPreviewBtn = (Button) findViewById(R.id.preview);
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap croppedImage = mCropImageView.getCroppedImage();
                mCroppedImageView.setImageBitmap(croppedImage);
                mCroppedImageView.setVisibility(View.VISIBLE);
                mFrameLayout.setVisibility(View.INVISIBLE);
            }
        });

        mRootLayout = (FrameLayout) findViewById(R.id.pvset_wallpaper);
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

        mInitializeBtn = (Button) findViewById(R.id.init);
        mInitializeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropImageView.initCropWindow();
            }
        });

        mFinishBtn = (Button) findViewById(R.id.finish);
        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PVSetWallpaper.this)
                        .setMessage("Set the image in the frame as the wallpaper?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                WallpaperManager wallpaperManager = WallpaperManager.getInstance(PVSetWallpaper.this);
                                Resources res = getResources();

                                Bitmap croppedImage = mCropImageView.getCroppedImage();
                                try
                                {
                                    wallpaperManager.setBitmap(croppedImage);
                                }
                                catch (IOException e)
                                {
                                    e.printStackTrace();
                                }



                                finish();
                                //Toast.makeText(PictureView.this, )
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ;
                            }
                        })
                        .show();
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
