package me.nestorbonilla.zact.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import me.nestorbonilla.zact.model.ActModel
import me.nestorbonilla.zact.room.ZactDao
import me.nestorbonilla.zact.room.ZactDatabase
import me.nestorbonilla.zact.room.ZactRepository

class ActViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: ZactRepository = ZactRepository(application)
    private var allActs: LiveData<List<ActModel>> = repository.getAllActs()

    fun insert(actModel: ActModel) {
        repository.insert(actModel)
    }

    fun getAllActs(): LiveData<List<ActModel>> {
        return allActs
    }
}