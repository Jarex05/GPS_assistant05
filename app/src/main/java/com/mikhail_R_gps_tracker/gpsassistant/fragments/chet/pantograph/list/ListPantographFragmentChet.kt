package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.pantograph.list

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
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentListPantographChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.pantograph.MyDbManagerPantograph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ListPantographFragmentChet : Fragment() {
    private lateinit var binding: FragmentListPantographChetBinding
    private lateinit var myDbManagerPantograph: MyDbManagerPantograph
    private val adapterPantographChet = AdapterPantographChet(ArrayList(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListPantographChetBinding.inflate(inflater, container, false)

        binding.fbAddPantographChet.setOnClickListener {
            findNavController().navigate(R.id.action_listPantographFragmentChet_to_addPantographFragmentChet)
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
        rcPantographChet.layoutManager = LinearLayoutManager(requireContext())
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcPantographChet)
        rcPantographChet.adapter = adapterPantographChet
    }

    private fun fillAdapter() {
        CoroutineScope(Dispatchers.Main).launch {
            adapterPantographChet.updatePantographChet(myDbManagerPantograph.readDbDataPantographChet())
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
                adapterPantographChet.removeItemPantographChet(viewHolder.adapterPosition, myDbManagerPantograph)
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListPantographFragmentChet()
    }
}