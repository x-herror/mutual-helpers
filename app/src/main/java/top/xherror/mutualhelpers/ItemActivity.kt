package top.xherror.mutualhelpers

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ItemActivity : AppCompatActivity() {
    /*
    val name:String,val imageId:Int,val location:String,val time:String
     */
    companion object {
        fun actionStart(context: Context, name:String, imageId:Int,location:String,time:String){
            val intent = Intent(context,ItemActivity::class.java)
            intent.run {
                putExtra("name",name)
                putExtra("imageId",imageId)
                putExtra("location",location)
                putExtra("time",time)
            }
            context.startActivity(intent)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
    }
}