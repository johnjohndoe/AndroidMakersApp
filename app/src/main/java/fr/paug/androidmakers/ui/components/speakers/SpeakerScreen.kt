package fr.paug.androidmakers.ui.components.speakers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.androidmakers.store.model.Speaker
import fr.paug.androidmakers.R
import fr.paug.androidmakers.ui.components.LoadingLayout
import fr.paug.androidmakers.ui.viewmodel.Lce

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakerScreen(
    modifier: Modifier = Modifier,
    speakerViewModel: SpeakerViewModel,
) {

  val speakersUiState by speakerViewModel.uiState.collectAsState(
      initial = Lce.Loading
  )

  when (val state = speakersUiState) {
    Lce.Loading -> LoadingLayout()
    Lce.Error -> {

    }

    is Lce.Content -> {

      var text by rememberSaveable { mutableStateOf("") }
      var active by rememberSaveable { mutableStateOf(false) }

      Box(Modifier.fillMaxSize()) {
        Box(Modifier
            .semantics { isContainer = true }
            .zIndex(1f)
            .fillMaxWidth()) {

          SearchBar(
              modifier = Modifier.align(Alignment.TopCenter),
              query = text,
              onQueryChange = { text = it },
              onSearch = { active = false },
              active = active,
              onActiveChange = {
                active = it
              },
              windowInsets = WindowInsets(0, 0, 0, 0),
              placeholder = { Text(stringResource(id = R.string.speaker_search_placeholder)) },
              leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) }
          ) {
            LazyColumn {
              items(state.content.speakers.filter { it.name?.contains(text, ignoreCase = true) == true }) { speaker ->
                SpeakerItem(
                    speaker = speaker
                )
              }
            }
          }
        }

        AnimatedVisibility(
            visible = !active,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
          LazyColumn(
              contentPadding = PaddingValues(start = 0.dp, top = 72.dp, end = 0.dp, bottom = 16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(state.content.speakers.filter { it.name?.contains(text, ignoreCase = true) == true }) { speaker ->
              SpeakerItem(
                  speaker = speaker
              )
            }
          }
        }
      }
    }

  }

}

@Composable
fun SpeakerItem(
    modifier: Modifier = Modifier,
    speaker: Speaker) {

  ListItem(
      modifier = modifier,
      headlineContent = {
        Text(
            text = speaker.name.orEmpty(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
      },
      supportingContent = {
        Text(
            text = speaker.company.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
        )
      },
      leadingContent = {
        AsyncImage(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            model = ImageRequest.Builder(LocalContext.current)
                .data(speaker.photoUrl)
                .build(),
            contentDescription = stringResource(R.string.title_speakers)
        )
      },
      trailingContent = {},
//      colors =,
//      tonalElevation =,
//      shadowElevation =
  )
}
