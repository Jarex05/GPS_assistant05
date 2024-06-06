package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet.limitation.list

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
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentListLimitationsChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.limitations.MyDbManagerLimitations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ListLimitationsFragmentChet : Fragment() {
    private lateinit var binding: FragmentListLimitationsChetBinding
    private lateinit var myDbManagerLimitations: MyDbManagerLimitations
    private val adapterLimitationsChet = AdapterLimitationsChet(ArrayList(), this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListLimitationsChetBinding.inflate(inflater, container, false)

        binding.fbAddLimitationsChet.setOnClickListener {
            findNavController().navigate(R.id.action_listLimitationsFragmentChet_to_addLimitationsFragmentChet)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    override fun onResume() {
        super.onResume()
        myDbManagerLimitations.openDb()
        fillAdapter()
    }

    private fun init(v: View) = with(binding) {
        myDbManagerLimitations = MyDbManagerLimitations(v.context)
        rcLimitationsChet.layoutManager = LinearLayoutManager(requireContext())
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcLimitationsChet)
        rcLimitationsChet.adapter = adapterLimitationsChet
    }

    private fun fillAdapter() {
        CoroutineScope(Dispatchers.Main).launch {
            adapterLimitationsChet.updateLimitationsChet(myDbManagerLimitations.readDbDataLimitationsChet())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManagerLimitations.closeDb()
    }

    private fun getSwapMg() : ItemTouchHelper{
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapterLimitationsChet.removeItemLimitationsChet(viewHolder.adapterPosition, myDbManagerLimitations)
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListLimitationsFragmentChet()
    }
}