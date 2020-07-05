package me.nestorbonilla.zact.room

import android.content.Context
import android.os.AsyncTask
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
                        .addCallback(roomCallback)
                        .allowMainThreadQueries()
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
    private val zactDao = db?.zactDao()

    override fun doInBackground(vararg p0: Unit?) {
        zactDao?.insertAttendee(AttendeeModel(1, 0))
        zactDao?.insertCreator(CreatorModel(
            1,
            "inflict canyon owner clog link divert gym ride resist ethics dust hazard run enemy venue weather unaware assist pipe salute damage burden observe notable",
            "zs1wk3v6543v08yah3tczk8sjxnlurm57g2rfvvjdres4zd7vw37xxlk4e3c3x3pr68staqk5r34un",
            0)
        )
    }
}