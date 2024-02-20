package dev.rmcmk.gspm.gradle

import org.gradle.api.provider.Property

/**
 * The extension for the GSPM settings plugin. This extension is used to configure the [GspmPlugin]'s behavior.
 *
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
interface GspmExtension {
    /** The name of the version catalog to use for the submodules. Default is "gspm". */
    val versionCatalogName: Property<String>
}
