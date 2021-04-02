package org.techpleiad.plato.core.advice;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;
import java.util.Set;

public class JsonSchemaValidationAspect implements ConstraintValidator<JsonSchemaValidation, JsonNode> {

    private static final String SCHEMA_TO_VALIDATE_JSON_SCHEMA = "{\n" +
            "    \"$schema\": \"http://json-schema.org/draft-06/schema#\",\n" +
            "    \"title\": \"SchemaValidation\",\n" +
            "    \"description\": \"JsonSchema to validate Jsonschema from API request body\",\n" +
            "    \"type\": \"object\",\n" +
            "    \"properties\": {\n" +
            "        \"$schema\": {\n" +
            "            \"description\": \"The unique identifier for a product\",\n" +
            "            \"type\": \"string\"\n" +
            "        },\n" +
            "        \"title\": {\n" +
            "            \"description\": \"title for the schema\",\n" +
            "            \"type\": \"string\"\n" +
            "        },\n" +
            "        \"description\": {\n" +
            "            \"type\": \"string\",\n" +
            "            \"description\": \"the intent of the schema\"\n" +
            "        },\n" +
            "        \"type\":{\n" +
            "            \"type\": \"string\"\n" +
            "        },\n" +
            "        \"properties\":{\n" +
            "            \"type\":\"object\",\n" +
            "            \"description\": \"rules for validaton\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"required\": [\"$schema\", \"title\", \"description\", \"type\", \"properties\"]\n" +
            "}";

    @Override
    public boolean isValid(JsonNode jsonNode, ConstraintValidatorContext constraintValidatorContext) {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(SCHEMA_TO_VALIDATE_JSON_SCHEMA);
        JsonSchema schemaToValidationJsonSchema = factory.getSchema(is);
        Set<ValidationMessage> errors = schemaToValidationJsonSchema.validate(jsonNode);
        System.out.println(errors);
        return false;
    }
}
