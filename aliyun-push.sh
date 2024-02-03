docker login --username=lht1605510398 --password=root1234 registry.cn-hangzhou.aliyuncs.com

repo_address="registry.cn-hangzhou.aliyuncs.com/group17"

# 将本地打包好的镜像打一个tag
# "softengineering"字段可更改，视docker-compose up -d后的具体情况而定，一般为项目文件名称。
docker tag softengineering-mysql-db ${repo_address}/mysql-db:latest
docker tag softengineering-conferences-serve ${repo_address}/conferences-serve:latest
docker tag softengineering-eureka ${repo_address}/eureka:latest
docker tag softengineering-paper-serve ${repo_address}/paper-serve:latest
docker tag softengineering-role-serve ${repo_address}/role-serve:latest
docker tag softengineering-user-serve ${repo_address}/user-serve:latest
docker tag softengineering-gateway ${repo_address}/gateway:latest
docker tag softengineering-frontend ${repo_address}/frontend:latest



# 推到自己的阿里云镜像仓库
docker push ${repo_address}/mysql-db:latest
docker push ${repo_address}/conferences-serve:latest
docker push ${repo_address}/eureka:latest
docker push ${repo_address}/paper-serve:latest
docker push ${repo_address}/role-serve:latest
docker push ${repo_address}/user-serve:latest
docker push ${repo_address}/gateway:latest
docker push ${repo_address}/frontend:latest

