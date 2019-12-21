package com.example.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter


private const val TAG = "ViewPagerAdaptor"

class ViewPagerAdaptor(private var instance: MainActivity) : PagerAdapter() {
    private lateinit var layoutInflater: LayoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return 3
    }

    override fun instantiateItem(pagerContainer: ViewGroup, position: Int): Any {
        Log.d(TAG, "Adding a page to viewpager at index $position")
        val p: PageModel = PageModel.values()[position]
        layoutInflater = LayoutInflater.from(instance.applicationContext)
        val layout = layoutInflater.inflate(p.layout, pagerContainer, false)
        pagerContainer.addView(layout)
        // We must initialize these here instead of in the onCreate method
        // As only after the viewpager loads the view, we can get the view's reference
        // And this is where the layout is being created
        when (p.name) {
            // If the layout is flutter_view
            PageModel.FLUTTER.name -> instance.onFlutterViewInit(layout)
            // If the layout is android_view
            PageModel.ANDROID.name -> instance.onAndroidViewInit(layout, p.id)
            // If it is page 0
            PageModel.ANDROID0.name -> instance.onAndroidViewInit(layout, p.id)
        }
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // https://stackoverflow.com/a/26654608/8608146
        container.removeView(`object` as View?)
        println("removing... $`object`")
    }
}