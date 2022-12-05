package top.xherror.mutualhelpers

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var waitdb:TinyDB
    private val waitpersonList = ArrayList<Person>()

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        waitdb=TinyDB(activity?.applicationContext  ,"waitList")
        initPersons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_setting, container, false)
        val fragmentFirstRecyclerView: RecyclerView =view.findViewById(R.id.fragmentSettingRecyclerView)
        val layoutManager= LinearLayoutManager(requireActivity())
        fragmentFirstRecyclerView.layoutManager=layoutManager
        val adapter=PersonAdapter(waitpersonList)
        fragmentFirstRecyclerView.adapter=adapter
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    inner class PersonAdapter(val waitpersonList: ArrayList<Person>) : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val waitpersonAccount: TextView = view.findViewById(R.id.waitpersonAccount)
            val waitpersonType: TextView = view.findViewById(R.id.waitpersonType)
            val waitpersonName: TextView = view.findViewById(R.id.waitpersonName)
            val waitpersonPhone: TextView = view.findViewById(R.id.waitpersonPhone)
            val waitpersonAccept:Button = view.findViewById(R.id.waitpersonAccept)
            val waitpersonReject:Button = view.findViewById(R.id.waitpersonReject)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.waitperson, parent, false)
            val viewHolder = ViewHolder(view)
            viewHolder.waitpersonAccept.setOnClickListener {
                val position = viewHolder.adapterPosition
                val person = waitpersonList[position]
                persondb?.putListString(person.account,person.toList())
                waitpersonList.remove(person)
                waitdb.remove(person.account)
                this.notifyItemRemoved(position)
            }
            viewHolder.waitpersonReject.setOnClickListener {
                val position = viewHolder.adapterPosition
                val person = waitpersonList[position]
                waitpersonList.remove(person)
                waitdb.remove(person.account)
                //TODO:rejectdb
                this.notifyItemRemoved(position)
            }
            return viewHolder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val person = waitpersonList[position]
            holder.waitpersonAccount.text=person.account
            holder.waitpersonType.text = person.type
            holder.waitpersonName.text= person.name
            holder.waitpersonPhone.text= person.phone
        }

        override fun getItemCount() = waitpersonList.size
    }

    private fun initPersons(){

        val waitMap=waitdb.all
        for ((k,v) in waitMap){
            val list=waitdb.getListString(k)
                val person=Person(k,list)
                waitpersonList.add(person)
        }
    }
}