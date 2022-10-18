package top.xherror.mutualhelpers
/*
author : xherror
home : https://github.com/gxherror
 */
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import top.xherror.mutualhelpers.databinding.ActivityMainBinding
import java.io.BufferedInputStream
import java.io.FileInputStream
import kotlin.math.log


@SuppressLint("StaticFieldLeak")
//全局数据库操作对象
val dbHelper=MyDBHelper(MyApplication.getContext(),"Items.db",1)
class MainActivity : AppCompatActivity() {
    private val tag="MainActivity"
    private val firstFragment=FirstFragment()
    private val secondFragment=SecondFragment()

    @JvmName("getFirstFragment1")
    fun getFirstFragment():FirstFragment{
        return firstFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        //MAIN界面
        binding.buttonToFirst.setOnClickListener {
            replaceFragment(firstFragment)
        }

        //MY界面
        binding.buttonToSecond.setOnClickListener {
            replaceFragment(secondFragment)
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
                                firstFragment.addItem(Item(id,name!!,bitmap,location!!,time!!))
                                secondFragment.addItem(Item(id,name!!,bitmap,location!!,time!!))
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
        replaceFragment(firstFragment)
    }

    //替换界面
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




}