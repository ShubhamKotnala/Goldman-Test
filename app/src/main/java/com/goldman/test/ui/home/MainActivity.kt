package com.goldman.test.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings.PluginState
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.goldman.test.R
import com.goldman.test.data.ApodDatabase
import com.goldman.test.data.ApodResponse
import com.goldman.test.databinding.ActivityMainBinding
import com.goldman.test.network.RetrofitBuilder
import com.goldman.test.ui.favorite.FavoriteActivity
import com.goldman.test.utils.AutoPlayWebViewClient
import com.goldman.test.utils.Status
import com.goldman.test.utils.Utils
import com.goldman.test.utils.Utils.DATE_FORMAT
import com.goldman.test.utils.Utils.VIDEO
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var apodResponse: ApodResponse = ApodResponse()
    private var cal = Calendar.getInstance()
    private lateinit var mainViewModel: MainViewModel
    private val REQ_FAVORITE_LIST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /** initialising view model */
        val database by lazy { ApodDatabase.getDatabase(this) }

        mainViewModel = ViewModelProvider(this, MainViewModelFactory(RetrofitBuilder.apiService, database.apodDao()))[MainViewModel::class.java]

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { showDateSelector() }
        binding.contentMain.btnAddToFav.setOnClickListener { addToFavorite() }
    }

    private fun addToFavorite() {
        if (apodResponse.date.isEmpty()) return

        mainViewModel.addToFavorite(apodResponse).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.root.snack(getString(R.string.added_to_fav_succes_msg))

                        binding.contentMain.btnAddToFav.setImageResource(R.drawable.ic_favorite_svg)
                    }
                    Status.ERROR -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            if (Utils.isNetworkConnected(this))
                getDataFromApi()
            else Utils.showNoConnectivityDialog(this)
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu.findItem(R.id.action_theme) as MenuItem
        item.setActionView(R.layout.day_night_toggle)
        val switchAB = item.actionView.findViewById<ToggleButton>(R.id.theme_toggle)
        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> switchAB.isChecked = true
            Configuration.UI_MODE_NIGHT_NO -> switchAB.isChecked = false
        }

        switchAB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorite -> {
                openFavoriteListActivity()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openFavoriteListActivity() {
        startActivityForResult(Intent(this, FavoriteActivity::class.java), REQ_FAVORITE_LIST)
    }

    private fun showDateSelector() {
        DatePickerDialog(this@MainActivity,
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun getDataFromApi() {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        mainViewModel.callGetApodData(sdf.format(cal.time)).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null) {
                            apodResponse = resource.data
                            setUIData(apodResponse)
                        }
                        showLoader(false)
                    }
                    Status.ERROR -> {
                        showLoader(false)
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        showLoader(true)
                        showSelectDateLabel(false)
                    }
                }
            }
        }
    }

    private fun showLoader(toShow: Boolean) {
        binding.loading.visibility = if (toShow) View.VISIBLE else View.GONE
    }

    private fun showSelectDateLabel(toShow: Boolean) {
        binding.textviewNoData.visibility = if (toShow && apodResponse.date.isNotEmpty()) View.VISIBLE else View.GONE
        binding.arrow.visibility = if (toShow && apodResponse.date.isNotEmpty()) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_FAVORITE_LIST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                data.apply {
                    val apodResponse = getSerializableExtra(FavoriteActivity.DATA) as ApodResponse
                    setUIData(apodResponse)
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUIData(response: ApodResponse) {
        this.apodResponse = response

        showSelectDateLabel(false)

        binding.contentMain.cardBanner.visibility = View.VISIBLE
        binding.contentMain.btnAddToFav.visibility = View.VISIBLE
        binding.contentMain.viewDate.visibility = View.VISIBLE
        binding.contentMain.tvDescription.text = apodResponse.explanation
        binding.contentMain.tvTitle.text = apodResponse.title
        binding.contentMain.tvDate.text = Utils.parseDate(apodResponse.date)

        if (apodResponse.mediaType == VIDEO) {
            binding.contentMain.webView.visibility = View.VISIBLE
            binding.contentMain.apodImage.visibility = View.GONE

            binding.contentMain.webView.settings.javaScriptEnabled = true
            binding.contentMain.webView.settings.pluginState = PluginState.ON
            binding.contentMain.webView.loadUrl( apodResponse.url + "?autoplay=1&vq=small")
            binding.contentMain.webView.webViewClient = AutoPlayWebViewClient()
        } else {
            binding.contentMain.webView.visibility = View.GONE
            binding.contentMain.apodImage.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(apodResponse.url)
                .centerCrop()
                .placeholder(Utils.getCircularProgress(this))
                .into(binding.contentMain.apodImage)
        }

        mainViewModel.isDataExists(apodResponse).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val isFav: Boolean? = resource.data
                        if (isFav == true)
                            binding.contentMain.btnAddToFav.setImageResource(R.drawable.ic_favorite_svg)
                         else
                            binding.contentMain.btnAddToFav.setImageResource(R.drawable.ic_not_favorite)
                    }
                }
            }
        }
    }
}


fun View.snack(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}