FROM sebp/elk

ADD ELK/02-beats-input.conf /etc/logstash/conf.d/02-beats-input.conf

WORKDIR ${LOGSTASH_HOME}
RUN gosu logstash bin/logstash-plugin install logstash-input-http