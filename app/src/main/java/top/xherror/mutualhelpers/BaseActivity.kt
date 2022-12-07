package top.xherror.mutualhelpers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.*



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

    override fun onBackPressed() {
        finishAll()
    }

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





}