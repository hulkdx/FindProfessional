package com.hulkdx.findprofessional.feature.authentication.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hulkdx.findprofessional.feature.authentication.R
import com.hulkdx.findprofessional.resources.MR

@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> (Unit),
) {
    CommonTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
            ),
        hint = stringResource(id = MR.strings.email.resourceId),
        leadingIconDrawable = R.drawable.ic_email,
        leadingIconContentDescription = stringResource(id = MR.strings.email.resourceId),
        value = value,
        onValueChanged = onValueChanged,
    )
}
