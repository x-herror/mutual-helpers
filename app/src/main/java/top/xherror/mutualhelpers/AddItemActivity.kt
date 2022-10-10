package top.xherror.mutualhelpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import top.xherror.mutualhelpers.databinding.ActivityAddItemBinding
import top.xherror.mutualhelpers.databinding.DialogChoosePicTypeBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


private const val RESULT_LOAD_IMAGE = 1
private const val RESULT_CAMERA_IMAGE = 2
class AddItemActivity : AppCompatActivity() {
    var imgPath = ""
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        var bitmap: Bitmap?=null
        super.onCreate(savedInstanceState)
        val binding =
            ActivityAddItemBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name
       // val selectPictureBinding = ActivitySelectPictureBinding.inflate(layoutInflater)
        val dialogChoosePicTypeBinding = DialogChoosePicTypeBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name
        setContentView(binding.root)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0
            )
        }

        val toGalleryActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    RESULT_OK -> {
                        // 拉起相机回调data为null，打开相册回调不为null
                        if (it.data == null && imgPath.isNotEmpty()) {
                            Glide.with(this)
                                .load(imgPath)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(binding.ivHelpImageFirst)
                            //binding.ivHelpImageFirstDelete.visibility = View.VISIBLE
                            //binding.ivHelpImageSecond.visibility = View.VISIBLE
                        }
                        else if (it.data != null) {
                            //Log.d("data_return", it.data.toString())
                            //Log.d("data_return", it.data!!.toUri(0))
                            //Log.d("data_return", it.data!!.data.toString())
                            //TODO:it.data!!.data
                            it.data!!.data?.let { it1->
                                Glide.with(this)
                                    .load(it1)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(binding.ivHelpImageFirst)
                                bitmap=getBitmapFromUri(it1)
                                //Log.d("TTT",bitmap.toString())
                            }
                            binding.ivHelpImageFirstDelete.visibility = View.VISIBLE
                            //binding.ivHelpImageSecond.visibility = View.VISIBLE
                        }
                    }
                    else -> {
                        Log.d("data_return", it.resultCode.toString())
                    }
                }
            }


        val toCameraActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    RESULT_OK -> {
                        // 拉起相机回调data为null，打开相册回调不为null
                            //val bitmap=BitmapFactory.decodeStream()
                            Glide.with(this)
                                .load(imgPath)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(binding.ivHelpImageFirst)
                            binding.ivHelpImageFirstDelete.visibility = View.VISIBLE
                            //binding.ivHelpImageSecond.visibility = View.VISIBLE
                    }
                    else -> {
                        Log.d("data_return", it.resultCode.toString())
                    }
                }
            }

        binding.ivHelpImageFirst.setOnClickListener {
            val chooseTypeView =
                LayoutInflater.from(this).inflate(R.layout.dialog_choose_pic_type, null)
            val selectDialog =
                AlertDialog.Builder(this).setView(chooseTypeView).setCancelable(true).create()
            //Objects.requireNonNull(selectDialog.window)!!.setBackgroundDrawableResource(R.color.transparent)



            val cameraButton:TextView=chooseTypeView.findViewById(R.id.tv_choose_pic_camera)
            cameraButton.setOnClickListener {
                selectDialog.dismiss()
                // 拉起相机
                toCameraActivity.launch(openCamera())
            }


            val galleryButton:TextView=chooseTypeView.findViewById(R.id.tv_choose_pic_gallery)
            galleryButton.setOnClickListener {
                selectDialog.dismiss()
                // 打开相册
                val gallery = Intent(Intent.ACTION_PICK)
                gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                toGalleryActivity.launch(gallery)
            }

            val cancelButton:TextView=chooseTypeView.findViewById(R.id.tv_choose_pic_cancel)
            cancelButton.setOnClickListener {
                selectDialog.dismiss()
            }
            selectDialog.show()
        }

        binding.activityAddItemButtonGo.setOnClickListener {
            val simpleDateFormat=SimpleDateFormat("yyyy.MM.dd-HH:mm:ss")
            val saveDateFormat=SimpleDateFormat("yyyyMMDDHHmmss")
            val date=Date(System.currentTimeMillis())
            val name=binding.activityAddItemEditTextName.text.toString()
            val owner="xherror"
            val location=binding.activityAddItemEditTextLocation.text.toString()
            var imagePath=""
            if (name!=""&&location!=""){
                val time=simpleDateFormat.format(date)
                bitmap?.let {
                    val saveTime=saveDateFormat.format(date)
                    val imageName=saveTime.toString()+name+location+kotlin.random.Random.nextInt().toString()
                    saveBitmap(imageName,it,this)
                    imagePath = this.filesDir.toString() + "/images/"+imageName
                    Log.d("Save Bitmap", "Save Path=$imagePath")
                }
                dbHelper.writableDatabase.run {
                    execSQL("INSERT INTO MyItems(name,imagePath,location,time,owner) VALUES(?,?,?,?,?)", arrayOf(name,imagePath,location,time,owner))
                }
                Toast.makeText(this,"成功提交！",Toast.LENGTH_SHORT).show()
                val intent=Intent()
                intent.run {
                    putExtra("isGo",true)
                    putExtra("name",name)
                    putExtra("imagePath",imagePath)
                    putExtra("location",location)
                    putExtra("time",time)
                    putExtra("owner",owner)
                }
                setResult(RESULT_OK,intent)
                finish()
            }else{
                Toast.makeText(this,"提交失败",Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode){
            0->{
                if (grantResults.isEmpty()&&grantResults[0]== PackageManager.PERMISSION_GRANTED){

                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    //通过Uri获取BitMap
    private fun getBitmapFromUri(uri: Uri)=contentResolver.openFileDescriptor(uri,"r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    fun saveBitmap(name: String, bm: Bitmap, mContext: Context) {

        Log.d("Save Bitmap", "Ready to save picture")
        //指定我们想要存储文件的地址
        val targetPath = mContext.filesDir.toString() + "/images/"
        Log.d("Save Bitmap", "Save Path=$targetPath")
        //
        val file = File(targetPath)
        if (file.exists()) {} else {
            file.mkdirs()
        }
        //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
        val saveFile = File(targetPath, name)
        try {
            val saveImgOut = FileOutputStream(saveFile)
            // compress - 压缩的意思
            bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut)
            //存储完成后需要清除相关的进程
            saveImgOut.flush()
            saveImgOut.close()
            Log.d("Save Bitmap", "The picture is save to your phone!")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

    }

    private fun fileIsExist(fileName: String?): Boolean {
        //传入指定的路径，然后判断路径是否存在
        val file = File(fileName)
        return if (file.exists()) true else {
            //file.mkdirs() 创建文件夹的意思
            file.mkdirs()
        }
    }


    private fun openCamera():Intent{
        // 创建照片存储目录
        val targetPath = "$filesDir/camera/"
        val imgDir: File = File(targetPath)
        if (imgDir.exists()) {} else {
            imgDir.mkdirs()
        }
        // 创建照片
        val photoName = System.currentTimeMillis().toString() + ".png"
        val picture = File(imgDir, photoName)
        if (!picture.exists()) {
            try {
                picture.createNewFile()
            } catch (e: IOException) {
                Log.d("TTT","create images error")
            }
        }
        imgPath = picture.absolutePath
        // 调用相机拍照
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camera.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(this, "top.xherror.mutualhelpers.fileprovider", picture)
        )
        return camera
    }

    fun getFilePath(dir: String): String? {
        val path: String
        // 判断是否有外部存储，是否可用
        path = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            getExternalFilesDir(dir)!!.absolutePath
        } else {
            // 使用内部储存
            filesDir.toString() + File.separator + dir
        }
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return path
    }

    /*
    private fun choosePictureDialog() {
    val chooseTypeView = LayoutInflater.from(this).inflate(R.layout.dialog_choose_pic_type, null)
    val selectDialog =
        AlertDialog.Builder(this).setView(chooseTypeView).setCancelable(false).create()
    selectDialog.show()
    Objects.requireNonNull(selectDialog.window)?.setBackgroundDrawableResource(R.color.transparent)
    val dialogChoosePicTypeBinding = DialogChoosePicTypeBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name

    dialogChoosePicTypeBinding.tvChoosePicCamera. setOnClickListener {
        selectDialog.dismiss()
        // 拉起相机
        //openCamera(type)
    }


    dialogChoosePicTypeBinding.tvChoosePicGallery.setOnClickListener {
        selectDialog.dismiss()
        // 打开相册
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        toGalleryActivity.launch(gallery)
    }

    dialogChoosePicTypeBinding.tvChoosePicCancel.setOnClickListener {
        selectDialog.dismiss()
    }

    }
    */

     */

}









