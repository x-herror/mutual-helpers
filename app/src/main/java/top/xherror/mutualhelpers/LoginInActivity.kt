package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import top.xherror.mutualhelpers.databinding.ActivityLoginInBinding
import java.util.ArrayList


lateinit var remoteHelper:RemoteHelper
class LoginInActivity : BaseActivity() {
    val tag="loginIn"
    override fun onCreate(savedInstanceState: Bundle?) {
        var isRemember=rememberdb.getBoolean("remember")
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginInBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name
        setContentView(binding.root)
        binding.activityLoginInAccountEdit.setText(rememberdb.getString("rememberAccount"))
        if (isRemember){
            if (person.type== ADMINTYPE) person= Admin(person.account, person.toList())
            person.loginIn(this)
            //binding.activityLoginInPasswordEdit.setText(sp.getString("rememberPassword",""))
        }
        binding.activityLoginInButtonLoginIn.setOnClickListener {
            val inputAccount=binding.activityLoginInAccountEdit.text.toString()
            var arraylist=persondb.getListString(inputAccount)

            if (arraylist!!.isEmpty()){
                Toast.makeText(this,"Please register or check your account", Toast.LENGTH_SHORT).show()
            } else{
                person= User(inputAccount,arraylist)
                val inputSecret=
                    MyApplication.createSignature(binding.activityLoginInPasswordEdit.text.toString(), KEY)
                if (person.password==inputSecret) {
                    if (binding.activityLoginInAdminCheckBox.isChecked){
                        if (person.type== USERTYPE){
                            Toast.makeText(this, "you don't have the privilege", Toast.LENGTH_SHORT).show()
                        }else if(person.type== ADMINTYPE){
                            person= Admin(person.account, person.toList())
                        }
                    }

                    if (binding.activityLoginInRememberCheckBox.isChecked) {
                        isRemember = true
                        rememberdb.putBoolean("remember",true)
                        rememberdb.putListString(inputAccount,arraylist)
                    }else {
                        isRemember = false
                        rememberdb.remove("remember")
                        rememberdb.remove(inputAccount)
                    }
                    rememberdb.putString("rememberAccount",inputAccount)
                    person.loginIn(this)
                }
                else{
                    Toast.makeText(this,"Password miss",Toast.LENGTH_SHORT).show()
                }
            }

        }
        binding.activityLoginInButtonRegister.setOnClickListener {
            person.register(this)
        }
    }

    override fun onResume() {
        super.onResume()
    }

}

