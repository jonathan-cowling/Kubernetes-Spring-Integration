FROM rabbitmq:management

COPY rabbit.conf /etc/rabbitmq/rabbit.conf

RUN chown rabbitmq:rabbitmq /etc/rabbitmq/rabbit.conf

ENV RABBITMQ_CONFIG_FILE=/etc/rabbitmq/rabbit.conf

EXPOSE 15672
EXPOSE 5672

RUN rabbitmq-server &

RUN rabbitmqadmin declare queue -u "guest" -p "guest" --vhost="/" name="/testQ" durable=true