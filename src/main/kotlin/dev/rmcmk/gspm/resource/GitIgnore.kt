package dev.rmcmk.gspm.resource

import java.io.File

class GitIgnore(override val file: File) : Resource() {
    override val fileName = ".gitignore"
    override val content =
        """
        # ============================================================================
        # THIS FILE COMES FROM THE GSPM GRADLE PLUGIN. DO NOT MODIFY DIRECTLY.
        # ============================================================================

        init.gradle.kts
        .gitignore
        """.trimIndent()
}
