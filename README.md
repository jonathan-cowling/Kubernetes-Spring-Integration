# Spring Integration as a Kubernetes Job

An example of a spring integration application reading from and writing to rabbitmq.
The job can be deployed to kubernetes (see `{rabbit,job}/k8s/*`) or run locally by
configuring kubernetes to expose rabbitmq locally (or install rabbitmq yourself).

The job will read all messages off the queue, log them, then terminate.

Messages can be added via the rabbitmq management dashboard (exposed on
`localhost:30000`).

Messages consisting only of the word `error` (any case) will be sent to the error
queue.

Scripts `scripts/{up,down}` are used to bring everything up and down respectfully
(rabbitmq initialisation time may cause the initial job instances to fail,
but kubernetes will restart the job automatically).