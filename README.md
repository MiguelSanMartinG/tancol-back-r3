# Pasos para configurar el proyecto

1. **CLONAR**  
   Clona el repositorio del proyecto desde el control de versiones.

2. **DESCARGAR DOCKER**  
   Asegúrate de tener Docker instalado en tu máquina. Si no lo tienes, puedes descargarlo desde [Docker](https://www.docker.com/products/docker-desktop).

3. **Levantar el Dockerfile**  
   Dirígete al directorio del proyecto y ejecuta los siguientes comandos:

    - **Build Docker image**:  
      Para construir la imagen Docker, usa el siguiente comando en la terminal:
      ```bash
      docker build -t postgres-db-desarrollo .
      ```

    - **Verificar existencia del directorio `data`**:  
      Asegúrate de que exista el directorio `data` en la raíz del proyecto. Si no está, crea el directorio.

    - **Ejecutar el Docker**:  
      Una vez construida la imagen, puedes ejecutar el contenedor de Docker con el siguiente comando:

        - En **CMD** (Command Prompt):
          ```bash
          docker run -d --name postgres-db-desarrollo -p 5432:5432 -v ${PWD}/data:/var/lib/postgresql/data postgres-db-desarrollo
          ```

        - En **PowerShell**:
          ```bash
          docker run -d --name postgres-db-desarrollo -p 5432:5432 -v %cd%/data:/var/lib/postgresql/data postgres-db-desarrollo
          ```

4. **Actualizar los datos de conexión a la base de datos**  
   Si es necesario, actualiza las credenciales o configuración de conexión a la base de datos en los archivos de configuración del proyecto.

5. **Run al proyecto**  
   Finalmente, corre el proyecto con el comando adecuado para tu entorno (puede ser un comando como `mvn spring-boot:run`, dependiendo de la tecnología utilizada).
