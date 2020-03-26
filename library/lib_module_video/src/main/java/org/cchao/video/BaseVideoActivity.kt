package org.cchao.video

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.*
import android.widget.RelativeLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.include_video.*
import kotlinx.android.synthetic.main.include_volume.*
import org.cchao.common.expansion.clickWithTrigger
import org.cchao.common.expansion.getScreenHeight
import org.cchao.common.expansion.getScreenWidth
import org.cchao.common.expansion.showToast
import org.cchao.common.ui.base.BaseActivity
import org.cchao.common.ui.dialog.PromptDialog
import org.cchao.common.utils.NetworkUtils
import org.cchao.video.receiver.NetChangeReceiver
import org.cchao.video.widget.VideoController

/**
 * @author cchen6
 * @Date on 2020/1/10
 * @Description
 */
abstract class BaseVideoActivity : BaseActivity(), MediaPlayer.OnInfoListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, Handler.Callback {

    private val TAG = "BaseVideoActivity"

    private val HIDE_VOLUME = 0
    private val AUTO_ROTATE = 1
    //横竖屏切换后多久可自动旋转
    private val AUTO_ROTATE_TIME = 3000

    //手势监听音量
    private var gestureDetector: GestureDetector? = null
    private var audioManager: AudioManager? = null
    //最大声音
    private var maxVolume: Int = 0
    //当前声音
    private var nowVolume = -1

    var isFullScreen = false
        protected set

    private var currentDuration = 0

    //能否在非wifi情况播放
    private val canPlayNoWifi = true

    //视频url
    var videoUrl: String? = null
        private set

    //是否分享返回resume
    private var isShareResume = false

    //是否已经开始了播放
    var isStartPlaying = false
        private set

    //是否已经初始化video
    private var isInitVideo = false

    private var isWifi = false

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    private var handler: Handler? = null

    //监听网络变化
    private var receiver: NetChangeReceiver? = null

    /**
     * 是否是横屏
     *
     * @return
     */
    protected val isLandscape: Boolean
        get() = false

    /**
     * 视频是否正在播放
     *
     * @return
     */
    protected val isPlay: Boolean
        get() = videoView != null && videoView!!.isPlaying

