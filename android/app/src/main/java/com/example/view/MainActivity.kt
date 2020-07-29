package com.example.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.flutter.embedding.android.FlutterTextureView
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.StringCodec

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var flutterIndex: Int = 0
    var flutterPage: Int = 0
    var xOffset = 0f
    private var flutterView: FlutterView? = null
    private var counter = 0
    private var prevCounter = 0
    private var messageChannel: BasicMessageChannel<String>? = null
    private lateinit var viewPager: ViewPager2
    val pages: ArrayList<PageModel> = arrayListOf()
    private val ids: ArrayList<Int> = arrayListOf()

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

//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        /*
//        on device orientation changed
//        TODO: data on the flutter side is lost when rebuilt
//        If not recreated the flutterView becomes black
//        This was only applicable if flutter_view is not in the same place
//        As a specific layout-land xml was defined
//        So this is no longer a problem as no landscape logic is required now
//        */
//        // recreate()
//    }

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

        viewPager = ViewPager2(applicationContext)
        viewPager.id = R.id.pages

        val appLayout = findViewById<LinearLayout>(R.id.linear)
        appLayout.addView(viewPager)

        val supportActionBar = supportActionBar
        supportActionBar?.hide()

        // Initialize pages
        pages.add(PageModel.ANDROID)
        pages.add(PageModel.ANDROID)
        pages.add(PageModel.FLUTTER)
        pages.add(PageModel.ANDROID)
        pages.add(PageModel.ANDROID)
        flutterIndex = 2    
        // done initializing pages


        viewPager = findViewById(R.id.pages)
        viewPager.adapter = ViewPagerAdaptor2(this)


        viewPager.run {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                // https://bit.ly/32gnXsh
                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager2.SCROLL_STATE_DRAGGING -> {
                            println("Drag scroll $xOffset")
                        }
                        ViewPager2.SCROLL_STATE_IDLE -> {
                            println("Idle scroll $state")
                            println("Current page is $currentItem")
                            // TODO: Recent change
                            if (currentItem != flutterIndex) {
                                // TODO: Currently working here
                                viewPager.isUserInputEnabled = true
                            }
                        }
                        ViewPager2.SCROLL_STATE_SETTLING -> {
                            Log.d(TAG, "Scroll settled or stopped $currentItem")
                        }
                    }
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    // Update xOffset
                    xOffset = (position + positionOffset)
                }

                override fun onPageSelected(position: Int) {
                    println("Page selected $position")
                    println("Current Item $currentItem")
                    // TODO: Recent change
                    viewPager.isUserInputEnabled = currentItem != flutterIndex
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
                    onFlutterIncrement(reply)
                }
                "enableScroll" -> {
                    viewPager.isUserInputEnabled = true
                    Log.d(TAG, "enabling wait...")
                }
                "disableScroll" -> {
                    viewPager.isUserInputEnabled = false
                    Log.d(TAG, "disabling wait...")
                }
                "page0" -> flutterPage = 0
                "page1" -> flutterPage = 1
                "page2" -> flutterPage = 2
                else -> {
                    reply.reply("unknown")
                }
            }
        }
    }

    // `layout` is a linear Layout defined in R.layout.flutter_view
    fun onFlutterViewInit(layout: View) {
        // Initialize the flutterview
        val transparentFlutterView = FlutterView(applicationContext, FlutterTextureView(applicationContext))
        transparentFlutterView.id = R.id.flutter_view
        ids.add(R.id.flutter_view)

//        val transparentFlutterView = FlutterView(applicationContext, FlutterView.TransparencyMode.transparent)
//        transparentFlutterView.id = R.id.flutter_view
//        ids.add(R.id.flutter_view)

        // Add this view i.e. the newly created transparent flutterview to the R.layout.flutter_view
        (layout as LinearLayout).addView(transparentFlutterView)

        // Attach this newly created flutterview to the running instance of the engine
        transparentFlutterView.attachToFlutterEngine(flutterEngine!!)
        this.flutterView = transparentFlutterView
    }

    // `layout` is a constrained layout defined in R.layout.android_view
    fun onAndroidViewInit(layout: View) {
        // create a new id
        // https://stackoverflow.com/a/35551268/8608146
//        val id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            View.generateViewId()
//        } else {
//            ViewCompat.generateViewId()
//        }
        val id = 3814978 + ViewCompat.generateViewId()
        layout.id = id
        ids.add(layout.id)
        Log.d(TAG, "New id is ${layout.id}")
        // MUST initialize with the existing values
        updateViews()
        val fab: FloatingActionButton = layout.findViewById(R.id.button)
        fab.setOnClickListener { sendAndroidIncrement() }
    }


    private fun sendAndroidIncrement() {
        // viewPager.pagingEnabled = !viewPager.pagingEnabled
        messageChannel!!.send(PING)
    }

    private fun onFlutterIncrement(reply: BasicMessageChannel.Reply<String>) {
        counter++
        Log.d(TAG, counter.toString())
        updateViews()
        prevCounter++
        reply.reply(EMPTY_MESSAGE)
    }

    private fun updateViews() {
        val value = "Flutter button tapped " + counter + if (counter == 1) " time" else " times"
//        Log.d(TAG, "UPDATE VIEWS")
//        Log.d(TAG, pages.size.toString())
        for (i in 0 until pages.size) {
            val it = pages[i]
            val id = if (i < ids.size) ids[i] else null
//            Log.d(TAG, "$it $id")
            if (id != null && it != PageModel.FLUTTER) {
                val layout = viewPager.findViewById<LinearLayout>(R.id.test_id)
                val textView = layout?.findViewById<TextView>(R.id.button_tap)
//                Log.d(TAG, textView?.text.toString())
                textView?.text = value
//                Log.d(TAG, "$id $layout $textView")
                if (prevCounter != counter) {
                    // https://stackoverflow.com/a/45182852/8608146
                    viewPager.post {
                        viewPager.adapter?.notifyItemChanged(i)
                        Log.d(TAG, "Updated $i view")
                    }
                }
            }
        }
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
        flutterView?.detachFromFlutterEngine()
        super.onDestroy()
    }

    companion object {
        var flutterEngine: FlutterEngine? = null
        private const val CHANNEL = "com.example.view/increment"
        private const val EMPTY_MESSAGE = ""
        private const val PING = "ping"
    }

//    fun openHomeSettings() {
//        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Intent(android.provider.Settings.ACTION_HOME_SETTINGS)
//        } else {
//            TODO("VERSION.SDK_INT < N")
//            // This can be done I think https://github.com/Neamar/KISS/blob/68189e4f6cc23b907f2058567793ae455e1fe99c/app/src/main/java/fr/neamar/kiss/preference/DefaultLauncherPreference.java
//            // TODO: Test this across multiple devices first to decide what to keep
//        }
//        startActivity(intent)
//    }
}

// TODO: Test All my apps on emulators, Realme 5 Pro, Redmi note 4, Moto g5, Moto C
//  If possible a TV emulator
