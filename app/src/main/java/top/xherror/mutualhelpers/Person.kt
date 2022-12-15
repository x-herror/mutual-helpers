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
    var avatarName=""
    var avatarWidth=-1
    var avatarHeight=-1
    constructor(accountP: String, arrayList: ArrayList<String>):this(){
        account=accountP
        if (arrayList.isNotEmpty()){
            /*
            * arraylist[0]:password
            * arraylist[1]:type  U A
            * arraylist[2]:name
            * arraylist[3]:phone
            * [4]:avatarName
            * [5]:avatarWidth
            * [6]"avatarHeight
            * */
            password=arrayList[0]
            type=arrayList[1]
            name=arrayList[2]
            phone=arrayList[3]
            avatarName=arrayList[4]
            avatarWidth=arrayList[5].toInt()
            avatarHeight=arrayList[6].toInt()

        }

    }

    fun toList():ArrayList<String>{
        val array= java.util.ArrayList<String>()
        array.add(password)
        array.add(type)
        array.add(name)
        array.add(phone)
        array.add(avatarName)
        array.add(avatarWidth.toString())
        array.add(avatarHeight.toString())
        return array
    }

    fun reset(){
        var account=""
        /*store HmacSHA256 password*/
        var password=""
        var type= USERTYPE
        var name="defaultName"
        var phone=""
        var avatarName=""
        var avatarWidth=-1
        var avatarHeight=-1
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