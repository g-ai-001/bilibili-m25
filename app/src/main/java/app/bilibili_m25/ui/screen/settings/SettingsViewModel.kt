package app.bilibili_m25.ui.screen.settings

import androidx.lifecycle.ViewModel
import app.bilibili_m25.util.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val logger: Logger
) : ViewModel()