package com.annguyenhoang.core.data.data_source

import com.annguyenhoang.core.data.model.AppLocalEventData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class AppLocalEventDataSource {
    private val _event: MutableSharedFlow<AppLocalEventData> = MutableSharedFlow()
    val event: SharedFlow<AppLocalEventData>
        get() = _event

    @OptIn(DelicateCoroutinesApi::class)
    fun emitLocalEvent(event: AppLocalEventData) {
        GlobalScope.launch {
            _event.emit(event)
        }
    }
}