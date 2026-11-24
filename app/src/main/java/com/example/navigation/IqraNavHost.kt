package com.example.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocalLibrary
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.ui.components.AmbientLibraryBackdrop
import com.example.ui.components.ImportFilesBottomSheet
import com.example.ui.components.IqraGlassBottomBar
import com.example.ui.components.IqraGlassNavRail
import com.example.ui.components.IqraNavDestination
import com.example.ui.screens.AboutDeveloperScreen
import com.example.ui.screens.AuthorDetailScreen
import com.example.ui.screens.AuthorsScreen
import com.example.ui.screens.BookDetailScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.ReaderScreen
import com.example.ui.viewmodel.LibraryViewModel

object IqraRoutes {
    const val ROUTE_LIBRARY = "library"
    const val ROUTE_AUTHORS = "authors"
    const val ROUTE_FAVORITES = "favorites"
    const val ROUTE_ABOUT = "about"
    const val ROUTE_AUTHOR_DETAIL = "author/{authorId}"
    const val ROUTE_BOOK_DETAIL = "book/{bookId}"
    const val ROUTE_READER = "reader/{bookId}"

    fun authorDetail(authorId: String): String = "author/$authorId"
    fun bookDetail(bookId: String): String = "book/$bookId"
    fun reader(bookId: String): String = "reader/$bookId"
}

