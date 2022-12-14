package top.xherror.mutualhelpers

import android.app.Activity
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import top.xherror.mutualhelpers.ItemActivity.Companion.actionStart

class FirstAdapter(val itemList: ArrayList<EntityItem>,val activity:BaseActivity) : RecyclerView.Adapter<FirstAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemImage: ImageView = view.findViewById(R.id.itemImage)
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemLocation: TextView = view.findViewById(R.id.itemLocation)
        val itemTime: TextView = view.findViewById(R.id.itemTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val item = itemList[position]
            ItemActivity.showEntityItem=item
            ItemActivity.actionStart(parent.context)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        activity.setBitmapUseGlide(item,holder.itemImage, activity,holder.itemImage.width,holder.itemImage.height)
        holder.itemName.text = item.name
        holder.itemLocation.text=item.location
        holder.itemTime.text=item.time
    }

    override fun getItemCount() = itemList.size
}