package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.repository

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.model.DemoItem
import kotlinx.coroutines.flow.Flow

interface DemoListRepository {
    fun observeDemoList(): Flow<List<DemoItem>>
}