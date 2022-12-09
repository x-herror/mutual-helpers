package top.xherror.mutualhelpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val CATEGORY = "category"
/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var categoryName: String? = null
    lateinit var adapter:FirstAdapter
    lateinit var category: Category

    fun addItem(item:EntityItem){
        category.itemList.add(item)
        adapter.notifyItemInserted(category.itemList.size-1)

    }


    fun removeItem(item:EntityItem){
        category.itemList.remove(item)
        adapter.notifyItemRemoved(category.itemList.size-1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString(CATEGORY)
            DateBase.getCategory(categoryName!!)?.let {
                category=it
                adapter=FirstAdapter(it.itemList,requireActivity() as BaseActivity)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_first, container, false)
        val fragmentFirstRecyclerView: RecyclerView =view.findViewById(R.id.fragmentFirstRecyclerView)
        val layoutManager=StaggeredGridLayoutManager(2,     StaggeredGridLayoutManager.VERTICAL)
        fragmentFirstRecyclerView.layoutManager=layoutManager
        fragmentFirstRecyclerView.adapter=adapter
        val fragmentFirstSearchView: SearchView =view.findViewById(R.id.fragment_first_search_view)
        fragmentFirstSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                query?.let {
                    val thirdFragment=ThirdFragment.newInstance(categoryName!!,it)
                    val activity=requireActivity() as MainActivity
                    activity.replaceFragment(thirdFragment)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        }
        )

        return  view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment FirstFragment.
         */
        @JvmStatic
        fun newInstance(categoryName: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(CATEGORY, categoryName)
                }
            }
    }


}

