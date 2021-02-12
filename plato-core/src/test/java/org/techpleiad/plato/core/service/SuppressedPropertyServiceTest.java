package org.techpleiad.plato.core.service;

//@ExtendWith(MockitoExtension.class) //TODO
//@Slf4j
class SuppressedPropertyServiceTest {

    /*@InjectMocks
    private SuppressedPropertyService suppressedPropertyService;

    private void validateMissingPropertiesResult(final List<String> missingProperties, final List<String> expectedProperties) {
        Assertions.assertEquals(expectedProperties.size(), missingProperties.size());
        expectedProperties.forEach(property -> Assertions.assertTrue(missingProperties.contains(property)));
    }

    @Test
    void given_SuppressedPropertiesAndMissingProperties_returnFilteredSuppressedProperties_1() {

        final List<String> missingProperties = Arrays.asList(
                "mongo.properties.in",
                "mongo.properties.tl",
                "mongo.properties.cw",
                "com.example",
                "com.example.config.management",
                "com.example.common.properties",
                "com.exa.common.properties"
        );
        final List<String> suppressedProperties = Arrays.asList(
                "com.example",
                "mongo.properties.*"
        );

        validateMissingPropertiesResult(suppressedPropertyService.filterSuppressedProperties(suppressedProperties, missingProperties),
                Arrays.asList(
                        "com.exa.common.properties"
                )
        );
    }

    @Test
    void given_SuppressedPropertiesAndMissingProperties_returnFilteredSuppressedProperties_2() {

        final List<String> missingProperties = Arrays.asList(
                "mongo.properties.in",
                "mongo.properties.tl",
                "mongo.properties.cw",
                "com.example",
                "com.example.common.properties",
                "com.exa.common.properties"
        );
        final List<String> suppressedProperties = Arrays.asList(
                "com.example.*", "mongo.properties.*"
        );

        validateMissingPropertiesResult(suppressedPropertyService.filterSuppressedProperties(suppressedProperties, missingProperties),
                Arrays.asList(
                        "com.exa.common.properties",
                        "com.example"
                )
        );
    }

    @Test
    void given_SuppressedPropertiesAndMissingProperties_returnFilteredSuppressedProperties_3() {

        final List<String> missingProperties = Arrays.asList(
                "zuul.spring.props.1.code", "zuul.spring.props.0.code",
                "zuul.spring.props.1.admin", "zuul.spring.config.local",
                "zuul.mongo.transaction"
        );
        List<String> suppressedProperties;

        suppressedProperties = Arrays.asList("zuul.spring.props.0.admin", "zuul.spring.props.*.code");
        validateMissingPropertiesResult(suppressedPropertyService.filterSuppressedProperties(suppressedProperties, missingProperties),
                Arrays.asList(
                        "zuul.spring.props.0.code", "zuul.spring.props.1.admin",
                        "zuul.spring.config.local", "zuul.mongo.transaction"
                )
        );

        suppressedProperties = Arrays.asList("zuul.spring.props.*.admin", "zuul.spring.props.1.code");
        validateMissingPropertiesResult(suppressedPropertyService.filterSuppressedProperties(suppressedProperties, missingProperties),
                Arrays.asList(
                        "zuul.spring.props.1.admin", "zuul.spring.props.0.code",
                        "zuul.spring.config.local", "zuul.mongo.transaction"
                )
        );
    }

    @Test
    void given_SuppressedPropertiesAndMissingProperties_returnFilteredSuppressedProperties_4() {

        final List<String> missingProperties = Arrays.asList(
                "zuul.spring.props.1.code", "zuul.spring.props.0.code",
                "zuul.spring.props.1.admin",
                "zuul.spring.config.local", "zuul.mongo.transaction"
        );
        List<String> suppressedProperties;

        suppressedProperties = Arrays.asList("zuul.spring.props.*.admin", "zuul.spring.props.1.code");
        validateMissingPropertiesResult(suppressedPropertyService.filterSuppressedProperties(suppressedProperties, missingProperties),
                Arrays.asList(
                        "zuul.spring.props.1.admin", "zuul.spring.props.0.code",
                        "zuul.spring.config.local", "zuul.mongo.transaction"
                )
        );

        suppressedProperties = Arrays.asList("zuul.spring.props.*.admin", "zuul.spring.props.*.code");
        validateMissingPropertiesResult(suppressedPropertyService.filterSuppressedProperties(suppressedProperties, missingProperties),
                Arrays.asList(
                        "zuul.spring.config.local", "zuul.mongo.transaction"
                )
        );
    }*/
}
