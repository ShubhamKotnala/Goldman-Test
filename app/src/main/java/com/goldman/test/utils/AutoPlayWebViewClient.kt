package com.goldman.test.utils

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.webkit.WebView
import android.webkit.WebViewClient

class AutoPlayWebViewClient : WebViewClient() {

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        // mimic onClick() event on the center of the WebView
        val delta: Long = 100
        val downTime = SystemClock.uptimeMillis()
        val x = (view.left + view.width / 2).toFloat()
        val y = (view.top + view.height / 2).toFloat()
        val tapDownEvent =
            MotionEvent.obtain(downTime, downTime + delta, MotionEvent.ACTION_DOWN, x, y, 0)
        tapDownEvent.source = InputDevice.SOURCE_CLASS_POINTER
        val tapUpEvent =
            MotionEvent.obtain(downTime, downTime + delta + 2, MotionEvent.ACTION_UP, x, y, 0)
        tapUpEvent.source = InputDevice.SOURCE_CLASS_POINTER
        view.dispatchTouchEvent(tapDownEvent)
        view.dispatchTouchEvent(tapUpEvent)
    }
}