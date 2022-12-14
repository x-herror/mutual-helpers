package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.os.Parcelable
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
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.io.File
import java.io.Serializable

const val DATABASE_NAME="items.db"
object DateBase {
    val tag="DateBase"
    lateinit var categorydb:TinyDB
    val categoryList=ArrayList<Category>()
    lateinit var myItemList:ArrayList<EntityItem>
    lateinit var db:AppDatabase
    lateinit var itemDao:ItemDao

    fun init(name:String, version:Int){

        db = Room.databaseBuilder(
            MyApplication.getContext(),
            AppDatabase::class.java, DATABASE_NAME
        ).allowMainThreadQueries().build()

        itemDao = db.itemDao()

        myItemList= ArrayList(itemDao.getMyItems(person.account))

        categorydb= TinyDB(MyApplication.getContext(),"categoryList")

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

    fun insertItem( item: EntityItem,file:File?){
        itemDao.insertItems(item)
        remoteHelper.addItem(item)
        file?.let { remoteHelper.addImage(file) }
    }

    //TODO:delete image
    fun deleteItems(vararg items: EntityItem){
        items.onEach {
            //val file=File(it.imagePath)
            //if (file.exists()){
            //    file.delete()
            //}
        }
        itemDao.deleteItems(*items)
    }

    fun getAll()=ArrayList<EntityItem>(itemDao.getAll())

    fun getSpecialCategory(categoryName:String)=ArrayList<EntityItem>(itemDao.getSpecialCategory(categoryName))

    fun getCategoryNameList():ArrayList<String>{
        val array=ArrayList<String>()
        categoryList.onEach {
            array.add(it.name)
        }
        return array
    }

    fun getCategory(name:String)=categoryList.find { it.name==name }

    fun getMyItemSearchResult(searchString:String):ArrayList<EntityItem>{
        //TODO:模糊搜索,ES,轻量NN
        val array=ArrayList<EntityItem>()
        for (index :Int in myItemList.lastIndex downTo 0) {
            if (searchString in myItemList[index].attributes||searchString in myItemList[index].name||searchString in myItemList[index].description) array.add(myItemList[index])
        }
        return  array
    }

    fun notifyMyItemAdd(addEntityItem:EntityItem){
        myItemList.add(addEntityItem)
    }

    fun notifyMyItemDelete(deleteEntityItem: EntityItem){
        myItemList.remove(deleteEntityItem)
    }

}

@androidx.room.Database(entities = [EntityItem::class], version = 1,exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}

const val CHOOSE_GALLERY=0
const val CHOOSE_CAMERA=1
@androidx.room.Entity
data class EntityItem (
    @PrimaryKey(autoGenerate = true) var id: Int=1,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "location") var location:String,
    @ColumnInfo(name = "time") var time:String,
    @ColumnInfo(name = "imagePath") var imageName:String,
    @ColumnInfo(name = "imageWidth") var imageWidth:Int,
    @ColumnInfo(name = "imageHeight") var imageHeight:Int,
    @ColumnInfo(name = "phone") var phone:String,
    @ColumnInfo(name = "ownerAccount") var ownerAccount:String,
    @ColumnInfo(name = "attributes") var attributes:String,
    @ColumnInfo(name = "description") var description:String,
)

@androidx.room.Entity
data class TestItem (
    @PrimaryKey(autoGenerate = true) var id: Int=-1,
    @ColumnInfo(name = "name") var name: String
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

    @Query("SELECT * FROM EntityItem WHERE ownerAccount = :ownerAccount")
    fun getMyItems(ownerAccount: String): List<EntityItem>
}