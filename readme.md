
# Trabajo Practico Especial de Programacion de Objetos Distribuidos

## Integrantes

- Bendayan, Alberto (Legajo: 62786)
- Boullosa Gutierrez, Juan Cruz (Legajo: 63414)
- Deyheralde, Ben (Legajo: 63559)

## Docentes
Meola, Franco Román
Turrin, Marcelo Emiliano

## Requisitos
Debe tener instalado:
- Maven


## Compilacion
Correr en la terminal el siguiente comando:
```bash
mvn clean install
```
```bash
./tpe_builder.sh
```
Con los siguientes argumentos:
- -c: Compila el cliente
- -s: Compila el servidor
- -cs: Compila el cliente y el servidor

## Ejecucion
### Servidor
Correr en la terminal el siguiente comando para correr el servidor:
```bash
bin/server/run-server.sh
```
### Servicios
En todos los servicios:
xx.xx.xx.xx:yyyy es la dirección IP y el puerto donde está publicado el servicio de administración
actionName es el nombre de la acción a realizar (que se detallan en las siguientes secciones)

#### Servicio de Administracion
Funcionalidad: Agregar consultorios, agregar médicos y administrar la disponibilidad de los médicos
Usuario: Encargado de la Sala de Emergencia
Cliente: La información de cuál es la acción a realizar se recibe a través de argumentos de línea de comando al llamar al script del cliente de administración administrationClient.sh y el resultado se debe imprimir en pantalla.
```bash
$> sh administrationClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Ddoctor=doctorName | -Dlevel=levelNumber | -Davailability=availabilityName ]
```

actionNames:
- Agregar un consultorio
```bash
$> sh administrationClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=addRoom
```
- Agregar un médico
```bash
$> sh administrationClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=addDoctor -Ddoctor=name -Dlevel=N
```
Donde N es un numero del 1 al 5
- Definir la disponibilidad de un médico
```bash
$> sh administrationClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=setDoctor -Ddoctor=name -Davailability=AVAILABILITY
```
Siendo AVAILABILITY uno de los siguientes valores:
- Available
- Unavailable
- Consultar la disponibilidad de un médico
```bash
$> sh administrationClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=checkDoctor -Ddoctor=John
```
#### Servicio de Sala de Espera
Funcionalidad: Registrar pacientes que ingresan a la sala, actualizar el nivel de sus emergencias y consultar la espera aproximada de los pacientes para ser atendidos.
Usuario: Encargado de la Sala de Espera
Cliente: La información de cuál es la acción a realizar se recibe a través de argumentos de línea de comando al llamar al script del cliente de sala de espera waitingRoomClient.sh y el resultado se debe imprimir en pantalla.
```bash
$> sh waitingRoomClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Dpatient=patientName | -Dlevel=levelNumber ]
```

actionNames:
- Registrar un paciente
```bash
$> sh waitingRoomClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=addPatient -Dpatient=name -Dlevel=N
```
Donde N es un numero del 1 al 5
- Actualizar el nivel de emergencia de un paciente
```bash
$> sh waitingRoomClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=updateLevel -Dpatient=name -Dlevel=N
```
Donde N es un numero del 1 al 5
- Consultar la espera aproximada de un paciente
```bash
$> sh waitingRoomClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=checkPatient -Dpatient=name
```
### Servicio de Atencion de Emergencias
Funcionalidad: Iniciar y finalizar la atención de emergencias en consultorios
Usuario: Encargado de la Sala de Emergencia
Cliente: La información de cuál es la acción a realizar se recibe a través de argumentos de línea de comando al llamar al script del cliente de atención de emergencias emergencyCareClient.sh y el resultado se debe imprimir en pantalla.
```bash
$> sh emergencyCareClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName [ -Droom=roomNumber | -Ddoctor=doctorName | -Dpatient=patientName ]
```

actionNames:
- Iniciar la atención de una emergencia en un consultorio
```bash
$> sh emergencyCareClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=carePatient -Droom=N
```
Donde N es un numero del 1 al 5
- Iniciar la atención de emergencias en los consultorios libres
```bash
$> sh emergencyCareClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=careAllPatients
```
- Finalizar la atención de una emergencia en un consultorio
```bash
$> sh emergencyCareClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=dischargePatient -Droom=N -Ddoctor=doctorName -Dpatient=patientName
```
Donde N es un numero del 1 al 5
### Servicio de Notificación al Personal
Funcionalidad: Registrar a los médicos para que sean notificados de los eventos relacionados a las emergencias, anular ese registro y consultar el historial de eventos que sucedieron
Usuario: Médicos de la Sala de Emergencias
Cliente: La información de cuál es la acción a realizar se recibe a través de argumentos de línea de comando al llamar al script del cliente de notificación al personal doctorPagerClient.sh y el resultado se debe imprimir en pantalla.
```bash
$> sh doctorPagerClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName -Ddoctor=doctorName
```

actionNames:
- Registrar a un médico para ser notificado
```bash
$> sh doctorPagerClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=register -Ddoctor=name
```
- Anular el registro de un médico
```bash
$> sh doctorPagerClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=unregister -Ddoctor=name
```
### Servicio de Consulta
Funcionalidad: Consultar el estado actual de los consultorios, el listado de pacientes que están esperando en la sala y el historial de atenciones de emergencias finalizadas.
Usuario: Área de Auditoría del Hospital
Cliente: La información de cuál es la acción a realizar se recibe a través de argumentos de línea de comando al llamar al script del cliente de consulta queryClient.sh y el resultado se debe imprimir en pantalla.
```bash
$> sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName -DoutPath=filePath.csv [ -Droom=roomNumber ]
```
donde:
filePath.csv es el path del archivo de salida (absoluto o relativo) con los resultados de la query elegida en formato CSV (separado por comas)

actionNames:
- Estado actual de los consultorios
```bash
$> sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=queryRooms -DoutPath=filePath.csv
```
- Pacientes esperando a ser atendidos
```bash
$> sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=queryWaitingRoom -DoutPath=filePath.csv
```
- Atenciones finalizadas
```bash
$> sh queryClient.sh -DserverAddress=xx.xx.xx.xx:yyyy -Daction=queryCares -DoutPath=filePath.csv
```
