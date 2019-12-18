package com.example.view

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.StringCodec
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    var flutterView: FlutterView? = null
    private var counter = 0
    private var messageChannel: BasicMessageChannel<String>? = null

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
        // on device orientation changed
        // TODO: data on the flutter side is lost when rebuilt
        // If not recreated the flutterView becomes black
        recreate()
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


        val viewPager: ViewPager = findViewById(R.id.pages)
        viewPager.adapter = ViewPagerAdaptor(this)

        Log.d(TAG, viewPager.childCount.toString())


        messageChannel = BasicMessageChannel(flutterEngine!!.dartExecutor, CHANNEL, StringCodec.INSTANCE)
        messageChannel!!.setMessageHandler { _: String?, reply: BasicMessageChannel.Reply<String> ->
            onFlutterIncrement()
            reply.reply(EMPTY_MESSAGE)
        }
    }

    fun sendAndroidIncrement() {
        Log.d(TAG, "++1")
        messageChannel!!.send(PING)
    }

    private fun onFlutterIncrement() {
        counter++
        val textView = findViewById<TextView>(R.id.button_tap)
        val value = "Flutter button tapped " + counter + if (counter == 1) " time" else " times"
        textView.text = value
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
        private const val CHANNEL = "increment"
        private const val EMPTY_MESSAGE = ""
        private const val PING = "ping"
    }
}