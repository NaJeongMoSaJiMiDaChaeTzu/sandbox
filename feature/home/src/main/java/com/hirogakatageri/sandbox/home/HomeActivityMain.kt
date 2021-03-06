package com.hirogakatageri.sandbox.home

import android.content.Intent
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaelmarhic.quadrant.QuadrantConstants
import com.github.ajalt.timberkt.d
import com.hirogakatageri.core.activity.CoreViewModelActivity
import com.hirogakatageri.sandbox.home.databinding.HomeActivityMainBinding
import com.hirogakatageri.sandbox.navigation.NavigationUtil
import com.hirogakatageri.sandbox.navigation.NavigationUtil.RC_PROFILE
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class HomeActivityMain :
    CoreViewModelActivity<HomeActivityMainBinding, HomeActivityMainViewModel>() {

    override val viewModel: HomeActivityMainViewModel by viewModel()

    private val controller: HomeActivityMainController by inject {
        parametersOf(viewModel)
    }

    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
    }

    private val scrollListener: HomeActivityMainViewModel.HomeActivityEndlessScrollListener by lazy {
        viewModel.getScrollListener(layoutManager)
    }

    override fun createBinding(): HomeActivityMainBinding =
        HomeActivityMainBinding.inflate(layoutInflater)

    override suspend fun HomeActivityMainBinding.bind() {
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.setItemSpacingDp(12)
        recyclerView.setController(controller)

        viewModel.quadrant.observe { (name, bundle) ->
            when (name) {
                QuadrantConstants.PROFILE_ACTIVITY_MAIN -> {
                    val intent = NavigationUtil.createProfileIntent(
                        this@HomeActivityMain,
                        bundle
                    )
                    startActivityForResult(intent, RC_PROFILE)
                }
            }
        }

        viewModel.network.observe { isAvailable ->
            txtNetworkStatus.isVisible = !isAvailable
            if (isAvailable) viewModel.retryQuery()
        }

        viewModel.isLoading.observe {
            controller.isLoading = it

            if (it) recyclerView.removeOnScrollListener(scrollListener).also {
                d { "Removing scroll listener..." }
            }
            else recyclerView.addOnScrollListener(scrollListener).also {
                d { "Adding scroll listener..." }
            }
        }

        viewModel.userList.observe {
            controller.models = it
        }

        txtSearch.doAfterTextChanged { text ->
            if (text?.isNotBlank() == true) {
                scrollListener.resetState()
                recyclerView.removeOnScrollListener(scrollListener).also {
                    d { "Removing scroll listener..." }
                }
            }

            viewModel.search(text.toString())

            if (text.isNullOrBlank())
                recyclerView.addOnScrollListener(scrollListener).also {
                    d { "Adding scroll listener..." }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_PROFILE -> if (resultCode == RESULT_OK) viewModel.refreshCurrentList()
        }
    }
}