import java.sql.*;

public class PruebaDeEstres {

    //datos de MI posgreSQL
    private static String port;
    private static String DBname;
    private static String url;
    private static  String user;
    private static  String password;
    //variables de control para hilos
    private static int NUM_CONEXIONES;
    private static int hilosProcesados = 0;
    private static int hilosPerdidos = 0;
    private static int hilosImpresos= 0;
    private static long tiempoInicial;
    private static long tiempoFinal;

    public static void main(String[] args) {   
    java.util.Scanner mainScanner = new java.util.Scanner(System.in);
    asignarDatosBD(mainScanner);
    verificarConexion();
    asignarNUM_CONEXIONES(mainScanner);
    //ya el scanner no se usara mas, se cierra para evitar filtramiento de recursos
    mainScanner.close();
    crearTablaPrueba();
    //hacer despues de crear tabla para que haga (select * from tabla) de una
    tiempoInicial = System.currentTimeMillis(); 
    pruebaDeEstres();      
    mostrarResumen();

}

//metodos usados (en orden)

    //metodo para verificar la conexion con la base de datos

    private static void asignarDatosBD(java.util.Scanner sc) {

            System.out.println("Indique el puerto de la base de datos: ");
            port = sc.nextLine();
            System.out.println("Indique el nombre de la base de datos: ");
            DBname = sc.nextLine();
            System.out.println("Indique el usuario de la base de datos: ");
            user = sc.nextLine();
            System.out.println("Indique la clave de la base de datos: ");
            password = sc.nextLine();

            //url final
            url = "jdbc:postgresql://localhost:" + port + "/" + DBname;
    }
    
    private static void verificarConexion() {
        try (Connection con = DriverManager.getConnection(url, user, password)) {
        if (con != null) {
            System.out.println("Conexion exitosa");
        } else {
            System.out.println("Conexion fallida");
            System.exit(1);
            }
        } catch (SQLException e) {
    System.out.println("Error: " + e.getMessage());
        System.exit(1);
        }
    }

    //metodo para crear tabla y registros (5000)
    private static void crearTablaPrueba() {

        try (Connection con = DriverManager.getConnection(url, user, password);
        //variable que almacena sentencias SQL (CRUD)
            Statement stmt = con.createStatement()) {
                //si la tabla existe, se elimina para hacer la prueba de estres desde 0
            stmt.executeUpdate("DROP TABLE IF EXISTS tabla ");
            stmt.executeUpdate("CREATE TABLE tabla (" + "id SERIAL PRIMARY KEY, " + "descript VARCHAR(10000))");
                //crear 5000 registros NO CONFUNDIR CON NUM_CONEXIONES YA QUE SI SE USA SE PUEDEN CREAR MAS REGISTROS DE LO HABITUAL PLS
                //texto de ejemplo:
            String texto = "a".repeat(10000);
            for (int i=0; i<5000; i++) {
                stmt.executeUpdate("INSERT INTO tabla (descript) VALUES ('" + texto + "')");
            }
            System.out.println("Tabla creada exitosamente");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    //metodo para asignar numero a NUM_CONEXIONES
    private static void asignarNUM_CONEXIONES(java.util.Scanner sc) {
        try {
            System.out.println("Indique el numero de consultas quiere realizar: ");
            NUM_CONEXIONES = sc.nextInt(); 
            sc.close();
        } catch (Exception e) {
            System.out.println("Error: Introduzca un valor entero");
            System.exit(1);
        }
    }

    //metodo para ejecutar prueba de estres 
    private static void pruebaDeEstres() {
    
        for (int i=0; i<NUM_CONEXIONES; i++) {
            //la variable ID se le sumara +1 despues de cada FOR loop y este se printea al final del metodo
            final int ID=i+1;
            new Thread(() -> {

                float inicio = System.currentTimeMillis() - tiempoInicial;
                String estado=" ";
                
                try (Connection con = DriverManager.getConnection(url, user, password);
                    Statement stmt = con.createStatement(); 
                    //variable que almacena la consulta SQL en forma de array (select)
                    ResultSet rs= stmt.executeQuery("SELECT * FROM tabla")) {

                    //vacio para simular carga real mientras se mueve por el array
                    while (rs.next()) {}

                    synchronized (PruebaDeEstres.class) {
                        hilosProcesados++;
                        estado="Hilo procesado";

                    } 
                } catch (SQLException e) {
                    synchronized (PruebaDeEstres.class) {
                        hilosPerdidos++;
                        //mensaje que muestra el error especifico (e.getMessage())
                        estado="Hilo perdido: " + e.getMessage();
                    }
                    
                } finally {

                    //NT: currentTimeMillis() agarra el tiempo del reloj, hacer diferencia=
                    float fin= System.currentTimeMillis() - tiempoInicial;
                    synchronized (System.out) {
                        System.out.printf("Hilo: %d / Inicio: %f segs / Fin: %f segs / Estado: %s%n", ID, inicio/1000, fin/1000, estado);
                        hilosImpresos++;
                        if (hilosImpresos == NUM_CONEXIONES) {
                            //proceeso termino, se le notificara a la consola que ya se imprimeron los HILOS, para que el resumen salga de ultimo
                            System.out.notifyAll(); 
                        }

                    }
                    
                }
            }).start();
        }   
    }

    //metodo para mostrar resumen
    private static void mostrarResumen() {
    
        synchronized (System.out) {
        while (hilosImpresos < NUM_CONEXIONES) {
            try {
                //necesario para que el resumen no se pierda entre status de hilos (main thread va a esperar)
                System.out.wait();
            } catch (InterruptedException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    tiempoFinal = System.currentTimeMillis(); 

    //no confundir con variable fin, esta es la duracion de TODA la prueba, fin es de hilo c/u
    double duracionTotal = (tiempoFinal - tiempoInicial)/1000;
         
    System.out.println("Hilos procesados: " + hilosProcesados);
    System.out.println("Hilos perdidos: " + hilosPerdidos);
    System.out.println("Total de hilos: " + (hilosProcesados + hilosPerdidos));
    System.out.printf("Duracion total de la prueba: %f segs", duracionTotal);
        }
    }
}