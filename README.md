Proyecto relacionado a la materia Programacion visual en URU.

1. Descripcion
   
Codigo hecho en Java que simula la prueba de estres y caida de una base de datos generica en postgreSQL usando hilos.

2. Metodos disponibles

    * asignarDatosDB: metodo utilizado para asignar los datos del postgreSQL (puerto, nombre de BD, usuario)
      
    * verificarConexion: metodo que realiza una sentencia try-catch para realizar la conexion a la base de datos usando los datos de la funcion "asignarDatosBD" si son correctos se conecta, si no se muestra
    en la consola la razon de por que fallo la conexion.

    * crearTablaPrueba: metodo que una vez conectado a la base de datos, crea la tabla con los campos "id" y "descript" (este ultimo con una capacidad de 10000 caracteres VARCHAR)
      NT: si existe una tabla anterior a esta o se vuelve a correr el programa, borrara la tabla anterior para hacer la prueba de estres desde 0.

    * asignarNUM_Conexiones: metodo utilizado para realizar el numero de consultas (conexiones) que se quieren hacer a la BDD.
  
    * pruebaDeEstres: metodo principal de la prueba, creara la N cantidad de hilos usando un runnable "new Thread(()-> {]).start()" (clase lambda) y llevara el control de los hilos perdidos y procesados con un contador
      que se almaceran en las variables de control globables (Static). Durante la ejecucion mostrara la informacion de cada hilo por separado (NRO HILO, tiempo de creacion, tiempo de finalizacion)

    * mostrarResumen: muestra el resumen final de la prueba de estres con: Hilos; perdidos y procesados; totales y el tiempo de duracion de la prueba en general.
  
3. Requisitos para ejecutar el codigo

   A. un IDE que soporte java (Eclipse, Vscode, Netbeans)
   B. jdk (Java Development Kit, adquirir de la pagina oficial de ORACLE™)
   C. archivo del driver jdbc (Java database connectivity) (importante para realizar la conectividad a postgreSQL)
   D. postgreSQL configurado y con una base de datos ya creada (puede estar vacio, lo importante es que la BDD exista)

NT: este proyecto se realizo sin repositorio de GITHUB, por eso solo existe un solo commit, lo quise subir a un repositorio ya que este proyecto sera la base de otro proyecto de la materia.


   
   
