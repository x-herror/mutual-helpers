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
            Log.d(tag,"account:${person.account.toString()},type:${person.type.toString()} login in")
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
            //binding.activityLoginInPasswordEdit.setText(sp.getString("rememberPassword",""))
        }
        binding.activityLoginInButtonLoginIn.setOnClickListener {
            val inputAccount=binding.activityLoginInAccountEdit.text.toString()
            var arraylist=persondb.getListString(inputAccount)
            /*
            * arraylist[0]:password
            * arraylist[1]:type  U A
            * arraylist[2]:name
            * arraylist[3]:phone
            * */
            if (arraylist!!.isEmpty()){
                //user or admin unregister
                Toast.makeText(this,"Please register or check your account", Toast.LENGTH_SHORT).show()
            } else{
                person= Person(inputAccount,arraylist)
                val inputSecret=
                    MyApplication.createSignature(binding.activityLoginInPasswordEdit.text.toString(), KEY)
                //success login in
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
                    }else{
                        isRemember = false
                        rememberdb.remove("remember")
                        rememberdb.remove(inputAccount)
                    }

                    rememberdb.putString("rememberAccount",inputAccount)
                    Log.d(tag,"account:${person.account.toString()},type:${person.type.toString()} login in")
                    startActivity(Intent(this, MainActivity::class.java))
                    this.finish()
                }
                //login in fail
                else{
                    Toast.makeText(this,"Password miss",Toast.LENGTH_SHORT).show()
                }
            }

        }
        binding.activityLoginInButtonRegister.setOnClickListener {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
    }

}

