FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY styrelserummet-server/build/install/styrelserummet-server /app
ENV STYRELSERUMMET_DATA=/data
ENV PORT=8080
EXPOSE 8080
VOLUME ["/data"]
ENTRYPOINT ["/app/bin/styrelserummet-server"]
