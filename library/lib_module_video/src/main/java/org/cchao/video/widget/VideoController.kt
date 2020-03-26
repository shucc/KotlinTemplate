package org.cchao.video.widget

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.VideoView
import kotlinx.android.synthetic.main.video_controller.view.*
import org.cchao.common.expansion.clickWithTrigger
import org.cchao.common.expansion.showToast
import org.cchao.common.ui.dialog.PromptDialog
import org.cchao.common.utils.NetworkUtils
import org.cchao.video.BaseVideoActivity
import org.cchao.video.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author cchen6
 * @Date on 2020/1/14
 * @Description
 */
class VideoController : RelativeLayout, Handler.Callback {

    //进度条最大进度
    private val SEEK_MAX = 1000
    //控制条自动隐藏时间
    private val DEFAULT_TIME = 5000L
    private val WHAT_FADE_OUT = 1;

    private var videoView: VideoView? = null

    //时分秒
    private lateinit var hmsDateFormat: SimpleDateFormat
    //分秒
    private lateinit var msDateFormat: SimpleDateFormat

    private var baseVideoActivity: BaseVideoActivity? = null

    private lateinit var myHandler: Handler

    //是否为直播
    private var isLive = false

    //是否锁屏
    var isLock = false

    //定时器修改播放进度
    private var timer: Timer? = null
    private var timerTask: MyTimerTask? = null
    //缓存进度0-100
    private var buffer = 0

    //重要:是否显示流量提示,用于wifi切流量时的特殊处理,wifi切流量点确定只提示一次
    var showPlayPrompt = false

    var shareAndCropListener: ShareAndCropListener? = null
        set(value) {
            field = value
            img_video_share.clickWithTrigger(OnClickListener { field!!.openCrop(it) })
            img_crop.clickWithTrigger(OnClickListener { field!!.openCrop(it) })
        }

