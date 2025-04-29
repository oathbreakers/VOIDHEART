package oathbreakers.voidheart.commands

data class CommandBuffer(private val string: String) {
    private val parts = string.split(" ")

    private var readIndex: Int = 0

    /**
     * Returns the current read position.
     */
    fun getPosition(): Int = readIndex

    /**
     * Sets the read position to a specific index.
     *
     * @param position The new position to set
     * @throws IndexOutOfBoundsException if the position is out of bounds (0-${parts.size})
     */
    fun setPosition(position: Int) {
        require(position in 0..parts.size) { "Position $position is out of bounds (0-${parts.size})" }
        readIndex = position
    }

    /**
     * Resets the read position to the beginning.
     */
    fun reset() {
        readIndex = 0
    }

    /**
     * Checks if there are more parts left to read.
     *
     * @return true if there are more parts, false otherwise
     */
    fun hasNext(): Boolean = readIndex < parts.size

    /**
     * Returns the number of remaining parts.
     *
     * @return The number of remaining parts
     */
    fun remaining(): Int = parts.size - readIndex

    /**
     * Retrieves the next command part without advancing the read position.
     *
     * @return The next command part, or null if there are no more parts to read
     */
    fun peek(): String? {
        return if (hasNext()) parts[readIndex] else null
    }

    /**
     * Retrieves and removes the next command part by advancing the read position.
     *
     * @return The next command part, or null if there are no more parts to read
     */
    fun pop(): String? {
        return if (hasNext()) parts[readIndex++] else null
    }

    /**
     * Advances the read position without returning any value.
     */
    fun skip() {
        if (hasNext()) readIndex++
    }

    /**
     * Joins all remaining parts into a single string and advances the read position to the end.
     *
     * @return A single string composed of all remaining parts
     */
    fun consume(): String {
        return if (!hasNext()) ""
        else {
            val result = parts.subList(readIndex, parts.size).joinToString(" ")
            readIndex = parts.size
            result
        }
    }

    /**
     * Collects command parts while the predicate is true and advances the read position.
     *
     * @param predicate The condition to test each part against
     * @return A list of collected parts that satisfy the predicate
     */
    fun popWhile(predicate: (String) -> Boolean): List<String> {
        val result = mutableListOf<String>()
        while (hasNext() && predicate(peek()!!)) {
            result.add(pop()!!)
        }
        return result
    }

    /**
     * Collects command parts until the predicate is true and advances the read position.
     *
     * @param predicate The condition to test each part against
     * @return A list of collected parts up until (but not including) the first part that satisfies the predicate
     */
    fun popUntil(predicate: (String) -> Boolean): List<String> {
        val result = mutableListOf<String>()
        while (hasNext() && !predicate(peek()!!)) {
            result.add(pop()!!)
        }
        return result
    }
}