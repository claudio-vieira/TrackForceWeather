package com.example.trackforceweather.domain.repository

interface NetworkMonitor {
    fun isConnected(): Boolean
}