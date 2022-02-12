package servidor;

import comun.Evento;
import comun.ServicioRemoto;
import comun.Usuario;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;

public class AlmacenDatos extends UnicastRemoteObject implements ServicioRemoto {

    //estructuras de almacenamiento de datos
    private ArrayList<Evento> almacenEventos = new ArrayList<>();
    private ArrayList<Usuario> almacenUsuarios = new ArrayList<>();

    //métodos remotos
    @Override
    public synchronized boolean añadirEvento(String nombre, String categoria, long duracion, String nombreUsuario, String password, Date fechaEvento) {
        if (comprobarUsuario(nombreUsuario, password)) {
            almacenEventos.add(new Evento(nombre, categoria, duracion, this.obtenerUsuario(nombreUsuario, password), fechaEvento));
            notifyAll();
            return true;
        } else {
            notifyAll();
            return false;
        }
    }

    @Override
    public synchronized int eliminarEvento(String nombreUsuario, String password, String nombreEvento) {
        int eliminados = 0;
        if (!almacenEventos.isEmpty() && almacenEventos != null) {
            for (int i = 0; i < almacenEventos.size(); i++) {
                if (almacenEventos.get(i).getNombre().equals(nombreEvento) && almacenEventos.get(i).getUsuarioCreador().equals(obtenerUsuario(nombreUsuario, password))) {
                    almacenEventos.remove(i);
                    eliminados++;
                }
            }
        }
        notifyAll();
        return eliminados;
    }

    @Override
    public synchronized ArrayList filtrarEventoPorCategoria(String nombreCategoria) {

        ArrayList<Evento> eventosFiltrados = new ArrayList();
        String categoria = nombreCategoria.toLowerCase();
        if (!almacenEventos.isEmpty() && almacenEventos != null) {
            for (int i = 0; i < almacenEventos.size(); i++) {
                if (almacenEventos.get(i).getCategoria().toLowerCase().equals(categoria)) {
                    eventosFiltrados.add(almacenEventos.get(i));
                } else {
                }
            }
        }
        notifyAll();
        return eventosFiltrados;
    }

    @Override
    public synchronized ArrayList ordenarEventos(boolean duracion, boolean fecha) { // falta por realizar ordenación por fechas
        ArrayList<Evento> almacenEventosOrdenados;
        almacenEventosOrdenados = almacenEventos;
        Evento eventoPosicionMenor;
        if (!almacenEventos.isEmpty() && almacenEventos != null) {
            if (duracion && fecha) {
                return null; 
            }

            if (duracion && !fecha) {
                for (int i = 0; i < almacenEventosOrdenados.size(); i++) {
                    for (int j = 0; j < almacenEventosOrdenados.size() - 1; j++) {
                        eventoPosicionMenor = almacenEventosOrdenados.get(j);
                        if (almacenEventosOrdenados.get(j).getDuracion() < almacenEventosOrdenados.get(j + 1).getDuracion()) {
                            almacenEventosOrdenados.set(j, almacenEventosOrdenados.get(j + 1));
                            almacenEventosOrdenados.set(j + 1, eventoPosicionMenor);
                        }
                    }
                }
            }

            if (!duracion && fecha) {
                for (int i = 0; i < almacenEventosOrdenados.size(); i++) {
                    for (int j = 0; j < almacenEventosOrdenados.size() - 1; j++) {
                        eventoPosicionMenor = almacenEventosOrdenados.get(j);
                        if (almacenEventosOrdenados.get(j).getFechaPublicacion().after(almacenEventosOrdenados.get(j + 1).getFechaPublicacion())) {
                            almacenEventosOrdenados.set(j, almacenEventosOrdenados.get(j + 1));
                            almacenEventosOrdenados.set(j + 1, eventoPosicionMenor);
                        }

                    }
                }
            }
        }
        notifyAll();
        return almacenEventosOrdenados;
    }

    @Override
    public synchronized ArrayList<Evento> getEventos() {
        ArrayList<Evento> eventos = almacenEventos;
        notifyAll();
        return eventos;

    }

    @Override
    public boolean añadirUsuario(Usuario usuario) {
        if (usuario == null) {
            return false;
        } else {
            almacenUsuarios.add(usuario);
            return true;
        }
    }

