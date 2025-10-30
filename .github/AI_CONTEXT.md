# AI Context: cdk-common

> **Purpose**: This document helps AI assistants quickly understand the cdk-common codebase architecture, patterns, and conventions.

## What is cdk-common?

An **enterprise AWS CDK abstraction library** that provides:
- Configuration-driven infrastructure (YAML/Mustache templates)
- High-level constructs wrapping AWS CDK
- Strong typing via Java 21 records
- Template-based resource definitions

**Key Technologies**: Java 21, AWS CDK 2.219.0, Jackson YAML, Mustache, Lombok

## Core Architecture Pattern

### Model-Construct Separation

```
┌─────────────────┐                    ┌──────────────────┐
│  YAML Template  │ ──Mustache──────>  │  Java Records    │
│  (Configuration)│                    │  (Models)        │
└─────────────────┘                    └────────┬─────────┘
                                               │
                                               v
                                        ┌──────────────────┐
                                        │  CDK Constructs  │
                                        │  (AWS Resources) │
                                        └──────────────────┘
```

**Models** (`fasti.sh.model.*`):
- ALL are **Java records** (immutable)
- Pure data classes, no logic
- Example: `record Table(String name, SortKey partitionKey, ...)`

**Constructs** (`fasti.sh.execute.*`):
- Extend `software.constructs.Construct`
- Build actual AWS CDK resources
- Constructor pattern: `(Construct scope, Common common, ConfigModel conf)`

## Directory Structure

```
src/main/java/fasti/sh/
├── execute/              # Constructs (CDK resource builders)
│   ├── aws/             # Service-specific constructs
│   │   ├── dynamodb/   # DynamoDbConstruct, TableConstruct
│   │   ├── fn/         # LambdaConstruct (fn = function/lambda)
│   │   ├── eks/        # EksConstruct, NodeGroupConstruct
│   │   └── [20+ services]
│   ├── init/           # Foundation (synthesizer, system roles)
│   └── serialization/  # Template.java, Mapper.java, Format.java
│
└── model/               # Configuration POJOs (all records)
    ├── aws/            # Service-specific configs
    │   ├── dynamodb/  # Table, Index, SortKey records
    │   ├── fn/        # Lambda, Runtime records
    │   └── [matches execute/ structure]
    └── main/           # Core domain models
        ├── Common.java    # Metadata container (every construct gets this)
        ├── Environment.java, Version.java, Host.java
        └── Hosted.java

src/test/java/fasti/sh/
├── test/               # CdkTestUtil, RecordTestUtil
├── execute/aws/        # {Service}ConstructsTest
└── model/aws/          # {Service}ModelsTest

src/test/resources/     # Test templates
└── {environment}/{version}/{service}/
    └── *.mustache, *.yaml, *.json
```

## Critical Conventions

### 1. Common Metadata Pattern

**Every construct receives `Common`**:
```java
public record Common(
  String id, account, region, organization,
  String name, alias, environment, version, domain,
  Map<String, String> tags
)
```

Usage:
```java
public DynamoDbConstruct(Construct scope, Common common, Table conf) {
  super(scope, Format.id("dynamodb", common.name()));
  // Use common for tagging, naming, context
}
```

### 2. Naming Conventions

**Package Names**:
- `execute.aws.{service}` - Constructs
- `model.aws.{service}` - Models
- Special: `fn` = Lambda functions

**Class Names**:
- Constructs: `{Service}Construct` (e.g., `DynamoDbConstruct`)
- Models: Descriptive nouns (e.g., `Table`, `Lambda`, `NetworkConf`)

**ID Generation**:
```java
Format.id("service", "component", name)    // "service.component.name"
Format.name("service", "component", name)  // "service-component-name"
Common.id(string)                          // SHA-256 → Base32, 15 chars
```

### 3. Template Processing Flow

```
CDK Context → Template Variables → Mustache Processing → YAML/JSON → Jackson → POJOs
```

**Key Classes**:
- `Template.java` - Loads and processes Mustache templates
- `Mapper.java` - Jackson ObjectMapper singleton (YAML/JSON)
- `Format.java` - Naming conventions, ID generation

**Template Variables**:
- `{{host:*}}` - Physical infrastructure context
- `{{hosted:*}}` - Logical service context
- `{{synthesizer:name}}` - CDK synthesizer name

**Template Location**: `resources/{environment}/{version}/{service}/{file}`
- Environment: `bootstrap`, `prototype`, `production`
- Version: `v1`, `v2`, `v3`

### 4. Construct Pattern

**Standard Constructor**:
```java
public class ServiceConstruct extends Construct {
  @Getter
  private final CdkResource resource;

  public ServiceConstruct(Construct scope, Common common, ConfigRecord conf) {
    super(scope, Format.id("service", common.name()));
    log.debug("ServiceConstruct [common: {} conf: {}]", common, conf);

    // Build CDK resource
    this.resource = CdkResource.Builder.create(this, "id")
      .tags(Maps.from(common.tags(), conf.tags()))
      // ... configuration
      .build();
  }
}
```

