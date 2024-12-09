#! /bin/sh
### 接收参数
#Deployment name
app=$1
env=$2
ver=$3
echo 'current env is ' + $env
imagename="fsserver/$app:$ver"
nexusip=''
if [ $env = 'dev' ]; then
  nexusip='dockerlib.fujica.com.cn:8082'
elif [ $env = 'test' ]; then
  nexusip='172.16.0.232:8082'
elif [ $env = 'pro' ]; then
  nexusip='172.16.0.224:8082'
elif [ $env = 'pre' ]; then
  nexusip='172.16.0.232:8082'
else
  nexusip='172.16.0.232:8082'
fi

sed -i "/namespacelabel/s/namespacelabel/fsprod/g" script/deployment.yaml

echo 'imagename is ' + $imagename

sed -i "/myapp/s/myapp/$app/g" script/deployment.yaml
sed -i "/imagename/s/imagename/$ver/g" script/deployment.yaml

sed -i "s/nexusip/$nexusip/g" script/deployment.yaml

kubectl delete -f script/deployment.yaml
kubectl apply -f script/deployment.yaml