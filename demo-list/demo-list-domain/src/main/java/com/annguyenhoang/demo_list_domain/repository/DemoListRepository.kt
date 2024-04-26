package com.annguyenhoang.demo_list_domain.repository

import com.annguyenhoang.demo_list_domain.model.DemoItem
import kotlinx.coroutines.flow.Flow

interface DemoListRepository {
    fun observeDemoList(): Flow<List<DemoItem>>
}