### 5. Dual Principal Pattern (IAM)

`RoleConstruct` has TWO constructors:
```java
// 1. External principal (Lambda, EKS, etc.)
public RoleConstruct(Construct scope, Common common, IPrincipal principal, IamRole conf)

// 2. Configured principal (from config)
public RoleConstruct(Construct scope, Common common, IamRole conf)
```

### 6. Testing Pattern

```java
// Test setup
var ctx = CdkTestUtil.createTestContext();
// Returns: TestContext(Construct scope, Common common)

// Create construct
var construct = new DynamoDbConstruct(ctx.scope(), ctx.common(), tableConf);

// Assert
assertNotNull(construct.table());
```

**Test File Naming**:
- `{Service}ConstructsTest` - Tests for constructs
- `{Service}ModelsTest` - Tests for models/records

## Key Service Patterns

### DynamoDB
- Model: `model/aws/dynamodb/Table.java`
- Construct: `execute/aws/dynamodb/DynamoDbConstruct.java`
- Features: GSI/LSI, encryption (AWS/DynamoDB/KMS), billing modes, streams

### Lambda
- Model: `model/aws/fn/Lambda.java`
- Construct: `execute/aws/lambda/LambdaConstruct.java`
- Features: VPC integration, IAM roles, layers, code signing

### VPC
- Model: `model/aws/vpc/NetworkConf.java`
- Construct: `execute/aws/vpc/VpcConstruct.java`
- Features: Multi-AZ subnets, security groups, NAT gateways

### EKS
- Complex nested: `model/aws/eks/addon/` for add-ons
- Support: node groups, service accounts, pod identity, OIDC
- Add-ons: Karpenter, Cert-Manager, AWS Load Balancer Controller

## Code Style

### Lombok Usage
- `@Slf4j` - Logging
- `@Getter` - Getters for constructs
- `@Builder` - Builder pattern for records
- `@SneakyThrows` - Checked exception handling

### Java 21 Features
- **Records** for all models (immutable)
- Pattern matching
- Text blocks for multi-line strings
- Sealed interfaces where appropriate

### Logging Pattern
```java
log.debug("{} [common: {} conf: {}]", "ConstructName", common, conf);
```

### Tag Merging
```java
Maps.from(common.tags(), conf.tags(), customTags)
```

## Creating New Constructs

### Checklist
1. ✅ Create model record in `model/aws/{service}/`
2. ✅ Create construct in `execute/aws/{service}/` extending `Construct`
3. ✅ Use constructor pattern: `(Construct scope, Common common, YourConfig conf)`
4. ✅ Use `Format.id()` for construct ID
5. ✅ Add comprehensive JavaDoc with examples
6. ✅ Create test in `test/execute/aws/{Service}ConstructsTest.java`
7. ✅ Add test templates in `test/resources/{env}/{version}/`

### Template
```java
@Slf4j
public class NewServiceConstruct extends Construct {
  @Getter
  private final AwsResource resource;

  public NewServiceConstruct(Construct scope, Common common, NewServiceConfig conf) {
    super(scope, Format.id("new-service", common.name()));
    log.debug("NewServiceConstruct [common: {} conf: {}]", common, conf);

    this.resource = AwsResource.Builder.create(this, "resource")
      .resourceName(Format.name("new-service", common.name()))
      .tags(Maps.from(common.tags(), conf.tags()))
      .build();
  }
}
```

## Understanding Existing Code

When analyzing a construct:
1. Check its **model record** for configuration structure
2. Review **constructor parameters** and initialization
3. Read **JavaDoc** for feature list and usage examples
4. Check **test files** for usage patterns
5. Look at **template files** in test resources for examples

## Common Pitfalls

1. **Don't forget Common** - Every construct needs it for metadata
2. **Use records for models** - Never use classes for configuration POJOs
3. **ID generation** - Always use `Format.id()` for construct IDs
4. **Tag merging** - Use `Maps.from()` to combine tag sources
5. **Logging** - Include `common` and `conf` in debug logs
6. **Template paths** - Follow `{environment}/{version}/{service}/` structure
7. **Test utilities** - Use `CdkTestUtil.createTestContext()` for tests

## Differences from Standard AWS CDK

1. **Configuration-driven** vs imperative code
2. **Template system** with Mustache and CDK context injection
3. **Higher abstraction** with opinionated defaults
4. **Common metadata** on every construct
5. **Record-based models** for immutability
6. **Dual context** (`host` and `hosted`) for nested deployments

## Quick Links

- 📚 [Full Documentation](../docs/)
- 🏗️ [Architecture Decisions](../docs/architecture/)
- 👩‍💻 [Developer Guide](../docs/developer-workflow/developer-guide.md)
- 🔧 [Quick Reference](../docs/technical-deep-dive/quick-reference.md)
- 📖 [Template System](../docs/build-process/template-system.md)

## Version Info

- **Java**: 21+
- **AWS CDK**: 2.219.0
- **Maven**: 3.8+
- **Package**: `fasti.sh`

---

**Last Updated**: 2025-10-29 (auto-generated by AI exploration)
