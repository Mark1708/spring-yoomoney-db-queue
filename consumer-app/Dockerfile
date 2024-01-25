FROM ubuntu:latest
WORKDIR /app
COPY build/native/nativeCompile/consumer-app .
EXPOSE 8080
ENTRYPOINT ["/app/consumer-app"]