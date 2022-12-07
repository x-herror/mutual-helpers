package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import top.xherror.mutualhelpers.ItemActivity.Companion.actionStart
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val adapter=SecondAdapter(DateBase.myItemList)

    fun addItem(item:EntityItem){
        DateBase.myItemList.add(item)
        adapter.notifyItemInserted(DateBase.myItemList.size-1)
        DateBase.insertItems(item)
    }


    fun removeItem(item:EntityItem){
        DateBase.myItemList.remove(item)
        adapter.notifyItemRemoved(DateBase.myItemList.size-1)
        DateBase.deleteItems(item)
    }

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //itemList.add(Item("herror",R.drawable.example,"SJTU","2022/10/5"))
        val view=inflater.inflate(R.layout.fragment_second, container, false)
        val fragmentFirstRecyclerView: RecyclerView =view.findViewById(R.id.fragmentSecondRecyclerView)
        val layoutManager= LinearLayoutManager(requireActivity())
        fragmentFirstRecyclerView.layoutManager=layoutManager
        fragmentFirstRecyclerView.adapter=adapter
        return  view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    inner class SecondAdapter(val itemList: ArrayList<EntityItem>) : RecyclerView.Adapter<SecondAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val itemImage: ImageView = view.findViewById(R.id.itemImage)
            val itemName: TextView = view.findViewById(R.id.itemName)
            val itemLocation: TextView = view.findViewById(R.id.itemLocation)
            val itemTime: TextView = view.findViewById(R.id.itemTime)
            val myItemDeleteButton: Button =view.findViewById(R.id.myItemDeleteButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.myitem, parent, false)
            val viewHolder = ViewHolder(view)
            viewHolder.itemView.setOnClickListener {
                val position = viewHolder.adapterPosition
                val item = itemList[position]
                actionStart(parent.context,item.id)
            }
            viewHolder.myItemDeleteButton.setOnClickListener {
                val position = viewHolder.adapterPosition
                val item = itemList[position]
                val selectDialog = AlertDialog.Builder(parent.context).run {
                    setTitle("DELETE!")
                    setMessage("确定删除?")
                    setCancelable(false)
                    setPositiveButton("删除!"){
                            dialog,which->
                        removeItem(item)
                        val activity=activity as MainActivity
                        activity.getFirstFragment().removeItem(item)
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
            val item = itemList[position]
            if (item.imagePath.isNotEmpty()) holder.itemImage.setImageBitmap(Utils.getBitmap(item.imagePath,item.chooseOption))
            holder.itemName.text = item.name
            holder.itemLocation.text=item.location
            holder.itemTime.text=item.time
        }

        override fun getItemCount() = itemList.size
    }


}