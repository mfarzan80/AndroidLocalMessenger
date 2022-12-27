package net.group.androidlocalmessanger.data

data class DataOrException<T , Boolean , E>(
    var data: T? = null,
    var loading: Boolean? = null,
    var info:E? = null
)