package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.memoria;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Alquiler;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IAlquileres;

public class Alquileres implements IAlquileres {

	private List<Alquiler> coleccionAlquileres;

	public Alquileres() {
		coleccionAlquileres = new ArrayList<>();
	}

	@Override
	public List<Alquiler> get() {

		return coleccionAlquileres;

	}

	@Override
	public List<Alquiler> get(Cliente cliente) {
		List<Alquiler> coleccionCliente = new ArrayList<>();
		for (Alquiler alquiler : coleccionAlquileres) {
			if (alquiler.getCliente().equals(cliente)) {
				coleccionCliente.add(alquiler);
			}

		}
		return coleccionCliente;
	}

	@Override
	public List<Alquiler> get(Vehiculo turismo) {
		List<Alquiler> coleccionTurismo = new ArrayList<>();
		for (Alquiler alquiler : coleccionAlquileres) {
			if (alquiler.getVehiculo().equals(turismo)) {
				coleccionTurismo.add(alquiler);
			}

		}
		return coleccionTurismo;
	}

	@Override
	public int getCantidad() {
		return coleccionAlquileres.size();
	}

	@Override
	public void insertar(Alquiler alquiler) throws OperationNotSupportedException {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede insertar un alquiler nulo.");
		}
		if (!coleccionAlquileres.contains(alquiler)) {
			comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(), alquiler.getFechaAlquiler());
			coleccionAlquileres.add(alquiler);
		}

	}

	private void comprobarAlquiler(Cliente cliente, Vehiculo turismo, LocalDate fechaAlquiler)
			throws OperationNotSupportedException {
		for (Alquiler alquiler : coleccionAlquileres) {
			if (alquiler.getFechaDevolucion() == null) {
				// alquileres sin devolver
				if (alquiler.getCliente().equals(cliente)) {
					throw new OperationNotSupportedException("ERROR: El cliente tiene otro alquiler sin devolver.");

				}
				if (alquiler.getVehiculo().equals(turismo)) {
					throw new OperationNotSupportedException("ERROR: El turismo está actualmente alquilado.");
				}
			} else {
				// alquileres devueltos

				if (alquiler.getCliente().equals(cliente) && !alquiler.getFechaDevolucion().isBefore(fechaAlquiler)) {
					throw new OperationNotSupportedException("ERROR: El cliente tiene un alquiler posterior.");

				}
				if (alquiler.getVehiculo().equals(turismo) && !alquiler.getFechaDevolucion().isBefore(fechaAlquiler)) {
					throw new OperationNotSupportedException("ERROR: El turismo tiene un alquiler posterior.");
				}
			}
		}
	}

	@Override
	public void devolver(Cliente cliente, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede devolver un alquiler de un cliente nulo.");
		}
		Alquiler alqAbierto = getAlquilerAbierto(cliente);
		if (alqAbierto != null ) {
			alqAbierto.devolver(fechaDevolucion);
		} else {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler abierto para ese cliente.");
		}
	}

	private Alquiler getAlquilerAbierto(Cliente cliente) {
		Alquiler alqAbierto = null;
		Iterator<Alquiler> iterator = coleccionAlquileres.iterator();
		while (iterator.hasNext() && alqAbierto == null) {
			Alquiler alquiler = iterator.next();
			if (alquiler.getCliente().equals(cliente) && alquiler.getFechaDevolucion() == null) {
				alqAbierto = alquiler;
			}

		}
		return alqAbierto;

	}

	@Override
	public void devolver(Vehiculo vehiculo, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede devolver un alquiler de un vehículo nulo.");
		}
		Alquiler alqAbierto = getAlquilerAbierto(vehiculo);
		if (alqAbierto != null) {
			alqAbierto.devolver(fechaDevolucion);
		} else {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler abierto para ese vehículo.");
		}
	}
	

	private Alquiler getAlquilerAbierto(Vehiculo vehiculo) {
		Alquiler alqAbierto = null;
		Iterator<Alquiler> iterator = coleccionAlquileres.iterator();
		while (iterator.hasNext() && alqAbierto == null) {
			Alquiler alquiler = iterator.next();
			if (alquiler.getVehiculo().equals(vehiculo) && alquiler.getFechaDevolucion() == null) {
				alqAbierto = alquiler;
			}

		}
		return alqAbierto;

	}	

	@Override
	public void borrar(Alquiler alquiler) throws OperationNotSupportedException {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede borrar un alquiler nulo.");
		}
		if (coleccionAlquileres.contains(alquiler)) {
			coleccionAlquileres.remove(alquiler);
		} else {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler igual.");
		}

	}

	@Override
	public Alquiler buscar(Alquiler alquiler) {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede buscar un alquiler nulo.");
		}

		if (coleccionAlquileres.contains(alquiler)) {
			return alquiler;
		} else {
			return null;

		}

	}

}
