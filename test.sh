
#!/bin/bash

# Inicializar variable de estado
estado="OK"

# Función para ejecutar comando y comparar la salida
function ejecutar_y_comparar {
    comando=$1
    mensaje_esperado=$2

    # Ejecutar el comando
    salida_comando=$(eval "$comando")

    # Comparar la salida con el mensaje esperado
    if [[ "$salida_comando" != *"$mensaje_esperado"* ]]; then
        echo "FAIL: Comando '$comando' falló. Se esperaba '$mensaje_esperado', pero se obtuvo '$salida_comando'."
        estado="FAIL"
    fi
}

# Lista de comandos y sus mensajes esperados

ejecutar_y_comparar "bin/client/administrationClient.sh -DserverAddress=127.0.0.1:50052 -Daction=addRoom" "Room #1 added successfully"
ejecutar_y_comparar "bin/client/administrationClient.sh -DserverAddress=127.0.0.1:50052 -Daction=addDoctor -Ddoctor=John -Dlevel=4" " Doctor John (4) added successfully"

# Verificar el estado final
if [[ "$estado" == "OK" ]]; then
    echo "Todos los comandos se ejecutaron correctamente: OK"
else
    echo "Algunos comandos fallaron: FAIL"
fi
