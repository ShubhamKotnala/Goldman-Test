package com.goldman.test.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.goldman.test.R
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    const val DATE_FORMAT = "yyyy-MM-dd"
    const val VIDEO = "video"

    fun parseDate(time: String?): String? {
        var datetime: String? = null
        val inputFormat: DateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        val d = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        try {
            val convertedDate: Date = inputFormat.parse(time)
            datetime = d.format(convertedDate)
        } catch (e: ParseException) {
        }
        return datetime
    }

    fun getCircularProgress(context: Context) : CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        return circularProgressDrawable
    }

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            else -> false
        }
    }

    fun showNoConnectivityDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.no_connection))
        builder.setMessage(context.getString(R.string.no_connection_msg))
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->

        }

        builder.show()
    }

    fun rotateView(context: Context, view: View) {
        val rotation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        rotation.fillAfter = true;
        view.startAnimation(rotation);
    }
}