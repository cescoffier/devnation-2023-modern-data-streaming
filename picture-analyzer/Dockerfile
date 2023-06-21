FROM registry.access.redhat.com/ubi8/python-39:1-120.1684740828
LABEL "io.k8s.display-name"="quay.io/devnation/devnation-2023-picture-analyzer" \
      "io.openshift.s2i.build.image"="registry.access.redhat.com/ubi8/python-39" \
      "io.openshift.s2i.build.source-location"="."
ENV APP_CONFIG="gunicorn_config.py"
USER root
# Copying in source code
COPY . /tmp/src
# Change file ownership to the assemble user. Builder image must support chown command.
RUN chown -R 1001:0 /tmp/src
USER 1001
# Assemble script sourced from builder image based on user input or image metadata.
# If this file does not exist in the image, the build will fail.
RUN /usr/libexec/s2i/assemble
# Run script sourced from builder image based on user input or image metadata.
# If this file does not exist in the image, the build will fail.
CMD /usr/libexec/s2i/run
