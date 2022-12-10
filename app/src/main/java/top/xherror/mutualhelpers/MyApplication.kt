package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context=applicationContext
        val rememberdb=TinyDB(applicationContext,"rememberList")
        person= Person(rememberdb.getString("rememberAccount"),rememberdb.getListString(rememberdb.getString("rememberAccount")))
        DateBase.init(DATABASE_NAME,1)
        val remoteHelper=RemoteHelper()
        //remoteHelper.getItems()
    }


    companion object Utils{
        @SuppressLint("StaticFieldLeak")
        private lateinit var context:Context
        fun getContext():Context{
            return context
        }
    }
}
