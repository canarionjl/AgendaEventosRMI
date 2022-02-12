
package cliente;

import comun.Evento;
import comun.ServicioRemoto;
import comun.Usuario;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class ClienteEventos {

    private static boolean sesionIniciada = false;
    private static String nombreUsuario;
    private static String password;
    private static Scanner lector = new Scanner(System.in);
    static String servidorRemoto;
    static ServicioRemoto sr;

    public static int mostrarMenuInicial(int opcionMenu) throws Exception {
        String texto;
        int opcion;
        switch (opcionMenu) {
            case 1:
                texto = "Seleccione una opción: \n1) Iniciar sesión \n2) Registrarse \n3) Continuar sin iniciar sesión\n4) Eliminar Usuario \n5) Salir";
                opcion = 5;
                break;
            case 2:
                texto = "Seleccione una opción: \n1) Presentar Eventos \n2) OrdenarEventos \n3) FiltrarEventos\n4) Añadir Evento \n5) Eliminar Evento \n6)Cerrar sesión y/o volver atrás\n7)Salir";
                opcion = 7;
                break;
            default:
                throw new Exception("Error en la selección del menú");
        }

        int valor;
        String entrada;
        boolean valorCorrecto = false;
        System.out.println(texto);
        while (!valorCorrecto) {
            try {
                entrada = lector.nextLine();
                valor = Integer.parseInt(entrada);
                if (valor < 1 || valor > opcion) {
                    System.out.println("Introduzca de nuevo el valor, por favor");
                    valorCorrecto = false;
                } else {
                    valorCorrecto = true;
                    return valor;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduzca otra vez el valor, por favor");
                valorCorrecto = false;
            }
        }
        return opcion;
    }

    public static void iniciarSesion() {
        System.out.println("Introduzca su nombre de usuario");
        String nombreUsuario = lector.nextLine();
        System.out.println("Introduzca su contraseña");
        String password = lector.nextLine();
        try {
            if (sr.comprobarUsuario(nombreUsuario, password)) {
                ClienteEventos.sesionIniciada = true;
                ClienteEventos.nombreUsuario = nombreUsuario;
                ClienteEventos.password = password;
                System.out.println("Se ha iniciado sesión correctamente para " + nombreUsuario);
            } else {
                System.out.println("No existe el usuario. Revise si ha introducido correctamente las credenciales");
            }
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    public static void registrarUsuario() {
        int contador=1;
        String nombreUsuario = "",correoElectronico="";
        boolean nombreNoValido = false;
        ArrayList<Usuario> usuarios = new ArrayList();

        do {
            try {
                nombreNoValido = false;
                System.out.println("¿Qué nombre de usuario desea? - Pulse 1 si desea cancelar");
                do {
                    if (contador > 1) {
                        System.out.println("Introduzca un nombre adecuado, por favor");
                    }
                    nombreUsuario = lector.nextLine();
                    if (nombreUsuario.equals("1")) {
                        return;
                    }
                    contador++;
                } while (nombreUsuario.equals(""));
                usuarios = sr.getUsuarios();
            } catch (RemoteException ex) {
                System.out.println(ex.getMessage());
                System.exit(1);

            }
            if (usuarios.size() > 0) {
                for (int i = 0; i < usuarios.size(); i++) {
                    if (usuarios.get(i).getNombreUsuario().equals(nombreUsuario)) {
                        nombreNoValido = true;
                        System.out.println("Ya existe un usuario con ese nombre");
                    }
                }
            } else {
            }

        } while (nombreNoValido == true);
        contador = 1;
        System.out.println("Indique su correo electronico o pulse 1 para cancelar");
        do {
            if (contador > 1) {
                System.out.println("Introduzca un correo adecuado, por favor");
            }
            correoElectronico = lector.nextLine();
            if (correoElectronico.equals("1")) {
                return;
            }
            contador++;
        } while (correoElectronico.equals("") || !correoElectronico.contains("@"));
        System.out.println("Indique, para finalizar, su contraseña");
        String password = lector.nextLine();
        Usuario usuario1 = new Usuario(nombreUsuario, password, correoElectronico);
        try {
            boolean resultado = sr.añadirUsuario(usuario1);
            System.out.println("El usuario se ha añadido: " + resultado);
        } catch (RemoteException ex) {
            System.out.println("El usuario no ha podido añadirse");
        }
    }

    public static void eliminarUsuario() {
        if (sesionIniciada == false) {
            System.out.println("Es necesario tener la sesión iniciada del usuario que se quiere eliminar. Inicie sesión primero, por favor");
            return;
        } else {
            System.out.println("Introduzca su contraseña de nuevo para confirmar o pulse 1 para cancelar");
            String password = lector.nextLine();
            if (password.equals("1")) {
                return;
            }
            if (!ClienteEventos.password.equals(password)) {
                do {
                    System.out.println("Contraseña incorrecta.Introduzcala de nuevo o pulse 1 para salir");
                    password = lector.nextLine();
                    if (password.equals("1")) {
                        return;
                    }
                    if (password.equals(ClienteEventos.password)) {
                        boolean eliminado = false;
                        try {
                            eliminado = sr.eliminarUsuario(ClienteEventos.nombreUsuario, ClienteEventos.password);
                            System.out.println("El usuario se ha eliminado correctamente: " + eliminado);
                        } catch (RemoteException ex) {
                            System.out.println("Error al intentar eliminar el usuario mediante el servidor");
                        }

                    }
                } while (password.equals("1") && password.equals(ClienteEventos.password));
            } else {
                boolean eliminado = false;
                try {
                    eliminado = sr.eliminarUsuario(ClienteEventos.nombreUsuario, ClienteEventos.password);
                    System.out.println("El usuario se ha eliminado correctamente: " + eliminado);
                } catch (RemoteException ex) {
                    System.out.println("Error al intentar eliminar el usuario mediante el servidor");
                }
                if (eliminado) {
                    sesionIniciada = false;
                    ClienteEventos.nombreUsuario = "";
                    ClienteEventos.password = "";
                }

            }
        }
    }

    public static void opcionPresentarEvento() {
        ArrayList<Evento> eventos = null;
        try {
            eventos = sr.getEventos();
        } catch (RemoteException ex) {
            System.out.println("\nNo se ha podido presentar los eventos existentes");
            return;
        }
        if (eventos.isEmpty()) {
            System.out.println("\nNo hay ningún evento añadido en este momento");
        }
        for (int i = 0; i < eventos.size(); i++) {
            presentarEvento(eventos.get(i));
        }
    }

    public static void opcionOrdenarEventos() {
        String opcion, mensaje;
        int valor, contador = 0;
        boolean duracion, fecha;
        ArrayList<Evento> eventos = null;
        do {
            if (contador == 0) {
                mensaje = "\n1)Ordenar por duracion \n2)Ordenar por proximidad temporal";
            } else {
                mensaje = "\nIntroduzca un valor válido, por favor: \n1) Ordenar por duracion \n2)Ordenar por proximidad temporal";
            }
            System.out.println(mensaje);
            opcion = lector.nextLine();
            valor = Integer.parseInt(opcion);
            contador++;
        } while (valor != 1 && valor != 2);
        switch (valor) {
            case 1:
                duracion = true;
                fecha = false;
                break;
            case 2:
                duracion = false;
                fecha = true;
                break;
            default:
                duracion = false;
                fecha = false;
                break;
        }
        try {
            eventos = sr.ordenarEventos(duracion, fecha);
        } catch (RemoteException ex) {
            System.out.println("\nNo se ha podido obtener los eventos ordenados por un fallo en el servidor");
            return;
        }
        if (eventos.isEmpty()) {
            System.out.println("No hay eventos en el servidor en este momento");
        } else {
            for (int i = 0; i < eventos.size(); i++) {
                presentarEvento(eventos.get(i));
            }
        }
    }

    public static void opcionFiltrarEventosPorCategoria() {
        ArrayList<Evento> eventosFiltrados = new ArrayList();

        System.out.println("\nIntroduzca la categoría buscada");
        String categoria = lector.nextLine();

        try {
            eventosFiltrados = sr.filtrarEventoPorCategoria(categoria);
        } catch (RemoteException ex) {
            System.out.println("\nError al conectar con el servidor para filtrar los datos");
        }

        if (eventosFiltrados.isEmpty()) {
            System.out.println("\nActualmente no hay ningún evento que mostrar");
        } else {
            for (int i = 0; i < eventosFiltrados.size(); i++) {
                presentarEvento(eventosFiltrados.get(i));
            }
        }
    }

    public static void opcionAñadirEvento() {
        if (sesionIniciada == false) {
            System.out.println("\nDebes iniciar sesion para poder añadir un evento");
            return;
        }
        System.out.println("Introduzca cáda uno de los parámetros soliciados a continuación\n\n");
        String valores[] = {"1. NOMBRE", "2. DURACION (minutos)", "3. CATEGORÍA"};
        String parametros[] = new String[3];
        long duracion;
        String entrada;
        int valorFecha = 0, ano = 0, mes = 0, dia = 0,hora=0,minutos=0;
        Date fechaActual = new Date();
        boolean valorCorrecto = false, añadido = false;

        for (int i = 0; i < 3; i++) {
            System.out.println(valores[i]);
            parametros[i] = lector.nextLine();
        }
        try {
            duracion = Math.abs(Long.parseLong(parametros[1]));
        } catch (NumberFormatException e) {
            duracion = 0;
        }

        System.out.println("4. AÑO DEL ACONTECIMIENTO");

        while (!valorCorrecto) {
            try {
                entrada = lector.nextLine();
                valorFecha = Integer.parseInt(entrada) - 1900;
                if (valorFecha < fechaActual.getYear()) {
                    System.out.println("\nIntroduzca de nuevo el valor, por favor");
                    valorCorrecto = false;
                } else {
                    valorCorrecto = true;
                    ano = valorFecha;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nIntroduzca otra vez el valor, por favor");
                valorCorrecto = false;
            }
        }
        valorCorrecto = false;
        System.out.println("5. MES DEL ACONTECIMIENTO");
        while (!valorCorrecto) {
            try {
                entrada = lector.nextLine();
                valorFecha = Integer.parseInt(entrada) - 1;
                if ((ano == fechaActual.getYear() && valorFecha < fechaActual.getMonth()) || valorFecha > 11) { //hay que tener en cuenta si el mes es mayor al actual solo si estamos en el año de la fecha actual
                    System.out.println("Introduzca de nuevo el valor, por favor");
                    valorCorrecto = false;
                } else {
                    valorCorrecto = true;
                    mes = valorFecha;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduzca otra vez el valor, por favor");
                valorCorrecto = false;
            }
        }
        valorCorrecto = false;
        System.out.println("6. DÍA DEL ACONTECIMIENTO");
        while (!valorCorrecto) {
            try {
                entrada = lector.nextLine();
                valorFecha = Integer.parseInt(entrada);
                if ((ano == fechaActual.getYear() && mes == fechaActual.getMonth() && valorFecha < fechaActual.getDate()) || valorFecha > 31) {
                    System.out.println("Introduzca de nuevo el valor, por favor");
                    valorCorrecto = false;
                } else {
                    switch (mes) {
                        case 0:
                        case 2:
                        case 4:
                        case 6:
                        case 7:
                        case 9:
                        case 11:
                            if (valorFecha <= 31) {
                                valorCorrecto = true;
                            }
                            dia = valorFecha;
                            break;
                        case 1:
                            if (valorFecha <= 28) {
                                valorCorrecto = true; //no se contemplan años bisiestos
                            }
                            dia = valorFecha;
                            break;
                        default:
                            if (valorFecha <= 30) {
                                valorCorrecto = true;
                            }
                            dia = valorFecha;
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduzca otra vez el valor, por favor");
                valorCorrecto = false;
            }
        }
        valorCorrecto = false;
        System.out.println("6. HORA DEL ACONTECIMIENTO (solo valor de la hora - los minutos se añaden despues)\n"
                + "Tenga en cuenta que si el evento sucede hoy y la hora (o minutos) seleccionados son pasadas, el evento se borrará inmediatamente)");
        while (!valorCorrecto) {
            try {
                entrada = lector.nextLine();
                valorFecha = Integer.parseInt(entrada);
                if (valorFecha<0|| valorFecha > 23) { 
                    System.out.println("Introduzca de nuevo el valor, por favor");
                    valorCorrecto = false;
                } else {
                    valorCorrecto = true;
                    hora = valorFecha;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduzca otra vez el valor, por favor");
                valorCorrecto = false;
            }
        }
          valorCorrecto = false;
        System.out.println("7. MINUTOS (de la hora) DEL ACONTECIMIENTO");
        while (!valorCorrecto) {
            try {
                entrada = lector.nextLine();
                valorFecha = Integer.parseInt(entrada);
                if (valorFecha<0|| valorFecha > 59) { 
                    System.out.println("Introduzca de nuevo el valor, por favor");
                    valorCorrecto = false;
                } else {
                    valorCorrecto = true;
                    minutos = valorFecha;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduzca otra vez el valor, por favor");
                valorCorrecto = false;
            }
        }
        Date fechaEvento = new Date(ano, mes, dia,hora,minutos);
        try {
            añadido = sr.añadirEvento(parametros[0], parametros[2], duracion, ClienteEventos.nombreUsuario, ClienteEventos.password, fechaEvento);
        } catch (RemoteException ex) {
            System.out.println("Error al conectar con el servidor para añadir el usuario");
        }
        if (añadido) {
            System.out.println("Evento añadido correctamente: " + true);
        } else {
            System.out.println("No se ha podido localizar al usuario en el servidor. Inténtelo de nuevo, por favor");
        }

    }

    public static void opcionEliminarEvento() {
        int eliminado = 0;
        if (!sesionIniciada) {
            System.out.println("Inicie sesion primero, por favor. Es necesario para poder eliminar eventos");
        } else {
            System.out.println("Introduzca el nombre del evento que desea eliminar: \n¡El evento debe haber sido añadido por el usuario que tiene la sesión iniciada! \n¡Tener cuidado al escribir el nombre!\n\n");
            String nombre = lector.nextLine();
            try {
                eliminado = sr.eliminarEvento(ClienteEventos.nombreUsuario, ClienteEventos.password, nombre);
            } catch (RemoteException ex) {
                System.out.println("Error en el servidor al eliminar el evento solicitado");
            }
            if (eliminado == 0) {
                System.out.println("No existe ningún evento con el nombre introducido asociado a el usuario con la sesión iniciada");
            } else {
                System.out.println("Se han eliminado " + eliminado + " evento(s) con nombre " + nombre + " y registrados por el usuario " + ClienteEventos.nombreUsuario);
            }
        }

    }

    public static void AccionesMenuEventos() throws Exception {
        int opcion;
        do {
            opcion = mostrarMenuInicial(2);
            switch (opcion) {
                case 1:
                    opcionPresentarEvento();
                    break;
                case 2:
                    opcionOrdenarEventos();
                    break;
                case 3:
                    opcionFiltrarEventosPorCategoria();
                    break;
                case 4:
                    opcionAñadirEvento();
                    break;
                case 5:
                    opcionEliminarEvento();
                    break;
                case 6:
                    sesionIniciada = false;
                    nombreUsuario = "";
                    password = "";
                    break;
                case 7:
                    System.exit(0); //se finaliza el programa de forma exitosa
                default:
                    System.out.println("Error desconocido. Por favor, reinicie la ejecución del programa");
                    break;
            }
        } while (opcion!=6&&opcion!=7);
    }

    public static void presentarEvento(Evento evento) {
        String hora =  String.valueOf(evento.getFechaPublicacion().getHours());
        String minutos = String.valueOf(evento.getFechaPublicacion().getMinutes());
        if(evento.getFechaPublicacion().getHours()<10) {
           hora ="0"+ hora;
        }
        if(evento.getFechaPublicacion().getMinutes()<10){
            minutos ="0"+minutos;
        }
        System.out.println("EVENTO: " + evento.getNombre() + " //Duración: " + evento.getDuracion() + " minutos //Categoría: " + evento.getCategoria() +
                " //Añadido por: " + evento.getUsuarioCreador().getNombreUsuario() + " //Fecha: " +
                evento.getFechaPublicacion().getDate()+"/"+(evento.getFechaPublicacion().getMonth()+1)+"/"+(evento.getFechaPublicacion().getYear()+1900)
                + "  "+hora+":"+minutos);
    }

    public static void main(String[] args) throws Exception {
        int valorMenuInicial = 5;
        servidorRemoto = "rmi://localhost:1099/Eventos";
        try {
            sr = (ServicioRemoto) Naming.lookup(servidorRemoto);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Se ha producido un error: " + ex.getMessage());
        }

        do {
            valorMenuInicial = mostrarMenuInicial(1);
            switch (valorMenuInicial) {
                case 1:
                    iniciarSesion();
                    if (sesionIniciada == true) {
                        AccionesMenuEventos();
                    }
                    break;
                case 2:
                     registrarUsuario();
                    break;
                case 3:
                    sesionIniciada = false;
                    nombreUsuario = "";
                    password = "";
                    System.out.println("Cesión cerrada");
                    AccionesMenuEventos();
                    break;
                case 4:
                    if(!sesionIniciada) iniciarSesion();
                    eliminarUsuario();
                    break;
                case 5:
                    System.exit(0); //se finaliza el programa de forma exitosa
                default:
                    System.out.println("Error desconocido. Por favor, reinicie la ejecución del programa");
                    break;
            }
        } while (valorMenuInicial != 5);
    }

}
