package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.redacktor.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentListRedacktorChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.MyDbManagerRedacktor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ListRedacktorFragmentChet : Fragment() {
    private lateinit var binding: FragmentListRedacktorChetBinding
    private lateinit var myDbManagerRedacktor: MyDbManagerRedacktor
    private val adapterRedacktorChet = AdapterRedacktorChet(ArrayList(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListRedacktorChetBinding.inflate(inflater, container, false)

        binding.fbAddRedacktorChet.setOnClickListener {
            findNavController().navigate(R.id.action_listRedacktorFragmentChet_to_addRedacktorFragmentChet)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerRedacktor.openDb()
        fillAdapter()
    }

    private fun init(v: View) = with(binding) {
        myDbManagerRedacktor = MyDbManagerRedacktor(v.context)
        rcRedacktorChet.layoutManager = LinearLayoutManager(requireContext())
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcRedacktorChet)
        rcRedacktorChet.adapter = adapterRedacktorChet
    }

    private fun fillAdapter(){
        CoroutineScope(Dispatchers.Main).launch {
            adapterRedacktorChet.updateAdapterChet(myDbManagerRedacktor.readDbDataRedacktorChet())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManagerRedacktor.closeDb()
    }

    private fun getSwapMg() : ItemTouchHelper{
        return  ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapterRedacktorChet.removeItemChet(viewHolder.adapterPosition, myDbManagerRedacktor)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListRedacktorFragmentChet()
    }
}