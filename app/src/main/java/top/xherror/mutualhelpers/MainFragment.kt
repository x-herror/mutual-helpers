package top.xherror.mutualhelpers

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val rvView: RecyclerView =view.findViewById(R.id.fragment_main_RV)
        val adapter = MainAdapter(DateBase.categoryList, requireActivity() as BaseActivity)
        val layoutManager= LinearLayoutManager(requireActivity())
        rvView.layoutManager=layoutManager
        rvView.adapter = adapter
        rvView.addItemDecoration(DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL))
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    inner class MainAdapter(val categoryList:ArrayList<Category>,val activity:BaseActivity) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val categoryName: TextView = view.findViewById(R.id.fragment_main_category_textview)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_main_category, parent, false)
            val viewHolder = ViewHolder(view)
            viewHolder.categoryName.setOnClickListener {
                val category = DateBase.categoryList[viewHolder.adapterPosition]
                val activity=requireActivity() as MainActivity
                activity.replaceFragment(FirstFragment.newInstance(category.name))
            }
            return viewHolder
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val category = categoryList[position]
            holder.categoryName.text=category.name
            if (position % 2 == 0){
                holder.categoryName.setTextColor(resources.getColor(R.color.pink,activity.theme))
            }else{
                holder.categoryName.setTextColor(resources.getColor(R.color.blue,activity.theme))
            }
        }

        override fun getItemCount() = categoryList.size
    }

}