FROM ubuntu:latest
WORKDIR /app
COPY build/native/nativeCompile/test-app .
EXPOSE 8080
ENTRYPOINT ["/app/test-app"]