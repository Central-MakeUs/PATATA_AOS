package com.cmc.presentation.map.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.cmc.common.base.BaseFragment
import com.cmc.common.base.GlobalNavigation
import com.cmc.common.constants.NavigationKeys
import com.cmc.common.util.DistanceFormatter
import com.cmc.design.component.BottomSheetDialog
import com.cmc.design.component.PatataAlert
import com.cmc.design.util.animateClickEffect
import com.cmc.domain.feature.location.Location
import com.cmc.domain.model.SpotCategory
import com.cmc.presentation.R
import com.cmc.presentation.databinding.ContentSheetMapSpotBinding
import com.cmc.presentation.databinding.FragmentAroundMeBinding
import com.cmc.presentation.map.manager.MarkerManager
import com.cmc.presentation.map.model.MapScreenLocation
import com.cmc.presentation.map.model.SpotWithMapUiModel
import com.cmc.presentation.map.viewmodel.AroundMeViewModel
import com.cmc.presentation.map.viewmodel.AroundMeViewModel.AroundMeSideEffect
import com.cmc.presentation.model.SpotCategoryItem
import com.cmc.presentation.util.toLatLng
import com.cmc.presentation.util.toLocation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AroundMeFragment: BaseFragment<FragmentAroundMeBinding>(R.layout.fragment_around_me), OnMapReadyCallback {

    private val viewModel: AroundMeViewModel by viewModels()

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var markerManager: MarkerManager

    private var dialog: com.google.android.material.bottomsheet.BottomSheetDialog? = null

    override fun initObserving() {
        repeatWhenUiStarted {
            launch {
                viewModel.state.collect { state -> updateUI(state) }
            }

            launch {
                viewModel.sideEffect.collectLatest { effect -> handleEffect(effect) }
            }
        }
    }

    override fun initView() {

        setAppBar()
        setMap()
        setCategory()
        setButton()

    }

    private fun updateUI(state: AroundMeViewModel.AroundMeState) {
        state.results.let {
            if (::markerManager.isInitialized) {
                markerManager.updateMarkersWithData(it)
            }
        }
        if (state.selectedTabPosition != null && state.selectedTabPosition != binding.tabMapFilter.getSelectedTabPosition()) {
            binding.tabMapFilter.setSelectedTabPosition(state.selectedTabPosition)
        }

        binding.layoutExploreThisArea.isVisible = state.exploreVisible
    }
    private fun handleEffect(effect: AroundMeSideEffect) {
        when (effect) {
            is AroundMeSideEffect.RequestLocationPermission -> { checkLocationRequest() }
            is AroundMeSideEffect.NavigateList -> { navigateList(effect.screenLocation) }
            is AroundMeSideEffect.NavigateAddLocation -> { navigateAddLocation(effect.location) }
            is AroundMeSideEffect.NavigateSearch -> { navigateSearch(effect.location) }
            is AroundMeSideEffect.NavigateSpotDetail -> { navigateSpotDetail(effect.spotId)}
            is AroundMeSideEffect.UpdateCurrentLocation -> { moveCameraPosition(effect.location) }
            is AroundMeSideEffect.ShowSpotBottomSheet -> { showSpotBottomSheet(effect.spot) }
        }
    }

    private fun setAppBar() {
        binding.appbar.setupAppBar(
            onHeadButtonClick = { viewModel.onClickHeadButton() },
            onBodyClick = { viewModel.onClickSearchBar(naverMap.cameraPosition.target) },
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
        viewModel.initCurrentLocation()

        with(naverMap) {
            markerManager = MarkerManager(requireContext(), this@with) { spot ->
                viewModel.onClickMarker(spot)
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
                viewModel.updateCurrentLocation(mapScreenLocation = getMapScreenLocation())
            }
        }
    }

    private fun getMapScreenLocation(): MapScreenLocation {
        val bounds = naverMap.contentBounds
        val southWest = bounds.southWest
        val northEast = bounds.northEast

        return MapScreenLocation(
            naverMap.cameraPosition.target.toLocation(),
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
        val cameraUpdate = CameraUpdate.scrollTo(location.toLatLng())
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
                    val bottomSheet = currentDialog.window?.decorView?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                    setOnDismissListener {
                        dialog = null
                    }
                    setOnShowListener {
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
        (activity as GlobalNavigation).navigateSelectLocation(location.latitude, location.longitude)
    }
    private fun navigateSearch(location: Location) {
        navigate(R.id.navigate_around_me_to_search_input, Bundle().apply {
            putDouble(NavigationKeys.Location.ARGUMENT_LATITUDE, location.latitude)
            putDouble(NavigationKeys.Location.ARGUMENT_LONGITUDE, location.longitude)
        })
    }
    private fun navigateSpotDetail(spotId: Int) { (activity as GlobalNavigation).navigateSpotDetail(spotId) }
    private fun navigateList(screenLocation: MapScreenLocation) {
        navigate(R.id.navigate_around_me_to_around_me_list, Bundle().apply {
            putDouble(NavigationKeys.Location.ARGUMENT_LATITUDE, screenLocation.targetLocation.latitude)
            putDouble(NavigationKeys.Location.ARGUMENT_LONGITUDE, screenLocation.targetLocation.longitude)
            putDouble(NavigationKeys.Location.ARGUMENT_MIN_LATITUDE, screenLocation.minLatitude)
            putDouble(NavigationKeys.Location.ARGUMENT_MIN_LONGITUDE, screenLocation.minLongitude)
            putDouble(NavigationKeys.Location.ARGUMENT_MAX_LATITUDE, screenLocation.maxLatitude)
            putDouble(NavigationKeys.Location.ARGUMENT_MAX_LONGITUDE, screenLocation.maxLongitude)
            putBoolean(NavigationKeys.Map.ARGUMENT_WITH_SEARCH, false)
        })
    }

    private val openAppSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) {
            checkLocationRequest()
        }
    }
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
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(context, "위치 권한이 허용되었습니다!", Toast.LENGTH_SHORT).show()
        } else {
            showLocationPermissionDialog()
        }
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
            "#$tag".also { tagView.findViewById<TextView>(com.cmc.design.R.id.tv_tag).text = it }
            layoutTagContainer.addView(tagView)
        }

        Glide.with(this.root)
            .load(spot.images.first())
            .into(ivSpotImage)

        ivSpotArchive.setOnClickListener {
            ivSpotArchive.animateClickEffect()
            viewModel.onClickSpotScrapButton(spot.spotId)
            ivSpotArchive.isSelected = ivSpotArchive.isSelected.not()
        }
        ivSpotImage.setOnClickListener { viewModel.onClickBottomSheetImage(spot.spotId) }
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }
}