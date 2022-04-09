package com.goldman.test.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goldman.test.R
import com.goldman.test.adapter.FavoriteAdapter
import com.goldman.test.data.ApodDatabase
import com.goldman.test.data.ApodResponse
import com.goldman.test.databinding.ActivityFavoriteBinding
import com.goldman.test.network.RetrofitBuilder
import com.goldman.test.ui.home.snack
import com.goldman.test.utils.Status

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteAdapter
    private lateinit var binding: ActivityFavoriteBinding
    companion object {
        const val DATA = "data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val database by lazy { ApodDatabase.getDatabase(this) }

        favoriteViewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory(RetrofitBuilder.apiService, database.apodDao())
        )[FavoriteViewModel::class.java]

        getFavoriteListData()
    }

    private fun getFavoriteListData() {
        favoriteViewModel.getFavoriteListData().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null) {
                            binding.textviewNoData.visibility =
                                if (resource.data.isNullOrEmpty()) View.VISIBLE else
                                    View.GONE

                            val data = resource.data
                            setUiData(data)
                        } else
                            Toast.makeText(
                                this,
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_LONG
                            ).show()
                    }
                    Status.ERROR -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setUiData(list: List<ApodResponse>) {
        setRecyclerView(list)
    }

    private fun setRecyclerView(list: List<ApodResponse>) {
        adapter = FavoriteAdapter(list as MutableList<ApodResponse>, ::onItemClick, ::onItemDelete, ::showEmptyView, this)
        adapter.setHasStableIds(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.rvData.layoutManager = layoutManager
        binding.rvData.adapter = adapter
    }

    private fun onItemClick(response: ApodResponse) {
        val intent = Intent()
        intent.putExtra(DATA, response)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun onItemDelete(response: ApodResponse, position: Int) {
       favoriteViewModel.deleteFavoriteItem(response).observe(this, Observer {
           it?.let { resource ->
               when (resource.status) {
                   Status.SUCCESS -> {
                       binding.root.snack(getString(R.string.data_deleted))
                       adapter.deleteItem(position)
                   }
                   Status.ERROR -> Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
               }
           }
       })
    }

    private fun showEmptyView() {
        binding.textviewNoData.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}