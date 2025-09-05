package com.ragul.notetaking.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ragul.notetaking.data.dao.NoteDao
import com.ragul.notetaking.data.model.Note
import com.ragul.notetaking.data.model.User
import com.ragul.notetaking.data.dao.UserDao
import com.ragul.notetaking.data.util.Converters

@Database(entities = [Note::class, User::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NoteDatabase : RoomDatabase() {
    
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null
        
        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}