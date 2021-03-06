package cn.net.yzl.base.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ${xingen} on 2017/11/1.
 * blog:http://blog.csdn.net/hexingen
 */

public class BitmapUtils {
    private final static String TAG = BitmapUtils.class.getSimpleName();
    /**
     * @param context
     * @param path
     * @param targetWith
     * @param targetHeight
     * @return
     */
    public synchronized static Bitmap decodeFileBitmap(Context context, String path, int targetWith, int targetHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            decodeStreamToBitmap(context, path, options);
            options.inSampleSize = calculateScaleSize(options, targetWith, targetHeight);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = decodeStreamToBitmap(context, path, options);
            return getNormalBitmap(bitmap, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param context
     * @param path
     * @return
     */
    public synchronized static Bitmap decodeFileBitmap(Context context, String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = decodeStreamToBitmap(context, path, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap decodeStreamToBitmap(Context context, String path, BitmapFactory.Options options) {
        Bitmap bitmap = null;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            //MIME type??????????????????
            InputStream inputStream = contentResolver.openInputStream(Uri.parse(path.contains("file:") ? path : "file://" + path));
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param options
     * @param targetWith
     * @param targetHeight
     * @return
     */
    private static int calculateScaleSize(BitmapFactory.Options options, int targetWith, int targetHeight) {
        int simpleSize;
        if (targetWith > 0 && targetHeight > 0) {
            int scaleWith = (int) Math.ceil((options.outWidth * 1.0f) / targetWith);
            int scaleHeight = (int) Math.ceil((options.outHeight * 1.0f) / targetHeight);
            simpleSize = Math.max(scaleWith, scaleHeight);
        } else {
            simpleSize = 1;
        }
        if (simpleSize == 0) {
            simpleSize = 1;
        }
        return simpleSize;
    }

    /**
     * ???????????????bitmap????????????????????????????????????bitmap
     *
     * @param bitmap
     * @param path
     * @return
     */
    private static Bitmap getNormalBitmap(Bitmap bitmap, String path) {
        int rotate = getBitmapRotate(path);
        Bitmap normalBitmap;
        switch (rotate) {
            case 90:
            case 180:
            case 270:
                try {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate);
                    normalBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    normalBitmap = bitmap;
                }
                break;
            default:
                normalBitmap = bitmap;
                break;
        }
        return normalBitmap;
    }

    /**
     * ExifInterface ???????????????jpeg??????????????????image ?????????
     * ????????????????????????????????????
     *
     * @param path
     * @return
     */
    public static int getBitmapRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static boolean isRotate(Context context,String path){
        Bitmap bitmap = null;

        try {
            int degree =getBitmapRotate(path);
            if(degree > 0){
                return true;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //MIME type??????????????????
            bitmap =BitmapFactory.decodeFile(path);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if(height > width){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(bitmap != null){
                bitmap.recycle();
            }
        }
        return  false;
    }

    public static void savePhotoToSDCard(Bitmap photoBitmap,String photoPath, String photoName) {
        if (checkSDCardAvailable()) {
            File photoFile = new File(photoPath,photoName);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    if(fileOutputStream  != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean checkSDCardAvailable() {
       return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }






}