    var showAndHideControllerListener: ShowAndHideControllerListener? = null
        set(value) {
            field = value
            img_volume.clickWithTrigger(OnClickListener { field!!.showVolume() })
        }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == WHAT_FADE_OUT) {
            showHideController(false)
            showHideProgressBar(true)
        }
        return false
    }

    private fun initView(context: Context) {
        hmsDateFormat = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
        msDateFormat = SimpleDateFormat("mm:ss", Locale.CHINA)
        myHandler = Handler(this)
        LayoutInflater.from(context).inflate(R.layout.video_controller, this)
        video_controller_progress.max = SEEK_MAX
        seekbar.max = SEEK_MAX
        bindEvent()
    }

    private fun bindEvent() {
        img_play_pause.clickWithTrigger(OnClickListener { setVideoPlayOrPause(!videoView!!.isPlaying) })
        rl_parent.clickWithTrigger(OnClickListener { showHideController() })
        img_video_back.clickWithTrigger(OnClickListener { baseVideoActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT })
        img_full_small.clickWithTrigger(OnClickListener { baseVideoActivity!!.requestedOrientation = if (baseVideoActivity!!.isFullScreen) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE })
        img_lock.clickWithTrigger(OnClickListener { switchLock() })
    }

    fun setPlayerController(baseVideoActivity: BaseVideoActivity, videoView: VideoView) {
        setPlayerController(baseVideoActivity, videoView, false)
    }

    fun setPlayerController(baseVideoActivity: BaseVideoActivity, videoView: VideoView, isLive: Boolean) {
        this.baseVideoActivity = baseVideoActivity
        this.videoView = videoView
        setLive(isLive)
    }

    fun setTitle(title: String) {
        text_video_title.text = title
    }

    fun setVideoPlayOrPause(isSetPlay: Boolean) {
        setVideoPlayOrPause(isSetPlay, -1)
    }

    fun setVideoPlayOrPause(isSetPlay: Boolean, seekTo: Int) {
        if (videoView == null) {
            return
        }
        if (isSetPlay) {
            if (!NetworkUtils.isConnected) {
                context.showToast(resources.getString(R.string.not_network))
            } else if (!NetworkUtils.isWifi) {
                if (showPlayPrompt) {
                    //流量播放提示
                    showPrompt(seekTo)
                } else {
                    if (seekTo != -1) {
                        videoView!!.seekTo(seekTo)
                    } else {
                        videoView!!.start()
                    }
                    img_play_pause.setImageResource(R.drawable.ic_video_pause)
                }
            } else {
                if (seekTo != -1) {
                    videoView!!.seekTo(seekTo)
                } else {
                    videoView!!.start()
                }
                img_play_pause.setImageResource(R.drawable.ic_video_pause)
            }
        } else {
            if (seekTo != -1) {
                videoView!!.seekTo(seekTo)
            }
            videoView!!.pause()
            img_play_pause.setImageResource(R.drawable.ic_video_play)
        }
    }

    private fun showPrompt() {
        showPrompt(-1)
    }

    private fun showPrompt(seekTo: Int) {
        val dialog = PromptDialog.show(baseVideoActivity!!.supportFragmentManager, resources.getString(R.string.video_play_not_wifi_title)
                , resources.getString(R.string.video_play_not_wifi_content)
                , resources.getString(R.string.video_play_not_wifi_continue)
                , resources.getString(R.string.video_play_not_wifi_cancel))
        dialog.setOnCancelClickListener(OnClickListener { dialog.dismiss() })
        dialog.setOnOkClickListener(OnClickListener {
            if (seekTo != -1) {
                videoView!!.seekTo(seekTo)
            } else {
                videoView!!.start()
            }
            img_play_pause.setImageResource(R.drawable.ic_video_pause)
            showPlayPrompt = false
            dialog.dismiss()
        })
    }

    /**
     * 进度重置
     */
    fun reset() {
        buffer = 0
        text_currentTime.text = "00:00"
        text_totalTime.text = "/00:00"
        video_controller_progress.progress = 0
        video_controller_progress.secondaryProgress = 0
        seekbar.progress = 0
        seekbar.secondaryProgress = 0
    }

    /**
     * 设置是否是直播
     */
    fun setLive(live: Boolean) {
        isLive = live
        if (isLive) {
            seekbar.visibility = View.INVISIBLE
            text_totalTime.visibility = View.INVISIBLE
            text_currentTime.visibility = View.INVISIBLE
            video_controller_progress.visibility = View.GONE
        } else {
            seekbar.visibility = View.VISIBLE
            text_currentTime.visibility = View.VISIBLE
            text_totalTime.visibility = View.VISIBLE
            seekbar.setOnSeekBarChangeListener(MySeekBarChangeListener())
            timer = Timer()
            timerTask = MyTimerTask()
            timer!!.schedule(timerTask, 0, 1000)
            video_controller_progress.visibility = View.VISIBLE
        }
    }

    /**
     * 显示隐藏控制条
     */
    private fun showHideController() {
        myHandler.removeMessages(WHAT_FADE_OUT)
        if (isLock) {
            if (img_lock.visibility == View.VISIBLE) {
                img_lock.visibility = View.INVISIBLE
                showHideProgressBar(true)
            } else {
                img_lock.visibility = View.VISIBLE
                showHideProgressBar(true)
                myHandler.sendEmptyMessageDelayed(WHAT_FADE_OUT, DEFAULT_TIME)
            }
        } else {
            if (rl_bottom_bar.visibility == View.VISIBLE) {
                showHideController(false)
                showHideProgressBar(true)
            } else {
                showHideController(true)
                showHideProgressBar(false)
                myHandler.sendEmptyMessageDelayed(WHAT_FADE_OUT, DEFAULT_TIME)
            }
        }
    }

    /**
     * 设置锁屏与取消锁屏
     */
    fun switchLock() {
        img_lock.setImageResource(if (isLock) R.drawable.ic_video_lock_no else R.drawable.ic_video_lock_yes)
        ll_top_bar.visibility = if (isLock) View.VISIBLE else View.INVISIBLE
        rl_bottom_bar.visibility = if (isLock) View.VISIBLE else View.INVISIBLE
        ll_share_crop.visibility = if (isLock) View.VISIBLE else View.INVISIBLE
        showHideController(!isLock)
        isLock = !isLock
        baseVideoActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    private fun showHideController(isShow: Boolean) {
        val show = if (isShow) View.VISIBLE else View.INVISIBLE
        rl_bottom_bar.visibility = show
        if (null == baseVideoActivity || baseVideoActivity!!.isFinishing || !baseVideoActivity!!.isFullScreen) {
            return
        }
        ll_top_bar.visibility = show
        img_lock.visibility = show
        ll_share_crop.visibility = show
        if (null != showAndHideControllerListener && !isShow) {
            showAndHideControllerListener!!.hide()
        }
    }

    /**
     * 显示隐藏底部进度条
     */
    private fun showHideProgressBar(isShow: Boolean) {
        if (!isShow) {
            video_controller_progress.visibility = View.GONE
        } else if (!isLive) {
            video_controller_progress.visibility = View.VISIBLE
        }
    }

    /**
     * 全屏与小屏view显示控制
     */
    fun setFullOrSmallScreen() {
        if (baseVideoActivity!!.isFullScreen) {
            ll_top_bar.visibility = View.VISIBLE
            img_lock.visibility = View.VISIBLE
            ll_share_crop.visibility = View.VISIBLE
            rl_bottom_bar.visibility = View.VISIBLE
            img_volume.visibility = View.VISIBLE
            img_full_small.setImageResource(R.drawable.ic_video_full_cancel)
            myHandler.sendEmptyMessageDelayed(WHAT_FADE_OUT, DEFAULT_TIME)
            showHideProgressBar(false)
        } else {
            img_lock.visibility = View.INVISIBLE
            isLock = false
            ll_top_bar.visibility = View.INVISIBLE
            img_lock.visibility = View.INVISIBLE
            ll_share_crop.visibility = View.INVISIBLE
            rl_bottom_bar.visibility = View.INVISIBLE
            img_volume.visibility = View.GONE
            img_full_small.setImageResource(R.drawable.ic_video_full)
            myHandler.sendEmptyMessageDelayed(WHAT_FADE_OUT, DEFAULT_TIME)
            showHideProgressBar(false)
        }
    }

    fun destroyTimer() {
        if (null != timerTask) {
            timerTask!!.cancel()
        }
        timer = null
        timerTask = null
    }

    /**
     * 定时器任务用来更新播放进度条的进度
     */
    private inner class MyTimerTask : TimerTask() {
        override fun run() {
            myHandler.post {
                if (videoView != null) {
                    val current = videoView!!.currentPosition
                    val duration = videoView!!.duration
                    if (duration >= 3600 * 1000) {
                        text_currentTime.text = hmsDateFormat.format(current - TimeZone.getDefault().rawOffset)
                        text_totalTime.text = "/${hmsDateFormat.format(duration - TimeZone.getDefault().rawOffset)}"
                    } else {
                        text_currentTime.text = msDateFormat.format(current - TimeZone.getDefault().rawOffset)
                        text_totalTime.text = "/${msDateFormat.format(duration - TimeZone.getDefault().rawOffset)}"
                    }
                    if (current <= duration && duration > 0) {
                        seekbar.progress = current * SEEK_MAX / duration
                        video_controller_progress.progress = current * SEEK_MAX / duration
                    } else if (current > duration && current > 0) {
                        seekbar.progress = SEEK_MAX
                        video_controller_progress.progress = SEEK_MAX
                    }
                    val progressInt = seekbar.progress / 10
                    //设置缓存进度
                    if (buffer <= videoView!!.bufferPercentage && buffer < 100 && videoView!!.bufferPercentage > progressInt) {
                        buffer = videoView!!.bufferPercentage
                    } else if (buffer < 100) {
                        buffer = seekbar.progress / 10 + 1
                        if (buffer > 100) {
                            buffer = 100
                        }
                    }
                    seekbar.secondaryProgress = buffer * 10
                    video_controller_progress.secondaryProgress = buffer * 10
                }
            }
        }
    }

    /**
     * 拖动进度条事件
     */
    private inner class MySeekBarChangeListener : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            val progress = seekBar.progress
            val currentTime = progress * videoView!!.duration / SEEK_MAX
            videoView!!.seekTo(currentTime)
            if (!videoView!!.isPlaying) {
                videoView!!.start()
            }
            img_play_pause.setImageResource(R.drawable.ic_video_pause)
        }
    }

    interface ShareAndCropListener {

        fun openShare(view: View)

        fun openCrop(view: View)
    }

    interface ShowAndHideControllerListener {
        fun hide()

        fun showVolume()
    }
}
