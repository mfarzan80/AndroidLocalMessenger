package net.group.androidlocalmessanger.module

import java.io.Serializable

data class OrderData<T>(val order: Order, val data: T? = null) : Serializable{

     var serialVersionUID = 6529685098267757690L
}