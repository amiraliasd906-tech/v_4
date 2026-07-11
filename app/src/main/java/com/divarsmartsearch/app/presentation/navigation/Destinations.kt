package com.divarsmartsearch.app.presentation.navigation

sealed class Destination(val route: String) {
    // New-search creation flow (nested graph): step 1 = filters, step 2 = permanent filters
    data object NewSearchGraph : Destination("new_search_graph")
    data object SearchForm : Destination("search_form")
    data object SelectPermanentFilters : Destination("select_permanent_filters")

    // Edit-search flow: same two steps, pre-filled from an existing search
    data object EditSearchGraph : Destination("edit_search_graph")
    data object EditSearchForm : Destination("edit_search_form/{searchId}") {
        fun createRoute(searchId: Int) = "edit_search_form/$searchId"
    }
    data object EditSelectPermanentFilters : Destination("edit_select_permanent_filters")

    // In-app WebView: browse Divar and passively extract listings for a given search
    data object DivarWebView : Destination("divar_webview/{searchId}") {
        fun createRoute(searchId: Int) = "divar_webview/$searchId"
    }

    // Main bottom-navigation destinations
    data object SearchList : Destination("search_list")
    data object Results : Destination("results")
    data object History : Destination("history")
    data object PermanentFiltersManagement : Destination("permanent_filters_management")
    data object Settings : Destination("settings")
}

data class BottomNavItem(
    val destination: Destination,
    val labelResId: Int,
)
