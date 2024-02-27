package dev.rmcmk.gspm.resource

data object GitIgnore : Resource {
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
