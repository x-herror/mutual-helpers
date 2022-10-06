package top.xherror.mutualhelpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(val context: Context, val name:String, val version:Int):
    SQLiteOpenHelper(context, name, null, version) {
    //private val createItems=
    //    "CREATE table Items(id integer primary key autoincrement,author text,price real,pages integer,name text)"
    /*
    putExtra("name",name)
    putExtra("imageId",imageId)
    putExtra("location",location)
    putExtra("time",time)
     */
    private val createMyItems=
        "CREATE table MyItems(id integer primary key autoincrement,name string,imagePath string,location string,time string)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createMyItems)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //if (oldVersion<=1){db?.execSQL(createCategory)}
        //if (oldVersion<=2){db?.execSQL("ALTER TABLE Book ADD COLUMN category_id integer")}
    }
}