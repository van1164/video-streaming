# video-streaming

## 지금까지 상황
### 동영상 업로드
![image](https://github.com/van1164/video-streaming/assets/52437971/bb5cc6ac-2b06-4e77-a8fc-8a0ff8c7c6dc)

### 라이브 스트리밍
![2024-04-1201-56-05-ezgif com-video-to-gif-converter](https://github.com/van1164/video-streaming/assets/52437971/0d95eeee-5a9a-4239-a309-1cf1580f9393)


## 1. 여러 Chunk들을 동시에 업로드해보기

>기존에는 Chunk들을 순차적으로 보내는 방식을 사용했었다.
>이번에는 모든 Chunk에 대한 요청을 동시에 보내고 Promise를 통해 다 보내지면 그 다음 동작을 하도록 나누어 보았다.

![](https://velog.velcdn.com/images/van1164/post/9694046e-4c06-479e-91c7-bfeeb7b9f19b/image.png)



## 약 270MB 영상으로 업로드 속도 비교
### 기존방식 (약 115초)
![](https://velog.velcdn.com/images/van1164/post/28c87411-a0f8-4090-b195-a04da5893dcd/image.png)

### Promise를 사용한 방식 (약 96초)

![](https://velog.velcdn.com/images/van1164/post/556418eb-b06e-475a-a889-ad8cb1e2f03b/image.png)

### ✅ 동시에 여러사람이 업로드했을 경우도 비교를 해보아야겠지만, 우선적으로 한명에 대해서는 비동기적으로 처리한게 더 빨랐다.

<br>

---

<br>

## 2. 비동기적으로 할 수있는건 비동기적으로 하기 (feat. Completable Future)

### 기존 방식 (약 96초)
![](https://velog.velcdn.com/images/van1164/post/af9d6a6b-602e-4965-968c-497d1c370c6d/image.png)




### 비동기적인 방식 (약 81초)
![](https://velog.velcdn.com/images/van1164/post/b4de1682-d47a-454b-a9d2-f84ac7447741/image.png)

![](https://velog.velcdn.com/images/van1164/post/f9ca7507-aba9-436d-bb50-a0cda9332657/image.png)

### ✅ 같은 용량의 파일을 업로드하는 데 13초정도의 시간 절약을 할 수있었다!!


### 디비 설계

![image](https://github.com/van1164/video-streaming/assets/52437971/8f303e85-175f-47c0-8e7e-f415199b8499)
