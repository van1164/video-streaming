FROM alpine:3.13.4 as builder

RUN apk add --update build-base git bash gcc make g++ zlib-dev linux-headers pcre-dev openssl-dev

# nginx, nginx-rtmp-module 다운
RUN git clone https://github.com/arut/nginx-rtmp-module.git && \
    git clone https://github.com/nginx/nginx.git

# nginx를 설치하고 nginx-rtmp-module 추가
RUN cd nginx && ./auto/configure --add-module=../nginx-rtmp-module && make && make install

FROM alpine:3.13.4 as nginx

# ffmpeg 설치
RUN apk add --update pcre ffmpeg

# 빌드된 파일 및 설정파일 복사
COPY --from=builder /usr/local/nginx /usr/local/nginx
COPY nginx.conf /usr/local/nginx/conf/nginx.conf

# 실행
ENTRYPOINT ["/usr/local/nginx/sbin/nginx"]
CMD ["-g", "daemon off;"]