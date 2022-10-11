package top.xherror.mutualhelpers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import top.xherror.mutualhelpers.databinding.ActivityItemBinding
import top.xherror.mutualhelpers.databinding.ActivityMainBinding

class ItemActivity : AppCompatActivity() {
    /*
    val name:String,val imageId:Int,val location:String,val time:String
     */
    companion object {
        //TODO:use unique id replace this
        fun actionStart(context: Context, name:String, bitmap: Bitmap?,location:String,time:String){
            val intent = Intent(context,ItemActivity::class.java)
            intent.run {
                putExtra("name",name)
                putExtra("location",location)
                putExtra("time",time)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name=intent.getStringExtra("name")
        val location=intent.getStringExtra("location")
        val time=intent.getStringExtra("time")

        binding.activityItemEditTextName.text=name
        binding.activityItemEditTextLocation.text=location
        binding.activityItemEditTextTime.text=time

    }
}