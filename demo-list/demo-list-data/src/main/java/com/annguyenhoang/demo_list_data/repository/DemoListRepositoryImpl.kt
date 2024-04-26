package com.annguyenhoang.demo_list_data.repository

import com.annguyenhoang.demo_list_data.local.LocalDataSource
import com.annguyenhoang.demo_list_data.local.model.DemoData
import com.annguyenhoang.demo_list_domain.model.DemoItem
import com.annguyenhoang.demo_list_domain.model.DemoType
import com.annguyenhoang.demo_list_domain.repository.DemoListRepository
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