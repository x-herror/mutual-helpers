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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import top.xherror.mutualhelpers.databinding.ActivityCategoryBinding
import java.io.File


class AddCategoryActivity : AppCompatActivity() {
    val tag="AddCategoryActivity"
    private val categoryList = DateBase.categoryList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val layoutManager= LinearLayoutManager(this)
        val RV=binding.actvityAddCategoryRV
        RV.layoutManager=layoutManager
        val adapter=CategoryAdapter(categoryList)
        RV.adapter=adapter
        binding.actvityAddCategoryFab.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater: LayoutInflater = this.layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_category, null)
            builder.setView(view)
            builder.setTitle("Enter Name and Attributes")
            val nameInput = view.findViewById<EditText>(R.id.dialog_add_category_name_input)
            val attributesInput = view.findViewById<EditText>(R.id.dialog_add_category_attributes_input)
            builder.setPositiveButton("Commit") { _, _ ->
                val name = nameInput.text.toString()
                val attributes = attributesInput.text.toString()
                val delimiter="-"
                val array = ArrayList(attributes.split(delimiter))
                Log.d(tag,"name:${name},array:${array}")
                val category = Category(name, array)
                categoryList.add(category)
                adapter.notifyItemInserted(categoryList.size)
                DateBase.categorydb.putListString(name,array)
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
                    setTitle("DELETE!")
                    setMessage("DELETE?")
                    setCancelable(false)
                    setPositiveButton("Commit!"){
                            dialog,which->
                        if(category.itemList.isEmpty()){
                            categoryList.remove(category)
                            super.notifyItemRemoved(position)
                            DateBase.categorydb.remove(category.name)
                        }else{
                            Toast.makeText(context,"category has items!",Toast.LENGTH_SHORT).show()
                        }
                    }
                    setNegativeButton("Cancel."){
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

}