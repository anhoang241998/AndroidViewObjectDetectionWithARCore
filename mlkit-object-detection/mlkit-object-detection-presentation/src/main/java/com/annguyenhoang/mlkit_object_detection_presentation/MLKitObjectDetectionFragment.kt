package com.annguyenhoang.mlkit_object_detection_presentation

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.hardware.Camera
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.camera.core.ImageCapture
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.annguyenhoang.core_ui.R
import com.annguyenhoang.core_ui.camera.CameraSource
import com.annguyenhoang.core_ui.camera.CameraSourcePreview
import com.annguyenhoang.core_ui.camera.GraphicOverlay
import com.annguyenhoang.core_ui.ext.showToast
import com.annguyenhoang.core_ui.fragment_binding.ViewBindingFragment
import com.annguyenhoang.core_ui.permission.CameraPermissionHandlerImpl
import com.annguyenhoang.core_ui.permission.PermissionHandler
import com.annguyenhoang.core_ui.utils.PreferenceUtils
import com.annguyenhoang.mlkit_object_detection_presentation.databinding.FragmentMlkitObjectDetectionBinding
import com.annguyenhoang.mlkit_object_detection_presentation.model.BoxWithText
import com.annguyenhoang.mlkit_object_detection_presentation.model.WorkflowModel
import com.annguyenhoang.mlkit_object_detection_presentation.objectdetection.MultiObjectProcessor
import com.annguyenhoang.mlkit_object_detection_presentation.objectdetection.ProminentObjectProcessor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.common.base.Objects
import com.google.common.collect.ImmutableList
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import java.io.IOException

