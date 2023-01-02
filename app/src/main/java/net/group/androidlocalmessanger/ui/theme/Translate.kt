package net.group.androidlocalmessanger.ui.theme

import net.group.androidlocalmessanger.module.ResponseCode

val ResponseCodeToString = mapOf(
    ResponseCode.OK to "Success",
    ResponseCode.SOCKET_CONNECT_ERROR to "Socket connection error",
    ResponseCode.PASSWORD_INCORRECT to "Password invalid",
    ResponseCode.EMAIL_EXIST to "User exist",
    ResponseCode.EMAIL_NOT_FOUND to "Username invalid",
    ResponseCode.USER_NAME_EXIST to "Username exist"
)

fun getStringByResponse(responseCode: ResponseCode) = ResponseCodeToString[responseCode]