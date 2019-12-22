package com.example.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.StringCodec
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var flutterView: FlutterView? = null
    private var counter = 0
    private var messageChannel: BasicMessageChannel<String>? = null
    private lateinit var viewPager: CustomViewPager

    private fun getArgsFromIntent(intent: Intent): Array<String?>? {
        // Before adding more entries to this list, consider that arbitrary
        // Android applications can generate intents with extra data and that
        // there are many security-sensitive args in the binary.
        val args = ArrayList<String>()
        if (intent.getBooleanExtra("trace-startup", false)) {
            args.add("--trace-startup")
        }
        if (intent.getBooleanExtra("start-paused", false)) {
            args.add("--start-paused")
        }
        if (intent.getBooleanExtra("enable-dart-profiling", false)) {
            args.add("--enable-dart-profiling")
        }
        if (args.isNotEmpty()) {
            val argsArray = arrayOfNulls<String>(args.size)
            return args.toArray(argsArray)
        }
        return null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        /*
        on device orientation changed
        TODO: data on the flutter side is lost when rebuilt
        If not recreated the flutterView becomes black
        This was only applicable if flutter_view is not in the same place
        As a specific layout-land xml was defined
        So this is no longer a problem as no landscape logic is required now
        */
        // recreate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = getArgsFromIntent(intent)
        if (flutterEngine == null) {
            flutterEngine = FlutterEngine(this, args)
            flutterEngine!!.dartExecutor.executeDartEntrypoint(
                    DartEntrypoint.createDefault()
            )
        }
        setContentView(R.layout.app_layout)

        val supportActionBar = supportActionBar
        supportActionBar?.hide()

        viewPager = findViewById(R.id.pages)
        viewPager.adapter = ViewPagerAdaptor(this)

        var xOffset = 0f

        with(viewPager) {
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                //                https://bit.ly/32gnXsh
                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager.SCROLL_STATE_DRAGGING -> {
                            println("Drag scroll $state")
                        }
                        ViewPager.SCROLL_STATE_IDLE -> {
                            println("Idle scroll $state")
                        }
                        ViewPager.SCROLL_STATE_SETTLING -> {
                            println("Scroll settled or stopped $state")
                            Log.d(TAG, xOffset.toString())
                        }
                    }
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                    Log.d("main", position.toString())
//                    Log.d("main", positionOffset.toString())
//                    Log.d("main", positionOffsetPixels.toString())
                    xOffset = (position + positionOffset)
//                    if (xOffset >= 0.6){
//                        Log.d(TAG, xOffset.toString())
//                    }
//                    wallpaperManager.setWallpaperOffsets(viewPager.windowToken, xOffset, 0.0f)
                }

                override fun onPageSelected(position: Int) {
//                    dock.text = pageModels[position].label
//                    currentPage = position
                    println("Page selected $position")
                    println("Current Item $currentItem")
                }
            })
        }

        Log.d(TAG, viewPager.childCount.toString())

        messageChannel = BasicMessageChannel(flutterEngine!!.dartExecutor, CHANNEL, StringCodec.INSTANCE)
        messageChannel!!.setMessageHandler(ReplyAgent())
    }

    // A simple alternative to a method call handler
    inner class ReplyAgent : BasicMessageChannel.MessageHandler<String> {
        override fun onMessage(s: String?, reply: BasicMessageChannel.Reply<String>) {
            // if (s != null) Log.d(TAG, s)
            when (s) {
                "pong" -> {
                    onFlutterIncrement()
                    reply.reply(EMPTY_MESSAGE)
                }
                "enableScroll" -> {
                    viewPager.pagingEnabled = true
                }
                "disableScroll" -> {
                    viewPager.pagingEnabled = false
                }
                else -> {
                    reply.reply("unknown")
                }
            }
        }

    }

    // `layout` is a linear Layout defined in R.layout.flutter_view
    fun onFlutterViewInit(layout: View) {
        // Initialize the flutterview
        val transparentFlutterView = FlutterView(applicationContext, FlutterView.TransparencyMode.transparent)
        transparentFlutterView.id = R.id.flutter_view

        // Add this view i.e. the newly created transparent flutterview to the R.layout.flutter_view
        (layout as LinearLayout).addView(transparentFlutterView)

        // Attach this newly created flutterview to the running instance of the engine
        transparentFlutterView.attachToFlutterEngine(flutterEngine!!)
        this.flutterView = transparentFlutterView

    }

    // `layout` is a constrained layout defined in R.layout.android_view
    fun onAndroidViewInit(layout: View, id: Int?) {
        layout.id = id!!
        // MUST initialize with the existing values'
        updateViews()
        val fab: FloatingActionButton = layout.findViewById(R.id.button)
        fab.setOnClickListener { sendAndroidIncrement() }
    }


    private fun sendAndroidIncrement() {
        // viewPager.pagingEnabled = !viewPager.pagingEnabled
        messageChannel!!.send(PING)
    }

    private fun onFlutterIncrement() {
        counter++
        updateViews()
    }

    private fun updateViews() {
        val layout0 = findViewById<LinearLayout>(R.id.android_view0)
        val layout = findViewById<LinearLayout>(R.id.android_view)
        val textView0 = layout0?.findViewById<TextView>(R.id.button_tap)
        val textView = layout?.findViewById<TextView>(R.id.button_tap)
        val value = "Flutter button tapped " + counter + if (counter == 1) " time" else " times"
        textView0?.text = value
        textView?.text = value

    }

    override fun onResume() {
        super.onResume()
        flutterEngine!!.lifecycleChannel.appIsResumed()
    }

    override fun onPause() {
        super.onPause()
        flutterEngine!!.lifecycleChannel.appIsInactive()
    }

    override fun onStop() {
        super.onStop()
        flutterEngine!!.lifecycleChannel.appIsPaused()
    }

    override fun onDestroy() {
        flutterView!!.detachFromFlutterEngine()
        super.onDestroy()
    }

    companion object {
        var flutterEngine: FlutterEngine? = null
        private const val CHANNEL = "com.example.view/increment"
        private const val EMPTY_MESSAGE = ""
        private const val PING = "ping"
    }

    fun openHomeSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent(android.provider.Settings.ACTION_HOME_SETTINGS)
        } else {
            TODO("VERSION.SDK_INT < N")
            // This can be done I think https://github.com/Neamar/KISS/blob/68189e4f6cc23b907f2058567793ae455e1fe99c/app/src/main/java/fr/neamar/kiss/preference/DefaultLauncherPreference.java
            // TODO: Test this across multiple devices first to decide what to keep
        }
        startActivity(intent)
    }
}

// TODO: Test All my apps on emulators, Realme 5 Pro, Redmi note 4, Moto g5, Moto C
//  If possible a TV emulator