class MLKitObjectDetectionFragment : ViewBindingFragment<FragmentMlkitObjectDetectionBinding>(),
    View.OnClickListener {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMlkitObjectDetectionBinding
        get() = FragmentMlkitObjectDetectionBinding::inflate

    private val cameraPermissionHandler by inject<PermissionHandler>(
        qualifier = named<CameraPermissionHandlerImpl>()
    )

    private val viewModel: MLKitObjectDetectionViewModel by viewModel()

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle Permission granted/rejected
        var allPermissionGranted = true
        permissions.entries.forEach { permission ->
            val (permissionName, permissionStatus) = permission
            if (permissionName in cameraPermissionHandler.requiredCameraPermissions && !permissionStatus) {
                allPermissionGranted = false
            }
        }

        if (!allPermissionGranted) {
            context?.showToast(getString(R.string.permission_request_denied))
        } else {
//            startCamera()
        }
    }

    private var isShowingCameraForCapture = false
    private var imageCapture: ImageCapture? = null

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var settingsButton: View? = null
    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var searchButton: ExtendedFloatingActionButton? = null
    private var searchButtonAnimator: AnimatorSet? = null
    private var searchProgressBar: ProgressBar? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null
    private var searchEngine: SearchEngine? = null

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var bottomSheetScrimView: BottomSheetScrimView? = null
    private var productRecyclerView: RecyclerView? = null
    private var bottomSheetTitleView: TextView? = null
    private var objectThumbnailForBottomSheet: Bitmap? = null
    private var slidingSheetUpFromHiddenState: Boolean = false

    override fun initViews() {
        super.initViews()

        searchEngine = SearchEngine(requireActivity().applicationContext)

        preview = view?.findViewById(com.annguyenhoang.mlkit_object_detection_presentation.R.id.camera_preview)
        graphicOverlay = view?.findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay)?.apply {
            setOnClickListener(this@MLKitObjectDetectionFragment)
            cameraSource = CameraSource(this)
        }
        promptChip = view?.findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(
                requireContext(),
                com.annguyenhoang.mlkit_object_detection_presentation.R.animator.bottom_prompt_chip_enter
            ) as AnimatorSet).apply {
                setTarget(promptChip)
            }
        searchButton = view?.findViewById<ExtendedFloatingActionButton>(R.id.product_search_button)?.apply {
            setOnClickListener(this@MLKitObjectDetectionFragment)
        }
        searchButtonAnimator =
            (AnimatorInflater.loadAnimator(
                requireContext(),
                com.annguyenhoang.mlkit_object_detection_presentation.R.animator.search_button_enter
            ) as AnimatorSet).apply {
                setTarget(searchButton)
            }
        searchProgressBar = view?.findViewById(R.id.search_progress_bar)
        setUpBottomSheet()
        view?.findViewById<View>(com.annguyenhoang.mlkit_object_detection_presentation.R.id.close_button)
            ?.setOnClickListener(this)
        flashButton =
            view?.findViewById<View>(com.annguyenhoang.mlkit_object_detection_presentation.R.id.flash_button)?.apply {
                setOnClickListener(this@MLKitObjectDetectionFragment)
            }
        settingsButton =
            view?.findViewById<View>(com.annguyenhoang.mlkit_object_detection_presentation.R.id.settings_button)
                ?.apply {
                    setOnClickListener(this@MLKitObjectDetectionFragment)
                }
        setUpWorkflowModel()
    }

    override fun onResume() {
        super.onResume()

        workflowModel?.markCameraFrozen()
        settingsButton?.isEnabled = true
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(
            if (PreferenceUtils.isMultipleObjectsMode(requireContext())) {
                MultiObjectProcessor(graphicOverlay!!, workflowModel!!)
            } else {
                ProminentObjectProcessor(graphicOverlay!!, workflowModel!!)
            }
        )
        workflowModel?.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
        searchEngine?.shutdown()
    }

    override fun onBackPressed() {
        if (bottomSheetBehavior?.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_HIDDEN)
        } else {
            super.onBackPressed()
        }
    }

    override fun initControls() {
        super.initControls()

//        binding.imgSampleOne.setOnThrottleClicked {
//            setViewAndStartDetect(
//                getSampleImageFromDrawable(R.drawable.demo_img1)
//            )
//        }
//
//        binding.imgSampleTwo.setOnThrottleClicked {
//            setViewAndStartDetect(
//                getSampleImageFromDrawable(R.drawable.demo_img2)
//            )
//        }
//
//        binding.imgSampleThree.setOnThrottleClicked {
//            setViewAndStartDetect(
//                getSampleImageFromDrawable(R.drawable.demo_img3)
//            )
//        }
//
//        binding.captureImageFab.setOnThrottleClicked {
//            if (cameraPermissionHandler.allPermissionsGranted().not()) {
//                cameraPermissionHandler.requestPermissions(permissionsLauncher)
//            }
//
//            if (!isShowingCameraForCapture) {
//                startCamera()
//                return@setOnThrottleClicked
//            }
//
//            val context = context
//            val imageCapture = imageCapture
//            if (context == null || imageCapture == null) return@setOnThrottleClicked
//
//            viewModel.takePhoto(
//                mainExecutors = ContextCompat.getMainExecutor(requireContext()),
//                imageCapture = imageCapture,
//                onImageCapturedSuccess = {
//                    binding.cameraImagePreview.gone()
//                }
//            )
//        }
    }

    private fun observeTakenPhoto() {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.photoTaken.collect { curTakenPhotos ->
//                    curTakenPhotos.lastOrNull()?.let { lastPhoto ->
//                        setViewAndStartDetect(photo = lastPhoto)
//                    }
//                }
//            }
//        }
    }

//    private fun setViewAndStartDetect(photo: Bitmap) {
//        if (binding.cameraImagePreview.isShow()) {
//            binding.cameraImagePreview.gone()
//        }
//
//        isShowingCameraForCapture = false
//        binding.imageView.load(photo)
//        binding.tvPlaceholder.hide()
//
//        viewModel.runObjectDetection(photo) { detectedResults ->
//            val visualizedResult = drawDetectionResult(photo, detectedResults)
//            binding.imageView.load(visualizedResult)
//        }
//    }

    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<BoxWithText>
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val paint = Paint()
        paint.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // Draw bounding box
            paint.color = Color.RED
            paint.strokeWidth = 8f
            paint.style = Paint.Style.STROKE
            val box = it.box
            canvas.drawRect(box, paint)

            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.color = Color.YELLOW
            paint.strokeWidth = 2f

            paint.textSize = 96f
            paint.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = paint.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < paint.textSize) {
                paint.textSize = fontSize
            }

            var margin = (box.width() - tagSize.width()) / 2f
            if (margin < 0f) {
                margin = 0f
            }

            canvas.drawText(
                it.text,
                box.left + margin,
                box.top + tagSize.height().times(1f),
                paint
            )
        }

        return outputBitmap
    }

