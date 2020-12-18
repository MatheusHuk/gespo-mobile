package br.com.bandtec.gespo.services.listeners

interface CounterListener {

    fun getCounterTimer(): Int

    fun setCounterTimer(counterTimer: Int)

    fun getStartTime(): Int

    fun isActive(): Boolean

}