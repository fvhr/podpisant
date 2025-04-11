package bob.colbaskin.iubip_spring2025.designsystem

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun PagerWithIndicator(
    pageCount: Int,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    content: @Composable (pageIndex: Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState
        ) { page ->
            content(page)
        }

        Spacer(modifier = Modifier.height(16.dp))

        PagerIndicator(
            currentPage = pagerState.currentPage,
            pageCount = pageCount
        )
    }
}

@Composable
private fun PagerIndicator(
    currentPage: Int,
    pageCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { pageIndex ->
            val isSelected = pageIndex == currentPage
            val shape = if (isSelected) {
                RoundedCornerShape(percent = 50)
            } else {
                CircleShape
            }
            val width by animateDpAsState(
                targetValue = if (isSelected) {
                    16.dp
                } else {
                    8.dp
                },
                label = "widthAnimation",
            )
            val color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            }

            Box(
                modifier = Modifier
                    .width(width)
                    .size(8.dp)
                    .clip(shape)
                    .background(color)
            )
        }
    }
}