package uk.co.bbc.perceptivepodcasts.channel

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils

sealed class MenuEvent
object ShowSettingsEvent: MenuEvent()
object ImportPodcastEvent: MenuEvent()
object ReloadChannelEvent: MenuEvent()


@Composable
fun FloatingActionMenu(onEvent: (MenuEvent) -> Unit) {

    val itemPadding = 56.dp + 8.dp

    var expanded by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = expanded, label = "fab expand transition")

    val tweenValue by transition.animateFloat(label = "fab expand tween") {
        if(it) 1.0f else 0.0f
    }

    Box(
        modifier = Modifier.background(color = Color.Transparent),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (tweenValue > 0.0f) {
            MenuItem(
                label = "Refresh RSS",
                alpha = tweenValue,
                offset = itemPadding * 3 * tweenValue,
                onClick = {
                    onEvent(ReloadChannelEvent)
                    expanded = !expanded
                }
            ) {
                Icon(Icons.Filled.Refresh, "Refresh Channel List")
            }

            MenuItem(
                label = "Open ZIP",
                alpha = tweenValue,
                offset = itemPadding * 2 * tweenValue,
                onClick = {
                    onEvent(ImportPodcastEvent)
                    expanded = !expanded
                }
            ) {
                Icon(Icons.Filled.AudioFile, "Import Zip File")
            }

            MenuItem(
                label = "Preferences",
                alpha = tweenValue,
                offset = itemPadding * 1 * tweenValue,
                onClick = {
                    onEvent(ShowSettingsEvent)
                    expanded = !expanded
                }
            ) {
                Icon(Icons.Filled.Settings, "Settings")
            }
        }

        MenuItem(
            rotation = tweenValue * 180.0f,
            saturation = 1.0f - tweenValue,
            onClick = { expanded = !expanded }
        ) {
            Icon(Icons.Filled.KeyboardArrowUp, "Menu")
        }
    }
}

@Composable
private fun MenuItem(
    label: String? = null,
    alpha: Float = 1.0f,
    saturation: Float = 1.0f,
    offset: Dp = 0.dp,
    onClick: () -> Unit,
    rotation: Float = 0.0f,
    content: @Composable () -> Unit
) {
    Row(modifier = Modifier.alpha(alpha)) {
        if (label != null) {
            Surface(
                modifier = Modifier.padding(8.dp).clickable { onClick() },
                elevation = 6.dp,
                color = Color.White
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = label
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        val hsl = MaterialTheme.colors.secondary.toHSL()
        FloatingActionButton(
            onClick = onClick,
            backgroundColor = Color.hsl(hsl[0], hsl[1] * saturation, hsl[2]),
            contentColor = Color.White,
            modifier = Modifier
                .padding(bottom = offset)
                .rotate(rotation),
            content = content
        )
    }
}

private fun Color.toHSL(): FloatArray {
    val outArray = FloatArray(3)
    ColorUtils.RGBToHSL(
        (this.red * 255.0f).toInt(),
        (this.green * 255.0f).toInt(),
        (this.blue * 255.0f).toInt(),
        outArray
    )
    return outArray
}
