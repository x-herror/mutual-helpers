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
        fun actionStart(context: Context, id:Int){
            val intent = Intent(context,ItemActivity::class.java)
            intent.run {
                putExtra("id",id)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id=intent.getIntExtra("id",0)
        val tuple=Utils.getTuple(id)
        val bitmap=Utils.getBitmap(tuple.imagePath,tuple.chooseOption)
        binding.activityItemImage.setImageBitmap(bitmap)
        binding.activityItemEditTextName.text=tuple.name
        binding.activityItemEditTextLocation.text=tuple.location
        binding.activityItemEditTextTime.text=tuple.time
        binding.activityItemEditTextPhone.text=tuple.phone
        if (tuple.description!=""){ binding.activityItemEditTextDescription.text=tuple.description }
    }
}