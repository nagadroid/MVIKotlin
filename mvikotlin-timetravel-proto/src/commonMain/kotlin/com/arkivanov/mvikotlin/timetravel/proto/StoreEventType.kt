package com.arkivanov.mvikotlin.timetravel.proto

enum class StoreEventType {

    INTENT, ACTION, RESULT, STATE, LABEL;

    companion object {
        val values: List<StoreEventType> = values().toList()
    }
}
