package net.codinux.log.loki.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection

@Serializable(with = Values.ValuesSerializer::class)
// Loki's values are not safely typed. The first value is the timestamp in RFC3339 or RFC3339Nano format, the second the log line
open class Values : OpenArrayList<String>(listOf("", "")) {

    open val timestamp: String
        get() = get(0)

    open val message: String
        get() = get(1)

    open fun set(timestamp: String, message: String) {
        this[0] = timestamp
        this[1] = message
    }

    override fun toString(): String {
        return "$timestamp $message"
    }


    open class ValuesSerializer : KSerializer<Values> {

        protected open val stringSerializer = String.serializer()

        protected open val delegateSerializer = ListSerializer(stringSerializer)

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor = SerialDescriptor("Values", delegateSerializer.descriptor)

        override fun deserialize(decoder: Decoder): Values =
            Values()

        override fun serialize(encoder: Encoder, value: Values) {
            encoder.encodeCollection(descriptor, 2) {
                this.encodeStringElement(stringSerializer.descriptor, 0, value.timestamp)
                this.encodeStringElement(stringSerializer.descriptor, 1, value.message)
            }
        }

    }

}