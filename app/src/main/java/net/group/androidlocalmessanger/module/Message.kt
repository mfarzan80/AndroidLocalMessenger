package net.group.androidlocalmessanger.module

import java.io.Serializable


data class Message(var text: String, var sender: User, val groupId: String) :
    Serializable {
    fun textOrFile(): String {
        return if (attachedFileName != null)
            attachedFileName!!
        else
            text
    }

    var attachedFileName: String? = null
    var messsage_serialVersionUID = 6529685098267757690L

}