package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.repository

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local.LocalDataSource
import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.data.local.model.DemoData
import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.model.DemoItem
import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.model.DemoType
import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.repository.DemoListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DemoListRepositoryImpl(
    private val dataSource: LocalDataSource
) : DemoListRepository {

    override fun observeDemoList(): Flow<List<DemoItem>> {
        return dataSource.observeDemoList().map {
            it.map { demoData ->
                mapToDomain(demoData)
            }
        }
    }

    private fun mapToDomain(demoData: DemoData) = DemoItem(
        demoId = demoData.demoId,
        demoName = demoData.demoName,
        demoType = DemoType.toDomain(demoData.demoType)
    )

}