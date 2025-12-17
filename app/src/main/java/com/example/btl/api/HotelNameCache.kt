package com.example.btl.api

import android.content.Context
import android.content.SharedPreferences

/**
 * A simple cache using SharedPreferences to store and retrieve hotel names
 * based on room IDs. This allows the app to "remember" the hotel name for a booking
 * without needing the API to provide it in the booking list.
 */
object HotelNameCache {

    private const val PREFS_NAME = "hotel_name_cache"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Saves the hotel name for a list of room IDs.
     * This should be called when a booking is confirmed.
     */
    fun saveHotelNameForRooms(context: Context, roomIds: List<Int>, hotelName: String) {
        val editor = getPrefs(context).edit()
        roomIds.forEach { roomId ->
            // Store hotel name against each room ID
            editor.putString(roomId.toString(), hotelName)
        }
        editor.apply()
    }

    /**
     * Retrieves the hotel name for a given room ID.
     * Returns null if no name is found for the given room.
     */
    fun getHotelNameForRoom(context: Context, roomId: Int): String? {
        return getPrefs(context).getString(roomId.toString(), null)
    }
}