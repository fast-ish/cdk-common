package fasti.sh.execute.aws.eks.addon;

import fasti.sh.execute.serialization.Mapper;
import fasti.sh.model.aws.eks.addon.core.GrafanaSecret;
import fasti.sh.model.main.Common;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.constructs.Construct;

@Slf4j
public abstract class GrafanaBaseConstruct extends Construct {

  protected GrafanaBaseConstruct(Construct scope, String id) {
    super(scope, id);
  }

  protected static GrafanaSecret fetchSecret(Common common, String secret) {
    if (secret == null || secret.isEmpty())
      return null;

    try (var client = SecretsManagerClient
      .builder()
      .region(Region.of(common.region()))
      .credentialsProvider(DefaultCredentialsProvider.builder().build())
      .build()) {

      var secretValueResponse = getSecret(client, secret);
      if (secretValueResponse != null)
        return secretValueResponse;

      var arn = String.format("arn:aws:secretsmanager:%s:%s:secret:%s", common.region(), common.account(), secret);
      return getSecret(client, arn);
    } catch (Exception e) {
      throw new RuntimeException("failed to retrieve grafana secret " + e.getMessage(), e);
    }
  }

  private static GrafanaSecret getSecret(SecretsManagerClient client, String id) {
    try {
      var secret = client
        .getSecretValue(
          GetSecretValueRequest
            .builder()
            .secretId(id)
            .build());

      var value = secret.secretString();
      if (value != null)
        return Mapper.get().readValue(value, GrafanaSecret.class);

      return null;
    } catch (Exception e) {
      return null;
    }
  }
}
