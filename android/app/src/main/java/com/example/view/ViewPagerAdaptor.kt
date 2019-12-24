package com.example.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


private const val TAG = "ViewPagerAdaptor"

// https://stackoverflow.com/a/54643817/8608146
// Look at it if needed, haven't looked at it yet
class ViewPagerAdaptor2(private var instance: MainActivity) : RecyclerView.Adapter<ViewPagerAdaptor2.ViewHolder>() {

    private lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        val layout = layoutInflater.inflate(R.layout.adapter_view, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return instance.pages.size
    }

    // https://stackoverflow.com/questions/54643379/use-of-viewpager2-in-android#comment105116718_54643817
    override fun onViewRecycled(holder: ViewHolder) {
        (holder.itemView as ViewGroup).removeAllViews()
        super.onViewRecycled(holder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "HELP meme ${holder.itemView}")
        val model: PageModel = instance.pages[position]
        val layout = layoutInflater.inflate(model.layout, (holder.itemView as ViewGroup), false)
        Log.d(TAG, layout.toString())
        // Add the new view to the holder view
        // We must initialize these here instead of in the onCreate method
        // As only after the viewpager loads the view, we can get the view's reference
        // And this is where the layout is being created
        Log.d(TAG, model.toString())
        when (model.name) {
            // If the layout is flutter_view
            PageModel.FLUTTER.name -> instance.onFlutterViewInit(layout)
            // If the layout is android_view
            PageModel.ANDROID.name -> instance.onAndroidViewInit(layout)
        }

        // Add it to the layout finally
        // https://stackoverflow.com/a/55137213/8608146
        holder.itemView.addView(layout)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}

