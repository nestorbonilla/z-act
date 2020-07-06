package me.nestorbonilla.zact.room

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.AttendeeModel
import me.nestorbonilla.zact.model.CreatorModel

@Database(version = 1, entities = [ActModel::class, CreatorModel::class, AttendeeModel::class])
abstract class ZactDatabase: RoomDatabase() {

    abstract fun zactDao(): ZactDao

    companion object {
        private var INSTANCE: ZactDatabase? = null
        private const val DB_NAME  = "zact.db"

        fun getDatabase(context: Context): ZactDatabase? {
            if (INSTANCE == null) {
                synchronized(ZactDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, ZactDatabase::class.java, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}
