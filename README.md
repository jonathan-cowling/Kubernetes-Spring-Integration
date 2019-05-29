# Spring Integration as a Kubernetes Job

An example of a spring integration application reading from and writing to rabbitmq.
The job can be deployed to with
- `helm`  (see `helm/`)
- `kubectl` (see `{rabbit,job}/k8s/*`)
- locally with rabbitmq installed (or configure kubernetes to expose rabbitmq locally,
see `rabbit/k8s/External.yml`).

The job will read all messages off the queue, log them, then terminate.

Messages can be added via the rabbitmq management dashboard (exposed by
default on `localhost:30000`).

Messages consisting only of the word `error` (any case) will be sent to the error
queue.

Scripts `scripts/{up,down}` are used to bring everything up and down respectfully
(rabbitmq initialisation time may cause the initial job instances to fail,
but kubernetes will restart the job automatically).

## Tools

### Application

- [Spring Boot RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Integration](https://spring.io/projects/spring-integration)
- [Spring](https://spring.io/)
- [RabbitMQ](https://www.rabbitmq.com/)

### Deployment

- [Helm](https://helm.sh/)
- [Kubernetes](https://kubernetes.io/)
- [Docker](https://www.docker.com/)