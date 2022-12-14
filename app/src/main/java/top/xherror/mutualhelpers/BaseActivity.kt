package top.xherror.mutualhelpers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.apache.commons.codec.binary.Hex
import java.io.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseActivity",javaClass.simpleName)
        addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeActivity(this)
    }

    /*
    override fun onBackPressed() {
        finishAll()
    }
     */
    protected  fun saveUseFile(inputText:String){
        try {
            val fd=openFileOutput("data", Context.MODE_PRIVATE)
            val writer= BufferedWriter(OutputStreamWriter(fd))
            writer.use {
                it.write(inputText)
            }
        }catch (e: IOException){
            e.printStackTrace()}
    }
    //TODO:type T
    protected fun SharedPreferences.save(block: SharedPreferences.Editor.()->Unit){
        edit().run {
            block()
            commit()
        }
    }

    protected fun loadFromFile(fileName:String):List<String>{
        lateinit var result:List<String>
        try {
            val fd=openFileInput(fileName)
            val reader= BufferedReader(InputStreamReader(fd))
            reader.use {
                result=it.readLines()
            }
        } catch (e: IOException){
            e.printStackTrace()
        }
        return result
    }

    private val activities=ArrayList<Activity>()

    private fun addActivity(activity: Activity){
        activities.add(activity)
    }

    private fun removeActivity(activity: Activity){
        activities.remove(activity)
    }

    private fun finishAll(){
        for (activity in activities){
            if(!activity.isFinishing){
                activity.finish()
            }
        }
        activities.clear()
    }

    fun setBitmapUseGlide(item: EntityItem, imageView: ImageView, activity: BaseActivity, viewWidth:Int=-1, viewHeight: Int=-1){

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

}