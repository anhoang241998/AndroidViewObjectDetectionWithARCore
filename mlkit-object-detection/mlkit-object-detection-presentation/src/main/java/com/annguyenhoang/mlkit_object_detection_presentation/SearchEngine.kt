package com.annguyenhoang.mlkit_object_detection_presentation

import android.content.Context
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.annguyenhoang.mlkit_object_detection_presentation.model.DetectedObjectInfo
import com.annguyenhoang.mlkit_object_detection_presentation.model.Product
import com.google.android.gms.tasks.Tasks
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/** A fake search engine to help simulate the complete work flow.  */
class SearchEngine(context: Context) {

    private val searchRequestQueue: RequestQueue = Volley.newRequestQueue(context)
    private val requestCreationExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun search(
        detectedObject: DetectedObjectInfo,
        listener: (detectedObject: DetectedObjectInfo, productList: List<Product>) -> Unit
    ) {
        // Crops the object image out of the full image is expensive, so do it off the UI thread.
        Tasks.call<JsonObjectRequest>(requestCreationExecutor, Callable { createRequest(detectedObject) })
            .addOnSuccessListener { productRequest -> searchRequestQueue.add(productRequest.setTag(TAG)) }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to create product search request!", e)
                // Remove the below dummy code after your own product search backed hooked up.
                val productList = ArrayList<Product>()
                for (i in 0..7) {
                    productList.add(
                        Product(/* imageUrl= */"", "Product title $i", "Product subtitle $i")
                    )
                }
                listener.invoke(detectedObject, productList)
            }
    }

    fun shutdown() {
        searchRequestQueue.cancelAll(TAG)
        requestCreationExecutor.shutdown()
    }

    companion object {
        private const val TAG = "SearchEngine"

        @Throws(Exception::class)
        private fun createRequest(searchingObject: DetectedObjectInfo): JsonObjectRequest {
            val objectImageData = searchingObject.imageData
                ?: throw Exception("Failed to get object image data!")

            // Hooks up with your own product search backend here.
            throw Exception("Hooks up with your own product search backend.")
        }
    }
}