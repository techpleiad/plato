package org.techpleiad.plato.api.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techpleiad.plato.api.exceptions.InvalidRuleException;

import javax.validation.ConstraintValidatorContext;

@ExtendWith(MockitoExtension.class)
class JsonSchemaValidatorTest {

    private static ConstraintValidatorContext context;
    private static ConstraintValidatorContext.ConstraintViolationBuilder builder;
    @InjectMocks
    JsonSchemaValidator jsonSchemaValidator;

    @BeforeAll
    public static void setup() {
        context = Mockito.mock(ConstraintValidatorContext.class);
        builder = Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(builder);
    }

    final JsonNode json1 = new ObjectMapper().readTree("{\n" +
            "        \"id\": \"https://example.com/person.schema.json\",\n" +
            "        \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
            "        \"description\": \"something\",\n" +
            "        \"title\": \"Person\",\n" +
            "        \"type\": \"object\",\n" +
            "        \"properties\": {\n" +
            "            \"firstName\": {\n" +
            "                \"type\": \"string\",\n" +
            "                \"description\": \"The person's first name.\"\n" +
            "            },\n" +
            "            \"lastName\": {\n" +
            "                \"type\": \"string\",\n" +
            "                \"description\": \"The person's last name.\"\n" +
            "            },\n" +
            "            \"age\": {\n" +
            "                \"description\": \"Age in years which must be equal to or greater than zero.\",\n" +
            "                \"type\": \"integer\",\n" +
            "                \"minimum\": 0\n" +
            "            }\n" +
            "        }\n" +
            "    }");

    final JsonNode json2 = new ObjectMapper().readTree("{\n" +
            "        \"id\": \"https://example.com/person.schema.json\",\n" +
            "        \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
            "        \"description\": \"something\",\n" +
            "        \"title\": \"Person\",\n" +
            "        \"properties\": {\n" +
            "            \"firstName\": {\n" +
            "                \"type\": \"string\",\n" +
            "                \"description\": \"The person's first name.\"\n" +
            "            },\n" +
            "            \"lastName\": {\n" +
            "                \"type\": \"string\",\n" +
            "                \"description\": \"The person's last name.\"\n" +
            "            },\n" +
            "            \"age\": {\n" +
            "                \"description\": \"Age in years which must be equal to or greater than zero.\",\n" +
            "                \"type\": \"integer\",\n" +
            "                \"minimum\": 0\n" +
            "            }\n" +
            "        }\n" +
            "    }");

    final JsonNode json3 = new ObjectMapper().readTree("{\n" +
            "   \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
            "   \"title\": \"Product\",\n" +
            "   \"description\": \"A product from Acme's catalog\",\n" +
            "   \"type\": \"object\",\n" +
            "\t\n" +
            "   \"properties\": {\n" +
            "\t\n" +
            "      \"id\": {\n" +
            "         \"description\": \"The unique identifier for a product\",\n" +
            "         \"type\": \"integer\"\n" +
            "      },\n" +
            "\t\t\n" +
            "      \"name\": {\n" +
            "         \"description\": \"Name of the product\",\n" +
            "         \"type\": \"string\"\n" +
            "      },\n" +
            "\t\t\n" +
            "      \"price\": {\n" +
            "         \"type\": \"number\",\n" +
            "         \"minimum\": 0,\n" +
            "         \"exclusiveMinimum\": true\n" +
            "      }\n" +
            "   },\n" +
            "\t\n" +
            "   \"required\": [\"id\", \"name\", \"price\"]\n" +
            "}");

    final JsonNode json4 = new ObjectMapper().readTree("{\n" +
            "  \"$id\": \"https://example.com/arrays.schema.json\",\n" +
            "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
            "  \"description\": \"A representation of a person, company, organization, or place\",\n" +
            "  \"type\": \"object\",\n" +
            "  \"properties\": {\n" +
            "    \"fruits\": {\n" +
            "      \"type\": \"array\",\n" +
            "      \"items\": {\n" +
            "        \"type\": \"string\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"vegetables\": {\n" +
            "      \"type\": \"array\",\n" +
            "      \"items\": { \"$ref\": \"#/$defs/veggie\" }\n" +
            "    }\n" +
            "  },\n" +
            "  \"$defs\": {\n" +
            "    \"veggie\": {\n" +
            "      \"type\": \"object\",\n" +
            "      \"required\": [ \"veggieName\", \"veggieLike\" ],\n" +
            "      \"properties\": {\n" +
            "        \"veggieName\": {\n" +
            "          \"type\": \"string\",\n" +
            "          \"description\": \"The name of the vegetable.\"\n" +
            "        },\n" +
            "        \"veggieLike\": {\n" +
            "          \"type\": \"boolean\",\n" +
            "          \"description\": \"Do I like this vegetable?\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}");

    final JsonNode json5 = new ObjectMapper().readTree("{\n" +
            "  \"$schema\": \"https://json-schema.org/draft/2020-12/schema\",\n" +
            "  \"$id\": \"https://example.com/product.schema.json\",\n" +
            "  \"title\": \"Product\",\n" +
            "  \"description\": \"A product in the catalog\",\n" +
            "  \"type\": \"object\",\n" +
            "  \"properties\":{\n" +
            "      \"random\": \"value\"\n" +
            "  }\n" +
            "}");

    JsonSchemaValidatorTest() throws JsonProcessingException {
    }

    @Test
    void givenSchema_whenAddValidationRule_thenDoesNotThrowException_1() {
        Assertions.assertDoesNotThrow(() -> jsonSchemaValidator.isValid(json1, context));
    }

    @Test
    void givenSchema_whenAddValidationRule_thenDoesNotThrowException_2() {
        Assertions.assertDoesNotThrow(() -> jsonSchemaValidator.isValid(json3, context));

    }

    @Test
    void givenSchema_whenAddValidationRule_thenDoesNotThrowException_3() {
        Assertions.assertDoesNotThrow(() -> jsonSchemaValidator.isValid(json5, context));
    }


    @Test
    void givenSchema_whenAddValidationRule_thenThrowsInvalidRuleException_1() {
        Assertions.assertThrows(InvalidRuleException.class, () -> jsonSchemaValidator.isValid(json2, context));

    }
}