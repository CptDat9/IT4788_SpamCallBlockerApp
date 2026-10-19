package com.example.spamcallblocker_20235030

import android.app.Application
import com.example.spamcallblocker_20235030.data.AppDatabase
import com.example.spamcallblocker_20235030.data.SpamBlockerRepository

class SpamBlockerApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { SpamBlockerRepository(database) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: SpamBlockerApp
            private set
    }
}
