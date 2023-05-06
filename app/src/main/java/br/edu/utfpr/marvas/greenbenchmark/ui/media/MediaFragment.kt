package br.edu.utfpr.marvas.greenbenchmark.ui.media

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Constants
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.commons.snack
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.databinding.FragmentMediaBinding

class MediaFragment : Fragment() {
    private lateinit var configRepository: ConfigRepository
    private lateinit var mediaController: MediaController
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var mVideoView: VideoView
    private lateinit var fileName: String
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        val configStorage = ConfigStorage(
            requireContext().getSharedPreferences(
                ConfigStorage.TEST_CONFIG,
                Context.MODE_PRIVATE
            )
        )
        configRepository = ConfigRepository(configStorage)
        val config = configRepository.getConfig()
        val uri = config.mediaUri.toUri()
        fileName = uri.lastPathSegment.toString()
        mVideoView = binding.videoView
        loadingProgressBar = binding.loading

        mediaController = MediaController(activity)
        mediaController.setAnchorView(mVideoView)
        mVideoView.setMediaController(mediaController)
        mVideoView.setVideoURI(uri)
        mVideoView.requestFocus()
        mVideoView.start()

        mVideoView.setOnErrorListener { mp, what, extra ->
            showMediaExecutionFailed(mp, what, extra)
        }
        mVideoView.setOnPreparedListener {
            loadingProgressBar = binding.loading
            loadingProgressBar.visibility = View.INVISIBLE
        }
        mVideoView.setOnCompletionListener {
            exitMediaPlayer()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mediaFileTextView = binding.mediaFile
        val loadingProgressBar = binding.loading

        mediaFileTextView.text = fileName
        loadingProgressBar.visibility = View.VISIBLE
    }

    private fun exitMediaPlayer() {
        println("Media Executed")
        requireView().snack("Media Executed")
        Thread.sleep(Constants.DELAY_MS_SHORT)
        findNavController().navigateUp()
    }

    private fun showMediaExecutionFailed(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        val wErr = wErrors[what]
        val eErr = eErrors[extra] ?: UNKNOWN_ERROR
        val msg = "$wErr: $eErr"
        Log.e(Tags.MEDIA_EXECUTION, msg)
        requireView().snack(msg)
        mp.release()
        findNavController().navigateUp()
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
