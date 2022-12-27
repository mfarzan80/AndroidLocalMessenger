package net.group.androidlocalmessanger.network.client

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.group.androidlocalmessanger.module.Group
import net.group.androidlocalmessanger.module.Order
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class MainViewModule : ViewModel() {

    val groups: SnapshotStateList<Group> = mutableStateListOf()



}