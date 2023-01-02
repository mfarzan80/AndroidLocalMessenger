package net.group.androidlocalmessanger.module

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(@PrimaryKey var userEmail: String, val password: String) : Serializable {
    var serialVersionUID = 6529685098267757690L

    lateinit var name: String
    var profilePath: String? = null
    var phoneNumber: String? = null
    var userName: String? = null

    var showPhoneNumber: Boolean = false
    var showProfile: Boolean = true

    constructor(email: String, password: String, name: String) : this(email, password) {
        this.name = name
    }


    override fun equals(other: Any?): Boolean {
        return if (other is User)
            other.userEmail == userEmail
        else
            false
    }


    override fun hashCode(): Int {
        var result = userEmail.hashCode()
        result = 31 * result + (userName?.hashCode() ?: 0)
        return result
    }
}


