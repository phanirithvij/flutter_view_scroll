package com.example.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.flutter.embedding.android.FlutterView

private const val TAG = "ViewPagerAdaptor"

class ViewPagerAdaptor(private var instance: MainActivity) : PagerAdapter() {
    private lateinit var layoutInflater: LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d(TAG, "Adding a page to viewpager at index $position")
        val p: PageModel = PageModel.values()[position]
        layoutInflater = LayoutInflater.from(instance.applicationContext)
        val layout = layoutInflater.inflate(p.layout, container, false)
        container.addView(layout)
        when (p.name) {
            // We must initialize these here instead of in the onCreate method
            // As only after the viewpager loads the view, we can get the view's reference
            // And this is where the layout is being created
            PageModel.FLUTTER.name -> {
                // If the layout is flutter_view

                // Initialize the flutterview
                val flutterView = FlutterView(instance.applicationContext, FlutterView.TransparencyMode.transparent)
                flutterView.id = R.id.flutter_view

                // Add this view i.e. the newly created transparent flutterview to the R.layout.flutter_view
                (layout as LinearLayout).addView(flutterView)

                // Attach this newly created flutterview to the running instance of the engine
                flutterView.attachToFlutterEngine(MainActivity.flutterEngine!!)
                instance.flutterView = flutterView
            }
            PageModel.ANDROID.name -> {
                // If the layout is android_view
                val fab: FloatingActionButton = layout.findViewById(R.id.button)
                fab.setOnClickListener { instance.sendAndroidIncrement() }
            }
        }
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // https://stackoverflow.com/a/26654608/8608146
        container.removeView(`object` as View?)
        println("removing... $`object`")
    }

}