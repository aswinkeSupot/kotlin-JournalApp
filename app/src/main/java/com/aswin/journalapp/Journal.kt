package com.aswin.journalapp

import com.google.firebase.Timestamp

/**
 * Created by Aswin on 20-09-2024.
 */
data class Journal(
    val title: String,
    val thoughts: String,
    val imageUrl: String,

    val userId: String,
    val timeAdded: Timestamp,
    val username: String
)
