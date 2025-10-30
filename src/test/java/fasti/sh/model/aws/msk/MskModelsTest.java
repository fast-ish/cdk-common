package fasti.sh.model.aws.msk;

import static fasti.sh.test.RecordTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

import fasti.sh.model.aws.eks.ServiceAccountConf;
import fasti.sh.model.aws.iam.IamRole;
import fasti.sh.model.aws.iam.Principal;
import fasti.sh.model.aws.iam.PrincipalType;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for MSK model records.
 */
class MskModelsTest {

  @Test
  void testClientBasic() {
    var metadata = new ObjectMetaBuilder()
      .withName("msk-client")
      .withNamespace("default")
      .build();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("kafka.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "msk-role",
      "MSK client role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var serviceAccount = new ServiceAccountConf(metadata, role);
    var client = new Client("kafka-client", serviceAccount);

    assertEquals("kafka-client", client.name());
    assertNotNull(client.serviceAccount());
    assertEquals("msk-client", client.serviceAccount().metadata().getName());
    assertRecordToString(client);
  }

  @Test
  void testClientEquality() {
    var metadata = new ObjectMetaBuilder()
      .withName("client1")
      .build();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("kafka.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "role1",
      "Role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var serviceAccount = new ServiceAccountConf(metadata, role);
    var client1 = new Client("c1", serviceAccount);
    var client2 = new Client("c1", serviceAccount);

    assertRecordEquality(client1, client2);
  }

  @Test
  void testMskBasic() {
    var metadata = new ObjectMetaBuilder()
      .withName("client1")
      .build();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("kafka.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "role1",
      "Role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var serviceAccount = new ServiceAccountConf(metadata, role);
    var client = new Client("client1", serviceAccount);

    var msk = new Msk(
      "test-cluster",
      List.of(client),
      Map.of("Environment", "test"));

    assertEquals("test-cluster", msk.name());
    assertEquals(1, msk.clients().size());
    assertEquals(1, msk.tags().size());
    assertRecordToString(msk);
  }

  @Test
  void testMskMultipleClients() {
    var metadata1 = new ObjectMetaBuilder()
      .withName("client1")
      .build();

    var metadata2 = new ObjectMetaBuilder()
      .withName("client2")
      .build();

    var principal = Principal
      .builder()
      .type(PrincipalType.SERVICE)
      .value("kafka.amazonaws.com")
      .conditions(Map.of())
      .build();

    var role = new IamRole(
      "msk-role",
      "MSK role",
      principal,
      List.of(),
      List.of(),
      Map.of());

    var serviceAccount1 = new ServiceAccountConf(metadata1, role);
    var serviceAccount2 = new ServiceAccountConf(metadata2, role);
    var client1 = new Client("client1", serviceAccount1);
    var client2 = new Client("client2", serviceAccount2);

    var msk = new Msk(
      "multi-client-cluster",
      List.of(client1, client2),
      Map.of());

    assertEquals(2, msk.clients().size());
    assertEquals("client1", msk.clients().get(0).name());
    assertEquals("client2", msk.clients().get(1).name());
  }

  @Test
  void testMskNoClients() {
    var msk = new Msk(
      "no-clients",
      List.of(),
      Map.of());

    assertTrue(msk.clients().isEmpty());
    assertEquals("no-clients", msk.name());
  }
}
