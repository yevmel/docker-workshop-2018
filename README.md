## kubernetes on minikube

```

kubectl create -f kubernetes.yml

open $(minikube service locations --url)/locations

http $(minikube service maintenance --url)/update?firmwareVersion=2

http $(minikube service maintenance --url)/metrics

kubectl delete pod,service,deployment,secret -l app=workshop

```