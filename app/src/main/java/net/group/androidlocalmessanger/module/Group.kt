package net.group.androidlocalmessanger.module

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.UUID
import kotlin.collections.ArrayList


@Entity(tableName = "groups")
data class Group(val name: String, val type: String) : Serializable {
    var serialVersionUID = 6529685098267757690L

    companion object {
        const val GROUP_TYPE_CHAT = "chat"
        const val GROUP_TYPE_CHANNEL = "channel"
        const val GROUP_TYPE_GROUP = "group"
    }

    @PrimaryKey
    var groupId: String = UUID.randomUUID().toString()

    var adminIds = ArrayList<String>()

    var messages = ArrayList<Message>()

    override fun equals(other: Any?): Boolean {
        if (other is Group)
            return groupId == other.groupId
        return false
    }

    override fun hashCode(): Int {
        return groupId.hashCode()
    }


}
