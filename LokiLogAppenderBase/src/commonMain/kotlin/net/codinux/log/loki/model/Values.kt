package net.codinux.log.loki.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection

@Serializable(with = Values.ValuesSerializer::class)
// Loki's values are not safely typed. The first value is the timestamp in RFC3339 or RFC3339Nano format, the second the log line
open class Values : OpenArrayList<Any>(listOf("", "", mapOf<String, String>())) {

    open var timestamp: String = ""
        protected set

    open var message: String = ""
        protected set

    open var structuredMetadata: Map<String, String> = emptyMap()
        protected set


    open fun set(timestamp: String, message: String, structuredMetadata: Map<String, String> = emptyMap()) {
        this[0] = timestamp
        this[1] = message
        this[2] = structuredMetadata

        this.timestamp = timestamp
        this.message = message
        this.structuredMetadata = structuredMetadata
    }

    override fun toString(): String {
        return "$timestamp $message"
    }


    open class ValuesSerializer : KSerializer<Values> {

        protected open val stringSerializer = String.serializer()

        protected open val delegateSerializer = ListSerializer(stringSerializer)

        protected open val mapSerializer = MapSerializer(stringSerializer, stringSerializer)

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor = SerialDescriptor("Values", delegateSerializer.descriptor)

        override fun deserialize(decoder: Decoder): Values =
            Values()

        override fun serialize(encoder: Encoder, value: Values) {
            val collectionSize = if (value.structuredMetadata.isNotEmpty()) 3 else 2

            encoder.encodeCollection(descriptor, collectionSize) {
                this.encodeStringElement(stringSerializer.descriptor, 0, value.timestamp)
                this.encodeStringElement(stringSerializer.descriptor, 1, value.message)

                if (value.structuredMetadata.isNotEmpty()) {
                    this.encodeSerializableElement(stringSerializer.descriptor, 2, mapSerializer, value.structuredMetadata)
                }
            }
        }

    }

}