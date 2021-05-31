package org.techpleiad.plato.deploy;

import io.awspring.cloud.secretsmanager.AwsSecretsManagerProperties;
import org.springframework.stereotype.Component;

@Component("aws.secretsmanager-io.awspring.cloud.secretsmanager.AwsSecretsManagerProperties")
public class AwsConfigBean extends AwsSecretsManagerProperties {
}
