## kubernetes on minikube

```
minikube start --vm-driver virtualbox --memory 4096 --cpus 3

kubectl apply -f kubernetes.yml

http $(minikube service locations --url)/locations 

http $(minikube service locations --url)/locations/Solingen

http $(minikube service maintenance --url)/update?firmwareVersion=2

http $(minikube service maintenance --url)/metrics

kubectl delete pod,service,deployment,secret -l app=workshop

```