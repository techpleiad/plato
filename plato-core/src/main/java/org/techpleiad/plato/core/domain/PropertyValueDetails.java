package org.techpleiad.plato.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@AllArgsConstructor
@Getter
@ToString
@Builder
public class PropertyValueDetails {
    private final String property;
    private final String value;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PropertyValueDetails that = (PropertyValueDetails) o;
        return property.equals(that.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property);
    }
}
