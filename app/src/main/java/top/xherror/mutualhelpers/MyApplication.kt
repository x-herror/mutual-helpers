package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import org.apache.commons.codec.binary.Hex
import java.util.ArrayList
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

lateinit var person:Person
lateinit var persondb:TinyDB
lateinit var settingdb:TinyDB
lateinit var rememberdb:TinyDB
lateinit var waitdb:TinyDB
val SALT="www"
val KEY="xherror"
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context=applicationContext
        dbInit()
        adminInit()
    }

    private fun dbInit(){
        rememberdb=TinyDB(applicationContext,"rememberList")
        person= User(rememberdb.getString("rememberAccount"),rememberdb.getListString(rememberdb.getString("rememberAccount")))
        settingdb= TinyDB(applicationContext,"settingList")
        waitdb=TinyDB(applicationContext  ,"waitList")
        persondb=TinyDB(applicationContext,"personList")
    }

    private fun adminInit(){
        val array= ArrayList<String>()
        val saltPassword = createSignature("123456",KEY)
        array.add(saltPassword)
        array.add(ADMINTYPE)
        array.add("xherror")
        array.add("18759628434")
        array.add("")
        array.add("-1")
        array.add("-1")
        if (persondb.getListString("admin00")!!.isEmpty())  persondb?.putListString("admin00",array)
    }

    companion object Utils{
        @SuppressLint("StaticFieldLeak")
        private lateinit var context:Context
        fun getContext():Context{
            return context
        }

        fun createSignature(rawSecret:String,key:String):String{
            val saltedSecret=rawSecret+ SALT
            val sha256Hmac= Mac.getInstance("HmacSHA256")
            val secretKey= SecretKeySpec(key.toByteArray(),"HmacSHA256")
            sha256Hmac.init(secretKey)
            return String(Hex.encodeHex(sha256Hmac.doFinal(saltedSecret.toByteArray())))
        }
    }
}
