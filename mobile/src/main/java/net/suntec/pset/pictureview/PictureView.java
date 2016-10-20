package net.suntec.pset.pictureview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class PictureView extends AppCompatActivity {

    private Button mBtnSetWallpaper;
    private Uri imageUri;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        Intent intent = getIntent();
        path = intent.getStringExtra("data");
        final File image = new File(path);
        imageUri = Uri.fromFile(image);

        ImageView imageview = (ImageView) findViewById(R.id.imageview);
        imageview.setImageURI(imageUri);

        mBtnSetWallpaper = (Button) findViewById(R.id.btn_set_wallpaper);
        mBtnSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PictureView.this, PVSetWallpaper.class);
//                intent.setAction("com.android.camera.action.CROP");
//                intent.setDataAndType(imageUri, "image/*");// mUri是已经选择的图片Uri
//                intent.putExtra("data", "true");
//                intent.putExtra("aspectX", 1);// 裁剪框比例
//                intent.putExtra("aspectY", 1);
//                intent.putExtra("outputX", 150);// 输出图片大小
//                intent.putExtra("outputY", 150);
//                intent.putExtra("return-data", true);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });


    }
}
