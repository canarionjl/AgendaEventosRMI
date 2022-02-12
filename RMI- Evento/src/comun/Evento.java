package comun;

import java.io.Serializable;
import java.util.Date;

public class Evento implements Serializable {

    private String nombre;
    private String categoria;
    private long duracion;
    private Usuario usuarioCreador;
    private Date fechaPublicacion;

    public Evento(String nombre, String categoria, long duracion, Usuario usuarioCreador, Date fechaPublicacion) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.duracion = duracion;
        this.usuarioCreador = usuarioCreador;
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public long getDuracion() {
        return duracion;
    }

    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

}
