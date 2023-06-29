package dev.berwyn.jellybox.ui.previews

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers

@Preview(
    name = "Theme - Dark",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "Theme - Light",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
annotation class ThemePreviews

@Preview(
    name = "Device - Phone",
    group = "Devices",
    device = Devices.PHONE
)
@Preview(
    name = "Device - Foldable",
    group = "Devices",
    device = Devices.FOLDABLE,
)
@Preview(
    name = "Device - Tablet",
    group = "Devices",
    device = Devices.TABLET,
)
@Preview(
    name = "Device - Desktop",
    group = "Devices",
    device = Devices.DESKTOP,
)
annotation class DevicePreviews

@Preview(group = "Dynamic Colour", name = "Dynamic Colour - Blue", wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Preview(group = "Dynamic Colour", name = "Dynamic Colour - Green", wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE)
@Preview(group = "Dynamic Colour", name = "Dynamic Colour - Red", wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview(group = "Dynamic Colour", name = "Dynamic Colour - Yellow", wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
annotation class DynamicColourPreviews
