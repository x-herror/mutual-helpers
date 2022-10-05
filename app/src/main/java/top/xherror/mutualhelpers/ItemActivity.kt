package top.xherror.mutualhelpers

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ItemActivity : AppCompatActivity() {

    companion object {
        fun actionStart(context: Context, data1:String, data2:String){
            val intent = Intent(context,ItemActivity::class.java)
            intent.putExtra("param1",data1)
            intent.putExtra("param2",data2)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
    }
}