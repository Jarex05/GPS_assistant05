package com.mikhail_R_gps_tracker.gpsassistant.fragments.chet

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikhail_R_gps_tracker.gpsassistant.MainApp
import com.mikhail_R_gps_tracker.gpsassistant.R
import com.mikhail_R_gps_tracker.gpsassistant.databinding.FragmentTracksChetBinding
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ViewTrackBinding
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackAdapter
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItem
import com.mikhail_R_gps_tracker.gpsassistant.mainViewModels.ModelViewChet
import com.mikhail_R_gps_tracker.gpsassistant.utils.openFragmentChet
import com.yandex.mobile.ads.impl.it1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.lang.Exception

class TracksFragmentChet : Fragment(), TrackAdapter.Listener {
    private lateinit var binding: ViewTrackBinding
    private lateinit var adapter: TrackAdapter
    private val modelChet: ModelViewChet by activityViewModels{
        ModelViewChet.ViewModelFactory((requireContext().applicationContext as MainApp).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ViewTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val directoryDownloadChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it != null) {
            Log.d("MyLog", "Selected path: ${it.data?.data}")
            it.data?.data?.let { uri ->
                val trackString = readTrack(requireContext(), uri)
                if (trackString.startsWith("gps_tracker#")) {
                    CoroutineScope(Dispatchers.IO).launch {
                        modelChet.insertTrack(TrackItem(
                            null,
                            trackString.split("#")[1],
                            trackString.split("#")[2],
                            trackString.split("#")[3],
                        ))
                    }
                } else {
                    Toast.makeText(requireContext(), "Файл не поддерживается!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private var trackItem: TrackItem? = null

    private val directoryChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it != null) {
            Log.d("MyLog", "Selected path: ${it.data?.data}")
            it.data?.data?.let { uri ->

                trackItem?.let { item ->
                    saveGpsTrackToFile(uri, item.title, item.distance, item.geoPoints)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        getTracks()
        init()
    }

    private fun init(){
        binding.idDownload.setOnClickListener {
            val i = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            directoryDownloadChooser.launch(Intent.createChooser(i, "Open with"))
        }
    }

    private fun getTracks(){
        modelChet.tracks.observe(viewLifecycleOwner){
            adapter.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun initRcView() = with(binding){
        adapter = TrackAdapter(this@TracksFragmentChet)
        rcView.layoutManager = LinearLayoutManager(requireContext())
        rcView.adapter = adapter
    }



    companion object {
        @JvmStatic
        fun newInstance() = TracksFragmentChet()
    }

    override fun onClick(track: TrackItem, type: TrackAdapter.ClickType) {
        when(type){
            TrackAdapter.ClickType.SAVE -> {
                trackItem = track
                val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                }
                directoryChooser.launch(i)
            }
            TrackAdapter.ClickType.DELETE -> modelChet.deleteTrack(track)
            TrackAdapter.ClickType.OPEN -> {
                modelChet.currentTrack.value = track
                openFragmentChet(ViewTrackFragment.newInstance())
            }
        }
    }

    private fun saveGpsTrackToFile(
        directoryUri: Uri?,
        title: String,
        distance: String,
        content: String
    ) {
        try {
            val resolver = requireActivity().contentResolver
            val directory = DocumentFile.fromTreeUri(requireContext(), directoryUri!!)
            if (directory != null && directory.isDirectory) {
                val newFile = directory.createFile(
                    "text/plan",
                    "gps_name_$title.txt"
                )
                Log.d("MyLog", "File location path: ${newFile?.uri}")
                if (newFile != null) {
                    try {
                        val outputStream = resolver.openOutputStream(newFile.uri)
                        if (outputStream != null) {
                            outputStream.write(("gps_tracker#$title#$distance#$content").toByteArray())
                            outputStream.close()
                            Toast.makeText(requireContext(), "File saved!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("FileCreator", "Failed to create the file!")
                    Toast.makeText(requireContext(), "Failed to create the file!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("FileCreator", "Invalid directory URI or not a directory!")
                Toast.makeText(requireContext(), "Invalid directory URI or not a directory!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readTrack(context: Context, uri: Uri): String {
        val pdf = context.contentResolver.openFileDescriptor(uri, "r")!!
        return  if (pdf.statSize.toInt() > 0){
            val data = ByteArray(pdf.statSize.toInt())
            val fd = pdf.fileDescriptor
            val inputStream = FileInputStream(fd)
            inputStream.read(data)
            pdf.close()
            String(data)
        } else {
            pdf.close()
            "empty"
        }
    }
}