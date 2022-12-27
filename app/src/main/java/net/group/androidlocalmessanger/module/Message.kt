package net.group.androidlocalmessanger.module

import java.io.Serializable

data class Message(val text: String, val sender: User, val groupKey: String) : Serializable {
    private var serialVersionUID = 6529685098267757690L

}