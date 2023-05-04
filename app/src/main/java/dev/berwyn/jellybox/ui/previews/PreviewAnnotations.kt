package dev.berwyn.jellybox.ui.previews

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers

@Preview(
    name = "Dark Theme",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "Light Theme",
    group = "Themes",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
annotation class ThemePreviews

@Preview(
    name = "Phone",
    group = "Devices",
    device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480"
)
@Preview(
    name = "Foldable",
    group = "Devices",
    device = "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480"
)
@Preview(
    name = "Tablet",
    group = "Devices",
    device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"
)
annotation class DevicePreviews

@Preview(name = "Dynamic Colour - Blue", wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Preview(name = "Dynamic Colour - Red", wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE)
@Preview(name = "Dynamic Colour - Green", wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview(name = "Dynamic Colour - Yellow", wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
annotation class DynamicColourPreviews
