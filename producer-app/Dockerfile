FROM ubuntu:latest
WORKDIR /app
COPY build/native/nativeCompile/producer-app .
EXPOSE 8080
ENTRYPOINT ["/app/producer-app"]
