package top.xherror.mutualhelpers

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction


const val DEFAULT_CATEGORY="默认分组"
const val DEFAULT_ATTRIBUTES="默认属性"
const val MY_CATEGORY="myCategory"
class Category(var name:String="",var attributes:ArrayList<String>) {
    val itemList=DateBase.getSpecialCategory(name)

    fun getAttrString():String{
        var str=""
        attributes.onEachIndexed { index, s ->
            str=str.plus(s+"\n")
        }
        return str
    }

    fun getSearchResult(searchString:String):ArrayList<EntityItem>{
        //TODO:模糊搜索,ES,轻量NN
        val array=ArrayList<EntityItem>()
        for (index :Int in itemList.lastIndex downTo 0) {
            if (searchString in itemList[index].attributes||searchString in itemList[index].name||searchString in itemList[index].description) array.add(itemList[index])
        }
        return  array
    }

    fun notifyItemAdd(addEntityItem:EntityItem){
        itemList.add(addEntityItem)
    }

    fun notifyItemDelete(deleteEntityItem: EntityItem){
        itemList.remove(deleteEntityItem)
    }


}
