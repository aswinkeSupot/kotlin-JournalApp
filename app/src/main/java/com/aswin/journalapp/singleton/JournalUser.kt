package com.aswin.journalapp.singleton

import android.app.Application

/**
 * Created by Aswin on 20-09-2024.
 */
class JournalUser : Application() {
    var username: String? = null
    var userId: String? = null

    companion object {
        var instance: JournalUser? = null
        get() {
            if (field == null) {
                // create a new instance from journal user
                field = JournalUser()
            }
            return field
        }
        private set
    }
}