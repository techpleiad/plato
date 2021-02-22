package org.techpleiad.plato.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Builder
@Getter
public class Pair<K, V> {
    private K first;
    private V second;
}
