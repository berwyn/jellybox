package dev.berwyn.jellybox.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

private fun Context.getActivityWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.getActivityWindow()
        else -> null
    }

@Composable
fun FullscreenDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
            decorFitsSystemWindows = false,
        ),
    ) {
        val activityWindow  = LocalView.current.context.getActivityWindow()
        val dialogWindow = ( LocalView.current.parent as? DialogWindowProvider )?.window
        val parentView = LocalView.current.parent as View

        SideEffect {
            if (activityWindow != null && dialogWindow != null) {
                val attributes = WindowManager.LayoutParams()

                attributes.copyFrom(activityWindow.attributes)
                attributes.type = dialogWindow.attributes.type

                dialogWindow.attributes = attributes

                parentView.layoutParams = FrameLayout.LayoutParams(
                    activityWindow.decorView.width,
                    activityWindow.decorView.height,
                )
            }
        }

        content()
    }
}
