package top.xherror.mutualhelpers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlin.math.roundToInt

object Utils {
    fun getBitmap(imagePath: String): Bitmap {
        Log.d("TTT", imagePath)
        //val fis = FileInputStream(imagePath)
        //val fis= getClassLoader().getResourceAsStream(imagePath)
        //Log.d("TTT",fis.toString())
        //bitmap = BitmapFactory.decodeFile(imagePath)
        //1 获取图片的原始大小

        //1 获取图片的原始大小
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true //如果设置为true，不获取图片，不分配内存，但会返回图片的高度宽度信息。

        BitmapFactory.decodeFile(imagePath, options)

        val srcWidth = options.outWidth.toFloat() //获取图片的宽度值

        val srcHeight = options.outHeight.toFloat() //获取图片的高度值


        //2 得到目标显示的大小
        val width = 200 //目标显示的长

        val height = 200 //目标显示的宽

        //3 得到缩放比例
        var inSampleSize = 1
        if (srcHeight > height || srcWidth > width) {
            inSampleSize = if (srcWidth > srcHeight) {
                (srcWidth / width).roundToInt()
            } else {
                (srcHeight / height).roundToInt()
            }
        }

        //4 进行缩放

        //4 进行缩放
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        return BitmapFactory.decodeFile(imagePath, options)
    }
}