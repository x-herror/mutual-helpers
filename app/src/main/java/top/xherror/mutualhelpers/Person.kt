package top.xherror.mutualhelpers

const val USERTYPE="U"
const val ADMINTYPE="A"
open class Person() {
    var account=""
    /*store HmacSHA256 password*/
    var password=""
    var type= USERTYPE
    var name="defaultName"
    var phone=""
    constructor(accountP: String, arrayList: ArrayList<String>):this(){
        account=accountP
        password=arrayList[0]
        type=arrayList[1]
        name=arrayList[2]
        phone=arrayList[3]
    }

    fun toList():ArrayList<String>{
        val array= java.util.ArrayList<String>()
        array.add(password)
        array.add(type)
        array.add(name)
        array.add(phone)
        return array
    }

}

class Admin:Person{
    constructor():super(){
    }
    constructor(accountP: String, arrayList: ArrayList<String>):super(accountP,arrayList){
    }
}

class User:Person(){

}