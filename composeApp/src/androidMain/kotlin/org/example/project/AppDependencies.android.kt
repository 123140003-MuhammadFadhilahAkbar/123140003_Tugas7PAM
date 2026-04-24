package org.example.project

import org.example.project.data.NoteLocalDataSource
import org.example.project.data.SettingsManager

actual fun provideDataSource(): NoteLocalDataSource = DatabaseModule.dataSource
actual fun provideSettingsManager(): SettingsManager = DatabaseModule.settingsManager