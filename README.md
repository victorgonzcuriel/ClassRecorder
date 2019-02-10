# ClassRecorder: Aplicación web y móvil para la grabación y procesamiento de clases presenciales

ClassRecorder es una aplicación distribuida que permite grabar simultaneamente la imagen del ordenador con la voz del interlocutor.
Ademas, dispone de un lector de pdf con control remoto desde un dispositivo móvil.
Funciona bajo GNU-Linux.

## Como empezar

Para trabajar con ClassRecorder, simplemente clone el repositorio en un repositorio propio.
Para desarrollar ClassRecorder, se ha utilizado:
* Spring Tool Suite 3.9.0
* Android Studio 3.0.1

### Prerequisitos para su uso:

ClassRecorder funciona bajo GNU Linux en el extremo web. Para poder desplegarlo, necesita las siguientes caracteristicas en el servidor:
* FFMPEG: https://www.ffmpeg.org/
* JRE 8 o superior: https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
* Apache tomcat: https://tomcat.apache.org/
* Ngrok: https://ngrok.com/

En el extremo móvil, ClassRecorder fuciona en cualquier dispositio android con version 26 o superior.

## Instalación
En el dispositivo móvil:cn
* Instale el apk.
* En su primera ejecución, acepte los permisos que le pide la aplicación.

En el ordenador:
* Depliegue el war en el tomcat del equipo.
* Configure el tomcat para que pueda utilizar el teclado del dispositivo (headless=false).

## Guia de uso
Consulte el Manual de usuario.

## Licencia

Este proyecto esta bajo licencia Apache 2. Consulte el [LICENSE](LICENSE) para más información.