package fasti.sh.model.aws.codebuild;

/**
 * Configuration record for AWS CodeStar connection source integration.
 *
 * <p>
 * Defines the source repository configuration for connecting to external version control systems like GitHub, Bitbucket, or GitLab through
 * AWS CodeStar.
 *
 * @param owner
 *          Repository owner (username or organization name)
 * @param repo
 *          Repository name
 * @param branch
 *          Source branch to monitor for changes
 * @param connection
 *          ARN or name of the AWS CodeStar connection
 * @param triggerOnPush
 *          Whether to automatically trigger builds on push events
 */
public record CodeStarConnectionSource(
  String owner,
  String repo,
  String branch,
  String connection,
  boolean triggerOnPush
) {}
