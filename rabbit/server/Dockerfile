FROM rabbitmq:management

COPY rabbit.conf /etc/rabbitmq/rabbit.conf
COPY definitions.json /etc/rabbitmq/definitions.json

RUN chown rabbitmq:rabbitmq /etc/rabbitmq/rabbit.conf

ENV RABBITMQ_CONFIG_FILE=/etc/rabbitmq/rabbit.conf

EXPOSE 15672
EXPOSE 5672

CMD rabbitmq-server