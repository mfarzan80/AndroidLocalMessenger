package net.group.androidlocalmessanger.module

import java.io.Serializable

data class Response<T>(
    var code: ResponseCode? = null,
    var responseType: ResponseType? = null,
    var data: T? = null


): Serializable{

    private var serialVersionUID = 6529685098267757690L
}