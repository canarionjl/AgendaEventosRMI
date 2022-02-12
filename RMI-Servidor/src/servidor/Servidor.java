
package servidor;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {
     
    
    public static void main (String[] args){
        try {
            Registry r = LocateRegistry.createRegistry(1099);
            AlmacenDatos almacenDatos = new AlmacenDatos();
            r.bind("Eventos",almacenDatos);
            ThreadComprobadorFecha comprobador = new ThreadComprobadorFecha(almacenDatos);
            comprobador.start();
            System.out.println("El servidor de Eventos se encuentra operativo");
           
        } catch (AlreadyBoundException |RemoteException ex) { 
            System.out.println("Error al inciar el servidor");
            System.out.println(ex.getMessage());
            System.exit(1);//se finaliza el programa con un error en la ejecución
          }
    }
}
