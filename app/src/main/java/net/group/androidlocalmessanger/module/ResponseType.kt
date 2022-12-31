package net.group.androidlocalmessanger.module

import java.io.Serializable

enum class ResponseType: Serializable {
    AllGroups, Login, Register , AllUsers, UpdateGroup;

    private var serialVersionUID = 6529685098267757690L
}