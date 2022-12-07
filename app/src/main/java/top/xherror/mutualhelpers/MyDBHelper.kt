package top.xherror.mutualhelpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(val context: Context, val name:String, val version:Int):
    SQLiteOpenHelper(context, name, null, version) {
    //begin with one
    private val createMyItems=
        "CREATE table MyItems(id integer primary key autoincrement,name string,imagePath string,location string,time string,phone string,owner string,description string,chooseOption integer)"

    override fun onCreate(db: SQLiteDatabase?) {
        settingdb.putInt("version",1)
        db?.execSQL(createMyItems)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}

class DateBase(){

}