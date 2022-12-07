package top.xherror.mutualhelpers
/*
author : xherror
home : https://github.com/gxherror
 */
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.room.Room
import org.jetbrains.exposed.sql.transactions.transaction
import top.xherror.mutualhelpers.databinding.ActivityMainBinding
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


//全局数据库操作对象
class MainActivity : BaseActivity() {
    private val tag="MainActivity"
    private lateinit var mainFragment:MainFragment
    private lateinit var firstFragment:FirstFragment
    private lateinit var secondFragment:SecondFragment
    private lateinit var settingFragment:SettingFragment
    @JvmName("getFirstFragment1")
    fun getFirstFragment():FirstFragment{
        return firstFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val date= Date(System.currentTimeMillis())
        val simpleDateFormat= SimpleDateFormat("yyyy.MM.dd-HH:mm:ss")
        val timeP=simpleDateFormat.format(date)


        /*
        val entityItem=EntityItem(name = "apple",
                category = DEFAULT_CATEGORY,
                location= "SJTU",
                time= timeP,
                imagePath = "",
                chooseOption= CHOOSE_GALLERY,
                phone= "18759628434",
                ownerAccount= "admin00",
                ownerName= "xherror",
                attributes= DEFAULT_ATTRIBUTES)

        DateBase.insertItems(entityItem)
        val itemList=DateBase.getAll()
        Log.d(tag,itemList.toString())
        */
        mainFragment=MainFragment()
        firstFragment=FirstFragment()
        secondFragment=SecondFragment()
        settingFragment=SettingFragment()
        setContentView(binding.root)
        //MAIN界面
        binding.buttonToFirst.setOnClickListener {
            replaceFragment(mainFragment)
        }

        //MY界面
        binding.buttonToSecond.setOnClickListener {
            replaceFragment(secondFragment)
        }

        binding.buttonToSetting.setOnClickListener {
            replaceFragment(settingFragment)
        }

        //添加物品按钮的回调事件
        val toAddItemActivity= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ it ->
            when(it.resultCode){
                RESULT_OK -> {
                    it.data?.let {
                        val isGo=it.getBooleanExtra("isGo",false)
                        val chooseOption=it.getIntExtra("chooseOption",-1)
                        val name=it.getStringExtra("name")
                        val imagePath=it.getStringExtra("imagePath")
                        val location=it.getStringExtra("location")
                        val time=it.getStringExtra("time")
                        var bitmap:Bitmap?=null
                        if (imagePath!=""){
                            bitmap=Utils.getBitmap(imagePath!!,chooseOption)
                        }
                        Log.d(tag,"return from add item activity")
                        Log.d(tag,"bitmap:${bitmap.toString()}")
                        if (isGo){
                            val id=Utils.getId(name!!,location!!,time!!)
                            if (id!=0){
                                //firstFragment.addItem(Item(id,name!!,bitmap,location!!,time!!))
                                //secondFragment.addItem(Item(id,name!!,bitmap,location!!,time!!))
                            }
                        }
                    }

                }
            }
        }

        //浮动添加物品按钮
        binding.fab.setOnClickListener {
            toAddItemActivity.launch(Intent(this,AddItemActivity::class.java))
        }

        //搜索界面
        binding.searchBar.setOnQueryTextListener(object:androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val thirdFragment=ThirdFragment()
                    thirdFragment.setSearchString(it)
                    replaceFragment(thirdFragment)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        //显示MAIN界面
        replaceFragment(mainFragment)
    }

    //替换界面
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




}