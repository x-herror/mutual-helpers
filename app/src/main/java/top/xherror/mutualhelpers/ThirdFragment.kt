package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

private const val CATEGORY_NAME = "categoryName"
private const val SEARCH_STRING = "searchString"

class ThirdFragment : Fragment() {
    private lateinit var itemList:ArrayList<EntityItem>
    private lateinit var adapter:FirstAdapter
    private var categoryName: String? = null
    private var searchString: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString(CATEGORY_NAME)
            searchString = it.getString(SEARCH_STRING)
            if (categoryName!= MY_CATEGORY){
                val category=DateBase.getCategory(categoryName!!)
                if (category != null)
                    itemList=category.getSearchResult(searchString!!)

            }else if (categoryName== MY_CATEGORY){
                itemList=DateBase.getMyItemSearchResult(searchString!!)
            }
            adapter=FirstAdapter(itemList)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_first, container, false)

        val fragmentFirstRecyclerView: RecyclerView =view.findViewById(R.id.fragmentFirstRecyclerView)

        val layoutManager= StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)

        fragmentFirstRecyclerView.layoutManager=layoutManager

        fragmentFirstRecyclerView.adapter=adapter

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(cateGoryName: String, searchString: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(CATEGORY_NAME, cateGoryName)
                    putString(SEARCH_STRING, searchString)
                }
            }
    }
}