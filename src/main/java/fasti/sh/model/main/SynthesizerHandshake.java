package fasti.sh.model.main;

import fasti.sh.model.aws.iam.IamRole;

public record SynthesizerHandshake<T>(
  Common common,
  IamRole handshake,
  SynthesizerResources synthesizer
) {}
