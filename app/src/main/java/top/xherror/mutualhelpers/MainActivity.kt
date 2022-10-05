package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import top.xherror.mutualhelpers.databinding.ActivityMainBinding
import top.xherror.mutualhelpers.databinding.FragmentFirstBinding
@SuppressLint("StaticFieldLeak")
val dbHelper=MyDBHelper(MyApplication.getContext(),"Items.db",1)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name
        setContentView(binding.root)

        binding.buttonToFirst.setOnClickListener {
            replaceFragment(FirstFragment())
        }
        binding.buttonToSecond.setOnClickListener {
            replaceFragment(SecondFragment())
        }
        binding.fab.setOnClickListener {
            //Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()

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