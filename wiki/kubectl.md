# Useful Commands

- `kubectl proxy --port <HOST_PORT>` - creates a proxy server that allows inspection
of kubernetes objects. The proxy can also forward HTTP traffic to pods. 
- `kubectl port-forward <POD_NAME> <HOST_PORT>:<CONTAINER_PORT>` - Forwards a
port on the host onto a port on a given pod
(forwards on the TCP level, so can work with more than HTTP).
- `kubectl get <FILTER>` gets a set of kubernetes objects e.g. `services`,
`deployments`, `<OBJECT_NAME>`
- `kubectl apply -f <FILE> -f <FILE>...` - creates or updates objects based on
the files supplied
- `kubectl delete <OBJECT_NAME>` - delete a given object, `kubectl delete` can
also take files using the same syntax as apply, this removes the objects that
were created with apply on the corresponding files

> Remember when using `kubectl delete -f <FILE_NAME>` to use the command
before making changes to the file