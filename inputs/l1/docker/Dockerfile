# Install File
FROM ipflevel1:6.10

USER root

# Use UTC, not EST
RUN rm -f /etc/localtime && ln -s /usr/share/zoneinfo/UTC /etc/localtime

COPY Orchestrator /opt/Orchestrator
# Orchestrator
ADD dependencies_orchestrator.tgz /

# Install
RUN rm -rf /usr/share/doc \
 && rpm -ivh /dependencies_orchestrator/*rpm \
 && cd /opt/Orchestrator && touch README && python setup.py install 

#Set default user
USER ops2pdgs

COPY entrypoint.sh /usr/local/bin/entrypoint.sh

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]