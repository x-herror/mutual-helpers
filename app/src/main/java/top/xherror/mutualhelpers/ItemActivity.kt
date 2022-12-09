package top.xherror.mutualhelpers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import top.xherror.mutualhelpers.databinding.ActivityItemBinding

class ItemActivity : BaseActivity() {

    companion object {
        lateinit var showEntityItem: EntityItem
        fun actionStart(context: Context){
            context.startActivity(Intent(context,ItemActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utils.setBitmapUseGlide(showEntityItem,binding.activityItemImage,this)
        binding.activityItemEditTextName.text= showEntityItem.name
        binding.activityItemEditTextLocation.text=showEntityItem.location
        binding.activityItemEditTextTime.text=showEntityItem.time
        binding.activityItemEditTextPhone.text=showEntityItem.phone
        binding.activityItemEditTextCategory.text= showEntityItem.category
        val gson=Gson()
        val mapType= object:TypeToken<Map<String, String>>(){ }.type
        val attrMap:Map<String,String> = gson.fromJson(showEntityItem.attributes,mapType)
        attrMap.onEach {
            val keyView = TextView(this)
            keyView.text=it.key
            binding.activityItemAttributesLinearLayout.addView(keyView)
            val valueView = TextView(this)
            valueView.text=it.value
            binding.activityItemAttributesLinearLayout.addView(valueView)
        }
    }
}