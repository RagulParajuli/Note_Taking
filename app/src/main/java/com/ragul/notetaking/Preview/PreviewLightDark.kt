package com.ragul.notetaking.Preview

import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
annotation class PreviewLightDark