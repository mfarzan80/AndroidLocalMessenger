package net.group.androidlocalmessanger.module

import java.io.Serializable

enum class ResponseType : Serializable {
    AllGroups, Login, Register, AllUsers, UpdatedGroup, UpdatedUser , SendingFile ;

    private var serialVersionUID = 6529685098267757690L
}