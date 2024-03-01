package fr.paug.androidmakers.ui.components.session

import androidx.lifecycle.ViewModel
import fr.androidmakers.domain.model.Session
import fr.androidmakers.domain.model.Speaker
import fr.paug.androidmakers.AndroidMakersApplication
import fr.paug.androidmakers.ui.viewmodel.Lce
import fr.paug.androidmakers.util.BookmarksStore
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

class SessionDetailViewModel(
    private val sessionId: String,
    roomId: String,
    private val startTimestamp: Long,
    private val endTimestamp: Long,
) : ViewModel() {

  val sessionDetailState = combine(
      AndroidMakersApplication.instance().sessionsRepository.getSession(sessionId),
      AndroidMakersApplication.instance().roomsRepository.getRoom(roomId),
      BookmarksStore.subscribe(sessionId),
  ) { session, room, isBookmarked ->

    val exception = session.exceptionOrNull() ?: room.exceptionOrNull()
    if (exception != null) {
      Lce.Error
    } else {
      Lce.Content(
          SessionDetailState(
              session = session.getOrThrow(),
              room = room.getOrThrow(),
              speakers = getSpeakers(session.getOrThrow()),
              startTimestamp = startTimestamp,
              endTimestamp = endTimestamp,
              isBookmarked = isBookmarked,
          )
      )
    }
  }

  private suspend fun getSpeakers(session: Session): List<Speaker> {
    val allSpeakers = AndroidMakersApplication.instance().speakersRepository.getSpeakers().firstOrNull()
        //?.recover { emptyList() }
        ?.getOrThrow()
        ?: return emptyList()

    val allSpeakersById = allSpeakers.associateBy { it.id }
    return session.speakers.mapNotNull { allSpeakersById[it] }
  }

  fun bookmark(bookmarked: Boolean) {
    BookmarksStore.setBookmarked(sessionId, bookmarked)
  }
}
