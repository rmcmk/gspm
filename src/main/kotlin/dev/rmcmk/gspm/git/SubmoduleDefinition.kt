package dev.rmcmk.gspm.git

import com.github.vincentrussell.ini.Ini
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.inputStream
import kotlin.io.path.isRegularFile
import kotlin.io.path.notExists

/**
 * Represents submodule definition entry as defined in `.gitmodules`.
 *
 * @param name The name of the submodule.
 * @param path The path of the submodule.
 * @param url The URL of the submodule.
 * @param fetchRecurseSubmodules Whether to fetch nested submodules.
 * @param shallow Whether to shallow clone.
 * @param branchName The branch name to check out.
 * @param updateMode The update mode.
 * @param ignoreMode The ignore mode.
 * @see <a href="https://git-scm.com/docs/gitmodules">.gitmodules spec</a>
 * @author <a href="http://github.com/klepto">Augustinas R.</a>
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
data class SubmoduleDefinition(
    val name: String,
    val path: String,
    val url: String,
    val fetchRecurseSubmodules: Boolean,
    val shallow: Boolean,
    val branchName: String,
    val updateMode: SubmoduleUpdateMode?,
    val ignoreMode: SubmoduleIgnoreMode?,
) {
    companion object {
        /**
         * Parses the given [path] into a list of [SubmoduleDefinition]s.
         *
         * @return A list of [SubmoduleDefinition]s from the given [path].
         * @throws IllegalArgumentException If the given [path] is not a regular
         */
        fun fromFile(path: Path): List<SubmoduleDefinition> {
            if (path.notExists()) {
                return emptyList()
            }
            if (!path.isRegularFile()) {
                throw IllegalArgumentException("Path must be a file: $path")
            }

            val ini = Ini().apply { load(path.inputStream(StandardOpenOption.READ)) }
            return ini.sections.map {
                val section = ini.getSection(it)
                val valueOf = { key: String -> section[key].toString() }

                SubmoduleDefinition(
                    name = valueOf("name"),
                    path = valueOf("path"),
                    url = valueOf("url"),
                    fetchRecurseSubmodules = valueOf("fetchRecurseSubmodules").toBoolean(),
                    shallow = valueOf("shallow").toBoolean(),
                    branchName = valueOf("branch"),
                    updateMode = section["update"]?.let { v -> SubmoduleUpdateMode.fromString(v.toString()) },
                    ignoreMode = section["ignore"]?.let { v -> SubmoduleIgnoreMode.fromString(v.toString()) },
                )
            }
        }
    }
}

/**
 * Represents submodule ignore mode.
 *
 * @see <a
 *     href="https://git-scm.com/docs/gitmodules#Documentation/gitmodules.txt-submoduleltnamegtignore">submodule.<name>.ignore
 *     spec</a>
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
enum class SubmoduleIgnoreMode {
    /**
     * The submodule will never be considered modified (but will nonetheless
     * show up in the output of status and commit when it has been staged).
     */
    ALL,

    /**
     * All changes to the submodule’s work tree will be ignored, only committed
     * differences between the HEAD of the submodule and its recorded state in
     * the superproject are taken into account.
     */
    DIRTY,

    /**
     * Only untracked files in submodules will be ignored. Committed
     * differences and modifications to tracked files will show up.
     */
    UNTRACKED,

    /**
     * No modifications to submodules are ignored, all of committed
     * differences, and modifications to tracked and untracked files are shown.
     * This is the default option.
     */
    NONE,

    ;

    companion object {
        /**
         * Returns the [SubmoduleIgnoreMode] from the given [string].
         *
         * @param string The string to parse.
         * @return The parsed [SubmoduleIgnoreMode].
         * @throws IllegalArgumentException If the given [string] is not a valid
         */
        fun fromString(string: String): SubmoduleIgnoreMode {
            return when (string) {
                "all" -> ALL
                "dirty" -> DIRTY
                "untracked" -> UNTRACKED
                "none" -> NONE
                else -> throw IllegalArgumentException("Unknown submodule ignore mode: $string")
            }
        }
    }
}

/**
 * Represents submodule update mode.
 *
 * @see <a
 *     href="https://git-scm.com/docs/gitmodules#Documentation/gitmodules.txt-submoduleltnamegtupdate">submodule.<name>.update
 *     spec</a>
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
enum class SubmoduleUpdateMode {
    /**
     * The commit recorded in the superproject will be checked out in the
     * submodule on a detached HEAD. If --force is specified, the submodule
     * will be checked out (using git checkout --force), even if the commit
     * specified in the index of the containing repository already matches the
     * commit checked out in the submodule.
     */
    CHECKOUT,

    /**
     * The current branch of the submodule will be rebased onto the commit
     * recorded in the superproject.
     */
    REBASE,

    /**
     * The commit recorded in the superproject will be merged into the current
     * branch in the submodule.
     */
    MERGE,

    /**
     * The submodule is not updated. This update procedure is not allowed on
     * the command line.
     */
    NONE,

    ;

    companion object {
        /**
         * Returns the [SubmoduleUpdateMode] from the given [string].
         *
         * @param string The string to parse.
         * @return The parsed [SubmoduleUpdateMode].
         * @throws IllegalArgumentException If the given [string] is not a valid
         */
        fun fromString(string: String): SubmoduleUpdateMode {
            return when (string) {
                "checkout" -> CHECKOUT
                "rebase" -> REBASE
                "merge" -> MERGE
                "none" -> NONE
                else -> throw IllegalArgumentException("Unknown submodule update mode: $string")
            }
        }
    }
}
