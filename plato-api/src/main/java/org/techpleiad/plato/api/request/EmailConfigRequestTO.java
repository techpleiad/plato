package org.techpleiad.plato.api.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class EmailConfigRequestTO {
    private boolean sendEmail;
    private List<String> recipients;
}