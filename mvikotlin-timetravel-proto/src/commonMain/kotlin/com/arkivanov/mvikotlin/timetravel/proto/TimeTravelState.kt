package com.arkivanov.mvikotlin.timetravel.proto

data class TimeTravelState(
    val events: List<TimeTravelEvent>,
    val selectedEventIndex: Int,
    val mode: Mode
) {

    enum class Mode {
        IDLE, RECORDING, STOPPED;

        companion object {
            val values: List<Mode> = values().toList()
        }
    }
}
