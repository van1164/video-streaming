FROM ubuntu

RUN apt-get -y update

RUN apt install openjdk-17-jdk -y

RUN  apt-get -y upgrade && apt-get install -y --no-install-recommends ffmpeg

# JAR_FILE 변수 정의 -> 기본적으로 jar file이 2개이기 때문에 이름을 특정해야함
ARG JAR_FILE=./video/build/libs/video-0.0.1-SNAPSHOT.jar

# JAR 파일 메인 디렉토리에 복사
COPY ${JAR_FILE} app.jar

# 시스템 진입점 정의
ENTRYPOINT ["java","-jar","/app.jar"]