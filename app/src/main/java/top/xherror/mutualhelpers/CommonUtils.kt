package top.xherror.mutualhelpers

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.binary.Hex.encodeHexString
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
val SALT="www"
val KEY="xherror"
object CommonUtils {
    fun createSignature(rawSecret:String,key:String):String{
        val saltedSecret=rawSecret+ SALT
        val sha256Hmac=Mac.getInstance("HmacSHA256")
        val secretKey=SecretKeySpec(key.toByteArray(),"HmacSHA256")
        sha256Hmac.init(secretKey)
        return String(Hex.encodeHex(sha256Hmac.doFinal(saltedSecret.toByteArray())))
    }
}