@Composable
fun IqraAppScaffold(
    viewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isImporting by viewModel.isImporting.collectAsStateWithLifecycle()
    val importFeedback by viewModel.importFeedback.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showImportSheet by remember { mutableStateOf(false) }
    var importAuthorNameInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(importFeedback) {
        val message = importFeedback ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearImportFeedback()
    }

    val pickFilesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.importPickedFiles(uris = uris, authorName = importAuthorNameInput)
            showImportSheet = false
        }
    }

    val linkFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { treeUri ->
        if (treeUri != null) {
            viewModel.linkAndSyncFolder(treeUri)
            showImportSheet = false
        }
    }

    val topDestinations = listOf(
        IqraNavDestination(
            route = IqraRoutes.ROUTE_LIBRARY,
            label = stringResource(R.string.nav_library),
            selectedIcon = Icons.Filled.LocalLibrary,
            unselectedIcon = Icons.Outlined.LocalLibrary,
            testTag = "nav_library"
        ),
        IqraNavDestination(
            route = IqraRoutes.ROUTE_AUTHORS,
            label = stringResource(R.string.nav_authors),
            selectedIcon = Icons.Filled.Groups,
            unselectedIcon = Icons.Outlined.Groups,
            testTag = "nav_authors"
        ),
        IqraNavDestination(
            route = IqraRoutes.ROUTE_FAVORITES,
            label = stringResource(R.string.nav_favorites),
            selectedIcon = Icons.Filled.Bookmarks,
            unselectedIcon = Icons.Outlined.Bookmarks,
            testTag = "nav_favorites"
        ),
        IqraNavDestination(
            route = IqraRoutes.ROUTE_ABOUT,
            label = stringResource(R.string.nav_about),
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info,
            testTag = "nav_about"
        )
    )

    val showChromeNavigation = currentRoute in topDestinations.map { it.route }

    if (showImportSheet) {
        ImportFilesBottomSheet(
            existingAuthors = uiState.authors,
            authorNameInput = importAuthorNameInput,
            onAuthorNameChange = { importAuthorNameInput = it },
            linkedFolderName = uiState.readerPreferences.linkedPdfFolderName,
            isImporting = isImporting,
            onPickFilesClick = {
                pickFilesLauncher.launch(arrayOf("application/pdf", "text/plain"))
            },
            onLinkFolderClick = {
                linkFolderLauncher.launch(null)
            },
            onSyncFolderNowClick = {
                viewModel.syncLinkedFolderNow()
                showImportSheet = false
            },
            onUnlinkFolderClick = {
                viewModel.unlinkPdfFolder()
            },
            onDismiss = { showImportSheet = false }
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedWidth = maxWidth >= 600.dp

        AmbientLibraryBackdrop()

        Row(modifier = Modifier.fillMaxSize()) {
            if (isExpandedWidth && showChromeNavigation) {
                IqraGlassNavRail(
                    destinations = topDestinations,
                    currentRoute = currentRoute,
                    onNavigate = { target ->
                        navController.navigate(target) {
                            popUpTo(IqraRoutes.ROUTE_LIBRARY) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.statusBars
            ) { innerPadding ->
                val bottomBarAllowance = if (showChromeNavigation && !isExpandedWidth) {
                    PaddingValues(bottom = 86.dp)
                } else {
                    PaddingValues(bottom = 12.dp)
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = IqraRoutes.ROUTE_LIBRARY,
                        enterTransition = { fadeIn(tween(260)) },
                        exitTransition = { fadeOut(tween(220)) },
                        popEnterTransition = { fadeIn(tween(240)) },
                        popExitTransition = { fadeOut(tween(200)) }
                    ) {
                        composable(IqraRoutes.ROUTE_LIBRARY) {
                            LibraryScreen(
                                uiState = uiState,
                                onSearchQueryChange = viewModel::setSearchQuery,
                                onSelectAuthorFilter = viewModel::selectAuthorFilter,
                                onToggleShelfMode = viewModel::toggleShelfMode,
                                onOpenImportSheet = { showImportSheet = true },
                                onQuickPickFiles = { showImportSheet = true },
                                onQuickLinkFolder = { linkFolderLauncher.launch(null) },
                                onSyncFolderNow = viewModel::syncLinkedFolderNow,
                                onAuthorClick = { author ->
                                    navController.navigate(IqraRoutes.authorDetail(author.id))
                                },
                                onBookClick = { book ->
                                    navController.navigate(IqraRoutes.bookDetail(book.id))
                                },
                                onToggleBookmark = viewModel::toggleBookmark,
                                onResumeBook = { book ->
                                    navController.navigate(IqraRoutes.reader(book.id))
                                },
                                contentBottomPadding = bottomBarAllowance
                            )
                        }

                        composable(IqraRoutes.ROUTE_AUTHORS) {
                            AuthorsScreen(
                                shelves = uiState.shelves,
                                isExpandedWidth = isExpandedWidth,
                                onAuthorClick = { author ->
                                    navController.navigate(IqraRoutes.authorDetail(author.id))
                                },
                                contentBottomPadding = bottomBarAllowance
                            )
                        }

                        composable(IqraRoutes.ROUTE_FAVORITES) {
                            FavoritesScreen(
                                bookmarkedBooks = uiState.bookmarkedBooks,
                                readingInProgressBooks = uiState.readingInProgressBooks,
                                onBookClick = { book ->
                                    navController.navigate(IqraRoutes.bookDetail(book.id))
                                },
                                onToggleBookmark = viewModel::toggleBookmark,
                                onOpenReader = { book ->
                                    navController.navigate(IqraRoutes.reader(book.id))
                                },
                                contentBottomPadding = bottomBarAllowance
                            )
                        }

                        composable(IqraRoutes.ROUTE_ABOUT) {
                            AboutDeveloperScreen(
                                totalAuthors = uiState.authors.size,
                                totalBooks = uiState.totalBooksCount,
                                contentBottomPadding = bottomBarAllowance
                            )
                        }

                        composable(
                            route = IqraRoutes.ROUTE_AUTHOR_DETAIL,
                            arguments = listOf(navArgument("authorId") { type = NavType.StringType }),
                            enterTransition = {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(280)) + fadeIn()
                            },
                            popExitTransition = {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(240)) + fadeOut()
                            }
                        ) { backStackEntry ->
                            val authorId = backStackEntry.arguments?.getString("authorId").orEmpty()
                            val author by viewModel.observeAuthor(authorId).collectAsStateWithLifecycle(initialValue = null)
                            val books by viewModel.observeBooksForAuthor(authorId).collectAsStateWithLifecycle(initialValue = emptyList())
                            AuthorDetailScreen(
                                author = author,
                                books = books,
                                onBack = { navController.popBackStack() },
                                onBookClick = { book ->
                                    navController.navigate(IqraRoutes.bookDetail(book.id))
                                },
                                onToggleBookmark = viewModel::toggleBookmark
                            )
                        }

                        composable(
                            route = IqraRoutes.ROUTE_BOOK_DETAIL,
                            arguments = listOf(navArgument("bookId") { type = NavType.StringType }),
                            enterTransition = {
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(280)) + fadeIn()
                            },
                            popExitTransition = {
                                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(240)) + fadeOut()
                            }
                        ) { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId").orEmpty()
                            val book by viewModel.observeBook(bookId).collectAsStateWithLifecycle(initialValue = null)
                            BookDetailScreen(
                                book = book,
                                onBack = { navController.popBackStack() },
                                onOpenAuthorSection = { authorId ->
                                    navController.navigate(IqraRoutes.authorDetail(authorId))
                                },
                                onOpenReader = { selectedBook ->
                                    navController.navigate(IqraRoutes.reader(selectedBook.id))
                                },
                                onToggleBookmark = viewModel::toggleBookmark,
                                onUpdateBookMetadata = viewModel::updateBookTitleAndAuthor,
                                onRemoveBook = viewModel::removeBook
                            )
                        }

                        composable(
                            route = IqraRoutes.ROUTE_READER,
                            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val bookId = backStackEntry.arguments?.getString("bookId").orEmpty()
                            val book by viewModel.observeBook(bookId).collectAsStateWithLifecycle(initialValue = null)
                            ReaderScreen(
                                book = book,
                                preferences = uiState.readerPreferences,
                                renderPdfPage = viewModel::renderPdfPage,
                                onBack = { navController.popBackStack() },
                                onPageChange = { nextPage ->
                                    viewModel.updateCurrentPage(bookId, nextPage)
                                },
                                onToggleBookmark = viewModel::toggleBookmark,
                                onFontSizeChange = viewModel::setReaderFontSize,
                                onThemeModeChange = viewModel::setReaderThemeMode
                            )
                        }
                    }

                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = if (showChromeNavigation && !isExpandedWidth) 96.dp else 16.dp)
                    )

                    if (!isExpandedWidth && showChromeNavigation) {
                        IqraGlassBottomBar(
                            destinations = topDestinations,
                            currentRoute = currentRoute,
                            onNavigate = { target ->
                                navController.navigate(target) {
                                    popUpTo(IqraRoutes.ROUTE_LIBRARY) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}
