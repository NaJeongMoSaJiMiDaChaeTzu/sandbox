package com.hirogakatageri.sandbox

import com.hirogakatageri.core.BaseApplication
import com.hirogakatageri.home.HomeModule
import com.hirogakatageri.repository.RepositoryModule
import org.koin.core.module.Module

class SandboxApp : BaseApplication() {
    override val moduleList: List<Module> = listOf(
        RepositoryModule.create(),
        HomeModule.create()
    )
}