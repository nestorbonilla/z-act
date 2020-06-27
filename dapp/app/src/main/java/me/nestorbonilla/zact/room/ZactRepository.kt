package me.nestorbonilla.zact.room

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import me.nestorbonilla.zact.model.ActModel

class ZactRepository(application: Application) {

    private var zactDao: ZactDao

    private var allActs: LiveData<List<ActModel>>

    init {
        val database: ZactDatabase = ZactDatabase.getDatabase(
            application.applicationContext
        )!!
        zactDao = database.zactDao()
        allActs = zactDao.allActs
    }

    fun insert(actModel: ActModel) {
        val insertActAsyncTask = InsertActAsyncTask(zactDao).execute(actModel)
    }

    fun getAllActs(): LiveData<List<ActModel>> {
        return allActs
    }

    private class InsertActAsyncTask(zactDao: ZactDao) : AsyncTask<ActModel, Unit, Unit>() {
        val zactDao = zactDao

        override fun doInBackground(vararg p0: ActModel?) {
            zactDao.insertAct(p0[0]!!)
        }
    }
}