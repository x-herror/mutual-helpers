package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context=applicationContext

    }

    companion object Utils{
        @SuppressLint("StaticFieldLeak")
        private lateinit var context:Context
        fun getContext():Context{
            return context
        }
    }
}
