package com.eposp.miseecamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 黑屏状态下拍照
 */
public class MainActivity extends AppCompatActivity {
    private View layout;
    private Camera camera;
    private Camera.Parameters parameters = null;
    private boolean isTakePicOk = true;//false没拍照完成
    Context mContext;

    Bundle bundle = null; // 声明一个Bundle对象，用来存储数据
//    int

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.carmera_layout);
        mContext = MainActivity.this;
        layout = this.findViewById(R.id.buttonLayout);

        SurfaceView surfaceView = (SurfaceView) this
                .findViewById(R.id.surfaceView);
        surfaceView.getHolder()
                .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(176, 144); //设置Surface分辨率
        surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
        surfaceView.getHolder().addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数
        AudioUtils.setMinVolume(mContext);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    /**
     * 按钮被点击触发的事件
     *
     * @param v
     */
    public void btnOnclick(View v) {
        if (camera != null) {
            switch (v.getId()) {
                case R.id.takepicture:
                    if (isTakePicOk) {
                        isTakePicOk = false;
                        // 拍照
                        camera.takePicture(null, null, new MyPictureCallback());

                    }

                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes", data); //将图片字节数据保存在bundle当中，实现数据交换
                saveToSDCard(data); // 保存图片到sd卡中
                isTakePicOk = true;
//                Toast.makeText(getApplicationContext(), R.string.success,
//                        Toast.LENGTH_SHORT).show();
                camera.startPreview(); // 拍完照后，重新开始预览

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将拍下来的照片存放在SD卡中
     *
     * @param data
     * @throws IOException
     */
    public static void saveToSDCard(byte[] data) throws IOException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String filename = format.format(date) + ".jpg";
        File fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/MiSeeCamera/");
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, filename);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流
    }

    private final class SurfaceCallback implements SurfaceHolder.Callback {

        // 拍照状态变化时调用该方法
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
//            parameters = camera.getParameters(); // 获取各项参数
//            parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
//            parameters.setPreviewSize(width, height); // 设置预览大小
//            parameters.setPreviewFrameRate(5);  //设置每秒显示4帧
//            parameters.setPictureSize(width, height); // 设置保存的图片尺寸
//            parameters.setJpegQuality(100); // 设置照片质量


//实现自动对焦
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if(success){
                        initCamera();//实现相机的参数初始化
                        camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                    }
                }

            });
        }

        // 开始拍照时调用该方法
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//            try {
////                camera = Camera.open(1); // 打开摄像头,一般//1代表前置摄像头，0代表后置摄像头
//                camera = Camera.open(); // 打开摄像头,一般//1代表前置摄像头，0代表后置摄像头
//                camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
////                camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
//                camera.setDisplayOrientation(270);
//                camera.startPreview(); // 开始预览
//            } catch (Exception e) {
//                e.printStackTrace();
//            }



            if(null==camera){
                camera=Camera.open();
                try {
                    camera.setPreviewDisplay(holder);
                    initCamera();
                    camera.startPreview();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        // 停止拍照时调用该方法
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera=null;

            }
        }
    }

    //相机参数的初始化设置
    private void initCamera() {
        parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        //parameters.setPictureSize(surfaceView.getWidth(), surfaceView.getHeight());  // 部分定制手机，无法正常识别该方法。
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启闪光灯
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//1连续对焦
        parameters.setJpegQuality(100); // 设置照片质量
        setDispaly(parameters, camera);
        camera.setParameters(parameters);
        camera.startPreview();
        camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上


//        parameters = camera.getParameters(); // 获取各项参数
//        parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
//        parameters.setPreviewSize(width, height); // 设置预览大小
//        parameters.setPreviewFrameRate(5);  //设置每秒显示4帧
//        parameters.setPictureSize(width, height); // 设置保存的图片尺寸
//        parameters.setJpegQuality(80); // 设置照片质量

    }

    //控制图像的正确显示方向
    private void setDispaly(Camera.Parameters parameters, Camera camera) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            setDisplayOrientation(camera, 90);
        } else {
            parameters.setRotation(90);
        }

    }
    //实现的图像的正确显示
    private void setDisplayOrientation(Camera camera, int i) {
        Method downPolymorphic;
        try{
            downPolymorphic=camera.getClass().getMethod("setDisplayOrientation", new Class[]{int.class});
            if(downPolymorphic!=null) {
                downPolymorphic.invoke(camera, new Object[]{i});
            }
        }
        catch(Exception e){
            Log.e("Came_e", "图像出错");
        }
    }
    /**
     * 点击手机屏幕是，显示两个按钮
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("TAG", "ACTION_DOWN");
                layout.setVisibility(ViewGroup.VISIBLE); // 设置视图可见
                break;
        }
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_CAMERA: // 按下拍照按钮
                if (camera != null && event.getRepeatCount() == 0) {
                    // 拍照
                    //注：调用takePicture()方法进行拍照是传入了一个PictureCallback对象——当程序获取了拍照所得的图片数据之后
                    //，PictureCallback对象将会被回调，该对象可以负责对相片进行保存或传入网络
                    camera.takePicture(null, null, new MyPictureCallback());
                }
            case KeyEvent.KEYCODE_BACK:
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

}
