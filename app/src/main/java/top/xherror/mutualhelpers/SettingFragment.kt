package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    lateinit var uri: Uri
    var bitmap: Bitmap?=null
    //0 for Gallery
    //1 for Camera
    var chooseOption=-1
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var imgPath = ""

    private val waitpersonList = ArrayList<Person>()

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        initPersons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_setting, container, false)
        val RV: RecyclerView =view.findViewById(R.id.fragmentSettingRecyclerView)
        val CT: Button = view.findViewById(R.id.fragmentSettingCategory)
        val avatar :ImageView=view.findViewById(R.id.fragment_setting_avatar)

        //相册事件回调
        val toGalleryActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    AppCompatActivity.RESULT_OK -> {
                        Log.d("GalleryReturn", "return uri is  ${it.data?.data.toString()}")
                        it.data!!.data?.let { it1->
                            Glide.with(this)
                                .load(it1)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(avatar)
                            bitmap=getBitmapFromUri(it1)
                            uri = it1
                        }
                        chooseOption= CHOOSE_GALLERY
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
                    AppCompatActivity.RESULT_OK -> {
                        Glide.with(this)
                            .load(imgPath)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(avatar)
                        chooseOption= CHOOSE_CAMERA
                    }
                    else -> {
                        Log.d("CameraReturn", "error with resultCode: ${it.resultCode.toString()}")
                    }
                }
            }

        avatar.setOnClickListener {
            val chooseTypeView =
                LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_choose_pic_type, null)
            val selectDialog =
                androidx.appcompat.app.AlertDialog.Builder(requireActivity()).setView(chooseTypeView).setCancelable(true).create()
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
        if (person is Admin){
            val layoutManager= LinearLayoutManager(requireActivity())
            RV.layoutManager=layoutManager
            val adapter=PersonAdapter(waitpersonList)
            RV.adapter=adapter
        }else{
            RV.visibility=View.GONE
            CT.visibility=View.GONE
        }
        val loginOut:Button=view.findViewById(R.id.fragmentSettingLoginOut)
        loginOut.setOnClickListener {
            person.reset()
            rememberdb.clear()
            activity?.finish()
        }
        CT.setOnClickListener {
            startActivity(Intent(activity,AddCategoryActivity::class.java))
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
            FileProvider.getUriForFile(requireActivity(), "top.xherror.mutualhelpers.fileprovider", picture)
        )
        return camera
    }

    private fun getBitmapFromUri(uri: Uri)=requireActivity().contentResolver.openFileDescriptor(uri,"r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    private fun getFileDir(dir: String): String? {
        // internal save dir /data/data/top.xherror.mutualhelpers/files/$dir
        val path: String = requireActivity().filesDir.toString() + File.separator + dir
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return path
    }

    inner class PersonAdapter(val waitpersonList: ArrayList<Person>) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val waitpersonAccount: TextView = view.findViewById(R.id.waitpersonAccount)
            val waitpersonType: TextView = view.findViewById(R.id.waitpersonType)
            val waitpersonName: TextView = view.findViewById(R.id.waitpersonName)
            val waitpersonPhone: TextView = view.findViewById(R.id.waitpersonPhone)
            val waitpersonAccept:Button = view.findViewById(R.id.waitpersonAccept)
            val waitpersonReject:Button = view.findViewById(R.id.waitpersonReject)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.waitperson, parent, false)
            val viewHolder = ViewHolder(view)
            viewHolder.waitpersonAccept.setOnClickListener {
                val position = viewHolder.adapterPosition
                val person = waitpersonList[position]
                persondb?.putListString(person.account,person.toList())
                waitpersonList.remove(person)
                waitdb.remove(person.account)
                this.notifyItemRemoved(position)
            }
            viewHolder.waitpersonReject.setOnClickListener {
                val position = viewHolder.adapterPosition
                val person = waitpersonList[position]
                waitpersonList.remove(person)
                waitdb.remove(person.account)
                //TODO:rejectdb
                this.notifyItemRemoved(position)
            }
            return viewHolder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val person = waitpersonList[position]
            holder.waitpersonAccount.text=person.account
            holder.waitpersonType.text = person.type
            holder.waitpersonName.text= person.name
            holder.waitpersonPhone.text= person.phone
        }

        override fun getItemCount() = waitpersonList.size
    }

    private fun initPersons(){

        val waitMap=waitdb.all
        for ((k,v) in waitMap){
            val list=waitdb.getListString(k)
                val person=Person(k,list)
                waitpersonList.add(person)
        }
    }
}