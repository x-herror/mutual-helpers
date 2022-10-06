package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import top.xherror.mutualhelpers.databinding.ActivityMainBinding


@SuppressLint("StaticFieldLeak")
val dbHelper=MyDBHelper(MyApplication.getContext(),"Items.db",1)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //adapter.notifyItemInserted(chatList.size-1)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name
        setContentView(binding.root)
        val firstFragment=FirstFragment()
        binding.buttonToFirst.setOnClickListener {
            replaceFragment(firstFragment)
        }
        binding.buttonToSecond.setOnClickListener {
            replaceFragment(SecondFragment())
        }



        val toAddItemActivity= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ it ->
            when(it.resultCode){
                RESULT_OK -> {
                    it.data?.let {
                        val isGo=it.getBooleanExtra("isGo",false)
                        if (isGo){
                            //firstFragment.getAdapter().notifyItemInserted()
                        }
                    }

                }
            }
        }
        binding.fab.setOnClickListener {
            //Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
            toAddItemActivity.launch(Intent(this,AddItemActivity::class.java))
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }




}