package top.xherror.mutualhelpers

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import top.xherror.mutualhelpers.databinding.ActivityItemBinding
import top.xherror.mutualhelpers.databinding.ActivityMainBinding

class ItemActivity : BaseActivity() {

    companion object {
        //TODO:use unique id replace this
        lateinit var showEntityItem: EntityItem
        fun actionStart(context: Context){
            context.startActivity(Intent(context,ItemActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.activityItemImage.setImageBitmap(Utils.getBitmap(showEntityItem.imagePath, showEntityItem.chooseOption))
        binding.activityItemEditTextName.text= showEntityItem.name
        binding.activityItemEditTextLocation.text=showEntityItem.location
        binding.activityItemEditTextTime.text=showEntityItem.time
        binding.activityItemEditTextPhone.text=showEntityItem.phone
        /*
        val id=intent.getIntExtra("id",0)
        val tuple=Utils.getTuple(id)
        val bitmap=Utils.getBitmap(tuple.imagePath,tuple.chooseOption)
        binding.activityItemImage.setImageBitmap(bitmap)
        binding.activityItemEditTextName.text=tuple.name
        binding.activityItemEditTextLocation.text=tuple.location
        binding.activityItemEditTextTime.text=tuple.time
        binding.activityItemEditTextPhone.text=tuple.phone
        if (tuple.description!=""){ binding.activityItemEditTextDescription.text=tuple.description }

         */
    }
}