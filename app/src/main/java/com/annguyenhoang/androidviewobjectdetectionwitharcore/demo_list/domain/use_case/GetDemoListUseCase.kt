package com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.use_case

import com.annguyenhoang.androidviewobjectdetectionwitharcore.demo_list.domain.repository.DemoListRepository

class GetDemoListUseCase(
    private val demoListRepository: DemoListRepository
) {
    operator fun invoke() = demoListRepository.observeDemoList()
}