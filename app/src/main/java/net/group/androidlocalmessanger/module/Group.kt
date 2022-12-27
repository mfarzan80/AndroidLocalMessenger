package net.group.androidlocalmessanger.module

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*


@Entity(tableName = "groups")
data class Group(val name: String, val type: String) : Serializable {
    var serialVersionUID = 6529685098267757690L

    companion object {
        const val GROUP_TYPE_CHAT = "chat"
        const val GROUP_TYPE_CHANNEL = "channel"
        const val GROUP_TYPE_GROUP = "group"
    }

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var users = HashSet<User>()
    var admins = HashSet<User>()
    var messages = HashSet<Message>()

    fun getGroupName(user: User): String {
        if (type == GROUP_TYPE_CHAT) {
            users.forEach {
                if (it != user)
                    return it.name
            }
            return user.name
        } else
            return name
    }


}
