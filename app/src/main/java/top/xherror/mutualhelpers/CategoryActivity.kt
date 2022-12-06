package top.xherror.mutualhelpers

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import top.xherror.mutualhelpers.databinding.ActivityCategoryBinding
import java.io.File

const val DEFAULT_CATEGORY="defaultCategory"
const val DEFAULT_ATTRIBUTES="defaultAttributes"
class AddCategoryActivity : AppCompatActivity() {
    lateinit var categorydb:TinyDB
    private val categoryList = ArrayList<Category>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initCategories()

        val layoutManager= LinearLayoutManager(this)
        val RV=binding.actvityAddCategoryRV
        RV.layoutManager=layoutManager
        val adapter=CategoryAdapter(categoryList)
        RV.adapter=adapter

        binding.actvityAddCategoryFab.setOnClickListener {
        }
    }


    inner class CategoryAdapter(val categoryList: ArrayList<Category>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val categoryName: TextView = view.findViewById(R.id.categoryName)
            val categoryImage: ImageView = view.findViewById(R.id.categoryImage)
            val categoryAttributes: TextView = view.findViewById(R.id.categoryAttributes)
            val delete :Button = view.findViewById(R.id.categoryDeleteButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category, parent, false)
            val viewHolder = ViewHolder(view)
            viewHolder.delete.setOnClickListener {
                val position=viewHolder.adapterPosition
                val category =categoryList[position]
                val selectDialog = AlertDialog.Builder(parent.context).run {
                    //TODO:assert itemList is empty
                    setTitle("DELETE!")
                    setMessage("确定删除?")
                    setCancelable(false)
                    setPositiveButton("删除!"){
                            dialog,which->
                        categoryList.remove(category)
                        super.notifyItemRemoved(position)
                        categorydb.remove(category.name)
                        val file= File(category.imagePath)
                        if (file.exists()){
                            file.delete()
                        }
                    }
                    setNegativeButton("算了."){
                            dialog,which->
                    }
                    show()
                }
            }
            return viewHolder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val category = categoryList[position]
            holder.categoryName.text=category.name
            if (File(category.imagePath).exists()) holder.categoryImage.setImageBitmap(Utils.getBitmap(category.imagePath,0))
            holder.categoryAttributes.text=category.getAttrString()
        }

        override fun getItemCount() = categoryList.size
    }


    private fun initCategories(){
        categorydb= TinyDB(applicationContext,"categoryList")
        if (categorydb.getListString(DEFAULT_CATEGORY).isEmpty()){
            val array=ArrayList<String>()
            array.add(DEFAULT_ATTRIBUTES)
            categorydb.putListString(DEFAULT_CATEGORY,array)
        }
        val categoryMap=categorydb.all
        for ((k,v) in categoryMap){
            val list=categorydb.getListString(k)
            val category=Category(k,list)
            categoryList.add(category)
        }
    }

}