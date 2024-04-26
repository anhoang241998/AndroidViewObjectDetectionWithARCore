package com.annguyenhoang.demo_list_presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.annguyenhoang.demo_list_domain.model.DemoItem
import com.annguyenhoang.demo_list_domain.use_case.GetDemoListUseCase
import com.annguyenhoang.demo_list_presentation.model.DemoItemUIState
import com.annguyenhoang.demo_list_presentation.model.DemoTypeUIState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DemoListViewModel(
    getDemoListUseCase: GetDemoListUseCase
) : ViewModel() {

    val demoList: StateFlow<List<DemoItemUIState>> = getDemoListUseCase()
        .map { demoItems ->
            demoItems.map { mapToUIState(it) }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(3000),
            initialValue = listOf()
        )

    private fun mapToUIState(demoItem: DemoItem): DemoItemUIState {
        return DemoItemUIState(
            demoId = demoItem.demoId,
            demoName = demoItem.demoName,
            demoType = DemoTypeUIState.toUIState(demoItem.demoType.type)
        )
    }

}