package net.group.androidlocalmessanger.module

import java.io.Serializable

enum class ResponseCode : Serializable {
    OK, EMAIL_EXIST, EMAIL_NOT_FOUND, PASSWORD_INCORRECT,
    SOCKET_CONNECT_ERROR, USER_NAME_EXIST;

    private var serialVersionUID = 6529685098267757690L
}