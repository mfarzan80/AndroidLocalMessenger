package net.group.androidlocalmessanger.network.client.controller

import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.Response
import net.group.androidlocalmessanger.network.client.MainViewModule

object MainController {
    val mainViewModule = MainViewModule()

    fun refreshGroups(response: Response<*>) {
        val groups = response.data as List<Group>
        mainViewModule.groups.clear()
        mainViewModule.groups.addAll(groups)
    }
}