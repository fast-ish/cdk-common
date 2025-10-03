package fasti.sh.execute.aws.ecr;

import static fasti.sh.execute.serialization.Format.id;

import fasti.sh.execute.aws.kms.KmsConstruct;
import fasti.sh.model.aws.ecr.EcrRepository;
import fasti.sh.model.main.Common;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecr.RepositoryEncryption;
import software.constructs.Construct;

@Slf4j
@Getter
public class EcrRepositoryConstruct extends Construct {
  private final Repository repository;

  public EcrRepositoryConstruct(Construct scope, Common common, EcrRepository conf) {
    super(scope, id("ecr", conf.name()));

    log.debug("{} [common: {} conf: {}]", "EcrRepositoryConstruct", common, conf);

    var ecr = Repository.Builder
      .create(scope, "ecr")
      .repositoryName(conf.name())
      .imageTagMutability(conf.tagMutability())
      .imageScanOnPush(conf.scanOnPush())
      .emptyOnDelete(conf.emptyOnDelete())
      .removalPolicy(conf.removalPolicy());

    if (conf.encryption().enabled()) {
      if (conf.encryption().kms() != null) {
        ecr.encryption(RepositoryEncryption.KMS);
        ecr.encryptionKey(new KmsConstruct(this, common, conf.encryption().kms()).key());
      } else {
        ecr.encryption(RepositoryEncryption.AES_256);
      }
    }

    this.repository = ecr.build();

    common.tags().forEach((k, v) -> Tags.of(this.repository()).add(k, v));
  }
}