    val cropBitmap: Bitmap?
        get() = null

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            HIDE_VOLUME -> rl_volume_controller.visibility = View.GONE
            AUTO_ROTATE -> if (canRotate()) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            }
        }
        return false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (videoController != null && videoController!!.isLock) {
            if (canRotate()) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        } else {
            if (getScreenWidth() > getScreenHeight()) {
                isFullScreen = true
                rl_img_video.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, screenWidth)
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                isFullScreen = false
                rl_volume_controller.visibility = View.GONE
                vertical_seekbar_wrapper.visibility = View.GONE
                setVideoSize()
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            changeView(isFullScreen)
            videoController!!.setFullOrSmallScreen()
            handler!!.removeMessages(AUTO_ROTATE)
            handler!!.sendEmptyMessageDelayed(AUTO_ROTATE, AUTO_ROTATE_TIME.toLong())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVideo()
        //TODO
        img_live_action.clickWithTrigger(View.OnClickListener {
            startVideoPlay("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4")
        })
    }

    private fun initVideo() {
        handler = Handler(this)
        screenWidth = getScreenWidth()
        screenHeight = getScreenHeight()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        gestureDetector = GestureDetector(this, VideoPlayGestureListener())
        vertical_seekbar.max = 100

        setVideoSize()

        videoView!!.setOnPreparedListener(this)
        videoView!!.setOnErrorListener(this)
        videoView!!.setOnInfoListener(this)
        videoView!!.setOnCompletionListener(this)

        videoController!!.setPlayerController(this, videoView, isLive)
        videoController!!.shareAndCropListener = object : VideoController.ShareAndCropListener {
            override fun openShare(view: View) {}

            override fun openCrop(view: View) {}
        }
        videoController!!.showAndHideControllerListener = object : VideoController.ShowAndHideControllerListener {
            override fun hide() {
                vertical_seekbar_wrapper.visibility = View.GONE
            }

            override fun showVolume() {
                if (vertical_seekbar_wrapper.visibility == View.VISIBLE) {
                    vertical_seekbar_wrapper.visibility = View.GONE
                } else {
                    nowVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
                    vertical_seekbar.progress = nowVolume * 100 / maxVolume
                    vertical_seekbar_wrapper.visibility = View.VISIBLE
                }
            }
        }
        vertical_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                //变更声音
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume * progress / 100, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        //注册网络变化广播监听器
//        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        receiver = NetChangeReceiver()
//        receiver!!.setBaseVideoActivity(this)
//        this.registerReceiver(receiver, filter)

        //若不可旋转
        if (!canRotate()) {
            if (isLandscape) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
        if (hideController()) {
            videoController!!.visibility = View.GONE
        } else {
            videoController!!.visibility = View.VISIBLE
        }
    }

    private fun setVideoSize() {
        val imgVideoParams = rl_img_video.layoutParams as ViewGroup.MarginLayoutParams
        if (!videoIsFullScreen()) {
            imgVideoParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
            imgVideoParams.height = screenWidth * 9 / 16
        } else {
            imgVideoParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
            if (isLandscape) {
                imgVideoParams.height = screenWidth
            } else {
                imgVideoParams.height = screenHeight
            }
        }
        imgVideoParams.topMargin = videoMargin()
        rl_img_video.layoutParams = imgVideoParams
    }

    protected fun setVideoTitle(title: String) {
        videoController!!.setTitle(title)
    }

    override fun onBackPressed() {
        if (!canRotate()) {
            super.onBackPressed()
        } else {
            //锁屏
            if (videoController!!.isLock) {
                return
            }
            if (isFullScreen) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        //若当前显示流量提示框,暂停
        if (videoController!!.showPlayPrompt) {
            videoController!!.setVideoPlayOrPause(false)
        }
        if (canRotate()) {
            //设置横竖屏可以切换
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
    }

    override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        when (what) {
            10001 ->
                //TODO 针对流量切回wifi不能播放
                if (!isPlay && NetworkUtils.isWifi && !isShareResume) {
                    videoController!!.setVideoPlayOrPause(true)
                }
            10002 -> if (isShareResume) {
                videoController!!.setVideoPlayOrPause(false, currentDuration)
                videoController!!.postDelayed({ isShareResume = false }, 200)
            }
            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> if (rl_video_loading.visibility == View.VISIBLE) {
                rl_video_loading.postDelayed({
                    rl_video_loading.visibility = View.GONE
                    view_prepared.visibility = View.GONE
                }, 200)
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                rl_video_loading.visibility = View.VISIBLE
                //TODO 针对流量切回wifi不能播放
                if (!isPlay && NetworkUtils.isWifi && !isShareResume) {
                    videoController!!.setVideoPlayOrPause(true)
                }
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                rl_video_loading.visibility = View.GONE
                view_prepared.visibility = View.GONE
            }
            else -> {
            }
        }
        return true
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onCompletion(mp: MediaPlayer) {
        onCompletionAndError()
    }

    /**
     * 视频正常播放结束和视频无法播放导致的视频播放结束统一处理
     *
     * @param mp
     */
    private fun onCompletionAndError() {
        videoController!!.reset()
        videoUrl = null
        currentDuration = 0
        img_video.visibility = View.VISIBLE
        img_live_action.visibility = View.VISIBLE
        rl_video_loading.visibility = View.GONE
        if (videoController!!.isLock) {
            videoController!!.switchLock()
        }
        if (canRotate()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * 播放视频
     *
     * @param url         视频地址
     * @param isStopVideo 播放前是否销毁当前视频
     */
    protected fun startVideoPlay(url: String?, isStopVideo: Boolean = true) {
        if (!isInitVideo) {
            initVideo()
            isInitVideo = true
        }
        if (isStopVideo) {
            stopVideoPlay()
        }
        videoUrl = url
        if (!NetworkUtils.isConnected) {
            showToast(resources.getString(R.string.not_network))
            return
        }
        //非Wi-Fi
        if (!NetworkUtils.isWifi) {
            if (!canPlayNoWifi) {
                showToast(resources.getString(R.string.video_play_not_wifi_prompt))
            } else {
                showPrompt()
            }
        } else {
            startVideoPlay()
        }
    }

    private fun startVideoPlay() {
        if (TextUtils.isEmpty(videoUrl)) {
            return
        }
        stopVideoPlay()
        view_prepared.visibility = View.VISIBLE
        img_live_action.visibility = View.GONE
        img_video.visibility = View.GONE
        rl_video_loading.visibility = View.VISIBLE
        videoView!!.setVideoPath(videoUrl)
        videoView!!.requestFocus()
        videoController!!.setVideoPlayOrPause(true)
        isStartPlaying = true
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    protected fun stopVideoPlay() {
        if (videoView == null) {
            return
        }
        if (canRotate()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        videoController!!.setVideoPlayOrPause(false)
        videoView!!.stopPlayback()
        img_video.visibility = View.VISIBLE
        img_live_action.visibility = View.VISIBLE
        rl_video_loading.visibility = View.GONE
        isStartPlaying = false
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * 是否是直播
     *
     * @return
     */
    protected abstract val isLive: Boolean

    /**
     * 横竖屏切换时隐藏显示view
     *
     * @param isFullScreen
     */
    protected abstract fun changeView(isFullScreen: Boolean)

    /**
     * 视频距顶部距离
     *
     * @return
     */
    protected abstract fun videoMargin(): Int

    /**
     * 视频是否全屏
     *
     * @return
     */
    protected fun videoIsFullScreen(): Boolean {
        return false
    }

    /**
     * 能否自动旋转
     *
     * @return
     */
    protected fun canRotate(): Boolean {
        return true
    }

    /**
     * 是否隐藏视频控制条
     *
     * @return
     */
    protected fun hideController(): Boolean {
        return false
    }

    private fun showPrompt() {
        val promptDialog = PromptDialog.show(supportFragmentManager, resources.getString(R.string.video_play_not_wifi_title), resources.getString(R.string.video_play_not_wifi_content), resources.getString(R.string.video_play_not_wifi_continue), resources.getString(R.string.video_play_not_wifi_cancel))
        promptDialog.setOnOkClickListener(View.OnClickListener { promptDialog.dismiss() })
        promptDialog.setOnOkClickListener(View.OnClickListener {
            videoController!!.showPlayPrompt = false
            if (isLive) {
                startVideoPlay()
            } else {
                if (currentDuration == 0) {
                    startVideoPlay()
                } else {
                    rl_video_loading.visibility = View.VISIBLE
                    videoController!!.setVideoPlayOrPause(true, currentDuration)
                }
            }
            promptDialog.dismiss()
        })
    }

    /**
     * 直播、点播横屏播放的时候监听手势变化 控制音量、亮度和（点播）播放的快进、快退
     */
    internal inner class VideoPlayGestureListener : GestureDetector.SimpleOnGestureListener() {

        private var fx: Float = 0.toFloat()

        private var fy: Float = 0.toFloat()

        private var halfScreenWidth: Float = 0.toFloat()

        override fun onDown(e: MotionEvent): Boolean {
            fx = e.x
            fy = e.y
            halfScreenWidth = (screenWidth / 2).toFloat()
            return super.onDown(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            var mOldY = 0f
            if (e1 != null) {
                mOldY = e1.y
            }
            val y = e2.rawY.toInt()
            val x = e2.rawX.toInt()

            if (Math.abs(y - fy) > Math.abs(x - fx) + 100 && fx > halfScreenWidth + 40) {
                onVolumeSlide((mOldY - y) / screenHeight)
            } else if (Math.abs(y - fy) > Math.abs(x - fx) + 100 && fx < halfScreenWidth - 40) {
                //调节亮度
                var myBright = mOldY.toInt() - y
                if (myBright >= 255) {

                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    protected fun onVolumeSlide(percent: Float) {
        if (nowVolume == -1) {
            nowVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            if (nowVolume < 0) {
                nowVolume = 0
            }
            handler!!.removeMessages(HIDE_VOLUME)
            rl_volume_controller.visibility = View.VISIBLE
        }
        var index = (percent * maxVolume).toInt() + nowVolume
        if (index > maxVolume) {
            index = maxVolume
        } else if (index < 0) {
            index = 0
        }
        // 变更声音
        audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0)
        vertical_seekbar.progress = index * 100 / maxVolume
        progress_volume.progress = index * 100 / maxVolume
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (isFullScreen) {
            gestureDetector!!.onTouchEvent(ev)
            //处理手势结束
            when (ev.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> endGesture()
                else -> {
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 隐藏音量控制
     * 0 代表控制音量的显示和隐藏 1代表亮度的显示和隐藏
     */
    protected fun endGesture() {
        nowVolume = -1
        handler!!.removeMessages(HIDE_VOLUME)
        handler!!.sendEmptyMessageDelayed(HIDE_VOLUME, 1000)
    }

    fun netChanged() {
        if (NetworkUtils.isConnected) {
            if (NetworkUtils.isWifi) {
                isWifi = true
            } else if (isWifi) {
                showToast(resources.getString(R.string.not_wifi))
                isWifi = false
            }
        } else if (isWifi) {
            showToast(resources.getString(R.string.not_wifi))
            isWifi = false
        }
    }

    override fun finish() {
        if (videoView != null) {
            stopVideoPlay()
            videoView!!.stopPlayback()
        }
        System.gc()
        super.finish()
    }

    override fun onPause() {
        super.onPause()
        if (videoView != null) {
            if (videoView!!.currentPosition > 0) {
                currentDuration = videoView!!.currentPosition
            }
            videoController!!.setVideoPlayOrPause(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (videoView != null) {
            if (isLive && !TextUtils.isEmpty(videoUrl)) {
                if (videoController!!.showPlayPrompt) {
                    showPrompt()
                } else {
                    rl_video_loading.visibility = View.VISIBLE
                    startVideoPlay(videoUrl)
                }
            } else if (currentDuration > 0) {
                if (videoController!!.showPlayPrompt) {
                    showPrompt()
                } else {
                    rl_video_loading.visibility = View.VISIBLE
                    view_prepared.visibility = View.VISIBLE
                    videoController!!.setVideoPlayOrPause(true, currentDuration)
                }
            }
        }
        if (videoController != null && videoController!!.isLock) {
            if (canRotate()) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null) {
            this.unregisterReceiver(receiver)
        }
        if (videoController != null) {
            videoController!!.destroyTimer()
        }
    }
}