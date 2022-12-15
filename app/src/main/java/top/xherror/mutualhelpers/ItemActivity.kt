package top.xherror.mutualhelpers

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import top.xherror.mutualhelpers.databinding.ActivityItemBinding

class ItemActivity : BaseActivity() {

    companion object {
        lateinit var showEntityItem: EntityItem
        fun actionStart(context: Context){
            context.startActivity(Intent(context,ItemActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBitmapUseGlide(showEntityItem,binding.activityItemImage,this)
        binding.activityItemEditTextName.text= showEntityItem.name
        binding.activityItemEditTextLocation.text=showEntityItem.location
        binding.activityItemEditTextTime.text=showEntityItem.time
        binding.activityItemEditTextCategory.text= showEntityItem.category
        val commentList=ArrayList<Comment>()
        val gson=Gson()
        val attrMapType= object:TypeToken<HashMap<String, String>>(){ }.type
        val attrMap:HashMap<String,String> = gson.fromJson(showEntityItem.attributes,attrMapType)
        attrMap.onEach {
            val keyView = TextView(this)
            keyView.text=it.key
            binding.activityItemAttributesLinearLayout.addView(keyView)
            val valueView = TextView(this)
            valueView.text=it.value
            binding.activityItemAttributesLinearLayout.addView(valueView)
        }
        val owner= Person(showEntityItem.ownerAccount,persondb.getListString(showEntityItem.ownerAccount))

        binding.activityItemPersonName.text=owner.name
        binding.activityItemPersonPhone.text=owner.phone
        setBitmapUseGlide(owner,binding.activityItemAvatar, this ,binding.activityItemAvatar.width,binding.activityItemAvatar.height)


        val commentsMapType= object:TypeToken<HashMap<String, ArrayList<String>>>(){ }.type
        val commentsMap:HashMap<String,ArrayList<String>> = gson.fromJson(showEntityItem.comments,commentsMapType)
        commentsMap.onEach {
            val commentPerson=Person(it.key, persondb.getListString(it.key))
            it.value.onEach {
                commentList.add(Comment(commentPerson,it))
            }
        }

        val adapter = CommentAdapter(commentList, this as BaseActivity)
        val layoutManager= LinearLayoutManager(this)
        layoutManager.canScrollVertically()
        binding.activityItemCommitRv.layoutManager=layoutManager
        binding.activityItemCommitRv.adapter = adapter
        binding.activityItemCommitRv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        binding.activityItemCommitButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater: LayoutInflater = this.layoutInflater
            val view = inflater.inflate(R.layout.dialog_add_comment, null)
            builder.setView(view)
            builder.setTitle("Commit")
            val commentInput = view.findViewById<EditText>(R.id.dialog_add_comment)
            builder.setPositiveButton("Commit") { _, _ ->
                val comment = commentInput.text.toString()
                if (comment.isNotBlank()){
                    if (commentsMap[person.account]==null){
                        val comments=ArrayList<String>()
                        comments.add(comment)
                        commentsMap[person.account] = comments

                    }else{
                        commentsMap[person.account]!!.add(comment)
                    }
                    commentList.add(Comment(person,comment))
                    adapter.notifyItemInserted(commentList.size)
                    val commentsJson=gson.toJson(commentsMap)
                    showEntityItem.comments=commentsJson
                    DateBase.updateItem(showEntityItem)
                }
            }
            builder.setNegativeButton("Cancel") { _, _ ->
            }
            builder.create().show()
        }
    }

    inner class CommentAdapter(private val commentsList:ArrayList<Comment>, val activity:BaseActivity) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val commentPersonName: TextView = view.findViewById(R.id.activity_item_comment_person_name)
            val commentAvatar: ImageView = view.findViewById(R.id.activity_item_comment_avatar)
            val comment: TextView = view.findViewById(R.id.activity_item_comment)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.comment, parent, false)
            val viewHolder = ViewHolder(view)
            /*
            viewHolder.categoryName.setOnClickListener {
                val category = DateBase.categoryList[viewHolder.adapterPosition]
            }

             */
            return viewHolder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val comment = commentsList[position]
            holder.commentPersonName.text=comment.person.name
            holder.comment.text=comment.comment
            setBitmapUseGlide(comment.person,holder.commentAvatar,activity,holder.commentAvatar.width,holder.commentAvatar.height)
        }

        override fun getItemCount() = commentsList.size
    }

    inner class Comment(val person:Person,val comment:String)
}