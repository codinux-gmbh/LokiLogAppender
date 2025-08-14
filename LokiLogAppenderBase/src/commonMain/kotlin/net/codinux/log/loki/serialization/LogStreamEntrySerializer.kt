package net.codinux.log.loki.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import net.codinux.log.loki.model.LogStreamEntry

open class LogStreamEntrySerializer : KSerializer<LogStreamEntry> {

    protected open val stringSerializer = String.serializer()

    protected open val delegateSerializer = ListSerializer(stringSerializer)

    protected open val mapSerializer = MapSerializer(stringSerializer, stringSerializer)

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor = SerialDescriptor("Values", delegateSerializer.descriptor)


    override fun deserialize(decoder: Decoder): LogStreamEntry =
        LogStreamEntry()

    override fun serialize(encoder: Encoder, value: LogStreamEntry) {
        val collectionSize = if (value.structuredMetadata.isNotEmpty()) 3 else 2

        encoder.encodeCollection(descriptor, collectionSize) {
            this.encodeStringElement(stringSerializer.descriptor, 0, value.timestamp)
            this.encodeStringElement(stringSerializer.descriptor, 1, value.logLine)

            if (value.structuredMetadata.isNotEmpty()) {
                this.encodeSerializableElement(stringSerializer.descriptor, 2, mapSerializer, value.structuredMetadata)
            }
        }
    }

}