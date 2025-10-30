package fasti.sh.execute.aws;

import static fasti.sh.test.CdkTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.execute.aws.rds.RdsConstruct;
import fasti.sh.execute.aws.vpc.SecurityGroupConstruct;
import fasti.sh.execute.aws.vpc.VpcConstruct;
import fasti.sh.model.aws.rds.Rds;
import fasti.sh.model.aws.rds.RdsPerformanceInsights;
import fasti.sh.model.aws.rds.RdsReader;
import fasti.sh.model.aws.rds.RdsWriter;
import fasti.sh.model.aws.secretsmanager.SecretCredentials;
import fasti.sh.model.aws.secretsmanager.SecretFormat;
import fasti.sh.model.aws.vpc.NetworkConf;
import fasti.sh.model.aws.vpc.Subnet;
import fasti.sh.model.aws.vpc.securitygroup.SecurityGroup;
import fasti.sh.model.aws.vpc.securitygroup.SecurityGroupIpRule;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.services.ec2.DefaultInstanceTenancy;
import software.amazon.awscdk.services.ec2.SubnetType;

/**
 * Tests for RDS (Aurora PostgreSQL) constructs.
 */
class RdsConstructsTest {

  @Test
  void testRdsConstructBasic() {
    var ctx = createTestContext();

    // Create VPC
    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-vpc",
      "10.0.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    // Create security group
    var dbRule = new SecurityGroupIpRule("10.0.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-sg",
      "RDS security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    // Create database credentials
    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-credentials",
      "RDS database credentials",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "LONG_TERM");
    var writer = new RdsWriter(false, true, "writer-instance", false, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "test-cluster",
      "testdb",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(),
      false,
      "DESTROY",
      Map.of("Type", "postgres"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructWithReaders() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-reader-vpc",
      "10.1.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.1.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-reader-sg",
      "RDS reader security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-reader-credentials",
      "RDS reader credentials",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "DEFAULT");
    var writer = new RdsWriter(false, true, "writer", false, perfInsights);

    var reader1 = new RdsReader(false, true, "reader-1", false, true, perfInsights);
    var reader2 = new RdsReader(false, true, "reader-2", false, true, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "reader-cluster",
      "readerdb",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(reader1, reader2),
      false,
      "DESTROY",
      Map.of("Readers", "2"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructWithDeletionProtection() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-protected-vpc",
      "10.2.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.2.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-protected-sg",
      "Protected RDS security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-protected-credentials",
      "Protected RDS credentials",
      "postgres",
      passwordFormat,
      "RETAIN",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "LONG_TERM");
    var writer = new RdsWriter(false, true, "protected-writer", false, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "protected-cluster",
      "protecteddb",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(),
      true, // deletionProtection
      "RETAIN",
      Map.of("Protected", "true"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructWithDataApi() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-dataapi-vpc",
      "10.3.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.3.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-dataapi-sg",
      "Data API RDS security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-dataapi-credentials",
      "Data API RDS credentials",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "DEFAULT");
    var writer = new RdsWriter(false, true, "dataapi-writer", false, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "dataapi-cluster",
      "dataapidb",
      credentials,
      "AURORA",
      true, // enableDataApi
      writer,
      List.of(),
      false,
      "DESTROY",
      Map.of("DataAPI", "enabled"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructWithAutoscalingReaders() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-autoscale-vpc",
      "10.4.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.4.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-autoscale-sg",
      "Autoscaling RDS security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-autoscale-credentials",
      "Autoscaling RDS credentials",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "DEFAULT");
    var writer = new RdsWriter(false, true, "autoscale-writer", false, perfInsights);

    var reader1 = new RdsReader(false, true, "autoscale-reader-1", false, false, perfInsights);
    var reader2 = new RdsReader(false, true, "autoscale-reader-2", false, false, perfInsights);
    var reader3 = new RdsReader(false, true, "autoscale-reader-3", false, false, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "autoscale-cluster",
      "autoscaledb",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(reader1, reader2, reader3),
      false,
      "DESTROY",
      Map.of("Readers", "3", "Type", "autoscaling"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructNoPerformanceInsights() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-nopi-vpc",
      "10.5.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.5.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-nopi-sg",
      "RDS without performance insights",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-nopi-credentials",
      "RDS credentials without PI",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "DEFAULT");
    var writer = new RdsWriter(false, true, "nopi-writer", false, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "nopi-cluster",
      "nopidb",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(),
      false,
      "DESTROY",
      Map.of("PerformanceInsights", "disabled"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructOlderPostgresVersion() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-old-vpc",
      "10.6.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.6.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-old-sg",
      "RDS older version security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-old-credentials",
      "Older PostgreSQL credentials",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "DEFAULT");
    var writer = new RdsWriter(false, true, "old-writer", false, perfInsights);

    var rdsConf = new Rds(
      "14.9",
      "old-version-cluster",
      "olddb",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(),
      false,
      "DESTROY",
      Map.of("Version", "14.9"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructMultipleSecurityGroups() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-multi-sg-vpc",
      "10.7.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.7.0.0/16", 5432, 5432);
    var sg1 = new SecurityGroup(
      "rds-app-sg",
      "Application security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct1 = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg1, vpcConstruct.vpc());

    var sg2 = new SecurityGroup(
      "rds-admin-sg",
      "Admin security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct2 = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg2, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-multi-sg-credentials",
      "Multi-SG RDS credentials",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "DEFAULT");
    var writer = new RdsWriter(false, true, "multi-sg-writer", false, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "multi-sg-cluster",
      "multisgdb",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(),
      false,
      "DESTROY",
      Map.of("SecurityGroups", "multiple"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct1.securityGroup(), sgConstruct2.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructFourReaders() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-four-readers-vpc",
      "10.8.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.8.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-four-readers-sg",
      "Four readers security group",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-four-readers-credentials",
      "Four readers credentials",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "LONG_TERM");
    var writer = new RdsWriter(false, true, "four-readers-writer", false, perfInsights);

    var reader1 = new RdsReader(false, true, "reader-1", false, true, perfInsights);
    var reader2 = new RdsReader(false, true, "reader-2", false, true, perfInsights);
    var reader3 = new RdsReader(false, true, "reader-3", false, true, perfInsights);
    var reader4 = new RdsReader(false, true, "reader-4", false, true, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "four-readers-cluster",
      "fourreadersdb",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(reader1, reader2, reader3, reader4),
      false,
      "DESTROY",
      Map.of("Readers", "4"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructDataApiWithReaders() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-dataapi-readers-vpc",
      "10.9.0.0/16",
      null,
      1,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.9.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-dataapi-readers-sg",
      "Data API with readers SG",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-dataapi-readers-creds",
      "Data API readers credentials",
      "postgres",
      passwordFormat,
      "DESTROY",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "DEFAULT");
    var writer = new RdsWriter(false, true, "dataapi-readers-writer", false, perfInsights);

    var reader1 = new RdsReader(false, true, "dataapi-reader-1", false, true, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "dataapi-readers-cluster",
      "dataapi_readers_db",
      credentials,
      "AURORA",
      true,
      writer,
      List.of(reader1),
      false,
      "DESTROY",
      Map.of("DataAPI", "enabled", "Readers", "1"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }

  @Test
  void testRdsConstructDeletionProtectionWithReaders() {
    var ctx = createTestContext();

    var publicSubnet = new Subnet("public", SubnetType.PUBLIC, 24, false, true, Map.of());
    var privateSubnet = new Subnet("private", SubnetType.PRIVATE_WITH_EGRESS, 24, false, false, Map.of());
    var networkConf = new NetworkConf(
      "rds-protected-readers-vpc",
      "10.10.0.0/16",
      null,
      2,
      List.of(),
      List.of(publicSubnet, privateSubnet),
      List.of("us-east-1a", "us-east-1b"),
      DefaultInstanceTenancy.DEFAULT,
      true,
      true,
      true,
      Map.of());
    var vpcConstruct = new VpcConstruct(ctx.scope(), ctx.common(), networkConf);

    var dbRule = new SecurityGroupIpRule("10.10.0.0/16", 5432, 5432);
    var sg = new SecurityGroup(
      "rds-protected-readers-sg",
      "Protected with readers SG",
      false,
      false,
      List.of(dbRule),
      List.of(),
      Map.of());
    var sgConstruct = new SecurityGroupConstruct(ctx.scope(), ctx.common(), sg, vpcConstruct.vpc());

    var passwordFormat = new SecretFormat(false, false, false, false, 32, true);
    var credentials = new SecretCredentials(
      "rds-protected-readers-creds",
      "Protected readers credentials",
      "postgres",
      passwordFormat,
      "RETAIN",
      Map.of());

    var perfInsights = new RdsPerformanceInsights(true, "LONG_TERM");
    var writer = new RdsWriter(false, true, "protected-readers-writer", false, perfInsights);

    var reader1 = new RdsReader(false, true, "protected-reader-1", false, true, perfInsights);
    var reader2 = new RdsReader(false, true, "protected-reader-2", false, true, perfInsights);

    var rdsConf = new Rds(
      "15.4",
      "protected-readers-cluster",
      "protected_readers_db",
      credentials,
      "AURORA",
      false,
      writer,
      List.of(reader1, reader2),
      true,
      "RETAIN",
      Map.of("Protected", "true", "Readers", "2"));

    var construct = new RdsConstruct(
      ctx.scope(),
      ctx.common(),
      rdsConf,
      vpcConstruct.vpc(),
      List.of(sgConstruct.securityGroup()));

    assertNotNull(construct);
    assertNotNull(construct.cluster());
  }
}
