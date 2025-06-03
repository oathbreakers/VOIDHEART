package oathbreakers.voidheart.scoreboard

import net.kyori.adventure.text.Component

// TODO: Document how these functions will be called from the global region scheduler
// TODO: Documentation
interface ScoreboardContent {
    /**
     * Called right before [getTitle] or [getLines]
     */
    fun preUpdate()

    fun getTitle(): Component
    fun getLines(): List<Component>
}
