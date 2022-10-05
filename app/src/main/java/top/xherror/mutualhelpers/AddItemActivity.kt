package top.xherror.mutualhelpers

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import top.xherror.mutualhelpers.databinding.ActivityAddItemBinding
import java.awt.PageAttributes.MediaType
import java.io.File

private const val RESULT_LOAD_IMAGE = 1
private const val RESULT_CAMERA_IMAGE = 2
class AddItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddItemBinding.inflate(layoutInflater) //FirstLayoutBinding bind to name
        setContentView(binding.root)
        binding.activityDBInsert.setOnClickListener {
            val db=dbHelper.writableDatabase.run {
                beginTransaction()
                try {
                    delete("Book",null,null)
                    insert("Book",null, ContentValues().apply {
                        put("name","QQQ")
                        put("author","xherror")
                        put("pages","122")
                        put("price",16.55) })
                    execSQL("INSERT INTO Book(name,author,pages,price) VALUES(?,?,?,?)", arrayOf("QWQ","xherror","654","12.45"))
                }catch (e:java.lang.Exception){
                    e.printStackTrace()
                }finally {
                    endTransaction()
                }

            }
            //val cursor=db.rawQuery("SELECT * FROM Book ORDER BY price",null)
            //Toast.makeText(this,cursor.columnNames.toString(),Toast.LENGTH_SHORT).show()
        }

    }
    private fun showPopueWindow() {
        val popView = View.inflate(this, R.layout.popupwindow_camera_need, null)
        val bt_album = popView.findViewById<View>(R.id.btn_pop_album) as Button
        val bt_camera = popView.findViewById<View>(R.id.btn_pop_camera) as Button
        val bt_cancle = popView.findViewById<View>(R.id.btn_pop_cancel) as Button
        //获取屏幕宽高
        val weight = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels * 1 / 3
        val popupWindow = PopupWindow(popView, weight, height)
        popupWindow.animationStyle = R.style.Theme_MutualHelpers
        popupWindow.isFocusable = true
        //点击外部popueWindow消失
        popupWindow.isOutsideTouchable = true
        bt_album.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, RESULT_LOAD_IMAGE)
            popupWindow.dismiss()
        }
        bt_camera.setOnClickListener {
            takeCamera(RESULT_CAMERA_IMAGE)
            popupWindow.dismiss()
        }
        bt_cancle.setOnClickListener { popupWindow.dismiss() }
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener {
            val lp = window.attributes
            lp.alpha = 1.0f
            window.attributes = lp
        }
        //popupWindow出现屏幕变为半透明
        val lp = window.attributes
        lp.alpha = 0.5f
        window.attributes = lp
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 50)
    }

    private fun takeCamera(num: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile)
                )
            }
        }
        startActivityForResult(takePictureIntent, num) //跳转界面传回拍照所得数据
    }

    private fun createImageFile(): File? {
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        var image: File? = null
        try {
            image = File.createTempFile(
                generateFileName(),  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mCurrentPhotoPath = image.getAbsolutePath()
        return image
    }

    fun generateFileName(): String? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        return "JPEG_" + timeStamp + "_"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE && null != data) {
                val selectedImage: Uri? = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? = contentResolver.query(
                    selectedImage,
                    filePathColumn, null, null, null
                )
                cursor.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                val picturePath: String = cursor.getString(columnIndex)
                upload(picturePath)
                cursor.close()
            } else if (requestCode == RESULT_CAMERA_IMAGE) {
                val target: SimpleTarget = object : SimpleTarget<Bitmap?>() {
                    fun onResourceReady(
                        resource: Bitmap,
                        glideAnimation: GlideAnimation<in Bitmap?>?
                    ) {
                        upload(saveMyBitmap(resource).getAbsolutePath())
                    }

                    fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                    }

                    fun onLoadFailed(e: Exception?, errorDrawable: Drawable?) {
                        super.onLoadFailed(e, errorDrawable)
                    }
                }
                Glide.with(this@RegisterUIActivity).load(mCurrentPhotoPath)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(1080, 1920) //图片压缩
                    .centerCrop()
                    .dontAnimate()
                    .into(target)
            }
        }
    }


    //将bitmap转化为png格式
    fun saveMyBitmap(mBitmap: Bitmap): File? {
        val storageDir: File =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        var file: File? = null
        try {
            file = File.createTempFile(
                UploadAccess.generateFileName(),  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
            val out = FileOutputStream(file)
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    private fun upload(picturePath: String) {
        val pb = ProgressDialog(this)
        pb.setMessage("正在上传")
        pb.setCancelable(false)
        pb.show()
        imageUpLoad(picturePath, object : Response<FileUpload?>() {
            fun onSuccess(response: FileUpload) {
                super.onSuccess(response)
                if (response.success) {
                    myFileId = response.fileID
                    runOnUiThread {
                        ToastUtils.showShortToast("上传成功")
                        pb.dismiss()
                    }
                }
            }

            fun onFaile(e: String?) {
                super.onFaile(e)
                runOnUiThread {
                    pb.dismiss()
                    ToastUtils.showShortToast("上传失败")
                }
            }
        })
    }

    fun imageUpLoad(localPath: String?, callBack: Response<FileUpload?>) {
        val MEDIA_TYPE_PNG: MediaType = MediaType.parse("image/png")
        val client = OkHttpClient()
        val builder: MultipartBody.Builder = Builder().setType(MultipartBody.FORM)
        val f = File(localPath)
        builder.addFormDataPart("file", f.getName(), RequestBody.create(MEDIA_TYPE_PNG, f))
        val requestBody: MultipartBody = builder.build()
        //构建请求
        val request: Request = Builder()
            .url("http://  ") //地址
            .post(requestBody) //添加请求体
            .build()
        client.newCall(request).enqueue(object : Callback() {
            fun onFailure(call: Call?, e: IOException) {
                callBack.onFaile(e.getMessage())
                System.out.println("上传失败:e.getLocalizedMessage() = " + e.getLocalizedMessage())
            }

            @Throws(IOException::class)
            fun onResponse(call: Call?, response: Response) {
                val resultBean: FileUpload =
                    Gson().fromJson(response.body().string(), FileUpload::class.java)
                callBack.onSuccess(resultBean)
            }
        })
    }

}


