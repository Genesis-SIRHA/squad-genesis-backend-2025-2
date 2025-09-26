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

![img.png](docs/UML/diagramaContexto.png)

### Diagramas de casos de Uso

![img.png](docs/UML/diagramaCasosUso.png)

### Diagrama Componentes General

![img.png](docs/UML/diagramaComponentesGeneral.png)
### Diegrama Componentes Especifico
![img.png](docs/UML/diagramaComponentesEspecifico.png)

### Diagrama de Bases de Datos
![img.png](docs/UML/diagramaBaseDatos.png)

### Diagrama de clases

![img.png](docs/UML/diagramaClases1.png)

![img.png](docs/UML/diagramaClases2.png)

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