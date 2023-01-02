package net.group.androidlocalmessanger.module

import java.io.Serializable

enum class Order : Serializable {
    Login, Register, SendMessage,
    CreateGroup, GetMyGroups, GetAllUsers, UpdateUser, UpdateProfile;

    private var serialVersionUID = 6529685098267757690L
}