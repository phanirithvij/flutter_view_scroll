package com.example.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


// https://stackoverflow.com/a/13437997/8608146
class CustomViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    private var _enabled: Boolean = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (_enabled) {
            return super.onTouchEvent(event)
        }
        performClick()
        return false
    }

    // TODO: Handle this safely by turning on talkback
    // This is called by some accessibility services so enable touch
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (_enabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    var pagingEnabled: Boolean
        get() = _enabled
        set(value) {
            this._enabled = value
        }

}