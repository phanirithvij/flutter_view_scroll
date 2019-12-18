package com.example.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import io.flutter.embedding.android.FlutterView

class ViewPagerAdaptor(private var context: Context) : PagerAdapter() {
    private lateinit var layoutInflater: LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Log.d("HELP", "Adding item $position")
        val p: PageModel = PageModel.values()[position]
        layoutInflater = LayoutInflater.from(context)
        val layout = layoutInflater.inflate(p.layout, container, false)
        container.addView(layout)
        if (p.name == PageModel.FLUTTER.name) {
            val flutterView = FlutterView(context, FlutterView.TransparencyMode.transparent)
            val existingView: FlutterView = layout.findViewById(R.id.flutter_view)
            val parentView = existingView.parent
            (existingView.parent as LinearLayout).removeView(existingView)
            (parentView as LinearLayout).addView(flutterView)
            flutterView.attachToFlutterEngine(MainActivity.flutterEngine!!)
        }
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // https://stackoverflow.com/a/26654608/8608146
        container.removeView(`object` as View?)
        println("removing... $`object`")
    }

}