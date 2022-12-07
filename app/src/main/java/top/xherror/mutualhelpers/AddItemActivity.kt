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
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import top.xherror.mutualhelpers.databinding.ActivityAddItemBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val RESULT_LOAD_IMAGE = 1
private const val RESULT_CAMERA_IMAGE = 2
class AddItemActivity : BaseActivity() {
    private val tag="AddItemActivity"
    var imgPath = ""
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        var bitmap: Bitmap?=null
        //0 for Gallery
        //1 for Camera
        var chooseOption=-1
        super.onCreate(savedInstanceState)
        val binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //权限申请
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0
            )
        }
        //相册事件回调
        val toGalleryActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    RESULT_OK -> {
                        Log.d("GalleryReturn", "return uri is  ${it.data?.data.toString()}")
                        it.data!!.data?.let { it1->
                            Glide.with(this)
                                .load(it1)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(binding.ivHelpImageFirst)
                            bitmap=getBitmapFromUri(it1)
                        }
                        chooseOption=0
                        binding.ivHelpImageFirstDelete.visibility = View.VISIBLE
                    }
                    else -> {
                        Log.d("GalleryReturn", "error with resultCode: ${it.resultCode.toString()}")
                    }
                }
            }

        //相机事件回调
        val toCameraActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    RESULT_OK -> {
                        Glide.with(this)
                            .load(imgPath)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(binding.ivHelpImageFirst)
                        binding.ivHelpImageFirstDelete.visibility = View.VISIBLE
                        chooseOption=1
                    }
                    else -> {
                        Log.d("CameraReturn", "error with resultCode: ${it.resultCode.toString()}")
                    }
                }
            }

        //添加照片
        binding.ivHelpImageFirst.setOnClickListener {
            val chooseTypeView =
                LayoutInflater.from(this).inflate(R.layout.dialog_choose_pic_type, null)
            val selectDialog =
                AlertDialog.Builder(this).setView(chooseTypeView).setCancelable(true).create()
            //Objects.requireNonNull(selectDialog.window)!!.setBackgroundDrawableResource(R.color.transparent)

            val cameraButton:TextView=chooseTypeView.findViewById(R.id.tv_choose_pic_camera)
            cameraButton.setOnClickListener {
                selectDialog.dismiss()
                toCameraActivity.launch(openCamera())
            }

            val galleryButton:TextView=chooseTypeView.findViewById(R.id.tv_choose_pic_gallery)
            galleryButton.setOnClickListener {
                selectDialog.dismiss()
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

        //选择框
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item)
        val editTextList=ArrayList<EditText>()
        val categoryList=DateBase.getCategoryNameList()
        var attributes =ArrayList<String>()
        var categoryName=""
        attributes.add(DEFAULT_ATTRIBUTES)
        adapter.addAll(categoryList)
        binding.activityAddItemSpinner.adapter = adapter
        binding.activityAddItemSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                categoryName=categoryList[pos]
                DateBase.getCategory(categoryName)?.attributes?.let { attributes = it }
                attributes.onEach {
                    binding.activityAddItemSpinnerLinearLayout.removeAllViews()
                    val widget=addTextView()
                    widget.text=it
                    binding.activityAddItemSpinnerLinearLayout.addView(widget)
                    val editText=addEditView()
                    binding.activityAddItemSpinnerLinearLayout.addView(editText)
                    editTextList.add(editText)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        //提交事件
        binding.activityAddItemButtonGo.setOnClickListener {
            val gson=Gson()
            val values=ArrayList<String>()
            editTextList.onEach { values.add(it.text.toString()) }
            val map:Map<String,String> =attributes.zip(values).toMap()
            val json=gson.toJson(map)
            val name=binding.activityAddItemEditTextName.text.toString()
            val location=binding.activityAddItemEditTextLocation.text.toString()
            val phone=binding.activityAddItemEditTextPhone.text.toString()


            if (name!="" && location!="" && phone!=""){
                Log.d(tag,"name:$name location:$location phone:$phone" )
                val simpleDateFormat=SimpleDateFormat("yyyy.MM.dd-HH:mm:ss")
                val saveDateFormat=SimpleDateFormat("yyyyMMDDHHmmss")
                val date=Date(System.currentTimeMillis())
                val ownerAccount= person.account
                val ownerName= person.name
                var imagePath=""
                val description=binding.activityAddItemEditTextDescription.text.toString()
                val time=simpleDateFormat.format(date)

                when (chooseOption){
                    0->{
                        bitmap?.let {
                            val saveTime=saveDateFormat.format(date)
                            val imageName=saveTime.toString()+name+location+kotlin.random.Random.nextInt().toString()
                            imagePath = saveBitmap(imageName,it,this)
                        }
                    }
                    1->{
                        imagePath=imgPath
                    }
                }

                val entityItem=EntityItem(name = name,
                    category = categoryName,
                    location= location,
                    time= time,
                    imagePath = imagePath,
                    chooseOption= chooseOption,
                    phone= phone,
                    ownerAccount= ownerAccount,
                    ownerName= ownerName,
                    attributes= json)

                DateBase.insertItems(entityItem)
                Toast.makeText(this,"成功提交！",Toast.LENGTH_SHORT).show()
                MainActivity.addEntityItem=entityItem
                val intent=Intent()
                /*
                intent.run {
                    putExtra("chooseOption",chooseOption)
                    putExtra("isGo",true)
                    putExtra("name",name)
                    putExtra("imagePath",imagePath)
                    putExtra("location",location)
                    putExtra("time",time)
                }

                 */
                setResult(RESULT_OK,intent)
                finish()
            }else{
                Toast.makeText(this,"请填写完整QWQ",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addTextView()=TextView(this)

    fun addEditView()=EditText(this)

    //通过Uri获取BitMap
    private fun getBitmapFromUri(uri: Uri)=contentResolver.openFileDescriptor(uri,"r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun saveBitmap(name: String, bm: Bitmap, mContext: Context):String {

        Log.d("SaveBitmap", "Ready to save bitmap")
        val targetPath = getFileDir("bitmaps")
        val saveFile = File(targetPath, name)
        try {
            val saveImgOut = FileOutputStream(saveFile)
            bm.compress(Bitmap.CompressFormat.JPEG, 80, saveImgOut)
            //存储完成后需要清除相关的进程
            saveImgOut.flush()
            saveImgOut.close()
            Log.d("SaveBitmap", "The bitmap is save to your phone!")
            Log.d("SaveBitmap", "Save Path=${saveFile.absolutePath}")
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return saveFile.absolutePath

    }

    private fun openCamera():Intent{
        Log.d("CreatePicture", "Ready to create picture")
        val targetPath = getFileDir("camera")
        val photoName = System.currentTimeMillis().toString() + ".png"
        val picture = File(targetPath, photoName)
        if (!picture.exists()) {
            try {
                picture.createNewFile()
                Log.d("CreatePicture", "The picture is save to your phone!")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        imgPath = picture.absolutePath
        Log.d("CreatePicture", "Save Path=${imgPath}")
        // 调用相机拍照
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camera.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(this, "top.xherror.mutualhelpers.fileprovider", picture)
        )
        return camera
    }

    private fun getFileDir(dir: String): String? {
        // internal save dir /data/data/top.xherror.mutualhelpers/files/$dir
        val path: String = filesDir.toString() + File.separator + dir
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return path
    }

}