//    private fun drawDetectionResult(
//        bitmap: Bitmap,
//        detectionResults: List<DetectionResult>
//    ): Bitmap {
//        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
//        val canvas = Canvas(outputBitmap)
//        val paint = Paint()
//        paint.textAlign = Paint.Align.LEFT
//
//        detectionResults.forEach {
//            // Draw bounding box
//            paint.color = Color.RED
//            paint.strokeWidth = 8f
//            paint.style = Paint.Style.STROKE
//            val box = it.boundingBox
//            canvas.drawRect(box, paint)
//
//            val tagSize = Rect(0, 0, 0, 0)
//
//            // calculate the right font size
//            paint.style = Paint.Style.FILL_AND_STROKE
//            paint.color = Color.YELLOW
//            paint.strokeWidth = 2f
//
//            paint.textSize = 96f
//            paint.getTextBounds(it.text, 0, it.text.length, tagSize)
//            val fontSize: Float = paint.textSize * box.width() / tagSize.width()
//
//            // adjust the font size so texts are inside the bounding box
//            if (fontSize < paint.textSize) {
//                paint.textSize = fontSize
//            }
//
//            var margin = (box.width() - tagSize.width()) / 2f
//            if (margin < 0f) {
//                margin = 0f
//            }
//
//            canvas.drawText(
//                it.text,
//                box.left + margin,
//                box.top + tagSize.height().times(1f),
//                paint
//            )
//        }
//
//        return outputBitmap
//    }

