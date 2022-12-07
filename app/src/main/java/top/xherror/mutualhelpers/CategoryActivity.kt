package top.xherror.mutualhelpers

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import top.xherror.mutualhelpers.databinding.ActivityCategoryBinding
import java.io.File

const val DEFAULT_CATEGORY="defaultCategory"
const val DEFAULT_ATTRIBUTES="defaultAttributes"
class AddCategoryActivity : AppCompatActivity() {
    val tag="AddCategoryActivity"
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
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(this)

            // Get the layout inflater
            val inflater: LayoutInflater = this.layoutInflater

            // Inflate the custom layout
            val view = inflater.inflate(R.layout.dialog_add_category, null)

            // Set the custom layout as the view for the dialog
            builder.setView(view)

            // Set the title of the dialog
            builder.setTitle("Enter Name and Attributes")
            // Get the input from the EditTexts

            val nameInput = view.findViewById<EditText>(R.id.dialog_add_category_name_input)
            val attributesInput = view.findViewById<EditText>(R.id.dialog_add_category_attributes_input)

            // Set the positive button to commit the input
            builder.setPositiveButton("Commit") { _, _ ->
                val name = nameInput.text.toString()
                val attributes = attributesInput.text.toString()
                val delimiter=","
                val array = ArrayList(attributes.split(delimiter))
                Log.d(tag,"name:${name},array:${array}")
                val category = Category(name, array)
                categoryList.add(category)
                adapter.notifyItemInserted(categoryList.size)
                categorydb.putListString(name,array)
            }

            // Set the negative button to cancel the dialog
            builder.setNegativeButton("Cancel") { _, _ ->
                // Send the cancel event back to the activity
            }

            builder.create().show()
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
        Log.d(tag,categoryList.toString())
    }

}