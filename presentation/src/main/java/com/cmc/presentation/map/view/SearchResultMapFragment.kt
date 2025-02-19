package com.cmc.presentation.map.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cmc.common.base.BaseFragment
import com.cmc.common.constants.NavigationKeys
import com.cmc.common.util.DistanceFormatter
import com.cmc.design.component.BottomSheetDialog
import com.cmc.design.component.PatataAlert
import com.cmc.domain.feature.location.Location
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetMapSpotBinding
import com.cmc.presentation.databinding.FragmentSearchResultMapBinding
import com.cmc.presentation.map.manager.MarkerManager
import com.cmc.presentation.map.model.MapScreenLocation
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.map.viewmodel.SearchResultMapViewModel
import com.cmc.presentation.map.viewmodel.SearchResultMapViewModel.SearchResultMapSideEffect
import com.cmc.presentation.map.viewmodel.SearchResultMapViewModel.SearchResultMapState
import com.cmc.presentation.model.SpotCategoryItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchResultMapFragment: BaseFragment<FragmentSearchResultMapBinding>(R.layout.fragment_search_result_map),
    OnMapReadyCallback {

    private val viewModel: SearchResultMapViewModel by viewModels()

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var markerManager: MarkerManager

    private var dialog: com.google.android.material.bottomsheet.BottomSheetDialog? = null

    override fun initObserving() {
        repeatWhenUiStarted {
            launch { viewModel.state.collect { state -> updateUI(state) }  }
            launch { viewModel.sideEffect.collectLatest { effect -> handleSideEffect(effect) } }
        }
    }

    override fun initView() {
        arguments?.let {
            val latitude = it.getDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE)
            val longitude = it.getDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE)
            val keyword = it.getString(NavigationKeys.Search.ARGUMENT_KEYWORD)

            viewModel.initCurrentTargetLocation(Location(latitude, longitude))
            keyword?.let { text -> viewModel.setKeyword(text) }
        }

        setAppBar()
        setMap()
        setCategory()
        setButton()
    }
    private var previousState: SearchResultMapState? = null
    private fun updateUI(state: SearchResultMapState) {
        if (previousState?.spots != state.spots && ::markerManager.isInitialized) {
            markerManager.updateMarkersWithData(state.spots)
        }
        if (state.selectedTabPosition != null && state.selectedTabPosition != binding.tabMapFilter.getSelectedTabPosition()) {
            binding.tabMapFilter.setSelectedTabPosition(state.selectedTabPosition)
        }
        if (previousState?.keyword != state.keyword) {
            binding.appbar.setSearchText(state.keyword)
        }
        if (previousState?.exploreVisible != state.exploreVisible) {
            binding.layoutExploreThisArea.isVisible = state.exploreVisible
        }
        previousState = state
    }
    private fun handleSideEffect(effect: SearchResultMapSideEffect) {
        when (effect) {
            is SearchResultMapSideEffect.RequestLocationPermission -> {}
            is SearchResultMapSideEffect.NavigateAddLocation -> { navigateAddLocation(effect.location) }
            is SearchResultMapSideEffect.NavigateSearch -> { navigateSearchInput(effect.location) }
            is SearchResultMapSideEffect.UpdateCurrentLocation -> { moveCameraPosition(effect.location) }
            is SearchResultMapSideEffect.ShowNoResultAlert -> { showSnackBar(effect.message) }
            is SearchResultMapSideEffect.NavigateAroundMe -> { navigateAroundMe() }
            is SearchResultMapSideEffect.ShowSpotBottomSheet -> { showSpotBottomSheet(effect.spot) }
        }
    }

    private fun setAppBar() {
        binding.appbar.setupAppBar(
            onBodyClick = { viewModel.onClickSearchBar(naverMap.cameraPosition.target) },
            onFootButtonClick = { viewModel.onClickCancelButton() },
            searchBarDisable = true
        )
    }
    private fun setMap() {
        mapView = binding.viewMap
        mapView.getMapAsync(this)
    }
    private fun setCategory() {
        val categoryTabs: List<Pair<String, Drawable?>> = SpotCategory.entries
            .map {
                val item = SpotCategoryItem(it)
                getString(item.getName()) to item.getIcon()?.let { resId ->
                    ContextCompat.getDrawable(requireContext(), resId)
                }
            }

        binding.tabMapFilter.apply {
            setTabSelectedListener { position ->
                viewModel.onClickCategoryTab(position)
            }
            setTabList(categoryTabs)
        }
    }
    private fun setButton() {
        with(binding) {
            ivAddLocation.setOnClickListener { viewModel.onClickAddLocationButton(naverMap.cameraPosition.target) }
            ivCurrentLocation.setOnClickListener { viewModel.getCurrentLocation() }
            layoutExploreThisArea.setOnClickListener { viewModel.onClickExploreThisArea() }
        }
    }


    override fun onMapReady(map: NaverMap) {
        naverMap = map

        viewModel.searchSpotByKeyword()

        with(naverMap) {
            markerManager = MarkerManager(requireContext(), this@with) {
                spot -> viewModel.onClickMarker(spot)
            }

            uiSettings.apply {
                isZoomControlEnabled = false
                isScaleBarEnabled = false
                isLogoClickEnabled = false
            }

            addOnCameraChangeListener { reason, _ ->
                if (reason == CameraUpdate.REASON_GESTURE) { viewModel.movedCameraPosition() }
            }
            addOnCameraIdleListener {
                viewModel.updateMapScreenLocation(getMapScreenLocation())
            }
        }
    }
    private fun getMapScreenLocation(): MapScreenLocation {
        val bounds = naverMap.contentBounds
        val southWest = bounds.southWest
        val northEast = bounds.northEast

        return MapScreenLocation(
            southWest.latitude,
            southWest.longitude,
            northEast.latitude,
            northEast.longitude
        )
    }
    private fun updateButtonPositions(view: View, isDismiss: Boolean = false) {
        if (isAdded) {
            with(binding) {
                val bottomSheetTop = view.top ?: return
                val navigationTop = viewMap.bottom
                val positionY =
                    if (bottomSheetTop >= navigationTop || isDismiss) navigationTop else bottomSheetTop

                val currentLocationY =
                    positionY - ivCurrentLocation.measuredHeight - ivCurrentLocation.marginBottom
                val addLocationY =
                    currentLocationY - ivAddLocation.measuredHeight - ivAddLocation.marginBottom
                val exploreThisAreaY = positionY - layoutExploreThisArea.measuredHeight - layoutExploreThisArea.marginBottom

                ivAddLocation.y = addLocationY.toFloat()
                ivCurrentLocation.y = currentLocationY.toFloat()
                layoutExploreThisArea.apply {
                    if (isVisible) y = exploreThisAreaY.toFloat()
                }
            }
        }
    }
    private fun moveCameraPosition(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val cameraUpdate = CameraUpdate.scrollTo(latLng)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun showLocationPermissionDialog() {
        PatataAlert(requireContext())
            .title("위치 권한 요청")
            .content("위치 권한 설정 창으로 이동할까요?")
            .multiButton {
                leftButton("네") {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", activity?.packageName, null)
                    )
                    openAppSettingsLauncher.launch(intent)
                }
                rightButton("아니오") {
                    Toast.makeText(requireContext(), "위치 권한이 꼭 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }.show()
    }
    private fun showSpotBottomSheet(spot: SpotWithMapUiModel) {
        val contentSheetMapSpot = ContentSheetMapSpotBinding.inflate(LayoutInflater.from(requireContext()))

        dialog = BottomSheetDialog(requireContext(), false)
            .bindBuilder(contentSheetMapSpot, false) { currentDialog ->
                with(currentDialog) {
                    setBottomSheetViewBind(spot)

                    val bottomSheet = window?.decorView?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    setOnDismissListener {
                        dialog = null
                    }
                    currentDialog.setOnShowListener {
                        bottomSheet?.let { updateButtonPositions(it)}
                    }
                    setOnDismissListener {
                        bottomSheet?.let { updateButtonPositions(it, true)}
                    }
                    behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            updateButtonPositions(bottomSheet)
                        }
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            when (newState) {
                                BottomSheetBehavior.STATE_HIDDEN -> updateButtonPositions(bottomSheet)
                                BottomSheetBehavior.STATE_EXPANDED -> updateButtonPositions(bottomSheet)
                            }
                        }
                    })

                    show()
                }
            }.setOutSideTouchable(requireActivity())
    }

    private fun navigateAddLocation(location: Location) {
        navigate(R.id.navigate_search_result_map_to_select_location, Bundle().apply {
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE, location.latitude)
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE, location.longitude)
        })
    }
    private fun navigateSearchInput(location: Location) {
        navigate(R.id.navigate_search_result_map_to_search_input, Bundle().apply{
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LATITUDE, location.latitude)
            putDouble(NavigationKeys.AddSpot.ARGUMENT_LONGITUDE, location.longitude)
        })
    }
    private fun navigateAroundMe() { navigate(R.id.navigate_search_result_map_to_around_me) }

    private fun checkLocationRequest() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissions.all {
                ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
            }) {
            locationPermissionRequest.launch(permissions)
        }
    }
    private val openAppSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 설정창에서 뒤로가기로 앱에 돌아오는 경우 CANCELED로 처리됨
        if (result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) {
            checkLocationRequest()
        }
    }
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(context, "위치 권한이 허용되었습니다!", Toast.LENGTH_SHORT).show()
        } else {
            showLocationPermissionDialog()
        }
    }

    private fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).apply {
            setBackgroundTint(ContextCompat.getColor(requireContext(), com.cmc.design.R.color.gray_100))
            setTextColor(ContextCompat.getColor(requireContext(), com.cmc.design.R.color.blue_20))
        }

        // 텍스트 중앙 정렬
        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        textView.gravity = Gravity.CENTER

        snackbar.show()
    }

    private fun ContentSheetMapSpotBinding.setBottomSheetViewBind(spot: SpotWithMapUiModel) {
        val category = SpotCategoryItem(SpotCategory.fromId(spot.categoryId))
        tvRecommendLabel.isVisible = SpotCategory.isRecommended(spot.categoryId)
        tvSpotTitle.text = spot.spotName
        tvCategory.text = getString(category.getName())
        category.getIcon()?.let { ivCategory.setImageResource(it) }
        ivSpotArchive.isSelected = spot.isScraped
        tvDistance.text = DistanceFormatter.formatDistance(spot.distance)
        "${spot.address} ${spot.addressDetail}".also { tvSpotLocation.text = it }

        layoutTagContainer.removeAllViews()
        spot.tags.forEach { tag ->
            val tagView = LayoutInflater.from(context).inflate(com.cmc.design.R.layout.view_tag_blue, layoutTagContainer, false)
            tagView.findViewById<TextView>(com.cmc.design.R.id.tv_tag).text = tag
            layoutTagContainer.addView(tagView)
        }

        Glide.with(this.root)
            .load(spot.images.first())
            .into(ivSpotImage)
        ivSpotArchive.setOnClickListener {
            viewModel.onClickSpotScrapButton(spot.spotId)
            ivSpotArchive.isSelected = ivSpotArchive.isSelected.not()
        }
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }
}