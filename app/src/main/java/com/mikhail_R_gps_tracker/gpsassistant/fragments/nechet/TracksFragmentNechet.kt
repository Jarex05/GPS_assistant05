package com.mikhail_R_gps_tracker.gpsassistant.fragments.nechet

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
import com.mikhail_R_gps_tracker.gpsassistant.databinding.ViewTrackNechetBinding
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackAdapter
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackAdapterNechet
import com.mikhail_R_gps_tracker.gpsassistant.dbRoom.TrackItemNechet
import com.mikhail_R_gps_tracker.gpsassistant.mainViewModels.ModelViewNechet
import com.mikhail_R_gps_tracker.gpsassistant.utils.openFragmentChet
import com.mikhail_R_gps_tracker.gpsassistant.utils.openFragmentNechet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.lang.Exception

class TracksFragmentNechet : Fragment(), TrackAdapterNechet.ListenerNechet  {
    private lateinit var binding: ViewTrackNechetBinding
    private lateinit var adapterNechet: TrackAdapterNechet
    private val modelNechet: ModelViewNechet by activityViewModels{
        ModelViewNechet.ViewModelFactory((requireContext().applicationContext as MainApp).databaseNechet)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ViewTrackNechetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val directoryDownloadChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it != null) {
            Log.d("MyLog", "Selected path: ${it.data?.data}")
            it.data?.data?.let { uri ->
                val trackStringNechet = readTrackNechet(requireContext(), uri)
                if (trackStringNechet.startsWith("gps_tracker#")) {
                    CoroutineScope(Dispatchers.IO).launch {
                        modelNechet.insertTrackNechet(
                            TrackItemNechet(
                            null,
                            trackStringNechet.split("#")[1],
                            trackStringNechet.split("#")[2],
                            trackStringNechet.split("#")[3],
                        )
                        )
                    }
                } else {
                    Toast.makeText(requireContext(), "Файл не поддерживается!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private var trackItemNechet: TrackItemNechet? = null

    private val directoryChooser = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it != null) {
            Log.d("MyLog", "Selected path: ${it.data?.data}")
            it.data?.data?.let { uri ->

                trackItemNechet?.let { item ->
                    saveGpsTrackToFileNechet(uri, item.titleNechet, item.distanceNechet, item.geoPointsNechet)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcViewNechet()
        getTracksNechet()
        initNechet()
    }

    private fun initNechet(){
        binding.idDownloadNechet.setOnClickListener {
            val i = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            directoryDownloadChooser.launch(Intent.createChooser(i, "Open with"))
        }
    }

    private fun getTracksNechet(){
        modelNechet.tracksNechet.observe(viewLifecycleOwner){
            adapterNechet.submitList(it)
            binding.tvEmptyNechet.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun initRcViewNechet() = with(binding){
        adapterNechet = TrackAdapterNechet(this@TracksFragmentNechet)
        rcViewNechet.layoutManager = LinearLayoutManager(requireContext())
        rcViewNechet.adapter = adapterNechet
    }

    companion object {
        @JvmStatic
        fun newInstance() = TracksFragmentNechet()
    }

    override fun onClickNechet(track: TrackItemNechet, type: TrackAdapterNechet.ClickTypeNechet) {
        when(type){
            TrackAdapterNechet.ClickTypeNechet.SAVE -> {
                trackItemNechet = track
                val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                    flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                }
                directoryChooser.launch(i)
            }
            TrackAdapterNechet.ClickTypeNechet.DELETE -> modelNechet.deleteTrackNechet(track)
            TrackAdapterNechet.ClickTypeNechet.OPEN -> {
                modelNechet.currentTrackNechet.value = track
                openFragmentNechet(ViewTrackFragmentNechet.newInstance())
            }
        }
    }

    private fun saveGpsTrackToFileNechet(
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

    private fun readTrackNechet(context: Context, uri: Uri): String {
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