package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.DocumentBuilder;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Alquiler;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IAlquileres;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Alquileres implements IAlquileres {
	private static File FICHEROS_ALQUILERES = new File("datos" + File.separator + "alquileres.xml");;
	private static DateTimeFormatter FORMATO_FECHA;
	private static String RAIZ = "raiz";
	private static String ALQUILER = "alquiler";
	private static String CLIENTE = "cliente";
	private static String VEHICULO = "vehiculo";
	private static String FECHA_ALQUILER = "fecha alquiler";
	private static String FECHA_DEVOLUCION = " fecha devolucion";

	private static Alquileres instancia;

	static Alquileres getInstancia() {
		if (instancia == null) {
			instancia = new Alquileres();

		}
		return instancia;
	}

	private void leerDom(Document documentoXml) {
		NodeList alquileres = documentoXml.getElementsByTagName(ALQUILER);
		for (int i = 0; i < alquileres.getLength(); i++) {
			Node nAlquiler = alquileres.item(i);
			if (nAlquiler.getNodeType() == Node.ELEMENT_NODE) {

				try {
					Alquiler alquiler = getAlquiler((Element) nAlquiler);
					insertar(alquiler);
				} catch (OperationNotSupportedException | NullPointerException e) {
					System.out.println(e.getMessage());
				}
			}
		}

	}

	private Alquiler getAlquiler(Element elemento) throws OperationNotSupportedException {
		String cliente = elemento.getAttribute(CLIENTE);
		String fechaAlquiler = elemento.getAttribute(FECHA_ALQUILER);
		String fechaDevolucion = elemento.getAttribute(FECHA_DEVOLUCION);
		String vehiculo = elemento.getAttribute(VEHICULO);
		Cliente buscarCliente = Clientes.getInstancia().buscar(Cliente.getClienteConDni(cliente));
		Vehiculo buscarVehiculo = Vehiculos.getInstancia().buscar(Vehiculo.getVehiculoConMatricula(vehiculo));

		if (buscarCliente == null) {
			throw new NullPointerException("ERROR: No se puede buscar un cliente nulo");
		}
		if (buscarVehiculo == null) {
			throw new NullPointerException("ERROR: No se puede buscar un vehiculo nulo ");

		}
		Alquiler alquiler = new Alquiler(buscarCliente, buscarVehiculo, LocalDate.parse(fechaAlquiler, FORMATO_FECHA));
		if (fechaDevolucion.isBlank()) {
			alquiler.devolver(LocalDate.parse(fechaDevolucion, FORMATO_FECHA));

		}
		return new Alquiler(alquiler);
	}

	private Document crearDom() {
		DocumentBuilder constructor = UtilidadesXml.crearConstructorDocumentoXml();
		Document documentoXml = null;
		if (constructor != null) {
			documentoXml = constructor.newDocument();
			documentoXml.appendChild(documentoXml.createElement(RAIZ));
			for (Alquiler alquiler : coleccionAlquileres) {
				Element eCliente = getElemento(documentoXml, alquiler);
				documentoXml.getDocumentElement().appendChild(eCliente);
			}
		}
		return documentoXml;
	}

	private Element getElemento(Document documentoXml, Alquiler alquiler) {
		Element elementoAlquiler = documentoXml.createElement(CLIENTE);
		elementoAlquiler.setAttribute(CLIENTE, String.format("%s",alquiler.getCliente()));
		elementoAlquiler.setAttribute(FECHA_ALQUILER,  String.format("%s",alquiler.getFechaAlquiler()));
		elementoAlquiler.setAttribute(VEHICULO,  String.format("%s",alquiler.getVehiculo()));
			
		LocalDate fechaDevolucion = alquiler.getFechaDevolucion();
		if (fechaDevolucion != null) {	
			elementoAlquiler.setAttribute(FECHA_DEVOLUCION,  String.format("%s",fechaDevolucion));

		}
		return elementoAlquiler;
	}

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
				if (alquiler.getCliente().equals(cliente)) {
					throw new OperationNotSupportedException("ERROR: El cliente tiene otro alquiler sin devolver.");

				}
				if (alquiler.getVehiculo().equals(turismo)) {
					throw new OperationNotSupportedException("ERROR: El turismo está actualmente alquilado.");
				}
			} else {

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
		if (alqAbierto != null) {
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

	@Override
	public void comenzar() {
		Document documento = UtilidadesXml.leerXmlDeFichero(FICHEROS_ALQUILERES);
		if (documento != null) {
			System.out.println("Fichero XML leído correctamente.");
			leerDom(documento);
		} else {
			System.out.println("No se ha podido leer el fichero XML.");
		}
	}

	@Override
	public void terminar() {
		UtilidadesXml.escribirXmlAFichero(crearDom(), FICHEROS_ALQUILERES);
	}

}
