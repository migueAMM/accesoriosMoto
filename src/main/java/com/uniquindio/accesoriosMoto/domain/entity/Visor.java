package com.uniquindio.accesoriosMoto.domain.entity;

import com.uniquindio.accesoriosMoto.domain.exception.ReglaDominioException;
import com.uniquindio.accesoriosMoto.domain.valueObject.*;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Visor extends Producto implements ProductoCompatible{

    private String tipoVisor;

    private final Set<Compatibilidad> compatibilidades = new LinkedHashSet<>();

    @Override
    public void indicarCompatibilidad(Compatibilidad compatibilidad) {
        validarCompatibilidad(compatibilidad);
        if(!compatibilidades.add(compatibilidad)){
            throw new ReglaDominioException("El producto ya tiene esa compatibilidad");
        }
    }

    @Override
    public void retirarCompatibilidad(Compatibilidad compatibilidad) {
        validarCompatibilidad(compatibilidad);
        if(!compatibilidades.remove(compatibilidad)){
            throw new ReglaDominioException("El producto no tiene esa compatibilidad");
        }
    }

    public void validarCompatibilidad(Compatibilidad compatibilidad){
        if(compatibilidad == null || compatibilidad.tipo() != TipoCompatibilidad.CASCO){
            throw new ReglaDominioException("El producto no admite ese tipo de compatibilidad");
        }
    }

    @Override
    public Set<Compatibilidad> getCompatibilidades() {
        return Set.copyOf(compatibilidades);
    }

    public List<String> getModelosCascoCompatibles(){
        return getCompatibilidades().stream().map(Compatibilidad::modelo).toList();
    }


    private Visor(String id, Marca marca, Precio precio, String color, EstadoPublicacion estadoPublicacion, String tipoVisor, List<String> modelosCascoCompatibles) {
        super(id, marca, precio, color, estadoPublicacion);
        this.tipoVisor = tipoVisor;
        if (modelosCascoCompatibles == null) {
            throw new ReglaDominioException("La lista de modelos compatibles no puede ser nula");
        }
        for (String modelo : modelosCascoCompatibles) {
            indicarCompatibilidad(new Compatibilidad(TipoCompatibilidad.CASCO, modelo));
        }
    }

    public static Visor crear(String id, Marca marca, Precio precio, String color, String tipoVisor, List<String> modelosCascoCompatibles) {
        return new Visor(id, marca, precio, color, EstadoPublicacion.NO_PUBLICADO, tipoVisor, modelosCascoCompatibles);
    }
}
