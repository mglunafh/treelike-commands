package org.some.project.kotlin.geometry.l10n

import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.Properties

object CommandMessageSource {

    private val props: Properties = Properties()

    fun init(language: Lang) {
        val propertiesFileName = when (language) {
            Lang.ENG -> "help-eng.properties"
            Lang.RUS -> TODO("Russian localization is not implemented")      // "help-rus.properties"
        }

        javaClass.classLoader.getResourceAsStream(propertiesFileName)
            ?.let { inputStream ->
                inputStream.use { InputStreamReader(it, Charset.forName("UTF-8")).use { utf8Stream -> props.load(utf8Stream) } } }
            ?: throw IllegalArgumentException("Could not find file with translations: $propertiesFileName")
    }

    operator fun get(key: String): String {
        return props.getProperty(key)
    }

}
