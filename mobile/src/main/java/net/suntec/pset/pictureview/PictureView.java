package net.suntec.pset.pictureview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PictureView extends AppCompatActivity implements View.OnTouchListener{

    /* 菜单 */
    ImageView btn_rotateR,btn_rotateL,btn_play,btn_set,btn_list;

    /* 展示图片的路径 */
    private String mImagePath;

    /* 图片列表 */
    private ArrayList<String> mImageList = new ArrayList<>();

    /* 当前图片在列表中的位置 */
    private int mCurrentIndex;


    /* 设置壁纸完成后的request code */
    private final int SET_WALLPAPER_OK = 1;

    private ImageView imgView;
    private LinearLayout linearLayout;
    private FrameLayout frameLayout;
    private boolean isButtonBarShow = true;

    // 不同状态的表示：
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF point0 = new PointF();
    private PointF pointM = new PointF();


    /**
     * 图片缩放矩阵
     */
    private Matrix matrix = new Matrix();
    /**
     * 保存触摸前的图片缩放矩阵
     */
    private Matrix savedMatrix = new Matrix();
    /**
     * 保存触点移动过程中的图片缩放矩阵
     */
    private Matrix matrix1 = new Matrix();
    /**
     * 屏幕高度
     */
    private int displayHeight;
    /**
     * 屏幕宽度
     */
    private int displayWidth;
    /**
     * 最小缩放比例
     */
    protected float minScale = 1f;
    /**
     * 最大缩放比例
     */
    protected float maxScale = 3f;
    /**
     * 当前缩放比例
     */
    protected float currentScale = 1f;
    /**
     * 多点触摸2个触摸点间的起始距离
     */
    private float oldDist;
    /**
     * 多点触摸时图片的起始角度
     */
    private float oldRotation = 0;
    /**
     * 旋转角度
     */
    protected float rotation = 0;
    /**
     * 图片初始宽度
     */
    private int imgWidth;
    /**
     * 图片初始高度
     */
    private int imgHeight;
    /**
     * 设置单点触摸退出图片显示时，单点触摸的灵敏度（可针对不同手机单独设置）
     */
    protected final int MOVE_MAX = 2;
    /**
     * 设置单点触摸退出图片显示时，单点触摸的灵敏度（可针对不同手机单独设置）
     */
    protected final int NORMAL_FINGER_NUM = 2;
    /**
     * 设置单点触摸退出图片显示时，单点触摸的灵敏度（可针对不同手机单独设置）
     */
    protected final int ILLEGAL_FINGER_NUM = 3;
    /**
     * 单点触摸时手指触发的‘MotionEvent.ACTION_MOVE’次数
     */
    private int fingerMove = 0;
    /**
     * 落在屏幕上的手指的个数
     */
    private int fingerNum = 0;

    private Bitmap bm;
    /**
     * 保存matrix缩放比例
     */
    private float matrixScale= 1;


    /**
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        initData();

    }


    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.btnRotateR:
                    Log.i("Button click","RotateR");
                    rotateM(matrix1,90f);
                    imgView.setImageMatrix(matrix1);
                    break;
                case R.id.btnRotateL:
                    Log.i("Button click","RotateL");
                    rotateM(matrix1,-90f);
                    imgView.setImageMatrix(matrix1);
                    break;
                case R.id.btnPlay: {
                    Log.i("Button click", "Play");
                    Intent intent = new Intent(PictureView.this, SlideShowActivity.class);
                    intent.putExtra("PictureList", mImageList);
                    intent.putExtra("index", mCurrentIndex);
                    startActivity(intent);
                    break;
                }
                case R.id.btnSet: {
                    Intent intent = new Intent(PictureView.this, PVSetWallpaper.class);
                    intent.putExtra("path", mImagePath);
                    startActivityForResult(intent, SET_WALLPAPER_OK);
                    Log.i("Button click", "Set");
                    break;
                }
                case R.id.btnList: {
                    Log.i("Button click", "List");
                    finish();
//                    Intent intent = new Intent(PictureView.this, PictureListActivity.class);
//                    startActivity(intent);
                    break;
                }
                default:
                    break;

            }
        }
    }

    public void initData() {
        btn_rotateR = (ImageView) findViewById(R.id.btnRotateR);
        btn_rotateL = (ImageView) findViewById(R.id.btnRotateL);
        btn_play = (ImageView) findViewById(R.id.btnPlay);
        btn_set = (ImageView) findViewById(R.id.btnSet);
        btn_list = (ImageView) findViewById(R.id.btnList);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        frameLayout = (FrameLayout) findViewById(R.id.activity_pic_show);
        /* 为五个按钮添加监听器 */
        btn_rotateL.setOnClickListener(new MyListener());
        btn_rotateR.setOnClickListener(new MyListener());
        btn_play.setOnClickListener(new MyListener());
        btn_set.setOnClickListener(new MyListener());
        btn_list.setOnClickListener(new MyListener());

        mImageList = getIntent().getStringArrayListExtra("PictureList");
        System.out.println("Show the ImageList after put:" + mImageList);
        mCurrentIndex = getIntent().getIntExtra("index", 0);
        System.out.println("Show the index:" + mCurrentIndex);
        mImagePath = mImageList.get(mCurrentIndex);

        bm = BitmapFactory.decodeFile(mImagePath);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        displayWidth = dm.widthPixels;
        displayHeight = dm.heightPixels;
        imgView = (ImageView)findViewById(R.id.img);
        imgView.setOnTouchListener(this);
        showImage();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        ImageView imgView = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {


            case MotionEvent.ACTION_DOWN:
                System.out.println("MotionEvent--ACTION_DOWN");
                savedMatrix.set(matrix1);
                point0.set(event.getX(), event.getY());
                mode = DRAG;
                fingerNum++;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                System.out.println("MotionEvent--ACTION_POINTER_DOWN---" + oldRotation);
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(matrix1);
                setMidPoint(pointM, event);
                mode = ZOOM;
                fingerNum++;

                if(ILLEGAL_FINGER_NUM == fingerNum) {
                    modify(matrix1, rotation);
                    rotation = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("MotionEvent--ACTION_UP");

                if (mode == DRAG && (fingerMove <= MOVE_MAX)) {

                    if (isButtonBarShow) {
                        /**
                         * 进入全屏模式
                         */
                        linearLayout.setVisibility(View.INVISIBLE);
                        isButtonBarShow = false;
                        // 隐藏状态栏
//                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        frameLayout.setSystemUiVisibility(v.SYSTEM_UI_FLAG_FULLSCREEN);
                        //frameLayout.setVisibility(View.INVISIBLE);
                        System.out.println("----------Full Screen Mode-----------");
                    } else {
                        /**
                         * 进入一般模式
                         */
                        linearLayout.setVisibility(View.VISIBLE);
                        isButtonBarShow = true;
//                        getWindow().setFlags(WindowManager.LayoutParams.FLA,
//                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        frameLayout.setSystemUiVisibility(v.SYSTEM_UI_FLAG_VISIBLE);
                        //frameLayout.setVisibility(View.VISIBLE);
                        System.out.println("---------------Normal Mode-----------");
                    }
                    //PicShow.this.finish();
                }
                //checkView();
                postToCenter(matrix1);
                //matrix1.postScale(currentScale, currentScale, pointM.x, pointM.y);
                imgView.setImageMatrix(matrix1);
                fingerMove = 0;
                fingerNum = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                System.out.println("MotionEvent--ACTION_POINTER_UP");

                if(ILLEGAL_FINGER_NUM > fingerNum) {
                    modify(matrix1, rotation);
                    rotation = 0;
                }
                fingerNum--;

                if(NORMAL_FINGER_NUM == fingerNum) {
                    mode = ZOOM;
                } else {
                    mode = NONE;
                }
                savedMatrix.set(matrix1);
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("MotionEvent--ACTION_MOVE");

                if(ILLEGAL_FINGER_NUM > fingerNum) {
                    operateMove(event,matrix1);
                }
                imgView.setImageMatrix(matrix1);
                fingerMove++;
                break;
        }
        return true;
    }

    /**
     * 显示图片
     */
    private void showImage() {
        imgWidth = bm.getWidth();
        imgHeight = bm.getHeight();
        imgView.setImageBitmap(bm);
        matrix.setScale(1, 1);
        //centerAndRotate(rotation);
        rotateM(matrix,rotation);
        rotation = 0;
        imgView.setImageMatrix(matrix);
        System.out.println("------------show------------" );

    }

    /**
     * 触点移动时的操作
     *
     * @param event 触摸事件
     */
    private void operateMove(MotionEvent event,Matrix mtrx) {
        matrix1.set(savedMatrix);
        System.out.println("模式---------：" + mode);
        switch (mode) {
            case DRAG:
                RectF rect = new RectF(0, 0, imgWidth, imgHeight);
                mtrx.mapRect(rect);
                if  (rect.top <= 0 && rect.bottom >= displayHeight) {
                    matrix1.postTranslate(event.getX() - point0.x, event.getY() - point0.y);
                } else {
                    matrix1.postTranslate(event.getX() - point0.x, 0);
                }
                break;
            case ZOOM:
                rotation = rotation(event) - oldRotation;
                float newDist = spacing(event);
                float scale = newDist / oldDist;
                currentScale = (scale > 3.5f) ? 3.5f : scale;
                System.out.println("缩放倍数---：" + currentScale);
                System.out.println("***倍数---：" + matrixScale);
                System.out.println("旋转角度---：" + rotation);
                /** 缩放 */
                checkView();
                postToCenter(matrix1);
                matrix1.postScale(currentScale, currentScale, pointM.x, pointM.y);
                /** 旋转 */
                postToCenter(matrix1);
                matrix1.postRotate(rotation, displayWidth / 2, displayHeight / 2);
                break;
        }
    }

    /**
     * 两个触点的距离
     *
     * @param event 触摸事件
     * @return float
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 获取旋转角度
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 两个触点的中间坐标
     *
     * @param pointM 中间坐标
     * @param event  触摸事件
     */
    private void setMidPoint(PointF pointM, MotionEvent event) {
        float x = event.getX(0) + event.getY(1);
        float y = event.getY(0) + event.getY(1);
        pointM.set(x / 2, y / 2);
    }

    /**
     * 检查约束条件(缩放倍数)
     */
    private void checkView() {
        if (currentScale > 1) {
            if (currentScale * matrixScale > maxScale) {
                matrix.postScale(maxScale / matrixScale, maxScale / matrixScale, pointM.x, pointM.y);
                matrixScale = maxScale;
            } else {
                matrix.postScale(currentScale, currentScale, pointM.x, pointM.y);
                matrixScale *= currentScale;
            }
        } else {
            if (currentScale * matrixScale < minScale) {
                matrix.postScale(minScale / matrixScale, minScale / matrixScale, pointM.x, pointM.y);
                matrixScale = minScale;
            } else {
                matrix.postScale(currentScale, currentScale, pointM.x, pointM.y);
                matrixScale *= currentScale;
            }
        }
    }

    /**
     * 图片居中显示
     *
     * @param mtrx 要旋转的矩形
     */
    private void postToCenter(Matrix mtrx) {
        RectF rect = new RectF(0, 0, imgWidth, imgHeight);
        mtrx.mapRect(rect);
        float width = rect.width();
        float height = rect.height();
        float dx = 0;
        float dy = 0;

        if (width < displayWidth) {
            dx = displayWidth / 2 - width / 2 - rect.left;
        } else if (rect.left > 0) {
            dx = -rect.left;
        } else if (rect.right < displayWidth) {
            dx = displayWidth - rect.right;
        }

        if (height < displayHeight) {
            dy = displayHeight / 2 - height / 2 - rect.top;
        } else if (rect.top > 0) {
            dy = -rect.top;
        } else if (rect.bottom < displayHeight) {
            dy = displayHeight - rect.bottom;
        }

        mtrx.postTranslate(dx, dy);
    }

    /**
     * 判断旋转角度 小于（90 * x + 45）度图片旋转（90 * x）度 大于则旋转（90 * (x+1)）
     *
     * @param mtrx 要旋转的矩形
     * @param m_rotate 旋转的角度
     */
    private void rotateM(Matrix mtrx,float m_rotate) {
        postToCenter(mtrx);
        if (m_rotate != 0) {
            int rotationNum = (int) (m_rotate / 90);
            float rotationAvai = new BigDecimal(m_rotate % 90).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            float realRotation = 0;
            if (m_rotate > 0) {
                realRotation = rotationAvai > 45 ? (rotationNum + 1) * 90 : rotationNum * 90;
            } else if (m_rotate < 0) {
                realRotation = rotationAvai < -45 ? (rotationNum - 1) * 90 : rotationNum * 90;
            }
            System.out.println("realRotation: " + realRotation);
            mtrx.postRotate(realRotation, displayWidth / 2, displayHeight / 2);
            //rotation = 0;
        }
    }

    /**
     * 修正图片的旋转角度 大于45度 ？ 旋转90 : 图片旋转回初始位置
     *
     * @param mtrx 要旋转的矩形
     * @param m_rotate 旋转的角度
     */
    private void modify(Matrix mtrx,float m_rotate) {
        postToCenter(mtrx);
        if (m_rotate != 0) {
            float rotationAvai = new BigDecimal(m_rotate % 90).setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
            float realRotation = 0;
            if (m_rotate > 0) {
                realRotation = rotationAvai > 45 ? 90 - rotationAvai : 0 - rotationAvai;
            } else if (m_rotate < 0) {
                realRotation = rotationAvai < -45 ? -90 - rotationAvai : 0 - rotationAvai;
            }
            System.out.println("modify rotation: " + -realRotation);
            mtrx.postRotate(realRotation, displayWidth / 2, displayHeight / 2);
        }
    }

    /**
    *  剪裁图片
     *
     *
    */

    private void CropPicture() {

    }
}
