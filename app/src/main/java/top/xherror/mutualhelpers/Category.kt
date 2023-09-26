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
        val useArray = MutableList<Boolean>(itemList.size) { false }
        //匹配每个字符串:显示屏->显,示,屏,显示,示屏,显示屏
        for (i :Int in searchString.length downTo 1){
            for (j :Int in 0 until  (searchString.length-i+1)){
                val subSearchString=searchString.substring(j,j+i)
                for (index :Int in itemList.lastIndex downTo 0) {
                    if ((subSearchString in itemList[index].attributes||subSearchString in itemList[index].name||subSearchString in itemList[index].description) && !useArray[index]){
                        array.add(itemList[index])
                        useArray[index]=true
                    }
                }

            }
        }

        return  array
    }

    fun getSearch(){

    }



    fun notifyItemAdd(addEntityItem:EntityItem){
        itemList.add(addEntityItem)
    }

    fun notifyItemDelete(deleteEntityItem: EntityItem){
        itemList.remove(deleteEntityItem)
    }


}
