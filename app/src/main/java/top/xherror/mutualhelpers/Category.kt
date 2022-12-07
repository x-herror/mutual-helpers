package top.xherror.mutualhelpers

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

const val DEFAULT_CATEGORY="defaultCategory"
const val DEFAULT_ATTRIBUTES="defaultAttributes"
class Category(var name:String="",var attributes:ArrayList<String>) {
    val itemList=DateBase.getSpecialCategory(name)

    fun getAttrString():String{
        var str=""
        attributes.onEachIndexed { index, s ->
            str=str.plus(s+"\n")
        }
        return str
    }

}
