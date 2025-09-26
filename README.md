# Squad-genesis-backend-2025-2
___

## Miembros del equipo:

- Sofía Ariza Goenaga
- Carolina Cepeda Valencia
- Marlio Jose Charry Espitia
- Manuel Alejandro Guarnizo Garcia
- Daniel Palacios Moreno

## Estructura del proyecto
![img.png](docs/Img/Estructura.png)

## Resumen manejo de las tareas
![img.png](docs/Img/jira.png)
## Estrategías de gitflow:

---
1. Ramas Principales: 
- main: rama encargada de las releases del sprint
- develop: En esta rama nos encargamos de desarrollar y de integrar las nuevas funcionalidades antes de pasar a main.

2. Otras ramas utilizadas:
* feature/create_courses: Verificamos funcionalidades de crear los cursos.
* feature/request-query: Para implementar la logica de consulta de solicitudes.
* feature/respondRequests: Para desarrollar la parte encargada de resolver las solicitudes.
* feature/unit-tests: Esta fue creada con el fin de llevar a cabo los test necesarios del back.
* prueba1: Esta rama fue temporal con el fin de los primeros bosquejos de codigo que teniamso pensados.
* feature/README: Rama para actualizar o modificar el archivo README del proyecto.
* hotfix/refactorizacion: Rama para correcciones rápidas y mejoras en la estructura del código.
* revert-10-feature/respondRequests: Rama creada para revertir cambios específicos relacionados con la funcionalidad de responder solicitudes.
* feature/pemsum: Rama para implementar o mejorar una funcionalidad específica (detalles no claros por el nombre).
* feature/new-unit-tests: Rama creada para agregar nuevos tests unitarios al proyecto.

# Analisis y diseño
___
### Diagramas de Contexto

Se representa la interacción entre los actores principales y el sistema SIHRA. Mientras el
estudiante crea solicitudes y observa las respuestas dadas por el administrativo.
![img.png](docs/UML/diagramaContexto.png)

### Diagramas de casos de Uso

![img.png](docs/UML/diagramaCasosUso.png)

### Diagrama Componentes General

![img.png](docs/UML/diagramaComponentesGeneral.png)
### Diegrama Componentes Especifico
![img.png](docs/UML/diagramaComponentesEspecifico.png)

### Diagrama de Bases de Datos
Se muestra la estructura de los documentos NOSQL. Las colecciones principales son las
siguientes:
- historical : historial académio de los estudiantes.
- faculties : información de facultades y sus cursos.
- groups : datos de los grupos (clases).
- sessions : los horarios de las sesiones de clase.
- requests : Solicitudes realizadas por los estudiantes.
- universityMembers : miembros de la universidad ( estudiante, administrativo)
![img.png](docs/UML/diagramaBaseDatos.png)

### Diagrama de clases basado en 3 servicios principales

#### Scheduler Service
Se definen las clases relacionadas con la gestión de horarios y sesiones. Se incluyen servicios que
permiten obtener las sesiones actuales, consultar horarios por grupo y manejar periodos académicos (1,2,I).
![img.png](docs/UML/diagramaClases1.png)

#### Request Service
Este servicio está centrado en la gestión de solicitudes. Maneja la creación,
gestión y respuesta de solicitudes, además de estadísticas asociadas. Tiene conexión con
el servicio MembersService y FacultyService para validar la información de usuarios y usar estrategias
de consulta dependiendo de su rol.
![img.png](docs/UML/diagramaClases2.png)

### Pensum Service
Este servicio está relacionado con la validación de planes de estudio y sus
respectivos cursos.Permite construir el perfil académico del estudiante, validar cursos aprobados
y su plan de estudio. Se apoya en FacultyService para manejar la información académica.

![img.png](docs/UML/diagramaClases3.png)

### Diagramas de secuencia

#### 1. Login
![img.png](docs/UML/diagramaLogin.png)

#### 2. Consultar Solicitudes administrativos
![img.png](docs/UML/diagramaConsultarSolicitudes.png)

#### 3. Consultar Solicitudes Estudiantes
![img.png](docs/UML/consultarSolcitudesEstudiantes.png)

#### 4. Responder Solicitudes 

![img.png](docs/UML/diagramaResponderSolicitudes.png)

### 5. Crear una Solicitud
![img.png](docs/UML/diagramaCrearSolicitud.png)

### 6. Actualizar Status de una solicitud
![img.png](docs/UML/diagramaActualizarStatus.png)

### 7. Estadísticas de solicitudes
![img.png](docs/UML/diagramaEstadisticasSolicitud.png)

### 8. Obtener todos los cursos
![img.png](docs/UML/diagramaObtenerCursos.png)

### 9. Obtener un curso por su código
![img.png](docs/UML/diagramaObtenerCursoPorCodigo.png)

### 10. Crear un curso
![img.png](docs/UML/diagramaCrearCurso.png)

### 11. Actualizar los cursos
![img.png](docs/UML/diagramaActualizarCursos.png)

### 12. Añadir grupo a los cursos
![img.png](docs/UML/diagramaAñadirGrupoCurso.png)

### 13. Eliminar un curso
![img.png](docs/UML/diagramaEliminarCurso.png)

### 14. Consultar horario
![img.png](docs/UML/diagramaConsultarHorario.png)

### 15. Obtener Pensum
![img.png](docs/UML/diagramaObtenerPEnsum.png)

## Calidad del código
___
### Cobertura de pruebas unitarias

En la sprint número dos se presenta la siguiente cobertura de pruebas unitarias
![img.png](docs/Img/jacocoS2.png)

Se hace aumento de pruebas unitarias, en base a ...

![img.png](docs/Img/jacocoS3.png)

### Calidad del código estático Sonarqube
![img.png](docs/Img/sonarQubeS3.png)