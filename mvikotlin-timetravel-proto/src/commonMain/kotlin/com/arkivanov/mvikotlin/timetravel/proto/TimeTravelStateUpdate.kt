package com.arkivanov.mvikotlin.timetravel.proto

sealed class TimeTravelStateUpdate {

    data class Full(val state: TimeTravelState) : TimeTravelStateUpdate()
    data class Update(val state: TimeTravelState) : TimeTravelStateUpdate()
}
