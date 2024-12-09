#! /bin/sh
### 接收参数
env=$2
ver=$3
echo 'current env is ' + $env
echo 'current ver is ' + $ver
imagename=''
nexusip=''
if [ $env = 'dev' ]; then
  nexusip='dockerlib.fujica.com.cn:8082'
elif [ $env = 'test' ]; then
  nexusip='172.16.0.232:8082'
elif [ $env = 'pre' ]; then
  nexusip='172.16.0.232:8082'
elif [ $env = 'pro' ]; then
  nexusip='172.16.0.224:8082'
else
  nexusip='172.16.0.224:8082'
fi
imagename="fsserver/$1:$ver"
echo 'imagename is ' + $imagename
#docker build -t $imagename --rm -f script/Dockerfile --build-arg JAR_NAME=$1 .
docker build -t $imagename --rm -f script/Dockerfile --build-arg JAR_NAME=$1 --build-arg ENV_NAME=$2 .
docker tag $imagename $nexusip/$imagename
docker push $nexusip/$imagename

