package br.edu.utfpr.marvas.greenbenchmark.ui.media

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentMediaBinding
import java.io.File

class MediaFragment : Fragment() {
    private lateinit var mediaController: MediaController
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!
    private lateinit var fileName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)

        fileName = getString(R.string.media_file)

        val mVideoView = binding.videoView
        val file = getFile(fileName)

        mediaController = MediaController(activity)
        mediaController.setAnchorView(mVideoView)
        mVideoView.setMediaController(mediaController)
        mVideoView.setVideoURI(file.toUri())
        mVideoView.requestFocus()
        mVideoView.start()

        mVideoView.setOnCompletionListener {
            exitMediaPlayer()
        }
        mVideoView.setOnPreparedListener {
            val loadingProgressBar = binding.loading
            loadingProgressBar.visibility = View.INVISIBLE
        }

        mVideoView.setOnErrorListener { mp, what, extra ->
            showMediaExecutionFailed(mp, what, extra)
        }
        return binding.root
    }

    private fun getFile(fileName: String): File {
        return File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            fileName
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaFileTextView = binding.mediaFile
        val loadingProgressBar = binding.loading
        mediaFileTextView.text = fileName
        loadingProgressBar.visibility = View.VISIBLE
    }

    private fun exitMediaPlayer() {
        Toast.makeText(requireContext(), "Media Executed", Toast.LENGTH_LONG).show()
        Thread.sleep(2000L)
        findNavController().navigate(R.id.action_MediaFragment_to_StartFragment)
    }

    private fun showMediaExecutionFailed(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        val wErr = wErrors[what]
        val eErr = eErrors[extra] ?: UNKNOWN_ERROR
        val msg = "$wErr: $eErr"
        Log.e(Tags.MEDIA_EXECUTION, msg)
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        mp.release()
        findNavController().navigate(R.id.action_MediaFragment_to_StartFragment)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val wErrors = mapOf(
            MediaPlayer.MEDIA_ERROR_UNKNOWN to "Unspecified media player error.",
            MediaPlayer.MEDIA_ERROR_SERVER_DIED to "Media server died. In this case, the application must release the MediaPlayer object and instantiate a new one."
        )
        val eErrors = mapOf(
            MediaPlayer.MEDIA_ERROR_IO to "File or network related operation errors.",
            MediaPlayer.MEDIA_ERROR_MALFORMED to "Bitstream is not conforming to the related coding standard or file spec.",
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED to "Bitstream is conforming to the related coding standard or file spec, but the media framework does not support the feature.",
            MediaPlayer.MEDIA_ERROR_TIMED_OUT to "Some operation takes too long to complete, usually more than 3-5 seconds.",
        )
        const val UNKNOWN_ERROR = "Unspecified low-level system error."
    }
}