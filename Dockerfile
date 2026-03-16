# 使用官方的 Java 17 运行环境作为基础镜像
FROM eclipse-temurin:17-jre

# 设定工作目录
WORKDIR /app

# 把编译打包好的 jar 包拷贝进容器里，重命名为 app.jar
COPY target/*.jar app.jar

# 告诉容器启动时运行这行命令
ENTRYPOINT ["java", "-jar", "app.jar"]