package fasti.sh.model.aws.cognito.userpool;

import fasti.sh.model.aws.fn.AsyncLambda;
import fasti.sh.model.aws.fn.Lambda;
import fasti.sh.model.aws.fn.LambdaLayer;

public record Triggers(
  LambdaLayer base,
  Lambda preSignUp,
  Lambda customMessage,
  Lambda postConfirmation,
  Lambda postAuthentication,
  AsyncLambda postEventProcessor
) {}
