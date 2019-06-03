# Spring Integration as a Kubernetes Job

An example of a spring integration application reading from and writing to rabbitmq.
The application will read all messages off the queue, log them, then terminate.

Messages can be added via the rabbitmq management dashboard (exposed by
default on `localhost:30000`).

Messages consisting solely of the word `error` (any case) will be sent to the error
queue.

## Deployment

The application can be deployed as a job with multiple tools (each separate
method/tool is given it's own directory under `deployment/`).
The deployment methods are as follows: 
- `helmfile`
- `helmsman`
- `helm`
- `kubectl`

> Some deployments can run the application as either a job or cronjob
> (scheduled job).

The application can also be run locally with rabbitmq installed (or configure kubernetes to expose rabbitmq locally,
see `deployment/kubernetes/RabbitExternal.yml`).

Scripts `deployment/*/{up,down}.sh` are used to bring everything up and down respectfully
(rabbitmq initialisation time may cause the initial job instances to fail,
but kubernetes will restart the job automatically).

## FAQ

- Q: Why isn't my application running with my latest changes?

  A: Make sure the docker image have been created (use `deployment/docker/images.sh`)
  since the changes have been made.

- Q: Why won't my helm/helmfile/helmsman deployments deploy

  A: check (using `helm list --all`) that no releases are there (remove deleted
  releases with `helm purge <RELEASE_NAME>`)

## Tools

### Application

- [Spring Boot RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Integration](https://spring.io/projects/spring-integration)
- [Spring](https://spring.io/)
- [RabbitMQ](https://www.rabbitmq.com/)

### Deployment

- [Helmfile](https://github.com/roboll/helmfile/)
- [Helmsman](https://github.com/Praqma/helmsman/)
- [Helm](https://helm.sh/)
- [Kubernetes](https://kubernetes.io/)
- [Docker](https://www.docker.com/)