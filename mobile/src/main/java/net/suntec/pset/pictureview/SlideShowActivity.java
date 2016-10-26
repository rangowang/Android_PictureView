package net.suntec.pset.pictureview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.id.list;

public class SlideShowActivity extends AppCompatActivity {

    private ImageView imgView_slide;
    private List<String> mImageList = new ArrayList<>();
    private int mCurrentIndex;
    private Bitmap bm;
    private String mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mImageList = getIntent().getStringArrayListExtra("PictureList");
        mCurrentIndex = getIntent().getIntExtra("index", 0);


        imgView_slide = (ImageView)findViewById(R.id.img_slide);
        imgView_slide.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                timer.cancel();
                System.out.println("timer.cancel---：");
                SlideShowActivity.this.finish();
                System.out.println("SlideShow.this.finish---：");
            }
        });

        timer.schedule(task, 0, 1000); // 0s后执行task,经过1s再次执行

    }

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            //发送一条空信息来通知系统改变前景图片
            mHandler.sendEmptyMessage(0xabc);
        }
    };

    //自定义一个用于定时更新UI界面的handler类对象
    Handler mHandler = new Handler()
    {
        //        int i = 0;
        @Override
        public void handleMessage(Message msg) {
            //判断信息是否为本应用发出的
            if(msg.what == 0xabc)
            {

                if(mCurrentIndex < mImageList.size())  {
                    mImagePath = mImageList.get(mCurrentIndex);
                    bm = BitmapFactory.decodeFile(mImagePath);
                    imgView_slide.setImageBitmap(bm);
                    mCurrentIndex++;
                } else {
                    timer.cancel();
                    SlideShowActivity.this.finish();
                }
//                imgView_slide.setImageResource(imgId[i%pic_num]);
            }
            super.handleMessage(msg);
        }
    };
}
