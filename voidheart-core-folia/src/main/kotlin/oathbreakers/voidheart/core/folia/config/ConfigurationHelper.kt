package oathbreakers.voidheart.core.folia.config

import com.akuleshov7.ktoml.Toml
import com.akuleshov7.ktoml.TomlInputConfig
import com.akuleshov7.ktoml.decoders.TomlMainDecoder
import com.akuleshov7.ktoml.encoders.TomlMainEncoder
import com.akuleshov7.ktoml.parsers.TomlParser
import com.akuleshov7.ktoml.tree.nodes.TomlNode
import com.akuleshov7.ktoml.tree.nodes.TomlTable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.serializer
import org.jetbrains.annotations.ApiStatus
import java.io.File

object ConfigurationHelper {
    inline fun <reified T> load(file: File, default: T): T {
        if (!file.exists()) {
            file.writeText(Toml.encodeToString(default))
            return default
        }

        val tomlParser = TomlParser(TomlInputConfig(allowNullValues = false, ignoreUnknownNames = true))
        val existingTree = tomlParser.parseLines(file.readLines().asSequence())
        val defaultTree = TomlMainEncoder.encode(Toml.serializersModule.serializer(), default)

        if (mergeTomlTables(existingTree, defaultTree)) {
            file.writeText(Toml.tomlWriter.writeToString(existingTree))
        }

        @OptIn(ExperimentalSerializationApi::class)
        return TomlMainDecoder.decode(Toml.serializersModule.serializer(), existingTree)
    }

    @ApiStatus.Internal
    fun mergeTomlTables(a: TomlNode, b: TomlNode): Boolean {
        var modified = false
        for (bChild in b.children) {
            val aChild = a.children.firstOrNull { it.name == bChild.name }

            if (aChild == null) {
                modified = true
                a.appendChild(bChild)
            } else if (aChild is TomlTable && bChild is TomlTable) {
                modified = modified || mergeTomlTables(
                    aChild,
                    bChild
                )
            }
        }

        return modified
    }
}