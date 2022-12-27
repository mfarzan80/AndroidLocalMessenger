package net.group.androidlocalmessanger.ui.theme

import net.group.androidlocalmessanger.module.ResponseCode

val ResponseCodeToString = mapOf(
    ResponseCode.OK to "Success",
    ResponseCode.SOCKET_CONNECT_ERROR to "Socket connection error",
    ResponseCode.PASSWORD_INCORRECT to "Password invalid",
    ResponseCode.USER_EXIST to "User exist",
    ResponseCode.USER_NAME_NOT_FOUND to "Username invalid"
)

fun getStringByResponse(responseCode: ResponseCode) = ResponseCodeToString[responseCode]