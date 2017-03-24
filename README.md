# Test: Sergio Meza #

La aplicación está desarrollada en su totalidad en KOTLIN, porque KOTLIN en ves de JAVA? porqué KOTLIN me ofrece mas versatilidad a la hora de realizar tareas en las cuales java es bastante complejo de manejar, ademas de tener una sintanxis bastante moderna para los estandares del momento, kotlin nos permite realizar acciones mas facil (lambdas, delegados, metodos de extensión, observables, etc) que java. al ser un lenguaje que corre sobre JVM el resultado y el performance es diria yo que el mismo sobre la plataforma, así que no hay perdida de performance.

Para la visualización de las imagenes solo se utilizaron las imagenes del api que estaban en baja calidad, para evitar el consumo de datos y recursos del sistema en dispositivos no potentes, falto visualizar la foto en alta calidad en el fullscreen de la imagen.

### JAVA ###

* Si es necesario tener la version en JAVA puro de la aplicación la puedo realizar tambien

### Set up y librerias? ###

* Android studio 2.3
* Kotlin 1.1.0
* Play services v25.1.1
* Retrofit para peticiones Web
* Picasso para procesamiento de Imagenes
* Gson Converter de Retrofit para parsear las respuesta en JSON hacia Object
* Libreria de soporte y diseño de google

### Qué faltó? ###

* Tests, 
* Offline o "cachear" las publicaciones locales
* Poner las fotos en alta resolución en el detalle fullscreen.

### Caracteristicas ###

* Infinite Scroll en Grid y Lista, el paginado se hace por medio del infinite scroll
* Filtro en tiempo real tanto sobre nombre como sobre titulo de la aplicación
* Swipe to refresh
* Detalle de la publicación
* Fullscreen detalle de la imagén principal
* Material Design
* Uso de Svg o drawable
* Auto Version en gradle
* MVP en el Main