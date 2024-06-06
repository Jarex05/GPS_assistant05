package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet.redacktor.list

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
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentListRedacktorNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.db.redacktor.MyDbManagerRedacktor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class ListRedacktorFragmentNechet : Fragment() {
    private lateinit var binding: FragmentListRedacktorNechetBinding
    private lateinit var myDbManagerRedacktor: MyDbManagerRedacktor
    private val adapterRedacktorNechet = AdapterRedacktorNechet(ArrayList(), this)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentListRedacktorNechetBinding.inflate(inflater, container, false)

        binding.fbAddRedacktorNechet.setOnClickListener {
            findNavController().navigate(R.id.action_listRedacktorFragmentNechet_to_addRedacktorFragmentNechet)
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
        rcRedacktorNechet.layoutManager = LinearLayoutManager(requireContext())
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcRedacktorNechet)
        rcRedacktorNechet.adapter =adapterRedacktorNechet
    }

    private fun fillAdapter(){
        CoroutineScope(Dispatchers.Main).launch {
            adapterRedacktorNechet.updateAdapterNechet(myDbManagerRedacktor.readDbDataRedacktorNechet())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManagerRedacktor.closeDb()
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
                adapterRedacktorNechet.removeItemNechet(viewHolder.adapterPosition, myDbManagerRedacktor)
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ListRedacktorFragmentNechet()
    }
}