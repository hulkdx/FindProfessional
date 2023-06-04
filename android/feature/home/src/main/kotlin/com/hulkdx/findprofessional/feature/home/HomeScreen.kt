package com.hulkdx.findprofessional.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hulkdx.findprofessional.common.feature.home.Professional
import com.hulkdx.findprofessional.core.commonui.CUSearchField
import com.hulkdx.findprofessional.core.commonui.CUSnackBar
import com.hulkdx.findprofessional.core.theme.AppTheme
import com.hulkdx.findprofessional.core.theme.h1
import com.hulkdx.findprofessional.resources.MR
import dev.icerock.moko.resources.compose.localized
import org.koin.androidx.compose.getViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = getViewModel()) {
    val error by viewModel.error.collectAsStateWithLifecycle()
    val professionals by viewModel.professionals.collectAsStateWithLifecycle()

    HomeScreen(
        professionals = professionals,
        onSearchClick = viewModel::onSearchClick,
        onLikeClick = viewModel::onLikeClick,
        onItemClick = viewModel::onItemClick,
        error = error?.localized(),
        onErrorDismissed = { viewModel.error.set(null) },
    )
}

@Composable
private fun HomeScreen(
    professionals: List<Professional>,
    onLikeClick: (Professional) -> Unit,
    onItemClick: (Professional) -> Unit,
    onSearchClick: (String) -> Unit,
    error: String?,
    onErrorDismissed: () -> Unit,
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onTertiary)
            .statusBarsPadding()
            .testTag("HomeScreen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Search(onSearchClick)
            Title()
            ProfessionalsGrid(
                professionals = professionals,
                onLikeClick = onLikeClick,
                onItemClick = onItemClick,
            )
        }
        CUSnackBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            message = error,
            onDismiss = onErrorDismissed
        )
    }
}

@Composable
private fun Search(onSearch: (String) -> Unit) {
    CUSearchField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        onSearch = onSearch,
    )
}

@Composable
private fun Title() {
    Text(
        modifier = Modifier.padding(top = 24.dp, start = 24.dp),
        text = stringResource(id = MR.strings.professionals.resourceId),
        style = h1,
    )
}

@Composable
private fun ColumnScope.ProfessionalsGrid(
    professionals: List<Professional>,
    onLikeClick: (Professional) -> Unit,
    onItemClick: (Professional) -> Unit,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .weight(1F, true),
        columns = GridCells.Fixed(2),
    ) {
        items(professionals) { professional ->
            ProfessionalItem(
                professional = professional,
                onLikeClick = onLikeClick,
                onItemClick = onItemClick,
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
            professionals = listOf(
                Professional(
                    title = "Mike Tyson",
                    description = "Boxer",
                    price = "100$/h",
                    imageUrl = "https://imgur.com/gallery/7R6wmYb"
                )
            ),
            onSearchClick = {},
            onLikeClick = {},
            onItemClick = {},
            error = "",
            onErrorDismissed = {}
        )
    }
}
