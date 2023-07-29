package com.vep1940.compose.kit.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import java.util.Locale

@Composable
@ReadOnlyComposable
fun currentJavaLocale(): Locale = with(LocalConfiguration.current){
    ConfigurationCompat.getLocales(this).get(0) ?: Locale.getDefault(Locale.Category.FORMAT)
}