package top.xherror.mutualhelpers

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
import kotlin.math.log


@SuppressLint("StaticFieldLeak")
val dbHelper=MyDBHelper(MyApplication.getContext(),"Items.db",1)
class MainActivity : AppCompatActivity(), FirstFragment.TestDataCallback {
    val firstFragment=FirstFragment()


    override fun testData() {
        Toast.makeText(this, "CallBack", Toast.LENGTH_SHORT).show()
    }


    @JvmName("getFirstFragment1")
    fun getFirstFragment():FirstFragment{
        return firstFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //adapter.notifyItemInserted(chatList.size-1)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name
        setContentView(binding.root)

        binding.buttonToFirst.setOnClickListener {
            replaceFragment(firstFragment)
        }
        binding.buttonToSecond.setOnClickListener {
            replaceFragment(SecondFragment())
        }
        val fragment=supportFragmentManager.findFragmentById(R.id.fragmentFirst)
        val toAddItemActivity= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ it ->
            when(it.resultCode){
                RESULT_OK -> {
                    it.data?.let {
                        val isGo=it.getBooleanExtra("isGo",false)
                        val name=it.getStringExtra("name")
                        val imagePath=it.getStringExtra("imagePath")
                        val location=it.getStringExtra("location")
                        val time=it.getStringExtra("time")
                        val owner=it.getStringExtra("owner")
                        var bitmap:Bitmap?=null
                        if (imagePath!=""){
                            bitmap = BitmapFactory.decodeFile(imagePath)
                        }
                        if (isGo){
                            firstFragment.addItem(Item(name!!,bitmap,location!!,time!!))
                            //firstFragment.getAdapter().notifyItemInserted(itemList.size-1)
                        }
                    }

                }
            }
        }
        binding.fab.setOnClickListener {
            //Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            toAddItemActivity.launch(Intent(this,AddItemActivity::class.java))
        }

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

        replaceFragment(firstFragment)
    }

    fun test(){
        Log.d("TTT","TTTTTTTTTT")
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




}