package net.group.androidlocalmessanger.module

import java.io.Serializable

enum class Order : Serializable {
    Login, Register, SendMessage, GetMessage,
    CreateGroup, UpdateGroup, RefreshGroup, JoinToGroup , GetMyGroups;
    private var serialVersionUID = 6529685098267757690L
}