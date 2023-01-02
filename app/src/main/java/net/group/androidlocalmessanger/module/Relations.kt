package net.group.androidlocalmessanger.module

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["userEmail", "groupId"])
data class UserGroupCrossRef(val userEmail: String, val groupId: String)


data class UserWithGroups(
    @Embedded var user: User,
    @Relation(
        parentColumn = "userEmail",
        entityColumn = "groupId",
        associateBy = Junction(UserGroupCrossRef::class)
    )
    var groups: List<Group>

) : java.io.Serializable {

}


data class GroupWithUsers(
    @Embedded var group: Group,
    @Relation(
        parentColumn = "groupId",
        entityColumn = "userEmail",
        associateBy = Junction(UserGroupCrossRef::class)
    )
    var users: List<User>

) : java.io.Serializable {


    fun getGroupName(me: User): String {
        if (group.type == Group.GROUP_TYPE_CHAT) {
            return getContact(me).name
        }
        return group.name
    }

    fun getContact(me: User): User {
        users.forEach {
            if (it != me)
                return it
        }
        return me
    }
}