    @Override
    public boolean eliminarUsuario(String nombreUsuario, String password) {
        if (!almacenUsuarios.isEmpty() && almacenUsuarios != null) {
            for (int i = 0; i < almacenUsuarios.size(); i++) {
                if (almacenUsuarios.get(i).getNombreUsuario().equals(nombreUsuario) && almacenUsuarios.get(i).getPassword().equals(password)) {
                    almacenUsuarios.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean comprobarUsuario(String nombreUsuario, String password) {
        if (almacenUsuarios == null || almacenUsuarios.isEmpty()) {
            return false;
        } else {
            for (int i = 0; i < almacenUsuarios.size(); i++) {
                if (almacenUsuarios.get(i).getNombreUsuario().equals(nombreUsuario)) {
                    if (almacenUsuarios.get(i).getPassword().equals(password)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    @Override
    public ArrayList<Usuario> getUsuarios() throws RemoteException {
        return this.almacenUsuarios;
    }

    //métodos no remotos
    public Usuario obtenerUsuario(String nombreUsuario, String password) {
        Usuario usuario;
        if (almacenUsuarios == null || almacenUsuarios.isEmpty()) {
            return null;
        } else {
            for (int i = 0; i < almacenUsuarios.size(); i++) {
                if (almacenUsuarios.get(i).getNombreUsuario().equals(nombreUsuario)) {
                    if (almacenUsuarios.get(i).getPassword().equals(password)) {
                        return almacenUsuarios.get(i);
                    }
                }
            }
        }
        return null;
    }
       
    public AlmacenDatos() throws RemoteException {
        almacenEventos.add(new Evento("Barcelona-Madrid", "Deportes", 120, new Usuario("juan1", "hola@gmail.com", "hola"), new Date(2022 - 1900, 12 - 1, 4, 13, 45)));
        almacenEventos.add(new Evento("Concierto Melendi", "Musica", 90, new Usuario("andres52", "hola@gmail.com", "hola"), new Date(2020 - 1900, 6 - 1, 27, 14, 40)));
        almacenEventos.add(new Evento("Formula 1", "Deportes", 67, new Usuario("wer2s3", "hola@gmail.com", "hola"), new Date(2021 - 1900, 10 - 1, 18, 15, 50)));
        almacenEventos.add(new Evento("Hackaton", "Tecnologia", 180, new Usuario("xxx32", "hola@gmail.com", "hola"), new Date(2021 - 1900, 11 - 1, 21, 20, 4)));
        almacenEventos.add(new Evento("Día de todos los Santos", "Festivos", 24 * 60, new Usuario("Cilina", "hola@gmail.com", "hola"), new Date(2021 - 1900, 11 - 1, 1, 3, 50)));
        almacenEventos.add(new Evento("Navidad", "Festivos", 24 * 60, new Usuario("Cilina", "hola@gmail.com", "hola"), new Date(2021 - 1900, 12 - 1, 25)));

        // public Evento(String nombre, String categoria, long duracion, Usuario usuarioCreador, Date fechaPublicacion)
    }

    public synchronized void comprobarFecha() {
        while (almacenEventos.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                return;
            }
        }
        Date fechaActual = new Date();
        Date fechaEvento;
        for (int i = 0; i < almacenEventos.size(); i++) {
            fechaEvento = almacenEventos.get(i).getFechaPublicacion();
            if (!(fechaEvento.after(fechaActual))) {
                almacenEventos.remove(almacenEventos.get(i));
            } else {
            }
        }
        notifyAll();
    }

    //getters and setters necesarios
    public ArrayList<Evento> getAlmacenEventos() {
        return almacenEventos;
    }

    public void setAlmacenEventos(ArrayList<Evento> almacenEventos) {
        this.almacenEventos = almacenEventos;
    }

    public ArrayList<Usuario> getAlmacenUsuarios() {
        return almacenUsuarios;
    }

    public void setAlmacenUsuarios(ArrayList<Usuario> almacenUsuarios) {
        this.almacenUsuarios = almacenUsuarios;
    }

}

class ThreadComprobadorFecha extends Thread {

    AlmacenDatos almacen;

    public ThreadComprobadorFecha(AlmacenDatos almacen) {
        this.almacen = almacen;
    }

    @Override
    public void run() {
        while (true) {
            try {
                /* se realiza el "sleep" primero para evitar problemas de acceso al array debido a que en el constructor del almacén se añadirán varios Eventos
                (a modo de simulación de la realidad por no disponer de BB.DD.) y, como el constructor no está sincronizado, podrían existir problemas al tratar y/o añadir datos
                De esta manera, dejamos un tiempo prudencial para que se añadan todos los datos iniciales
                 */
                Thread.sleep(60000); //60000 milisegundos --> 1 minuto --> se actualiza la lista de eventos, eliminando los acontecidos en fechas pasadas, cada minuto
            } catch (InterruptedException ex) {
            }
            almacen.comprobarFecha();
        }
    }

}
