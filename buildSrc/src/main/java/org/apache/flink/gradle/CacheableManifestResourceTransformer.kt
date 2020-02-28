package org.apache.flink.gradle

import com.github.jengelman.gradle.plugins.shadow.transformers.CacheableTransformer
import com.github.jengelman.gradle.plugins.shadow.transformers.Transformer
import com.github.jengelman.gradle.plugins.shadow.transformers.TransformerContext
import org.gradle.api.file.FileTreeElement
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import shadow.org.apache.tools.zip.ZipEntry
import shadow.org.apache.tools.zip.ZipOutputStream
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.jar.Attributes
import java.util.jar.JarFile
import java.util.jar.Manifest

@CacheableTransformer
class CacheableManifestResourceTransformer : Transformer {
    // Configuration
    @get:Optional @get:Input
    var mainClass: String? = null
    @get:Input
    val attributes: MutableMap<String, Any> = HashMap()
    // Fields
    @Transient
    private var manifestDiscovered = false
    @Transient
    private var manifest: Manifest? = null

    override fun canTransformResource(element: FileTreeElement): Boolean =
        JarFile.MANIFEST_NAME.equals(element.relativePath.pathString, ignoreCase = true)

    override fun transform(context: TransformerContext) {
        if (!manifestDiscovered) {
            manifest = Manifest(context.getIs())
            manifestDiscovered = true
        }
    }

    override fun hasTransformedResource(): Boolean = true

    override fun modifyOutputStream(os: ZipOutputStream, preserveFileTimestamps: Boolean) {
        // If we didn't find a manifest, then let's create one.
        val manifest = this.manifest ?: Manifest()
        val attributes = manifest.mainAttributes
        if (mainClass != null) {
            attributes[Attributes.Name.MAIN_CLASS] = mainClass
        }
        for ((key, value) in this.attributes) {
            attributes[Attributes.Name(key)] = value
        }
        val entry = ZipEntry(JarFile.MANIFEST_NAME)
        entry.time = TransformerContext.getEntryTimestamp(preserveFileTimestamps, entry.time)
        os.putNextEntry(entry)
        ByteArrayOutputStream().use { baos ->
            manifest.write(baos)
            // need to make sure to call the "correct" write as Ant's ZipOutputStream will fail on the others...
            os.write(baos.toByteArray(), 0, baos.size())
        }
    }

    fun attributes(attributes: Map<String, Any>): CacheableManifestResourceTransformer {
        this.attributes.putAll(attributes)
        return this
    }
}