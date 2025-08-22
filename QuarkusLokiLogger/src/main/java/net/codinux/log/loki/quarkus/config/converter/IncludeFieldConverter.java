package net.codinux.log.loki.quarkus.config.converter;

import net.codinux.log.loki.config.field.IncludeField;
import org.eclipse.microprofile.config.spi.Converter;

public class IncludeFieldConverter implements Converter<IncludeField> {

    @Override
    public IncludeField convert(String value) throws IllegalArgumentException, NullPointerException {
        String lowercase =  value.toLowerCase();

        return switch (lowercase) {
            case "no", "false" -> IncludeField.No;
            case "label", "true" -> IncludeField.Label;
            case "structuredmetadata", "metadata", "meta" -> IncludeField.StructuredMetadata;
            default -> throw new IllegalArgumentException("Invalid include field value: " + value +
                    ". Valid values are (ignoring case): Label, true, StructuredMetadata, metadata, meta, No, false");
        };
    }

}