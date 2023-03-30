package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.memoria;

import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IVehiculos;

public class Vehiculos implements IVehiculos {
	private List<Vehiculo> coleccionVehiculo;

	public Vehiculos() {
		coleccionVehiculo = new ArrayList<>();
	}

	@Override
	public List<Vehiculo> get() {
		return coleccionVehiculo;
	}

	@Override
	public int getCantidad() {
		return coleccionVehiculo.size();
	}

	@Override
	public void insertar(Vehiculo vehiculo) throws OperationNotSupportedException {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede insertar un vehículo nulo.");
		}
		if (!coleccionVehiculo.contains(vehiculo)) {
			coleccionVehiculo.add(vehiculo);
		} else {
			throw new OperationNotSupportedException("ERROR: Ya existe un vehículo con esa matrícula.");
		}
	}

	@Override
	public Vehiculo buscar(Vehiculo vehiculo) {

		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede buscar un vehículo nulo.");
		}
		if (coleccionVehiculo.contains(vehiculo)) {
			return vehiculo;
		} else {
			return null;
		}
	}

	@Override
	public void borrar(Vehiculo vehiculo) throws OperationNotSupportedException {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede borrar un vehículo nulo.");
		}
		if (coleccionVehiculo.contains(vehiculo)) {
			coleccionVehiculo.remove(vehiculo);
		} else {
			throw new OperationNotSupportedException("ERROR: No existe ningún vehículo con esa matrícula.");
		}
	}

}
