package it.vfsfitvnm.vimusic.ui.screens.localplaylist

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import com.github.doyaaaaaken.kotlincsv.client.KotlinCsvExperimental
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import it.vfsfitvnm.compose.persist.persist
import it.vfsfitvnm.compose.persist.persistList
import it.vfsfitvnm.compose.reordering.draggedItem
import it.vfsfitvnm.compose.reordering.rememberReorderingState
import it.vfsfitvnm.compose.reordering.reorder
import it.vfsfitvnm.innertube.Innertube
import it.vfsfitvnm.innertube.models.bodies.BrowseBody
import it.vfsfitvnm.innertube.models.bodies.NextBody
import it.vfsfitvnm.innertube.requests.playlistPage
import it.vfsfitvnm.innertube.requests.relatedSongs
import it.vfsfitvnm.vimusic.Database
import it.vfsfitvnm.vimusic.LocalPlayerAwareWindowInsets
import it.vfsfitvnm.vimusic.LocalPlayerServiceBinder
import it.vfsfitvnm.vimusic.R
import it.vfsfitvnm.vimusic.enums.NavigationBarPosition
import it.vfsfitvnm.vimusic.enums.PlaylistSongSortBy
import it.vfsfitvnm.vimusic.enums.RecommendationsNumber
import it.vfsfitvnm.vimusic.enums.SortOrder
import it.vfsfitvnm.vimusic.enums.ThumbnailRoundness
import it.vfsfitvnm.vimusic.enums.UiType
import it.vfsfitvnm.vimusic.models.Playlist
import it.vfsfitvnm.vimusic.models.PlaylistPreview
import it.vfsfitvnm.vimusic.models.Song
import it.vfsfitvnm.vimusic.models.SongPlaylistMap
import it.vfsfitvnm.vimusic.query
import it.vfsfitvnm.vimusic.service.isLocal
import it.vfsfitvnm.vimusic.transaction
import it.vfsfitvnm.vimusic.ui.components.LocalMenuState
import it.vfsfitvnm.vimusic.ui.components.themed.ConfirmationDialog
import it.vfsfitvnm.vimusic.ui.components.themed.FloatingActionsContainerWithScrollToTop
import it.vfsfitvnm.vimusic.ui.components.themed.HeaderIconButton
import it.vfsfitvnm.vimusic.ui.components.themed.HeaderWithIcon
import it.vfsfitvnm.vimusic.ui.components.themed.IconButton
import it.vfsfitvnm.vimusic.ui.components.themed.IconInfo
import it.vfsfitvnm.vimusic.ui.components.themed.InPlaylistMediaItemMenu
import it.vfsfitvnm.vimusic.ui.components.themed.InputTextDialog
import it.vfsfitvnm.vimusic.ui.components.themed.NowPlayingShow
import it.vfsfitvnm.vimusic.ui.components.themed.Playlist
import it.vfsfitvnm.vimusic.ui.components.themed.PlaylistsItemMenu
import it.vfsfitvnm.vimusic.ui.components.themed.SortMenu
import it.vfsfitvnm.vimusic.ui.items.SongItem
import it.vfsfitvnm.vimusic.ui.styling.Dimensions
import it.vfsfitvnm.vimusic.ui.styling.LocalAppearance
import it.vfsfitvnm.vimusic.ui.styling.favoritesIcon
import it.vfsfitvnm.vimusic.ui.styling.onOverlay
import it.vfsfitvnm.vimusic.ui.styling.overlay
import it.vfsfitvnm.vimusic.ui.styling.px
import it.vfsfitvnm.vimusic.utils.BehindMotionSwipe
import it.vfsfitvnm.vimusic.utils.LeftAction
import it.vfsfitvnm.vimusic.utils.RightActions
import it.vfsfitvnm.vimusic.utils.UiTypeKey
import it.vfsfitvnm.vimusic.utils.asMediaItem
import it.vfsfitvnm.vimusic.utils.center
import it.vfsfitvnm.vimusic.utils.color
import it.vfsfitvnm.vimusic.utils.completed
import it.vfsfitvnm.vimusic.utils.contentWidthKey
import it.vfsfitvnm.vimusic.utils.downloadedStateMedia
import it.vfsfitvnm.vimusic.utils.durationTextToMillis
import it.vfsfitvnm.vimusic.utils.enqueue
import it.vfsfitvnm.vimusic.utils.forcePlay
import it.vfsfitvnm.vimusic.utils.forcePlayAtIndex
import it.vfsfitvnm.vimusic.utils.forcePlayFromBeginning
import it.vfsfitvnm.vimusic.utils.formatAsTime
import it.vfsfitvnm.vimusic.utils.getDownloadState
import it.vfsfitvnm.vimusic.utils.isRecommendationEnabledKey
import it.vfsfitvnm.vimusic.utils.manageDownload
import it.vfsfitvnm.vimusic.utils.navigationBarPositionKey
import it.vfsfitvnm.vimusic.utils.playlistSongSortByKey
import it.vfsfitvnm.vimusic.utils.preferences
import it.vfsfitvnm.vimusic.utils.recommendationsNumberKey
import it.vfsfitvnm.vimusic.utils.rememberPreference
import it.vfsfitvnm.vimusic.utils.reorderInQueueEnabledKey
import it.vfsfitvnm.vimusic.utils.secondary
import it.vfsfitvnm.vimusic.utils.semiBold
import it.vfsfitvnm.vimusic.utils.songSortOrderKey
import it.vfsfitvnm.vimusic.utils.thumbnailRoundnessKey
import it.vfsfitvnm.vimusic.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

