package dev.rmcmk.gspm.gradle

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.property

/**
 * The extension for the GSPM settings plugin. This extension is used to configure the [GspmPlugin]'s behavior.
 *
 * @param factory The object factory. This is used to create various kinds of modelled properties.
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
open class GspmExtension @Inject constructor(factory: ObjectFactory) {
    /** The name of the version catalog to use for the submodules. Default is "gspm". */
    internal val versionCatalogName = factory.property<String>()

    /**
     * Configures the name of the version catalog to use for the submodules.
     *
     * @param name The name of the version catalog to use.
     */
    fun versionCatalogName(name: String) {
        versionCatalogName.set(name)
        versionCatalogName.disallowChanges()
    }
}
