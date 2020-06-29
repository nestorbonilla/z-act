package me.nestorbonilla.zact.room

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.model.ContactModel
import me.nestorbonilla.zact.model.GroupModel

@Database(version = 1, entities = [ActModel::class, ContactModel::class, GroupModel::class])
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
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return INSTANCE
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDbAsyncTask(INSTANCE)
                    .execute()
            }
        }
    }
}

class PopulateDbAsyncTask(db: ZactDatabase?) : AsyncTask<Unit, Unit, Unit>() {
    private val noteDao = db?.zactDao()

    override fun doInBackground(vararg p0: Unit?) {
        //noteDao?.insertAct(ActModel(1,"Title 1", "description 1"))
        //noteDao?.insertAct(ActModel(2,"Title 2", "description 2"))
    }
}