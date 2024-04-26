package com.annguyenhoang.demo_list_domain.use_case

import com.annguyenhoang.demo_list_domain.repository.DemoListRepository

class GetDemoListUseCase(
    private val demoListRepository: DemoListRepository
) {
    operator fun invoke() = demoListRepository.observeDemoList()
}