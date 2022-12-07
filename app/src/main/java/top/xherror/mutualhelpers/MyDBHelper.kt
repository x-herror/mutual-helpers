package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import androidx.room.*
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

class MyDBHelper(val name:String, val version:Int):
    SQLiteOpenHelper(MyApplication.getContext(), name, null, version) {
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

object DateBase {
    val tag="DateBase"
    lateinit var myDBHelper:MyDBHelper
    lateinit var categoryList:ArrayList<Category>

    fun init(name:String, version:Int){
        // In file
        Database.connect("jdbc:sqlite:${MyApplication.getContext().filesDir}/data.db", "org.sqlite.JDBC")
        myDBHelper=MyDBHelper(name, version)
        val categorydb= TinyDB(MyApplication.getContext(),"categoryList")

        if (categorydb.getListString(DEFAULT_CATEGORY).isEmpty()){
            val array=ArrayList<String>()
            array.add(DEFAULT_ATTRIBUTES)
            categorydb.putListString(DEFAULT_CATEGORY,array)
        }

        val categoryMap=categorydb.all

        for ((k,v) in categoryMap){
            val list=categorydb.getListString(k)
            val category=Category(k,list)
            categoryList.add(category)
        }
        Log.d(tag,categoryList.toString())
    }
}

object ItemTable : IntIdTable() {
    val name= varchar("name", 50)
    val category= varchar("category", 50)
    val location= varchar("location", 50)
    val time= varchar("time", 50)
    val imagePath = varchar("imagePath", 255)
    val chooseOption= integer("chooseOption")
    val phone= varchar("phone",     50)
    val ownerAccount= varchar("ownerAccount", 50)
    val ownerName= varchar("ownerName", 50)
    val attributes=varchar("attributes",255)
}

const val CHOOSE_GALLERY=0
const val CHOOSE_CAMERA=1
class  DAOItem(id:EntityID<Int>):IntEntity(id){
    companion object  : IntEntityClass<DAOItem>(ItemTable)
    var name by ItemTable.name
    var category by ItemTable.category
    var location by ItemTable.location
    var time by ItemTable.time
    var imagePath by ItemTable.imagePath
    var chooseOption by ItemTable.chooseOption
    var phone by ItemTable.phone
    var ownerAccount by ItemTable.ownerAccount
    var ownerName by ItemTable.ownerName
    var attributes by ItemTable.attributes
}

@androidx.room.Database(entities = [EntityItem::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}

@androidx.room.Entity
data class EntityItem(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "location") var location:String,
    @ColumnInfo(name = "time") var time:String,
    @ColumnInfo(name = "imagePath") var imagePath:String,
    @ColumnInfo(name = "chooseOption") var chooseOption:Int,
    @ColumnInfo(name = "phone") var phone:String,
    @ColumnInfo(name = "ownerAccount") var ownerAccount:String,
    @ColumnInfo(name = "ownerName") var ownerName:String,
    @ColumnInfo(name = "attributes") var attributes:String,
)

@Dao
interface ItemDao {
    @Insert
    fun insertItems(vararg items: EntityItem)

    @Delete
    fun deleteItems(vararg items: EntityItem)

    @Update
    fun updateItems(vararg items: EntityItem)

    @Query("SELECT * FROM EntityItem WHERE category = :category")
    fun getSpecialCategory(category:String): List<EntityItem>

    @Query("SELECT * FROM EntityItem")
    fun getAll(): List<EntityItem>
}