package comun;

import java.util.ArrayList;
import java.util.Date;
import java.rmi.*;

public interface ServicioRemoto extends Remote {

    /**
     * Método que permite añadir un nuevo Evento a la lista de Eventos manejada
     * por el Servidor
     *
     * @param nombre Nombre del Evento
     * @param categoria Categoria del Evento (deportes, entretenimiento,
     * tecnologia, etc.)
     * @param duracion duración en segundos del Evento
     * @param nombreUsuario nombre de usuario del usuario que añadió el evento
     * @param password contraseña del usuario que añadió el evento
     * @param fechaEvento fecha en la que sucedera el Evento. No puede ser
     * anterior a la fecha actual
     * @return true si se lleva a cabo, false si no se lleva a cabo
     * @throws java.rmi.RemoteException
     */
    public abstract boolean añadirEvento(String nombre, String categoria, long duracion, String nombreUsuario, String password, Date fechaEvento) throws RemoteException;

    /**
     * Método que elimina un Evento de la lista.Solo puede ser eliminado por el
     * Usuario que lo añadio
     *
     * @param nombreUsuario nombre de usuario del Usuario. Debe existir en la
     * lista de Usuarios registrados en el servidor
     * @param password contraseña del usuario que quiere eliminar el evento
     * @param nombreEvento Nombre del elemento que se quiere eliminar
     * @return eliminados, número de eventos eliminados con ese nombre (que
     * hayan sido creados por el usuario indicado)
     * @throws java.rmi.RemoteException
     */
    public abstract int eliminarEvento(String nombreUsuario, String password, String nombreEvento) throws RemoteException;

    /**
     * Metodo que filtra del ArrayList de eventos principal los eventos
     * pertenecientes a una categoría pasada por parámetro
     *
     * @param nombreCategoria nombre de la categoría que se desea filtrar
     * @return ArrayList de objetos de tipo Evento pertenecientes a la categoría
     * seleccionada
     * @throws java.rmi.RemoteException
     */
    public abstract ArrayList filtrarEventoPorCategoria(String nombreCategoria) throws RemoteException;

    /**
     * Ordena los Eventos del servidor, de forma local, por duracion o
     * fecha.Nunca ambos parámetros podrán ser "true" de forma simultánea
     *
     * @param duracion Se indica true si se quiere ordenar de mayor a menor
     * duracion
     * @param fecha Se indica true si se quiere ordenar de más recientes a más
     * lejanos en el tiempo (futuro)
     * @return ArrayList de tipo Evento que contiene los eventos ordenados como
     * se indique
     * @throws java.rmi.RemoteException
     */
    public abstract ArrayList ordenarEventos(boolean duracion, boolean fecha) throws RemoteException;

    /**
     * Devuelve la lista actual de eventos
     *
     * @return ArrayList de Eventos almacenado en el servidor
     * @throws java.rmi.RemoteException
     */
    public abstract ArrayList<Evento> getEventos() throws RemoteException;

    /**
     * Añade un usuario al ArrayList de usuarios registrados del servidor
     *
     * @param usuario objeto de la clase Usuario que va a ser añadido
     * @return true si se lleva a cabo, false si no se lleva a cabo
     * @throws java.rmi.RemoteException
     */
    public abstract boolean añadirUsuario(Usuario usuario) throws RemoteException;

    /**
     * Elimina un usuario de la lista de usuarios registrados del servidor
     *
     * @param nombreUsuario del usuario que se quiere eliminar
     * @param password del usuario que se quiere eliminar
     * @return true si se lleva a cabo, false si no se lleva a cabo
     * @throws java.rmi.RemoteException
     */
    public abstract boolean eliminarUsuario(String nombreUsuario, String password) throws RemoteException;

    /**
     * Comprueba si existe un usuario en la estructura de almacenamiento pasando como 
     * parametro los valores de los atributos nombreUsuario y password.
     * @param nombreUsuario nombre del usuario que se quiere comprobar que
     * existe
     * @param password contraseña del mismo
     * @return true si el usuario existe o false si no existe
     * @throws java.rmi.RemoteException
     */
    public abstract boolean comprobarUsuario(String nombreUsuario, String password) throws RemoteException;

    /**
     * Devuelve el arrayList de Usuarios en su estado actual
     * @return ArrrayList con los usuarios actuales almacenados en el servidor
     * @throws RemoteException
     */
    public abstract ArrayList<Usuario> getUsuarios() throws RemoteException;
}
