package net.group.androidlocalmessanger.module

import java.io.Serializable

data class Response<T>(
    val code: ResponseCode? = null,
    val responseType: ResponseType? = null,
    val data: T? = null


): Serializable{

    private var serialVersionUID = 6529685098267757690L
}