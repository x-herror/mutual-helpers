package top.xherror.mutualhelpers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
    private val itemList=ArrayList<Item>()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val adapter=FirstAdapter(itemList)
    /*
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState == Lifecycle.State.CREATED) {
                    val fragmentFirstRecyclerView: RecyclerView =requireView().findViewById(R.id.fragmentFirstRecyclerView)
                    val layoutManager= LinearLayoutManager(requireActivity())
                    fragmentFirstRecyclerView.layoutManager=layoutManager
                    fragmentFirstRecyclerView.adapter=FirstAdapter(itemList)
                    lifecycle.removeObserver(this)
                }
            }
        })

    }
    */
    fun addItem(item:Item){
        itemList.add(item)
        adapter.notifyItemInserted(itemList.size-1)
    }

    fun removeItem(objectItem:Item){
        Log.d("itemList change","remove ${objectItem.toString()} from itemList :${itemList[0].toString()}")
        //itemList.remove(item)
        var position=-1
        for (i in 0 until itemList.size){
            val item=itemList[i]
            if (item.name==objectItem.name&&item.time==objectItem.time&&item.location==objectItem.location){
                position=i
            }
        }
        if (position>=0){itemList.removeAt(position)}
        adapter.notifyItemRemoved(itemList.size-1)
    }

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val cursor=dbHelper.readableDatabase.rawQuery("SELECT * FROM MyItems",null)
        cursor.use {
            if (it.moveToFirst()){
                do{
                    val name=it.getString(it.getColumnIndex("name"))
                    val imagePath=it.getString(it.getColumnIndex("imagePath"))
                    val chooseOption=it.getInt(it.getColumnIndex("chooseOption"))
                    var bitmap:Bitmap?=null
                    if (imagePath!=""){
                        bitmap=Utils.getBitmap(imagePath,chooseOption)
                    }
                    val location=it.getString(it.getColumnIndex("location"))
                    val time=it.getString(it.getColumnIndex("time"))
                    itemList.add(Item(name, bitmap,location,time))
                    val activity=activity as MainActivity

                } while (cursor.moveToNext())
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //itemList.add(Item("Xherror",R.drawable.example,"SJTU","2022/10/5"))
        //return inflater.inflate(R.layout.fragment_first, container, false)
        val view=inflater.inflate(R.layout.fragment_first, container, false)

        val fragmentFirstRecyclerView: RecyclerView =view.findViewById(R.id.fragmentFirstRecyclerView)
        //val layoutManager= LinearLayoutManager(requireActivity())
        val layoutManager=StaggeredGridLayoutManager(2,     StaggeredGridLayoutManager.VERTICAL)
        fragmentFirstRecyclerView.layoutManager=layoutManager
        fragmentFirstRecyclerView.adapter=adapter
        return  view
    }

    interface TestDataCallback {
        fun testData()
    }

    fun setCallBack(testDataCallback: TestDataCallback){
        testDataCallback.testData()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

