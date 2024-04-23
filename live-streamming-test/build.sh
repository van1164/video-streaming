# shellcheck disable=SC2164
cd nginx
docker build -t van133/nginx-rtmp:test .

copy ../build/libs/KoreanYoutube-0.0.1-SNAPSHOT.jar ./spring/


# shellcheck disable=SC2103
cd ../spring
docker build -t van133/streaming:test .

# shellcheck disable=SC2103
cd ..
docker compose up -d
