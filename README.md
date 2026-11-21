# PagoFlex

Aplicación Android para gestionar cuentas por pagar, registrar pagos y consultar su historial, con una interfaz desarrollada en Jetpack Compose. La app consume una API REST construida con microservicios (Spring Boot + Docker).

> Proyecto académico. El registro de pagos es una simulación: no realiza cobros bancarios ni procesa transacciones reales.

## Arquitectura

```text
App Android (Compose + MVVM)
        │  Retrofit + JWT
        ▼
API Gateway :8080 ──► usuarios · deudas · pagos · tarjetas
                      (cada uno con su propia base de datos MySQL)