@KotlinCsvExperimental
@ExperimentalMaterialApi
@ExperimentalTextApi
@SuppressLint("SuspiciousIndentation")
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@UnstableApi
@Composable
fun LocalPlaylistSongs(
    playlistId: Long,
    onDelete: () -> Unit,
) {
    val (colorPalette, typography, thumbnailShape) = LocalAppearance.current
    val binder = LocalPlayerServiceBinder.current
    val menuState = LocalMenuState.current
    val uiType by rememberPreference(UiTypeKey, UiType.RiMusic)

    var playlistSongs by persistList<Song>("localPlaylist/$playlistId/songs")
    var playlistPreview by persist<PlaylistPreview?>("localPlaylist/playlist")


    var sortBy by rememberPreference(playlistSongSortByKey, PlaylistSongSortBy.Title)
    var sortOrder by rememberPreference(songSortOrderKey, SortOrder.Descending)

    var filter: String? by rememberSaveable { mutableStateOf(null) }

    LaunchedEffect(Unit, filter, sortOrder, sortBy) {
        Database.songsPlaylist(playlistId, sortBy, sortOrder).filterNotNull()
            .collect{ playlistSongs = it }
    }

    LaunchedEffect(Unit) {
        Database.singlePlaylistPreview(playlistId).collect { playlistPreview = it }
    }

    //**** SMART RECOMMENDATION
    val recommendationsNumber by rememberPreference(recommendationsNumberKey,   RecommendationsNumber.`5`)
    var isRecommendationEnabled by rememberPreference(isRecommendationEnabledKey, false)
    var relatedSongsRecommendationResult by persist<Result<Innertube.RelatedSongs?>?>(tag = "home/relatedSongsResult")
    var songBaseRecommendation by persist<Song?>("home/songBaseRecommendation")
    var positionsRecommendationList = arrayListOf<Int>()
    if (isRecommendationEnabled) {
        LaunchedEffect(Unit,isRecommendationEnabled) {
            Database.songsPlaylist(playlistId, sortBy, sortOrder).distinctUntilChanged()
                .collect { songs ->
                    val song = songs.firstOrNull()
                    if (relatedSongsRecommendationResult == null || songBaseRecommendation?.id != song?.id) {
                        relatedSongsRecommendationResult =
                            Innertube.relatedSongs(NextBody(videoId = (song?.id ?: "HZnNt9nnEhw")))
                    }
                    songBaseRecommendation = song
                }
        }
        //relatedSongsRecommendationResult?.getOrNull()?.songs?.toString()?.let { Log.d("mediaItem", "related  $it") }
        //Log.d("mediaItem","related size "+relatedSongsRecommendationResult?.getOrNull()?.songs?.size.toString())
        //val numRelated = relatedSongsResult?.getOrNull()?.songs?.size ?: 0
        //val relatedMax = playlistSongs.size
        if (relatedSongsRecommendationResult != null) {
            for (index in 0..recommendationsNumber.number) {
                positionsRecommendationList.add((0..playlistSongs.size).random())
            }
        }
        //Log.d("mediaItem","positionsList "+positionsRecommendationList.toString())
        //**** SMART RECOMMENDATION
    }

    var filterCharSequence: CharSequence
    filterCharSequence = filter.toString()

    if (!filter.isNullOrBlank())
        playlistSongs =
            playlistSongs.filter { songItem ->
                songItem.asMediaItem.mediaMetadata.title?.contains(filterCharSequence,true) ?: false
                        || songItem.asMediaItem.mediaMetadata.artist?.contains(filterCharSequence,true) ?: false
            }

    var searching by rememberSaveable { mutableStateOf(false) }

    var totalPlayTimes = 0L
    playlistSongs.forEach {
        totalPlayTimes += it.durationText?.let { it1 ->
            durationTextToMillis(it1) }?.toLong() ?: 0
    }


    val thumbnailRoundness by rememberPreference(
        thumbnailRoundnessKey,
        ThumbnailRoundness.Heavy
    )

    val sortOrderIconRotation by animateFloatAsState(
        targetValue = if (sortOrder == SortOrder.Ascending) 0f else 180f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing), label = ""
    )

    val lazyListState = rememberLazyListState()

    val reorderingState = rememberReorderingState(
        lazyListState = lazyListState,
        key = playlistSongs,
        onDragEnd = { fromIndex, toIndex ->
            Log.d("mediaItem","reoder playlist $playlistId, from $fromIndex, to $toIndex")
            query {
                Database.move(playlistId, fromIndex, toIndex)
            }
        },
        extraItemCount = 1
    )


    var isDeleting by rememberSaveable {
        mutableStateOf(false)
    }

    if (isDeleting) {
        ConfirmationDialog(
            text = stringResource(R.string.delete_playlist),
            onDismiss = { isDeleting = false },
            onConfirm = {
                query {
                    playlistPreview?.playlist?.let(Database::delete)
                }
                onDelete()
            }
        )
    }

    var isRenumbering by rememberSaveable {
        mutableStateOf(false)
    }
    if (isRenumbering) {
        ConfirmationDialog(
            text = stringResource(R.string.do_you_really_want_to_renumbering_positions_in_this_playlist),
            onDismiss = { isRenumbering = false },
            onConfirm = {
                query {
                    playlistSongs.forEachIndexed { index, song ->
                        playlistPreview?.playlist?.let {
                            Database.updateSongPosition( it.id, song.id, index )
                        }
                    }
                }

            }
        )
    }

    var isReorderDisabled by rememberPreference(reorderInQueueEnabledKey, defaultValue = true)

    val playlistThumbnailSizeDp = Dimensions.thumbnails.playlist
    val playlistThumbnailSizePx = playlistThumbnailSizeDp.px

    val thumbnailSizeDp = Dimensions.thumbnails.song
    val thumbnailSizePx = thumbnailSizeDp.px

    val rippleIndication = rememberRipple(bounded = false)

    var downloadState by remember {
        mutableStateOf(Download.STATE_STOPPED)
    }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var showConfirmDeleteDownloadDialog by remember {
        mutableStateOf(false)
    }

    var showConfirmDownloadAllDialog by remember {
        mutableStateOf(false)
    }

    var scrollToNowPlaying by remember {
        mutableStateOf(false)
    }

    var nowPlayingItem by remember {
        mutableStateOf(-1)
    }

    /*
    var showSortTypeSelectDialog by remember {
        mutableStateOf(false)
    }
     */
