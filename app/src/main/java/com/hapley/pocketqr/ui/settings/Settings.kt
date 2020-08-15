package com.hapley.pocketqr.ui.settings

import androidx.annotation.IdRes
import androidx.annotation.StringDef
import com.hapley.pocketqr.R

@Retention(AnnotationRetention.SOURCE)
@StringDef(value = [RECENT, MOST_FREQUENT, ALPHABETICAL], open = false)
annotation class SortMode

const val RECENT = "recent"
const val MOST_FREQUENT = "most_frequent"
const val ALPHABETICAL = "alphabetical"

object Mapper {
    @IdRes
    fun sortModeToMenuItemId(@SortMode sortMode: String): Int {
        return when (sortMode) {
            RECENT -> R.id.item_recent
            MOST_FREQUENT -> R.id.item_most_frequent
            ALPHABETICAL -> R.id.item_alphabetical
            else -> R.id.item_recent
        }
    }

    @SortMode
    fun menuItemIdToSortMode(@IdRes menuItemId: Int): String {
        return when (menuItemId) {
            R.id.item_recent -> RECENT
            R.id.item_most_frequent -> MOST_FREQUENT
            R.id.item_alphabetical -> ALPHABETICAL
            else -> RECENT
        }
    }
}