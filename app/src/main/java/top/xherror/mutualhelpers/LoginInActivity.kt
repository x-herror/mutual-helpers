package top.xherror.mutualhelpers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import top.xherror.mutualhelpers.databinding.ActivityLoginInBinding
import java.util.ArrayList

class LoginInActivity : BaseActivity() {
    private var tinydb:TinyDB?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        tinydb=TinyDB(applicationContext)
        val sp=getSharedPreferences("global", MODE_PRIVATE)
        //adminInit()
        var isCorrection=sp.getBoolean("rememberPassword",false)
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginInBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name
        setContentView(binding.root)
        binding.activityLoginInAccountEdit.setText(sp.getString("account",""))
        if (isCorrection){
            binding.activityLoginInPasswordEdit.setText(sp.getString(sp.getString("account",""),""))
        }
        binding.activityLoginInButtonLoginIn.setOnClickListener {
            if (!isCorrection){
                val inputAccount=binding.activityLoginInAccountEdit.text.toString()
                var arraylist=tinydb?.getListString(inputAccount)
                /*
                * arraylist[0]:password
                * arraylist[1]:type
                * arraylist[2]:name
                * */
                if (arraylist!![0]==""){
                    Toast.makeText(this,"Please register first", Toast.LENGTH_SHORT).show()
                } else{
                    val inputSecret=
                        CommonUtils.createSignature(binding.activityLoginInPasswordEdit.text.toString(), KEY)
                    if (arraylist[0]==inputSecret) {
                        if (binding.activityLoginInRememberCheckBox.isChecked) {
                            isCorrection = true
                            sp.save { putBoolean("rememberPassword",true) }
                        }

                        startActivity(Intent(this, MainActivity::class.java))
                    }else{
                        Toast.makeText(this,"Password miss",Toast.LENGTH_SHORT).show()
                    }
                }
            }else{startActivity(Intent(this, MainActivity::class.java))}
        }
    }

    fun adminInit(){
        val array=ArrayList<String>()
        val saltPassword = CommonUtils.createSignature("123456",KEY)
        array.add(saltPassword)
        array.add("u")
        array.add("xherror")
        tinydb?.putListString("admin00",array)
    }
}

