package com.example.data

import kotlinx.coroutines.flow.Flow

class WebAppRepository(private val webAppDao: WebAppDao) {
    val allApps: Flow<List<WebApp>> = webAppDao.getAllApps()

    suspend fun getAppById(id: Long): WebApp? = webAppDao.getAppById(id)

    suspend fun insertApp(app: WebApp): Long = webAppDao.insertApp(app)

    suspend fun updateApp(app: WebApp) = webAppDao.updateApp(app)

    suspend fun deleteApp(app: WebApp) = webAppDao.deleteApp(app)
}
