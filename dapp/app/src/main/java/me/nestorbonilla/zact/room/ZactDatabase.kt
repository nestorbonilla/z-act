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
        attendeeProfile()
    }

    private fun creatorProfile() {
        zactDao?.insertAttendee(AttendeeModel(1, 0))
        zactDao?.insertCreator(CreatorModel(
            1,
            "inflict canyon owner clog link divert gym ride resist ethics dust hazard run enemy venue weather unaware assist pipe salute damage burden observe notable",
            "zs1wk3v6543v08yah3tczk8sjxnlurm57g2rfvvjdres4zd7vw37xxlk4e3c3x3pr68staqk5r34un",
            0)
        )
    }

    private fun attendeeProfile() {
        Log.d("ZACT_DAPP", "adding attendee profile test records...")
        zactDao?.insertAttendee(AttendeeModel(1, 0))
        zactDao?.insertCreator(CreatorModel(
            1,
            "",
            "",
            0)
        )
        zactDao?.insertAct(ActModel(
            0,
            "",
            "",
        "",
            "inflict canyon owner clog link divert gym ride resist ethics dust hazard run enemy venue weather unaware assist pipe salute damage burden observe notable",
            "Call for March on July 23rd",
        "This is a call for march this next July 23rd for our freedom of speech. To increase our security, please be at the following point at 3 PM, and find the puzzle words on the walls to reveal the final destination.",
        100,
            "9.001152025357648,-79.53058283776045"
            )
        )
    }

    private fun productionProfile() {
        zactDao?.insertAttendee(AttendeeModel(1, 0))
        zactDao?.insertCreator(CreatorModel(
            1,
            "",
            "",
            0)
        )
    }
}