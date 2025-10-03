package fasti.sh.model.aws.codebuild;

import org.apache.commons.text.WordUtils;

/**
 * Enumeration of build steps in the AWS CodeBuild pipeline process.
 *
 * <p>
 * Defines the different phases of a build process including building, asset management, and deployment steps.
 */
public enum BuildStep {
  BUILD, ASSETS, DEPLOY;

  /**
   * Returns the string representation of the build step.
   *
   * <p>
   * Converts the enum constant to its appropriate string value: BUILD becomes lowercase "build", while ASSETS and DEPLOY become properly
   * capitalized strings using WordUtils.
   *
   * @return String representation of the build step
   */
  public String value() {
    if (this.equals(BUILD)) {
      return BUILD.toString().toLowerCase();
    }
    return WordUtils.capitalizeFully(this.toString());
  }
}
