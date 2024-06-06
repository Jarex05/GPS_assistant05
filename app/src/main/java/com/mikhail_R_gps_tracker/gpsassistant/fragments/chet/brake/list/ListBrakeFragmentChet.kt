package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.brake.list

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
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentListBrakeChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.brake.MyDbManagerBrake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ListBrakeFragmentChet : Fragment() {
    private lateinit var binding: FragmentListBrakeChetBinding
    private lateinit var myDbManagerBrake: MyDbManagerBrake
    private val adapterBrakeChet = AdapterBrakeChet(ArrayList(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListBrakeChetBinding.inflate(inflater, container, false)

        binding.fbAddBrakeChet.setOnClickListener {
            findNavController().navigate(R.id.action_listBrakeFragmentChet_to_addBrakeFragmentChet)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerBrake.openDb()
        fillAdapter()
    }

    private fun init(v: View) = with(binding) {
        myDbManagerBrake = MyDbManagerBrake(v.context)
        rcBrakeChet.layoutManager = LinearLayoutManager(requireContext())
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcBrakeChet)
        rcBrakeChet.adapter = adapterBrakeChet
    }

    private fun fillAdapter() {
        CoroutineScope(Dispatchers.Main).launch {
            adapterBrakeChet.updateBrakeChet(myDbManagerBrake.readDbDataBrakeChet())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManagerBrake.closeDb()
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
                adapterBrakeChet.removeItemBrakeChet(viewHolder.adapterPosition, myDbManagerBrake)
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListBrakeFragmentChet()
    }
}