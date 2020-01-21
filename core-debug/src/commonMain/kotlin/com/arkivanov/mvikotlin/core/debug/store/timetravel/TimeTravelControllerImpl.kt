package com.arkivanov.mvikotlin.core.debug.store.timetravel

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.debug.store.StoreEventType
import com.arkivanov.mvikotlin.core.debug.store.timetravel.TimeTravelState.Mode
import com.arkivanov.mvikotlin.core.internal.rx.Subject
import com.arkivanov.mvikotlin.core.internal.rx.observer
import com.arkivanov.mvikotlin.core.internal.rx.onNext
import com.arkivanov.mvikotlin.core.internal.rx.subscribe
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.utils.assertOnMainThread
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.AtomicMap
import com.arkivanov.mvikotlin.utils.internal.clear
import com.arkivanov.mvikotlin.utils.internal.containsKey
import com.arkivanov.mvikotlin.utils.internal.get
import com.arkivanov.mvikotlin.utils.internal.minusAssign
import com.arkivanov.mvikotlin.utils.internal.plusAssign
import com.arkivanov.mvikotlin.utils.internal.set
import com.arkivanov.mvikotlin.utils.internal.size
import com.arkivanov.mvikotlin.utils.internal.values
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.updateAndGet

internal class TimeTravelControllerImpl : TimeTravelController {

    @Suppress("ObjectPropertyName")
    private var _state = AtomicReference(TimeTravelState())
    override val state: TimeTravelState get() = _state.value
    private val stateSubject = Subject<TimeTravelState>()
    private val postponedEvents = AtomicList<TimeTravelEvent>()
    private val stores = AtomicMap<String, TimeTravelStore<*, *, *>>()

    override fun states(observer: Observer<TimeTravelState>): Disposable = stateSubject.subscribe(observer, state)

    @MainThread
    internal fun attachStore(store: TimeTravelStore<*, *, *>) {
        assertOnMainThread()
        val storeName = store.name
        check(!stores.containsKey(storeName)) { "Duplicate store: $storeName" }

        stores[storeName] = store
        store.events(observer(onComplete = { stores -= storeName }, onNext = ::onEvent))
        store.init()
    }

    override fun restoreEvents(events: List<TimeTravelEvent>) {
        assertOnMainThread()

        if (events.isNotEmpty()) {
            swapState { it.copy(events = events, selectedEventIndex = -1, mode = Mode.STOPPED) }
            moveToEnd()
        }
    }

    override fun startRecording() {
        assertOnMainThread()

        if (state.mode === Mode.IDLE) {
            swapState { it.copy(mode = Mode.RECORDING) }
        }
    }

    override fun stopRecording() {
        assertOnMainThread()

        if (state.mode === Mode.RECORDING) {
            swapState { it.copy(mode = if (it.events.isNotEmpty()) Mode.STOPPED else Mode.IDLE) }
        }
    }

    override fun moveToStart() {
        assertOnMainThread()

        if (state.mode === Mode.STOPPED) {
            move(state.events, state.selectedEventIndex, -1)
        }
    }

    override fun stepBackward() {
        assertOnMainThread()

        if (state.mode === Mode.STOPPED) {
            step(state.events, state.selectedEventIndex, false)
        }
    }

    override fun stepForward() {
        assertOnMainThread()

        if (state.mode === Mode.STOPPED) {
            step(state.events, state.selectedEventIndex, true)
        }
    }

    override fun moveToEnd() {
        assertOnMainThread()

        if (state.mode === Mode.STOPPED) {
            move(state.events, state.selectedEventIndex, state.events.lastIndex)
        }
    }

    override fun cancel() {
        assertOnMainThread()

        if (state.mode !== Mode.IDLE) {
            val oldMode = state.mode
            swapState { it.copy(events = emptyList(), mode = Mode.IDLE) }

            if (oldMode !== Mode.RECORDING) {
                stores.values.forEach { it.restoreState() }
                postponedEvents.value.forEach { process(it) }
            }

            postponedEvents.clear()
        }
    }

    override fun debugEvent(event: TimeTravelEvent) {
        assertOnMainThread()

        if (state.mode === Mode.STOPPED) {
            stores[event.storeName]?.eventDebugger?.debug(event)
        }
    }

    private fun onEvent(event: TimeTravelEvent) {
        when (state.mode) {
            Mode.RECORDING -> {
                swapState { it.copy(events = it.events + event, selectedEventIndex = it.events.size) }
                process(event)
            }

            Mode.IDLE -> process(event)

            Mode.STOPPED ->
                if (event.type === StoreEventType.RESULT) {
                    process(event)
                } else {
                    postponedEvents += event
                }
        }.let {}
    }

    private fun step(events: List<TimeTravelEvent>, from: Int, isForward: Boolean) {
        val progression =
            if (isForward) {
                (from + 1)..events.lastIndex
            } else {
                (from - 1) downTo -1
            }

        for (i in progression) {
            val item = events.getOrNull(i)
            if ((item == null) || (item.type === StoreEventType.STATE)) {
                move(events, from, i)
                break
            }
        }
    }

    private fun move(events: List<TimeTravelEvent>, from: Int, to: Int, publish: Boolean = true) {
        if (from == to) {
            return
        }

        val set = HashSet<String>()
        val deque = ArrayList<TimeTravelEvent>() // FIXME: Use queue
        val isForward = to > from
        val progression =
            if (isForward) {
                to downTo from + 1
            } else {
                to + 1..from
            }

        for (i in progression) {
            val event = events[i]
            if ((event.type === StoreEventType.STATE) && stores.containsKey(event.storeName) && !set.contains(event.storeName)) {
                set.add(event.storeName)
                deque += event
                if (set.size == stores.size) {
                    break
                }
            }
        }
        while (!deque.isEmpty()) {
            deque.removeAt(0).also { event ->
                if ((event.type === StoreEventType.STATE) && !isForward) {
                    process(event, event.state)
                } else {
                    process(event)
                }
            }
        }

        if (publish) {
            swapState { it.copy(events = events, selectedEventIndex = to) }
        }
    }

    private fun process(event: TimeTravelEvent, previousValue: Any? = null) {
        stores[event.storeName]?.eventProcessor?.process(event.type, previousValue ?: event.value)
    }

    private inline fun swapState(reducer: (TimeTravelState) -> TimeTravelState): TimeTravelState =
        _state
            .updateAndGet(reducer)
            .also(stateSubject::onNext)
}