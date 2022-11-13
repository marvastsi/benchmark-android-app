package br.edu.utfpr.marvas.greenbenchmark.ui.media

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentMediaBinding

class MediaFragment : Fragment() {
    private lateinit var mediaController: MediaController
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!
    private var mediaPath: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)

        val mVideoView = binding.videoView

        mediaController = MediaController(activity)
        mediaController.setAnchorView(mVideoView)

//        val uri = Uri.parse("https://www.youtube.com/watch?v=jxV_8zb_YD8")
        val packageName = javaClass.getPackage()?.name
        mediaPath = "android.resource://$packageName/${R.raw.video}"

        mVideoView.setMediaController(mediaController)
        mVideoView.setVideoURI(Uri.parse(mediaPath))
        mVideoView.requestFocus()
        mVideoView.start()

        mVideoView.setOnPreparedListener { }

        mVideoView.setOnCompletionListener {
            showMessage()
        }

        mVideoView.setOnErrorListener { mp, what, extra ->
            showMediaExecutionFailed(mp, what, extra)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mediaFileTextView = binding.mediaFile
        val loadingProgressBar = binding.loading

        mediaFileTextView.text = mediaPath
        loadingProgressBar.visibility = View.VISIBLE
    }

    private fun showMessage() {
        println("Media Executed")
        Toast.makeText(requireContext(), "Media Executed", Toast.LENGTH_LONG).show()
        Thread.sleep(2000L)
        findNavController().navigate(R.id.action_MediaFragment_to_StartFragment)
    }

    private fun showMediaExecutionFailed(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        val wErr = wErrors[what]
        val eErr = eErrors[extra] ?: UNKNOWN_ERROR
        val msg = "$wErr: $eErr"
        Log.e(Tags.LOGIN, msg)
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
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