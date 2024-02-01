package dev.rmcmk.gspm.gradle

import org.gradle.api.initialization.Settings
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

/**
 * The extension for the GSPM settings plugin. This extension is used to configure the [GspmPlugin]'s behavior.
 *
 * @param factory The object factory. This is used to create various kinds of modelled properties.
 * @author Ryley Kimmel <me@rmcmk.dev>
 */
open class GspmExtension
    @Inject
    constructor(factory: ObjectFactory) {
        /** The name of the version catalog to use for the submodules. Default is "gspm". */
        internal val versionCatalogName = factory.property<String>()

        /** Sets up the defaults for this extension. */
        init {
            versionCatalogName.convention("gspm")
        }

        /**
         * Configures the name of the version catalog to use for the submodules.
         *
         * @param name The name of the version catalog to use.
         */
        fun versionCatalogName(name: String) {
            versionCatalogName.set(name)
            versionCatalogName.disallowChanges()
        }

        companion object {
            /**
             * Creates a new [GspmExtension] from the given [Settings].
             *
             * @param settings The settings to create the extension from.
             * @return The created extension.
             */
            fun fromSettings(settings: Settings): GspmExtension {
                return settings.extensions.create<GspmExtension>("gspm")
            }
        }
    }
