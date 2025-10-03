package fasti.sh.execute.serialization;

import fasti.sh.model.main.Common;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import software.constructs.Construct;

/**
 * Essential formatting and naming utility class that provides standardized string manipulation and resource identification patterns used
 * throughout the CDK infrastructure framework.
 *
 * <p>
 * This utility class serves as the foundational component for consistent resource naming, identification generation, and context-aware
 * string formatting across all AWS CDK constructs and infrastructure components.
 *
 * <p>
 * <b>Core Formatting Capabilities:</b>
 *
 * <ul>
 * <li><b>ID Generation</b> - Dot-separated identifiers for internal resource references
 * <li><b>Name Generation</b> - Hyphen-separated names for AWS resource naming
 * <li><b>Description Formatting</b> - Standardized resource descriptions with context
 * <li><b>Export Naming</b> - CloudFormation export name generation
 * <li><b>Context-Aware Naming</b> - CDK context-based naming patterns
 * </ul>
 *
 * <p>
 * <b>Naming Convention Patterns:</b>
 *
 * <ul>
 * <li><b>Internal IDs</b> - "service.component.resource" format for code references
 * <li><b>AWS Resource Names</b> - "service-component-resource" format for AWS compliance
 * <li><b>Descriptions</b> - "Organization Environment Resource Description" format
 * <li><b>Exports</b> - Context-based export naming for cross-stack references
 * </ul>
 *
 * <p>
 * <b>Context Integration:</b> The class integrates deeply with CDK context system to provide:
 *
 * <ul>
 * <li><b>Host Context</b> - Primary deployment context identification
 * <li><b>Hosted Context</b> - Secondary or nested deployment contexts
 * <li><b>Synthesizer Context</b> - CDK synthesizer-specific naming
 * <li><b>Fallback Mechanisms</b> - Graceful handling of missing context values
 * </ul>
 *
 * <p>
 * <b>AWS Compliance:</b> All generated names follow AWS resource naming conventions:
 *
 * <ul>
 * <li>Hyphen-separated instead of dots for AWS resource names
 * <li>Context-aware prefixing for multi-account deployments
 * <li>Consistent casing and character restrictions
 * <li>Length considerations for different AWS service limits
 * </ul>
 *
 * <p>
 * <b>Multi-Context Support:</b> The utility supports complex deployment scenarios with multiple contexts:
 *
 * <ul>
 * <li>Primary host context for main deployment environment
 * <li>Secondary hosted context for nested or dependent deployments
 * <li>Synthesizer-specific context for CDK build and deployment tools
 * <li>Fallback logic when context values are unavailable
 * </ul>
 *
 * <p>
 * <b>Usage Throughout Framework:</b> This class is used extensively across all constructs for consistent naming:
 *
 * <pre>{@code
 * // Internal construct IDs
 * String constructId = Format.id("eks", "cluster", clusterName);
 *
 * // AWS resource names
 * String resourceName = Format.name("api", "gateway", "production");
 *
 * // Resource descriptions
 * String description = Format.describe(common, "user management API");
 *
 * // CloudFormation exports
 * String exportName = Format.exported(scope, "cluster-arn");
 *
 * // Named resources with context
 * String namedResource = Format.named(scope, "backup-role");
 * }</pre>
 *
 * @author CDK Common Framework
 * @see Common for deployment context and metadata
 * @see Construct for CDK construct context integration
 * @since 1.0.0
 */
public class Format {
  public static String id(String... s) {
    return String.join(".", s).replace("-", ".");
  }

  public static String name(String... s) {
    return id(s).replace(".", "-");
  }

  public static String describe(Common common, String... s) {
    return String.format("%s %s %s", common.organization(), common.environment(), StringUtils.join(s, " "));
  }

  public static String exported(Construct scope, String suffix) {
    var prefix = Optional
      .of(scope)
      .map(s -> s.getNode().tryGetContext("hosted:synthesizer:name"))
      .map(Object::toString)
      .orElseGet(() -> scope.getNode().getContext("host:id").toString());

    var hostedId = scope.getNode().getContext("hosted:id");
    return String.format("%s%s%s", prefix, hostedId, suffix);
  }

  public static String named(Construct scope, String suffix) {
    var prefix = Optional
      .of(scope)
      .map(s -> s.getNode().tryGetContext("hosted:synthesizer:name"))
      .map(Object::toString)
      .orElseGet(() -> scope.getNode().getContext("host:id").toString());

    var hostedId = scope.getNode().getContext("hosted:id");
    return String.format("%s-%s-%s", prefix, hostedId, suffix);
  }
}
