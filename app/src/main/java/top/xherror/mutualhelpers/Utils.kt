package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.biometrics.BiometricManager
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.time.LocalDate
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

object Utils {
    private fun getBitmapFromBitmap(imagePath: String): Bitmap {
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
        val width = 300 //目标显示的长

        val height = 300 //目标显示的宽

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

    private fun scaleBitmap(bitmap: Bitmap):Bitmap{
        val srcWidth = bitmap.width
        val srcHeight = bitmap.height

        val width = 300
        val height = 300

        var inSampleSize = 1.0
        if (srcHeight > height || srcWidth > width) {
            inSampleSize = if (srcWidth > srcHeight) {
                (width.toDouble()/srcWidth)
            } else {
                (height.toDouble()/srcWidth)
            }
        }
        val matrix=Matrix()
        matrix.postScale(inSampleSize.toFloat(),inSampleSize.toFloat())
        return Bitmap.createBitmap(bitmap,0,0,srcWidth,srcWidth,matrix,true)
    }

    fun getBitmap(imagePath: String,chooseOption:Int):Bitmap{
        lateinit var bitmap:Bitmap
        when (chooseOption){
            0 ->{
                bitmap = getBitmapFromBitmap(imagePath)
            }
            1->{
                val bis= BufferedInputStream(FileInputStream(imagePath))
                bitmap=BitmapFactory.decodeStream(bis)
                bitmap= scaleBitmap(bitmap)
            }
        }
        return bitmap
    }

    fun getCompressBitmap(imagePath: String,chooseOption:Int):Bitmap{
        return getBitmap(imagePath, chooseOption)
    }

    fun setBitmapUseGlide(item: EntityItem,imageView: ImageView,activity: BaseActivity,viewWidth:Int=-1,viewHeight: Int=-1){

        val targetWidth=viewWidth.toFloat()
        val targetHeight=viewHeight.toFloat()
        if (item.imageName.isNotEmpty()){
            if (viewWidth!=-1&&viewHeight!=-1){
                val width = item.imageWidth.toFloat()
                val height = item. imageHeight.toFloat()
                var inSampleSize = 1f
                if (height > targetHeight || width > targetHeight) {
                    inSampleSize = if (width > height) {
                        (width / targetWidth)
                    } else {
                        (height / targetHeight)
                    }
                }
                val resultWidth = (width/inSampleSize).toInt()
                val resultHeight = (height/inSampleSize).toInt()

                Glide.with(activity)
                    .load("http://192.168.0.184:8080/images/${item.imageName}")
                    .apply(RequestOptions().override(resultWidth, resultHeight))
                    .into(imageView)
            }else{
                Glide.with(activity)
                    .load("http://192.168.0.184:8080/images/${item.imageName}")
                    .into(imageView)
            }

        }
    }



    /*
    @SuppressLint("Range")
    fun fillItemList(itemList: ArrayList<Item>,type:Int=1,searchString:String=""){
        //id PK auto int,name string,imagePath string,location string
        //time string,phone string,owner string,description string
        //chooseOption integer
        //TODO:cache query result
        val cursor=DateBase.myDBHelper.readableDatabase.rawQuery("SELECT * FROM MyItems",null)
        cursor.use {
            if (it.moveToFirst()){
                do{
                    val id=it.getInt(it.getColumnIndex("id"))
                    val name=it.getString(it.getColumnIndex("name"))
                    val imagePath=it.getString(it.getColumnIndex("imagePath"))
                    val location=it.getString(it.getColumnIndex("location"))
                    val time=it.getString(it.getColumnIndex("time"))
                    val phone=it.getString(it.getColumnIndex("phone"))
                    val owner=it.getString(it.getColumnIndex("owner"))
                    val description=it.getString(it.getColumnIndex("description"))
                    val chooseOption=it.getInt(it.getColumnIndex("chooseOption"))
                    var bitmap:Bitmap?=null
                    if (imagePath!=""){
                        bitmap=Utils.getBitmap(imagePath,chooseOption)
                    }
                    when (type){
                        1 ->{
                            itemList.add(Item(id,name, bitmap,location,time))
                        }
                        2 ->{
                            if (owner=="xherror"){itemList.add(Item(id,name, bitmap,location,time))}
                        }
                        3 ->{
                            if (searchString in name||searchString in description){itemList.add(Item(id,name, bitmap,location,time))}
                        }
                    }

                } while (cursor.moveToNext())
            }
        }
    }

    @SuppressLint("Range")
    fun getId(name:String, location:String, time: String):Int{
        val cursor=DateBase.myDBHelper.readableDatabase.rawQuery("SELECT id FROM MyItems WHERE name=? AND location=? AND time=?",
            arrayOf(name,location,time)
        )
        var id=0
        Log.d("GetId",cursor.moveToFirst().toString())
        if (cursor.moveToFirst()){
            id=cursor.getInt(cursor.getColumnIndex("id"))
        }
        return id
    }


    @SuppressLint("Range")
    fun getTuple(id:Int):Tuple {
        val cursor = DateBase.myDBHelper.readableDatabase.rawQuery("SELECT * FROM MyItems WHERE id=?", arrayOf(id.toString()))
        lateinit var tuple:Tuple
        cursor.use {
            it.moveToFirst()
            Log.d("GetTuple",it.moveToFirst().toString())
            val name = it.getString(it.getColumnIndex("name"))
            val imagePath = it.getString(it.getColumnIndex("imagePath"))
            val location = it.getString(it.getColumnIndex("location"))
            val time = it.getString(it.getColumnIndex("time"))
            val phone = it.getString(it.getColumnIndex("phone"))
            val owner = it.getString(it.getColumnIndex("owner"))
            val description = it.getString(it.getColumnIndex("description"))
            val chooseOption = it.getInt(it.getColumnIndex("chooseOption"))
            tuple=
                Tuple(id, name, imagePath, location, time, phone, owner, description, chooseOption)
        }
        return tuple
    }

     */
}
