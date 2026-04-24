package org.example.project

import org.example.project.data.NoteLocalDataSource
import org.example.project.data.SettingsManager

expect fun provideDataSource(): NoteLocalDataSource
expect fun provideSettingsManager(): SettingsManager