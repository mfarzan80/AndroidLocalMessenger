package net.group.androidlocalmessanger.module

import java.io.Serializable

enum class ResponseCode : Serializable {
    OK, USER_EXIST, USER_NAME_NOT_FOUND, PASSWORD_INCORRECT ,
    SOCKET_CONNECT_ERROR;

    private var serialVersionUID = 6529685098267757690L
}