package org.iesalandalus.programacion.alquilervehiculos.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Alquiler;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;

public class ModeloCascada extends Modelo {

	public ModeloCascada(FactoriaFuenteDatos factoriaFuenteDatos) {
	super(factoriaFuenteDatos);

	}

	@Override
	public void insertar(Cliente cliente) throws OperationNotSupportedException {
		getClientes().insertar(cliente);
	}

	@Override
	public void insertar(Vehiculo vehiculo) throws OperationNotSupportedException {
		getVehiculos().insertar(Vehiculo.copiar(vehiculo));
	}

	@Override
	public void insertar(Alquiler alquiler) throws OperationNotSupportedException {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede realizar un alquiler nulo.");
		}
		Cliente cliente = getClientes().buscar(alquiler.getCliente());

		if (cliente == null)

		{
			throw new OperationNotSupportedException("ERROR: No existe el cliente del alquiler.");

		}
		Vehiculo vehiculo = getVehiculos().buscar(alquiler.getVehiculo());
		if (vehiculo == null) {
			throw new OperationNotSupportedException("ERROR: No existe el turismo del alquiler.");

		}

		getAlquileres().insertar(new Alquiler(cliente, vehiculo, alquiler.getFechaAlquiler()));
	}

	@Override
	public Cliente buscar(Cliente cliente) {
		return new Cliente(getClientes().buscar(cliente));
	}

	@Override
	public Vehiculo buscar(Vehiculo vehiculo) {
		return  Vehiculo.copiar(getVehiculos().buscar(vehiculo));
	}

	@Override
	public Alquiler buscar(Alquiler alquiler) {
		return new Alquiler(getAlquileres().buscar(alquiler));
	}

	@Override
	public void modificar(Cliente cliente, String nombre, String telefono) throws OperationNotSupportedException {
		getClientes().modificar(cliente, nombre, telefono);
	}

	@Override
	public void devolver(Vehiculo vehiculo, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		getAlquileres().devolver(vehiculo, fechaDevolucion);

	}

	@Override
	public void devolver(Cliente cliente, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		getAlquileres().devolver(cliente, fechaDevolucion);
	}

	@Override
	public void borrar(Cliente cliente) throws OperationNotSupportedException {
		for (Alquiler alquiler : getAlquileres().get(cliente)) {
			getAlquileres().borrar(alquiler);
		}
		getClientes().borrar(cliente);
	}

	@Override
	public void borrar(Vehiculo vehiculo) throws OperationNotSupportedException {
		for (Alquiler alquiler : getAlquileres().get(vehiculo)) {
			getAlquileres().borrar(alquiler);
		}
		getVehiculos().borrar(vehiculo);
	}

	@Override
	public void borrar(Alquiler alquiler) throws OperationNotSupportedException {
		getAlquileres().borrar(alquiler);
	}

	@Override
	public List<Cliente> getListaClientes() {
		List<Cliente> listaCliente = new ArrayList<>();
		for (Cliente cadaCliente : getClientes().get()) {
			Cliente cliente = new Cliente(cadaCliente);
			listaCliente.add(cliente);
		}
		return listaCliente;
	}

	@Override
	public List<Vehiculo> getListaVehiculos() {
		List<Vehiculo> listaVehiculo = new ArrayList<>();
		for (Vehiculo cadaVehiculo : getVehiculos().get()) {
			Vehiculo vehiculo = Vehiculo.copiar(cadaVehiculo);
			listaVehiculo.add(vehiculo);
		}
		return listaVehiculo;
	}

	@Override
	public List<Alquiler> getListaAlquileres() {
		List<Alquiler> listaAlquiler = new ArrayList<>();
		for (Alquiler cadaAlquiler : getAlquileres().get()) {
			Alquiler alquiler = new Alquiler(cadaAlquiler);
			listaAlquiler.add(alquiler);
		}
		return listaAlquiler;
	}

	@Override
	public List<Alquiler> getListaAlquileres(Cliente cliente) {
		List<Alquiler> listaAlquiler = new ArrayList<>();
		for (Alquiler cadaAlquiler : getAlquileres().get(cliente)) {
			Alquiler alquiler = new Alquiler(cadaAlquiler);
			listaAlquiler.add(alquiler);

		}
		return listaAlquiler;
	}

	@Override
	public List<Alquiler> getListaAlquileres(Vehiculo vehiculo) {
		List<Alquiler> listaAlquiler = new ArrayList<>();
		for (Alquiler cadaAlquiler : getAlquileres().get(vehiculo)) {
			Alquiler alquiler = new Alquiler(cadaAlquiler);
			listaAlquiler.add(alquiler);

		}
		return listaAlquiler;
	}

}