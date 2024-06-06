package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.pantograph.list

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
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentListPantographNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.MyDbManagerPantograph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ListPantographFragmentNechet : Fragment() {
    private lateinit var binding: FragmentListPantographNechetBinding
    private lateinit var myDbManagerPantograph: MyDbManagerPantograph
    private val adapterPantographNechet = AdapterPantographNechet(ArrayList(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListPantographNechetBinding.inflate(inflater, container, false)

        binding.fbAddPantographNechet.setOnClickListener {
            findNavController().navigate(R.id.action_listPantographFragmentNechet_to_addPantographFragmentNechet)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerPantograph.openDb()
        fillAdapter()
    }

    private fun init(v: View) = with(binding) {
        myDbManagerPantograph = MyDbManagerPantograph(v.context)
        rcPantographNechet.layoutManager = LinearLayoutManager(requireContext())
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcPantographNechet)
        rcPantographNechet.adapter = adapterPantographNechet
    }

    private fun fillAdapter() {
        CoroutineScope(Dispatchers.Main).launch {
            adapterPantographNechet.updatePantographNechet(myDbManagerPantograph.readDbDataPantographNechet())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManagerPantograph.closeDb()
    }

    private fun getSwapMg() : ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapterPantographNechet.removeItemPantographNechet(viewHolder.adapterPosition, myDbManagerPantograph)
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListPantographFragmentNechet()
    }
}