package top.xherror.mutualhelpers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import top.xherror.mutualhelpers.databinding.ActivityAddItemBinding
import top.xherror.mutualhelpers.databinding.ActivityRegisterBinding
import java.util.ArrayList

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var passwordAvailable=false
        var accountAvailable=false
        var confirmAvailable=false
        var phoneAvailable=false
        val person = Person()
        val regexPassword=Regex("(?=.*[a-z])(?=.*[0-9])[\\w]{8,16}")
        val regexPhone=Regex("(?:(?:\\+|00)86)?1[3-9]\\d{9}")
        binding.activityRegisterAccountEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                var arraylist= persondb?.getListString(s.toString())
                if (arraylist!!.isEmpty()){
                    accountAvailable=true
                    binding.activityRegisterAccountHint.text="available"
                    person.account=s.toString()
                }else{
                    binding.activityRegisterAccountHint.text="account has been used"
                }
            }
        })

        binding.activityRegisterPasswordEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (regexPassword.matches(s.toString())){
                    passwordAvailable=true
                    person.password=s.toString()
                    binding.activityRegisterPasswordHint.text="password available"
                }else{
                    binding.activityRegisterPasswordHint.text="password must include letters and numbers,with 8~16 characters long"
                }
            }
        })

        binding.activityRegisterConfirmEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == person.password){
                    confirmAvailable=true
                    binding.activityRegisterConfirmHint.text="pass"
                }else{
                    binding.activityRegisterConfirmHint.text="input password differs"
                }
            }
        })

        binding.activityRegisterNameEdit.setText(person.name)

        binding.activityRegisterPhoneEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (regexPhone.matches(s.toString())){
                    phoneAvailable=true
                    person.phone=s.toString()
                    binding.activityRegisterPhoneHint.text="pass"
                }else{
                    binding.activityRegisterPhoneHint.text="input correct phone"
                }
            }
        })

        binding.activityRegisterButton.setOnClickListener {
            if (passwordAvailable&&accountAvailable&&confirmAvailable&&phoneAvailable){
                if (binding.activityRegisterAdminRegister.isChecked) person.type="A"
                val waitdb=TinyDB(applicationContext,"waitList")
                waitdb.putListString(person.account, person.toList())
                Toast.makeText(this, "register success wait for admin to verify", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "check your input", Toast.LENGTH_SHORT).show()
            }
        }
    }
}