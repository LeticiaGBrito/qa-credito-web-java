# Compila e empacota a aplicação com Java 17.
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /projeto

COPY . .

RUN chmod +x gradlew \
    && ./gradlew --no-daemon --console=plain installDist


# Executa somente a aplicação e suas dependências.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /projeto/build/install/ /opt/aplicacoes/

# Padroniza o diretório, independentemente do nome da distribuição.
RUN mkdir -p /app/distribuicao \
    && cp -a /opt/aplicacoes/*/. /app/distribuicao/

ENV HOST=0.0.0.0
ENV PORT=8080

EXPOSE 8080

CMD ["java", "-cp", "/app/distribuicao/lib/*", "credito.CreditoWebApplication"]