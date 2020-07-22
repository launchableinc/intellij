#!/bin/bash -ex
bazel build //ijwb:ijwb_bazel_zip --define=ij_product=intellij-canary 
cp bazel-bin/ijwb/ijwb_bazel.zip /tmp/idea/.IdeaIC/config/plugins/
pushd /tmp/idea/.IdeaIC/config/plugins/
  rm -rf ijwb
  unzip ijwb_bazel.zip
popd
IDEA_PROPERTIES=/$PWD/idea.properties IDEA_VM_OPTIONS=$PWD/idea.vmoptions exec idea
