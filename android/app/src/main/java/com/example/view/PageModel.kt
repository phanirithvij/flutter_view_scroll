package com.example.view

enum class PageModel(var layout: Int) {
    ANDROID0(R.layout.android_view, R.id.android_view0),
    FLUTTER(R.layout.flutter_view),
    ANDROID(R.layout.android_view, R.id.android_view);

    var id: Int? = null

    constructor(layout: Int, idx: Int) {
        this.layout = layout
        id = idx
    }
}