package top.xherror.mutualhelpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
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
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


private const val RESULT_LOAD_IMAGE = 1
private const val RESULT_CAMERA_IMAGE = 2
class AddItemActivity : BaseActivity() {
    private val tag="AddItemActivity"
    var imgPath = ""
    lateinit var uri: Uri
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SimpleDateFormat")
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
                            uri = it1
                        }
                        chooseOption= CHOOSE_GALLERY
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
                        chooseOption= CHOOSE_CAMERA
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
                gallery.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*")
                toGalleryActivity.launch(gallery)
            }

            val cancelButton:TextView=chooseTypeView.findViewById(R.id.tv_choose_pic_cancel)
            cancelButton.setOnClickListener {
                selectDialog.dismiss()
            }
            selectDialog.show()
        }

        //选择框
        val adapter = ArrayAdapter<String>(this, R.layout.simple_dropdown_item)
        val editTextList=ArrayList<EditText>()
        val categoryList=DateBase.getCategoryNameList()
        var attributes =ArrayList<String>()
        var categoryName=""
        //attributes.add(DEFAULT_ATTRIBUTES)
        adapter.addAll(categoryList)
        binding.activityAddItemSpinner.adapter = adapter
        binding.activityAddItemSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                binding.activityAddItemSpinnerLinearLayout.removeAllViews()
                categoryName=categoryList[pos]
                DateBase.getCategory(categoryName)?.attributes?.let { attributes = it }
                attributes.onEach {
                    val widget=addTextView()
                    widget.text=it
                    widget.setTextColor(resources.getColor(R.color.pink,theme))
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
            //val phone=binding.activityAddItemEditTextPhone.text.toString()


            if (name!="" && location!="" ){
                Log.d(tag,"name:$name location:$location " )
                val simpleDateFormat=SimpleDateFormat("yyyy.MM.dd-HH:mm:ss")
                val saveDateFormat=SimpleDateFormat("yyyyMMDDHHmmss")
                val date=Date(System.currentTimeMillis())
                val ownerAccount= person.account
                val phone = person.phone
                var imagePath=""
                var imageName=""
                var file:File?=null
                val description=binding.activityAddItemEditTextDescription.text.toString()
                val time=simpleDateFormat.format(date)
                val options = BitmapFactory.Options()
                val comments=HashMap<String,ArrayList<String>>()
                val commentsJson=gson.toJson(comments)
                options.inJustDecodeBounds = true
                when (chooseOption){
                    CHOOSE_GALLERY->{
                        bitmap?.let {
                            imagePath=RealPathFromUriUtils.getRealPathFromUri(this,uri);
                            file = File(imagePath)
                            imageName =file!!.name
                            //val saveTime=saveDateFormat.format(date)
                            //val imageName=saveTime.toString()+name+location+kotlin.random.Random.nextInt().toString()
                            //imagePath = saveBitmap(imageName,it,this)

                            BitmapFactory.decodeFile(uri.path, options)
                        }
                    }
                    CHOOSE_CAMERA->{
                        imagePath=imgPath
                        file = File(imagePath)
                        imageName = file?.name.toString()
                        val bis= BufferedInputStream(FileInputStream(imagePath))
                        BitmapFactory.decodeStream(bis,null,options)
                    }
                }

                val entityItem=EntityItem(name = name,
                    category = categoryName,
                    location= location,
                    time= time,
                    imageName = imageName,
                    imageWidth = options.outWidth ,
                    imageHeight = options.outHeight,
                    phone= phone,
                    ownerAccount= ownerAccount,
                    attributes= json,
                    description = description,
                    comments = commentsJson )

                DateBase.insertItem(entityItem,file)
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
                val category=DateBase.getCategory(categoryName)
                category?.notifyItemAdd(entityItem)
                DateBase.notifyMyItemAdd(entityItem)
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
        val imageName = System.currentTimeMillis().toString() + ".png"
        val picture = File(targetPath, imageName)
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

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String {
        var result=""
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        return result
    }

    private fun getPath(context: Context, uri: Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

}









