package com.example.playlistmaker.mediateka.ui

object MediaTextFormatter {
    fun getTrackDeclension(tracksCount: Int): String {
        return when {
            tracksCount % 10 == 1 && tracksCount % 100 != 11 -> "трек"
            tracksCount % 10 in 2..4 && tracksCount % 100 !in 12..14 -> "трека"
            else -> "треков"
        }
    }
}