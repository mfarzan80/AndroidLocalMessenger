package net.group.androidlocalmessanger.module

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(@PrimaryKey var email: String, val password: String) : Serializable {
    var serialVersionUID = 6529685098267757690L

    lateinit var name: String
    var profilePath: String? = null
    var id: String? = null


    constructor(email: String, password: String, name: String) : this(email, password) {
        this.name = name
    }


    override fun equals(other: Any?): Boolean {
        return if (other is User)
            other.email == email
        else
            false
    }
}