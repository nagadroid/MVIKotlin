package com.arkivanov.mvikotlin.timetravel.proto

data class TimeTravelEvent(
    val id: String,
    val storeName: String,
    val type: StoreEventType,
    val value: Values,
    val state: Values
)