/*
    var showAddPlaylistSelectDialog by remember {
        mutableStateOf(false)
    }
    var isCreatingNewPlaylist by rememberSaveable {
        mutableStateOf(false)
    }
    var showPlaylistSelectDialog by remember {
        mutableStateOf(false)
    }
    */
    var listMediaItems = remember {
        mutableListOf<MediaItem>()
    }

    var selectItems by remember {
        mutableStateOf(false)
    }

    var plistId by remember {
        mutableStateOf(0L)
    }
    var plistName by remember {
        mutableStateOf(playlistPreview?.playlist?.name)
    }
    /*
    val playlistPreviews by remember {
        Database.playlistPreviews(PlaylistSortBy.Name, SortOrder.Ascending)
    }.collectAsState(initial = emptyList(), context = Dispatchers.IO)
     */

    var position by remember {
        mutableIntStateOf(0)
    }

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

                context.applicationContext.contentResolver.openOutputStream(uri)
                    ?.use { outputStream ->
                        csvWriter().open(outputStream){
                            writeRow("PlaylistBrowseId", "PlaylistName", "MediaId", "Title", "Artists", "Duration", "ThumbnailUrl")
                            if (listMediaItems.isEmpty()) {
                                playlistSongs.forEach {
                                    writeRow(
                                        playlistPreview?.playlist?.browseId,
                                        plistName,
                                        it.id,
                                        it.title,
                                        it.artistsText,
                                        it.durationText,
                                        it.thumbnailUrl
                                    )
                                }
                            } else {
                                listMediaItems.forEach {
                                    writeRow(
                                        playlistPreview?.playlist?.browseId,
                                        plistName,
                                        it.mediaId,
                                        it.mediaMetadata.title,
                                        it.mediaMetadata.artist,
                                        "",
                                        it.mediaMetadata.artworkUri
                                    )
                                }
                            }
                        }
                    }

        }


    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@rememberLauncherForActivityResult

                context.applicationContext.contentResolver.openInputStream(uri)
                    ?.use { inputStream ->
                        csvReader().open(inputStream) {
                            readAllWithHeaderAsSequence().forEachIndexed { index, row: Map<String, String> ->

                                transaction {
                                    plistId = row["PlaylistName"]?.let {
                                        Database.playlistExistByName(
                                            it
                                        )
                                    } ?: 0L

                                   if (plistId == 0L) {
                                       plistId = row["PlaylistName"]?.let {
                                           Database.insert(
                                               Playlist(
                                                   name = it,
                                                   browseId = row["PlaylistBrowseId"]
                                               )
                                           )
                                       }!!
                                   } else {
                                       /**/
                                       if (row["MediaId"] != null && row["Title"] != null) {
                                           val song =
                                               row["MediaId"]?.let {
                                                   row["Title"]?.let { it1 ->
                                                       Song(
                                                           id = it,
                                                           title = it1,
                                                           artistsText = row["Artists"],
                                                           durationText = row["Duration"],
                                                           thumbnailUrl = row["ThumbnailUrl"]
                                                       )
                                                   }
                                               }
                                            transaction {
                                                if (song != null) {
                                                    Database.insert(song)
                                                    Database.insert(
                                                        SongPlaylistMap(
                                                            songId = song.id,
                                                            playlistId = plistId,
                                                            position = index
                                                        )
                                                    )
                                                }
                                            }


                                       }
                                       /**/
                                   }
                                }

                            }
                        }

                    }
        }

    var isRenaming by rememberSaveable {
        mutableStateOf(false)
    }
    var isExporting by rememberSaveable {
        mutableStateOf(false)
    }

    if (isRenaming|| isExporting) {
        InputTextDialog(
            onDismiss = {
                isRenaming = false
                isExporting = false
            },
            title = stringResource(R.string.enter_the_playlist_name),
            value = playlistPreview?.playlist?.name ?: "",
            placeholder = stringResource(R.string.enter_the_playlist_name),
            setValue = { text ->
                if (isRenaming) {
                    query {
                        playlistPreview?.playlist?.copy(name = text)?.let(Database::update)
                    }
                }
                if(isExporting) {
                    plistName = text
                    try {
                        @SuppressLint("SimpleDateFormat")
                        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
                        exportLauncher.launch("RMPlaylist_${text.take(20)}_${dateFormat.format(Date())}")
                    } catch (e: ActivityNotFoundException) {
                        context.toast("Couldn't find an application to create documents")
                    }
                }

            }
        )
    }

    val navigationBarPosition by rememberPreference(navigationBarPositionKey, NavigationBarPosition.Left)
    val contentWidth = context.preferences.getFloat(contentWidthKey,0.8f)

    Box(
        modifier = Modifier
            .background(colorPalette.background0)
            //.fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth(if (navigationBarPosition == NavigationBarPosition.Left) 1f else contentWidth)
    ) {
        LazyColumn(
            state = reorderingState.lazyListState,
            contentPadding = LocalPlayerAwareWindowInsets.current
                .only(WindowInsetsSides.Vertical + WindowInsetsSides.End).asPaddingValues(),
            modifier = Modifier
                .background(colorPalette.background0)
                .fillMaxSize()
        ) {
            item(
                key = "header",
                contentType = 0
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    HeaderWithIcon(
                        title = playlistPreview?.playlist?.name ?: "Unknown",
                        iconId = R.drawable.playlist,
                        enabled = true,
                        showIcon = true,
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        onClick = {}
                    )

                }

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        //.background(colorPalette.background4)
                        .fillMaxSize(0.99F)
                        .background(
                            color = colorPalette.background4,
                            shape = thumbnailRoundness.shape()
                        )
                ) {

                    playlistPreview?.let {
                        Playlist(
                            playlist = it,
                            thumbnailSizeDp = playlistThumbnailSizeDp,
                            thumbnailSizePx = playlistThumbnailSizePx,
                            alternative = true,
                            showName = false,
                            modifier = Modifier
                                .padding(top = 14.dp)
                        )
                    }

                    Column (
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxHeight()
                            //.border(BorderStroke(1.dp, Color.White))
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        IconInfo(
                            title = playlistPreview?.songCount.toString(),
                            icon = painterResource(R.drawable.musical_notes)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        IconInfo(
                            title = formatAsTime(totalPlayTimes),
                            icon = painterResource(R.drawable.time)
                        )
                        if (isRecommendationEnabled) {
                            Spacer(modifier = Modifier.height(5.dp))
                            IconInfo(
                                title = positionsRecommendationList.distinct().size.toString(),
                                icon = painterResource(R.drawable.smart_shuffle)
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }



                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween, //Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {

                    HeaderIconButton(
                        icon = R.drawable.downloaded,
                        enabled = playlistSongs.isNotEmpty(),
                        color = if (playlistSongs.isNotEmpty()) colorPalette.text else colorPalette.textDisabled,
                        onClick = {
                            showConfirmDownloadAllDialog = true
                        }
                    )


                    if (showConfirmDownloadAllDialog) {
                        ConfirmationDialog(
                            text = stringResource(R.string.do_you_really_want_to_download_all),
                            onDismiss = { showConfirmDownloadAllDialog = false },
                            onConfirm = {
                                showConfirmDownloadAllDialog = false
                                isRecommendationEnabled = false
                                downloadState = Download.STATE_DOWNLOADING
                                if (playlistSongs.isNotEmpty() == true)
                                    playlistSongs.forEach {
                                        binder?.cache?.removeResource(it.asMediaItem.mediaId)
                                        query {
                                            Database.insert(
                                                Song(
                                                    id = it.asMediaItem.mediaId,
                                                    title = it.asMediaItem.mediaMetadata.title.toString(),
                                                    artistsText = it.asMediaItem.mediaMetadata.artist.toString(),
                                                    thumbnailUrl = it.thumbnailUrl,
                                                    durationText = null
                                                )
                                            )
                                        }
                                        manageDownload(
                                            context = context,
                                            songId = it.asMediaItem.mediaId,
                                            songTitle = it.asMediaItem.mediaMetadata.title.toString(),
                                            downloadState = false
                                        )
                                    }
                            }
                        )
                    }

                    HeaderIconButton(
                        icon = R.drawable.download,
                        enabled = playlistSongs.isNotEmpty(),
                        color = if (playlistSongs.isNotEmpty()) colorPalette.text else colorPalette.textDisabled,
                        onClick = {
                            showConfirmDeleteDownloadDialog = true

                        }
                    )

                    if (showConfirmDeleteDownloadDialog) {
                        ConfirmationDialog(
                            text = stringResource(R.string.do_you_really_want_to_delete_download),
                            onDismiss = { showConfirmDeleteDownloadDialog = false },
                            onConfirm = {
                                showConfirmDeleteDownloadDialog = false
                                downloadState = Download.STATE_DOWNLOADING
                                if (playlistSongs.isNotEmpty() == true)
                                    playlistSongs.forEach {
                                        binder?.cache?.removeResource(it.asMediaItem.mediaId)
                                        manageDownload(
                                            context = context,
                                            songId = it.asMediaItem.mediaId,
                                            songTitle = it.asMediaItem.mediaMetadata.title.toString(),
                                            downloadState = true
                                        )
                                    }
                            }
                        )
                    }

                    HeaderIconButton(
                        icon = R.drawable.enqueue,
                        enabled = playlistSongs.isNotEmpty(),
                        color = if (playlistSongs.isNotEmpty()) colorPalette.text else colorPalette.textDisabled,
                        onClick = {
                            playlistSongs
                                .map(Song::asMediaItem)
                                .let { mediaItems ->
                                    binder?.player?.enqueue(mediaItems)
                                }
                        }
                    )

                    HeaderIconButton(
                        icon = R.drawable.smart_shuffle,
                        enabled = true,
                        color = if (isRecommendationEnabled) colorPalette.text else colorPalette.textDisabled,
                        onClick = {
                            isRecommendationEnabled = !isRecommendationEnabled
                        }
                    )

                    HeaderIconButton(
                        icon = R.drawable.shuffle,
                        enabled = playlistSongs.isNotEmpty() == true,
                        color = if (playlistSongs.isNotEmpty() == true) colorPalette.text else colorPalette.textDisabled,
                        onClick = {
                            playlistSongs.let { songs ->
                                if (songs.isNotEmpty()) {
                                    binder?.stopRadio()
                                    binder?.player?.forcePlayFromBeginning(
                                        songs.shuffled().map(Song::asMediaItem)
                                    )
                                }
                            }
                        }
                    )


                    HeaderIconButton(
                        icon = if (isReorderDisabled) R.drawable.locked else R.drawable.unlocked,
                        enabled = playlistSongs.isNotEmpty() == true,
                        color = if (playlistSongs.isNotEmpty() == true) colorPalette.text else colorPalette.textDisabled,
                        onClick = {
                            if (sortBy == PlaylistSongSortBy.Position && sortOrder == SortOrder.Ascending) {
                                isReorderDisabled = !isReorderDisabled
                            } else {
                                context.toast("Playlist sorting only possible for ascending position")
                            }
                        }
                    )

                    HeaderIconButton(
                        icon = R.drawable.ellipsis_horizontal,
                        color = colorPalette.text, //if (playlistWithSongs?.songs?.isNotEmpty() == true) colorPalette.text else colorPalette.textDisabled,
                        enabled = true, //playlistWithSongs?.songs?.isNotEmpty() == true,
                        modifier = Modifier
                            .padding(end = 4.dp),
                        onClick = {
                            menuState.display {
                                playlistPreview?.let { playlistPreview ->
                                    PlaylistsItemMenu(
                                        onDismiss = menuState::hide,
                                        onSelect = { selectItems = true },
                                        onUncheck = {
                                            selectItems = false
                                            listMediaItems.clear()
                                        },
                                        playlist = playlistPreview,
                                        onEnqueue = {
                                            if (listMediaItems.isEmpty()) {
                                                binder?.player?.enqueue(playlistSongs.map(Song::asMediaItem))
                                            } else {
                                                binder?.player?.enqueue(listMediaItems)
                                                listMediaItems.clear()
                                                selectItems = false
                                            }
                                        },
                                        showOnSyncronize = !playlistPreview.playlist.browseId.isNullOrBlank(),
                                        onSyncronize = {
                                                transaction {
                                                    runBlocking(Dispatchers.IO) {
                                                        withContext(Dispatchers.IO) {
                                                            Innertube.playlistPage(
                                                                BrowseBody(
                                                                    browseId = playlistPreview.playlist.browseId ?: ""
                                                                )
                                                            )
                                                                ?.completed()
                                                        }
                                                    }?.getOrNull()?.let { remotePlaylist ->
                                                        Database.clearPlaylist(playlistId)

                                                        remotePlaylist.songsPage
                                                            ?.items
                                                            ?.map(Innertube.SongItem::asMediaItem)
                                                            ?.onEach(Database::insert)
                                                            ?.mapIndexed { position, mediaItem ->
                                                                SongPlaylistMap(
                                                                    songId = mediaItem.mediaId,
                                                                    playlistId = playlistId,
                                                                    position = position
                                                                )
                                                            }?.let(Database::insertSongPlaylistMaps)
                                                    }
                                                }
                                        },
                                        onRename = {
                                            isRenaming = true
                                        },
                                        onAddToPlaylist = { playlistPreview ->
                                            position =
                                                playlistPreview.songCount.minus(1) ?: 0
                                            //Log.d("mediaItem", " maxPos in Playlist $it ${position}")
                                            if (position > 0) position++ else position = 0
                                            //Log.d("mediaItem", "next initial pos ${position}")
                                            if (listMediaItems.isEmpty()) {
                                                playlistSongs.forEachIndexed { index, song ->
                                                    transaction {
                                                        Database.insert(song.asMediaItem)
                                                        Database.insert(
                                                            SongPlaylistMap(
                                                                songId = song.asMediaItem.mediaId,
                                                                playlistId = playlistPreview.playlist.id,
                                                                position = position + index
                                                            )
                                                        )
                                                    }
                                                    //Log.d("mediaItemPos", "added position ${position + index}")
                                                }
                                            } else {
                                                listMediaItems.forEachIndexed { index, song ->
                                                    //Log.d("mediaItemMaxPos", position.toString())
                                                    transaction {
                                                        Database.insert(song)
                                                        Database.insert(
                                                            SongPlaylistMap(
                                                                songId = song.mediaId,
                                                                playlistId = playlistPreview.playlist.id,
                                                                position = position + index
                                                            )
                                                        )
                                                    }
                                                    //Log.d("mediaItemPos", "add position $position")
                                                }
                                                listMediaItems.clear()
                                                selectItems = false
                                            }
                                        },
                                        onRenumberPositions = {
                                            isRenumbering = true
                                        },
                                        onDelete = {
                                            isDeleting = true
                                        },
                                        showonListenToYT = !playlistPreview.playlist.browseId.isNullOrBlank(),
                                        onListenToYT = {
                                            binder?.player?.pause()
                                            uriHandler.openUri(
                                                "https://youtube.com/playlist?list=${
                                                    playlistPreview?.playlist?.browseId?.removePrefix(
                                                        "VL"
                                                    )
                                                }"
                                            )
                                        },
                                        onExport = {
                                            isExporting = true
                                        },
                                        /*
                                        onImport = {
                                            try {
                                                importLauncher.launch(
                                                    arrayOf(
                                                        "text/csv",
                                                        "text/txt"
                                                    )
                                                )
                                            } catch (e: ActivityNotFoundException) {
                                                context.toast("Couldn't find an application to open documents")
                                            }
                                        }
                                        */
                                    )
                                }
                                /*
                                Menu {
                                    playlistPreview?.playlist?.browseId?.let { browseId ->
                                        MenuEntry(
                                            icon = R.drawable.sync,
                                            text = stringResource(R.string.sync),
                                            onClick = {
                                                menuState.hide()
                                                transaction {
                                                    runBlocking(Dispatchers.IO) {
                                                        withContext(Dispatchers.IO) {
                                                            Innertube.playlistPage(
                                                                BrowseBody(
                                                                    browseId = browseId
                                                                )
                                                            )
                                                                ?.completed()
                                                        }
                                                    }?.getOrNull()?.let { remotePlaylist ->
                                                        Database.clearPlaylist(playlistId)

                                                        remotePlaylist.songsPage
                                                            ?.items
                                                            ?.map(Innertube.SongItem::asMediaItem)
                                                            ?.onEach(Database::insert)
                                                            ?.mapIndexed { position, mediaItem ->
                                                                SongPlaylistMap(
                                                                    songId = mediaItem.mediaId,
                                                                    playlistId = playlistId,
                                                                    position = position
                                                                )
                                                            }?.let(Database::insertSongPlaylistMaps)
                                                    }
                                                }
                                            }
                                        )
                                    }

                                    MenuEntry(
                                        icon = R.drawable.pencil,
                                        text = stringResource(R.string.rename),
                                        onClick = {
                                            menuState.hide()
                                            isRenaming = true
                                        }
                                    )



                                    MenuEntry(
                                        icon = R.drawable.add_in_playlist,
                                        text = stringResource(R.string.add_to_playlist),
                                        onClick = {
                                            menuState.hide()
                                            if (!selectItems)
                                                showAddPlaylistSelectDialog = true  else
                                                showPlaylistSelectDialog = true
                                        }
                                    )


                                    MenuEntry(
                                        icon = R.drawable.position,
                                        text = stringResource(R.string.renumber_songs_positions),
                                        onClick = {
                                            menuState.hide()
                                            isRenumbering = true
                                        }
                                    )

                                    MenuEntry(
                                        icon = R.drawable.trash,
                                        text = stringResource(R.string.delete),
                                        onClick = {
                                            menuState.hide()
                                            isDeleting = true
                                        }
                                    )

                                    if (!playlistPreview?.playlist?.browseId.isNullOrBlank())
                                    MenuEntry(
                                        icon = R.drawable.play,
                                        text = stringResource(R.string.listen_on_youtube),
                                        onClick = {
                                            menuState.hide()
                                            binder?.player?.pause()
                                            uriHandler.openUri(
                                                "https://youtube.com/playlist?list=${
                                                    playlistPreview?.playlist?.browseId?.removePrefix(
                                                        "VL"
                                                    )
                                                }"
                                            )
                                        }
                                    )

                                    val ytNonInstalled =
                                        stringResource(R.string.it_seems_that_youtube_music_is_not_installed)
                                    if (!playlistPreview?.playlist?.browseId.isNullOrBlank())
                                    MenuEntry(
                                        icon = R.drawable.musical_notes,
                                        text = stringResource(R.string.listen_on_youtube_music),
                                        onClick = {
                                            menuState.hide()
                                            binder?.player?.pause()
                                            if (!launchYouTubeMusic(
                                                    context,
                                                    "playlist?list=${
                                                        playlistPreview?.playlist?.browseId?.removePrefix(
                                                            "VL"
                                                        )
                                                    }"
                                                )
                                            )
                                                context.toast(ytNonInstalled)
                                        }
                                    )

                                }
                                */
                            }
                        }
                    )
                    //}


                }

                Spacer(modifier = Modifier.height(10.dp))

                /*        */
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween, //Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {

                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween, //Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth(0.50f)
                    ) {
                        HeaderIconButton(
                            onClick = { searching = !searching },
                            icon = R.drawable.search_circle,
                            color = colorPalette.text,
                            iconSize = 24.dp
                        )
                        HeaderIconButton(
                            icon = R.drawable.locate,
                            enabled = playlistSongs.isNotEmpty(),
                            color = if (playlistSongs.isNotEmpty()) colorPalette.text else colorPalette.textDisabled,
                            onClick = {
                                nowPlayingItem = -1
                                scrollToNowPlaying = false
                                playlistSongs
                                    .forEachIndexed { index, song ->
                                        if (song.asMediaItem.mediaId == binder?.player?.currentMediaItem?.mediaId)
                                            nowPlayingItem = index
                                    }

                                if (nowPlayingItem > -1)
                                    scrollToNowPlaying = true
                            }
                        )
                        LaunchedEffect(scrollToNowPlaying) {
                            if (scrollToNowPlaying)
                                lazyListState.scrollToItem(nowPlayingItem, 1)
                            scrollToNowPlaying = false
                        }
                    }

                    Spacer(
                        modifier = Modifier
                            .width(30.dp)
                    )

                    BasicText(
                        text = when (sortBy) {
                            PlaylistSongSortBy.AlbumYear -> stringResource(R.string.sort_album_year)
                            PlaylistSongSortBy.Position -> stringResource(R.string.sort_position)
                            PlaylistSongSortBy.Title -> stringResource(R.string.sort_title)
                            PlaylistSongSortBy.DatePlayed -> stringResource(R.string.sort_date_played)
                            PlaylistSongSortBy.Artist -> stringResource(R.string.sort_artist)
                            PlaylistSongSortBy.PlayTime -> stringResource(R.string.sort_listening_time)
                        },
                        style = typography.xs.semiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable {
                                menuState.display{
                                    SortMenu(
                                        title = stringResource(R.string.sorting_order),
                                        onDismiss = menuState::hide,
                                        onTitle = { sortBy = PlaylistSongSortBy.Title },
                                        onAlbumYear = { sortBy = PlaylistSongSortBy.AlbumYear },
                                        onDatePlayed = { sortBy = PlaylistSongSortBy.DatePlayed },
                                        onPosition = { sortBy = PlaylistSongSortBy.Position },
                                        onArtist = { sortBy = PlaylistSongSortBy.Artist },
                                        onPlayTime = { sortBy = PlaylistSongSortBy.PlayTime },
                                    )
                                }
                                //showSortTypeSelectDialog = true
                            }
                    )

                    /*
                    if (showSortTypeSelectDialog)
                        ValueSelectorDialog(
                            onDismiss = { showSortTypeSelectDialog = false },
                            title = stringResource(R.string.sorting_order),
                            selectedValue = sortBy,
                            values = enumValues<PlaylistSongSortBy>().toList(),
                            onValueSelected = { sortBy = it },
                            valueText = {
                                when (it) {
                                    PlaylistSongSortBy.AlbumYear -> stringResource(R.string.sort_album_year)
                                    PlaylistSongSortBy.Position -> stringResource(R.string.sort_position)
                                    PlaylistSongSortBy.Title -> stringResource(R.string.sort_title)
                                    PlaylistSongSortBy.DatePlayed -> stringResource(R.string.sort_date_played)
                                    PlaylistSongSortBy.Artist -> stringResource(R.string.sort_artist)
                                    PlaylistSongSortBy.PlayTime -> stringResource(R.string.sort_listening_time)
                                }
                            }
                        )

                     */

                    /*
                    HeaderIconButton(
                        icon = R.drawable.calendar,
                        color = if (sortBy == PlaylistSongSortBy.AlbumYear) colorPalette.text else colorPalette.textDisabled,
                        onClick = { sortBy = PlaylistSongSortBy.AlbumYear }
                    )

                    HeaderIconButton(
                        icon = R.drawable.up_right_arrow,
                        color = if (sortBy == PlaylistSongSortBy.DatePlayed) colorPalette.text else colorPalette.textDisabled,
                        onClick = { sortBy = PlaylistSongSortBy.DatePlayed }
                    )

                    HeaderIconButton(
                        icon = R.drawable.position,
                        color = if (sortBy == PlaylistSongSortBy.Position) colorPalette.text else colorPalette.textDisabled,
                        onClick = { sortBy = PlaylistSongSortBy.Position }
                    )

                    HeaderIconButton(
                        icon = R.drawable.trending,
                        color = if (sortBy == PlaylistSongSortBy.PlayTime) colorPalette.text else colorPalette.textDisabled,
                        onClick = { sortBy = PlaylistSongSortBy.PlayTime }
                    )

                    HeaderIconButton(
                        icon = R.drawable.text,
                        color = if (sortBy == PlaylistSongSortBy.Title) colorPalette.text else colorPalette.textDisabled,
                        onClick = { sortBy = PlaylistSongSortBy.Title }
                    )

                    HeaderIconButton(
                        icon = R.drawable.person,
                        color = if (sortBy == PlaylistSongSortBy.Artist) colorPalette.text else colorPalette.textDisabled,
                        onClick = { sortBy = PlaylistSongSortBy.Artist }
                    )
                     */

                    HeaderIconButton(
                        icon = R.drawable.arrow_up,
                        color = colorPalette.text,
                        onClick = { sortOrder = !sortOrder },
                        modifier = Modifier
                            .graphicsLayer { rotationZ = sortOrderIconRotation }
                    )

                }


                Row (
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .padding(all = 10.dp)
                        .fillMaxWidth()
                ) {
                    AnimatedVisibility(visible = searching) {
                        val focusRequester = remember { FocusRequester() }
                        val focusManager = LocalFocusManager.current
                        val keyboardController = LocalSoftwareKeyboardController.current

                        LaunchedEffect(searching) {
                            focusRequester.requestFocus()
                        }

                        BasicTextField(
                            value = filter ?: "",
                            onValueChange = { filter = it },
                            textStyle = typography.xs.semiBold,
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                if (filter.isNullOrBlank()) filter = ""
                                focusManager.clearFocus()
                            }),
                            cursorBrush = SolidColor(colorPalette.text),
                            decorationBox = { innerTextField ->
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 10.dp)
                                ) {
                                    IconButton(
                                        onClick = {},
                                        icon = R.drawable.search,
                                        color = colorPalette.favoritesIcon,
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .size(16.dp)
                                    )
                                }
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 30.dp)
                                ) {
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = filter?.isEmpty() ?: true,
                                        enter = fadeIn(tween(100)),
                                        exit = fadeOut(tween(100)),
                                    ) {
                                        BasicText(
                                            text = stringResource(R.string.search),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            style = typography.xs.semiBold.secondary.copy(color = colorPalette.textDisabled)
                                        )
                                    }

                                    innerTextField()
                                }
                            },
                            modifier = Modifier
                                .height(30.dp)
                                .fillMaxWidth()
                                .background(
                                    colorPalette.background4,
                                    shape = thumbnailRoundness.shape()
                                )
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    if (!it.hasFocus) {
                                        keyboardController?.hide()
                                        if (filter?.isBlank() == true) {
                                            filter = null
                                            searching = false
                                        }
                                    }
                                }
                        )
                    }
                }



            }

            itemsIndexed(
                items = playlistSongs ?: emptyList(),
                key = { _, song -> song.id },
                contentType = { _, song -> song },
            ) { index, song ->

                if (index in positionsRecommendationList.distinct()) {
                    val songRecommended = relatedSongsRecommendationResult?.getOrNull()?.songs?.shuffled()
                        ?.lastOrNull()
                    val duration = songRecommended?.durationText
                    songRecommended?.asMediaItem?.let {
                        SongItem(
                            song = it,
                            duration = duration,
                            isRecommended = true,
                            thumbnailSizeDp = thumbnailSizeDp,
                            thumbnailSizePx = thumbnailSizePx,
                            isDownloaded = false,
                            onDownloadClick = {},
                            downloadState = Download.STATE_STOPPED,
                            trailingContent = {},
                            onThumbnailContent = {},
                            modifier = Modifier
                                .clickable {
                                    binder?.stopRadio()
                                    binder?.player?.forcePlay(it)
                                }

                        )
                    }
                }

                BehindMotionSwipe(
                    content = {
                        val isLocal by remember { derivedStateOf { song.asMediaItem.isLocal } }
                        downloadState = getDownloadState(song.asMediaItem.mediaId)
                        val isDownloaded = if (!isLocal) downloadedStateMedia(song.asMediaItem.mediaId) else true
                        //if (isDownloaded && !listDownloadedMedia.contains(song)) listDownloadedMedia.add(song)
                        //if (!isDownloaded) listDownloadedMedia.dropWhile {  it.asMediaItem.mediaId == song.asMediaItem.mediaId } else listDownloadedMedia.add(song)
                        //Log.d("mediaItem", "loop items listDownloadedMedia ${listDownloadedMedia.distinct().size} ${listDownloadedMedia.distinct()}")
                        SongItem(
                            song = song,
                            isDownloaded = isDownloaded,
                            onDownloadClick = {
                                binder?.cache?.removeResource(song.asMediaItem.mediaId)
                                query {
                                    Database.insert(
                                        Song(
                                            id = song.asMediaItem.mediaId,
                                            title = song.asMediaItem.mediaMetadata.title.toString(),
                                            artistsText = song.asMediaItem.mediaMetadata.artist.toString(),
                                            thumbnailUrl = song.thumbnailUrl,
                                            durationText = null
                                        )
                                    )
                                }

                                if (!isLocal) {
                                    manageDownload(
                                        context = context,
                                        songId = song.asMediaItem.mediaId,
                                        songTitle = song.asMediaItem.mediaMetadata.title.toString(),
                                        downloadState = isDownloaded
                                    )
                                }
                                //if (isDownloaded) listDownloadedMedia.dropWhile { it.asMediaItem.mediaId == song.asMediaItem.mediaId } else listDownloadedMedia.add(song)
                                //Log.d("mediaItem", "manageDownload click isDownloaded ${isDownloaded} listDownloadedMedia ${listDownloadedMedia.distinct().size}")
                            },
                            downloadState = downloadState,
                            thumbnailSizePx = thumbnailSizePx,
                            thumbnailSizeDp = thumbnailSizeDp,
                            trailingContent = {
                                val checkedState = remember { mutableStateOf(false) }
                                if (selectItems)
                                    Checkbox(
                                        checked = checkedState.value,
                                        onCheckedChange = {
                                            checkedState.value = it
                                            if (it) listMediaItems.add(song.asMediaItem) else
                                                listMediaItems.remove(song.asMediaItem)
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = colorPalette.accent,
                                            uncheckedColor = colorPalette.text
                                        )
                                    )

                                if (!isReorderDisabled) {
                                    IconButton(
                                        icon = R.drawable.reorder,
                                        color = colorPalette.textDisabled,
                                        indication = rippleIndication,
                                        onClick = {},
                                        modifier = Modifier
                                            .reorder(
                                                reorderingState = reorderingState,
                                                index = index
                                            )
                                            .size(18.dp)
                                    )
                                }
                            },
                            onThumbnailContent = {
                                if (sortBy == PlaylistSongSortBy.PlayTime) {
                                    BasicText(
                                        text = song.formattedTotalPlayTime,
                                        style = typography.xxs.semiBold.center.color(colorPalette.onOverlay),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        colorPalette.overlay
                                                    )
                                                ),
                                                shape = thumbnailShape
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .align(Alignment.BottomCenter)
                                    )
                                }

                                if (nowPlayingItem > -1)
                                    NowPlayingShow(song.asMediaItem.mediaId)
                            },
                            modifier = Modifier
                                .combinedClickable(
                                    onLongClick = {
                                        menuState.display {
                                            InPlaylistMediaItemMenu(
                                                playlistId = playlistId,
                                                positionInPlaylist = index,
                                                song = song,
                                                onDismiss = menuState::hide
                                            )
                                        }
                                    },
                                    onClick = {
                                        playlistSongs
                                            .map(Song::asMediaItem)
                                            .let { mediaItems ->
                                                binder?.stopRadio()
                                                binder?.player?.forcePlayAtIndex(mediaItems, index)
                                            }
                                    }
                                )
                                //.animateItemPlacement(reorderingState)
                                .draggedItem(reorderingState = reorderingState, index = index)
                                .background(color = colorPalette.background0)
                        )
                    },
                leftActionsContent = {
                    if (!reorderingState.isDragging)
                        LeftAction(
                            icon = R.drawable.enqueue,
                            backgroundColor = Color.Transparent, //colorPalette.background4,
                            onClick = {
                                binder?.player?.enqueue( song.asMediaItem )
                            }
                        )
                },
                rightActionsContent = {
                    if (!reorderingState.isDragging) {
                        var likedAt by remember {
                            mutableStateOf<Long?>(null)
                        }
                        LaunchedEffect(Unit, song.asMediaItem.mediaId) {
                            Database.likedAt(song.asMediaItem.mediaId).collect { likedAt = it }
                        }

                        RightActions(
                            iconAction1 = if (likedAt == null) R.drawable.heart_outline else R.drawable.heart,
                            backgroundColorAction1 = Color.Transparent, //colorPalette.background4,
                            onClickAction1 = {
                                query {
                                    if (Database.like(
                                            song.asMediaItem.mediaId,
                                            if (likedAt == null) System.currentTimeMillis() else null
                                        ) == 0
                                    ) {
                                        Database.insert(song.asMediaItem, Song::toggleLike)
                                    }
                                }
                            },
                            iconAction2 = R.drawable.trash,
                            backgroundColorAction2 = Color.Transparent, //colorPalette.iconButtonPlayer,
                            onClickAction2 = {
                                transaction {
                                    Database.move(playlistId, index, Int.MAX_VALUE)
                                    Database.delete(
                                        SongPlaylistMap(
                                            song.id,
                                            playlistId,
                                            Int.MAX_VALUE
                                        )
                                    )
                                }
                            }
                        )

                    }
                }
                )

            }
        }

        FloatingActionsContainerWithScrollToTop(lazyListState = lazyListState)

        if(uiType == UiType.ViMusic)
        FloatingActionsContainerWithScrollToTop(
            lazyListState = lazyListState,
            iconId = R.drawable.shuffle,
            visible = !reorderingState.isDragging,
            onClick = {
                playlistSongs.let { songs ->
                    if (songs.isNotEmpty()) {
                        binder?.stopRadio()
                        binder?.player?.forcePlayFromBeginning(
                            songs.shuffled().map(Song::asMediaItem)
                        )
                    }
                }
            }
        )


    }
}







