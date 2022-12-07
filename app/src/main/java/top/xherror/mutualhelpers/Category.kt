package top.xherror.mutualhelpers

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class Category(var name:String="",var attributes:ArrayList<String>) {
    val itemList=ItemList(name, attributes)

    fun getAttrString():String{
        var str=""
        attributes.onEachIndexed { index, s ->
            str=str.plus(s+"\n")
        }
        return str
    }
}

class ItemList(var name:String="",var attributes:ArrayList<String>){
    lateinit var list:ArrayList<DAOItem>
    init {
        transaction {
            val daoItems=DAOItem.find { ItemTable.category eq name }
            daoItems.forEach {
                list.add(it)
            }
        }
    }
}