//    private fun startCamera() {
//        try {
//            val context = requireContext()
//            val cameraProviderFuture = ProcessCameraProvider.getInstance(
//                context
//            )
//
//            binding.cameraImagePreview.show()
//
//            cameraProviderFuture.addListener({
//                // Used to bind the lifecycle of cameras to the lifecycle owner
//                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//                // Preview
//                val preview = Preview.Builder()
//                    .build()
//                    .also {
//                        it.setSurfaceProvider(binding.cameraImagePreview.surfaceProvider)
//                    }
//
//                // initialize image capture
//                imageCapture = ImageCapture.Builder()
//                    .build()
//
//                // Select back camera as a default
//                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//                // Unbind use cases before rebinding
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(
//                    viewLifecycleOwner,
//                    cameraSelector,
//                    preview,
//                    imageCapture,
//                )
//            }, ContextCompat.getMainExecutor(context))
//
//            isShowingCameraForCapture = true
//        } catch (e: IllegalStateException) {
//            Timber.e("There are no context attach to this fragment!")
//        } catch (e: Exception) {
//            Timber.e(e)
//        }
//    }

    private fun getSampleImageFromDrawable(@DrawableRes drawableId: Int): Bitmap {
        return BitmapFactory.decodeResource(
            resources,
            drawableId,
            BitmapFactory.Options().apply {
                inMutable = true
            }
        )
    }

    override fun onClick(v: View) {
        when (view?.id) {
            R.id.product_search_button -> {
                searchButton?.isEnabled = false
                workflowModel?.onSearchButtonClicked()
            }

            com.annguyenhoang.mlkit_object_detection_presentation.R.id.bottom_sheet_scrim_view -> bottomSheetBehavior?.setState(
                BottomSheetBehavior.STATE_HIDDEN
            )

            com.annguyenhoang.mlkit_object_detection_presentation.R.id.close_button -> onBackPressed()
            com.annguyenhoang.mlkit_object_detection_presentation.R.id.flash_button -> {
                if (flashButton?.isSelected == true) {
                    flashButton?.isSelected = false
                    cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                } else {
                    flashButton?.isSelected = true
                    cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                }
            }

            com.annguyenhoang.mlkit_object_detection_presentation.R.id.settings_button -> {
//                settingsButton?.isEnabled = false
//                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }

    private fun setUpBottomSheet() {
        bottomSheetBehavior =
            BottomSheetBehavior.from(view?.findViewById(com.annguyenhoang.mlkit_object_detection_presentation.R.id.bottom_sheet)!!)
        bottomSheetBehavior?.setBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    Log.d(TAG, "Bottom sheet new state: $newState")
                    bottomSheetScrimView?.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE else View.VISIBLE
                    graphicOverlay?.clear()

                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> workflowModel?.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
                        BottomSheetBehavior.STATE_COLLAPSED,
                        BottomSheetBehavior.STATE_EXPANDED,
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> slidingSheetUpFromHiddenState = false

                        BottomSheetBehavior.STATE_DRAGGING, BottomSheetBehavior.STATE_SETTLING -> {
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    val searchedObject = workflowModel!!.searchedObject.value
                    if (searchedObject == null || java.lang.Float.isNaN(slideOffset)) {
                        return
                    }

                    val graphicOverlay = graphicOverlay ?: return
                    val bottomSheetBehavior = bottomSheetBehavior ?: return
                    val collapsedStateHeight = bottomSheetBehavior.peekHeight.coerceAtMost(bottomSheet.height)
                    val bottomBitmap = objectThumbnailForBottomSheet ?: return
                    if (slidingSheetUpFromHiddenState) {
                        val thumbnailSrcRect = graphicOverlay.translateRect(searchedObject.boundingBox)
                        bottomSheetScrimView?.updateWithThumbnailTranslateAndScale(
                            bottomBitmap,
                            collapsedStateHeight,
                            slideOffset,
                            thumbnailSrcRect
                        )
                    } else {
                        bottomSheetScrimView?.updateWithThumbnailTranslate(
                            bottomBitmap, collapsedStateHeight, slideOffset, bottomSheet
                        )
                    }
                }
            })

        bottomSheetScrimView =
            view?.findViewById<BottomSheetScrimView>(com.annguyenhoang.mlkit_object_detection_presentation.R.id.bottom_sheet_scrim_view)
                ?.apply {
                    setOnClickListener(this@MLKitObjectDetectionFragment)
                }

        bottomSheetTitleView =
            view?.findViewById(com.annguyenhoang.mlkit_object_detection_presentation.R.id.bottom_sheet_title)
        productRecyclerView =
            view?.findViewById<RecyclerView>(com.annguyenhoang.mlkit_object_detection_presentation.R.id.product_recycler_view)
                ?.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = ProductAdapter(ImmutableList.of())
                }
    }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java).apply {

            // Observes the workflow state changes, if happens, update the overlay view indicators and
            // camera preview state.
            workflowState.observe(viewLifecycleOwner, Observer { workflowState ->
                if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                    return@Observer
                }
                currentWorkflowState = workflowState

                if (PreferenceUtils.isAutoSearchEnabled(requireContext())) {
                    stateChangeInAutoSearchMode(workflowState)
                } else {
                    stateChangeInManualSearchMode(workflowState)
                }
            })

            // Observes changes on the object to search, if happens, fire product search request.
            objectToSearch.observe(viewLifecycleOwner, Observer { detectObject ->
                searchEngine!!.search(detectObject) { detectedObject, products ->
                    workflowModel?.onSearchCompleted(detectedObject, products)
                }
            })

            // Observes changes on the object that has search completed, if happens, show the bottom sheet
            // to present search result.
            searchedObject.observe(viewLifecycleOwner, Observer { nullableSearchedObject ->
                val searchedObject = nullableSearchedObject ?: return@Observer
                val productList = searchedObject.productList
                objectThumbnailForBottomSheet = searchedObject.getObjectThumbnail()
                bottomSheetTitleView?.text = resources
                    .getQuantityString(
                        R.plurals.bottom_sheet_title, productList.size, productList.size
                    )
                productRecyclerView?.adapter = ProductAdapter(productList)
                slidingSheetUpFromHiddenState = true
                bottomSheetBehavior?.peekHeight =
                    preview?.height?.div(2) ?: BottomSheetBehavior.PEEK_HEIGHT_AUTO
                bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            })
        }
    }

    private fun stateChangeInAutoSearchMode(workflowState: WorkflowModel.WorkflowState) {
        val wasPromptChipGone = promptChip!!.visibility == View.GONE

        searchButton?.visibility = View.GONE
        searchProgressBar?.visibility = View.GONE
        when (workflowState) {
            WorkflowModel.WorkflowState.DETECTING, WorkflowModel.WorkflowState.DETECTED, WorkflowModel.WorkflowState.CONFIRMING -> {
                promptChip?.visibility = View.VISIBLE
                promptChip?.setText(
                    if (workflowState == WorkflowModel.WorkflowState.CONFIRMING)
                        R.string.prompt_hold_camera_steady
                    else
                        R.string.prompt_point_at_an_object
                )
                startCameraPreview()
            }

            WorkflowModel.WorkflowState.CONFIRMED -> {
                promptChip?.visibility = View.VISIBLE
                promptChip?.setText(R.string.prompt_searching)
                stopCameraPreview()
            }

            WorkflowModel.WorkflowState.SEARCHING -> {
                searchProgressBar?.visibility = View.VISIBLE
                promptChip?.visibility = View.VISIBLE
                promptChip?.setText(R.string.prompt_searching)
                stopCameraPreview()
            }

            WorkflowModel.WorkflowState.SEARCHED -> {
                promptChip?.visibility = View.GONE
                stopCameraPreview()
            }

            else -> promptChip?.visibility = View.GONE
        }

        val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
        if (shouldPlayPromptChipEnteringAnimation && promptChipAnimator?.isRunning == false) {
            promptChipAnimator?.start()
        }
    }

    private fun stateChangeInManualSearchMode(workflowState: WorkflowModel.WorkflowState) {
        val wasPromptChipGone = promptChip?.visibility == View.GONE
        val wasSearchButtonGone = searchButton?.visibility == View.GONE

        searchProgressBar?.visibility = View.GONE
        when (workflowState) {
            WorkflowModel.WorkflowState.DETECTING, WorkflowModel.WorkflowState.DETECTED, WorkflowModel.WorkflowState.CONFIRMING -> {
                promptChip?.visibility = View.VISIBLE
                promptChip?.setText(R.string.prompt_point_at_an_object)
                searchButton?.visibility = View.GONE
                startCameraPreview()
            }

            WorkflowModel.WorkflowState.CONFIRMED -> {
                promptChip?.visibility = View.GONE
                searchButton?.visibility = View.VISIBLE
                searchButton?.isEnabled = true
                searchButton?.setBackgroundColor(Color.WHITE)
                startCameraPreview()
            }

            WorkflowModel.WorkflowState.SEARCHING -> {
                promptChip?.visibility = View.GONE
                searchButton?.visibility = View.VISIBLE
                searchButton?.isEnabled = false
                searchButton?.setBackgroundColor(Color.GRAY)
                searchProgressBar!!.visibility = View.VISIBLE
                stopCameraPreview()
            }

            WorkflowModel.WorkflowState.SEARCHED -> {
                promptChip?.visibility = View.GONE
                searchButton?.visibility = View.GONE
                stopCameraPreview()
            }

            else -> {
                promptChip?.visibility = View.GONE
                searchButton?.visibility = View.GONE
            }
        }

        val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
        promptChipAnimator?.let {
            if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
        }

        val shouldPlaySearchButtonEnteringAnimation = wasSearchButtonGone && searchButton?.visibility == View.VISIBLE
        searchButtonAnimator?.let {
            if (shouldPlaySearchButtonEnteringAnimation && !it.isRunning) it.start()
        }
    }

    private fun startCameraPreview() {
        val cameraSource = this.cameraSource ?: return
        val workflowModel = this.workflowModel ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        if (workflowModel?.isCameraLive == true) {
            workflowModel!!.markCameraFrozen()
            flashButton?.isSelected = false
            preview?.stop()
        }
    }

}