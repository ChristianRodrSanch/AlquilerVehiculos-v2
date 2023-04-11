package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.DocumentBuilder;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Autobus;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Furgoneta;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Turismo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IVehiculos;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Vehiculos implements IVehiculos {
	private static File FICHEROS_VEHICULOS = new File("datos" + File.separator + "vehiculos.xml");
	private static String RAIZ = "raiz";
	private static String VEHICULO = "vehiculo";
	private static String MARCA = "marca";
	private static String MODELO = "modelo";
	private static String MATRICULA = "matricula";
	private static String CILINDRADA = "modelo";
	private static String PLAZAS = "plazas";
	private static String PMA = "pma";
	private static String TIPO = "tipo";

	private static Vehiculos instancia;

	static Vehiculos getInstancia() {
		if (instancia == null) {
			instancia = new Vehiculos();

		}
		return instancia;
	}

	private void leerDom(Document documentoXml) {
		NodeList vehiculos = documentoXml.getElementsByTagName(VEHICULO);
		for (int i = 0; i < vehiculos.getLength(); i++) {
			Node nVehiculo = vehiculos.item(i);
			if (nVehiculo.getNodeType() == Node.ELEMENT_NODE) {

				try {
					Vehiculo vehiculo = getVehiculo((Element) nVehiculo);
					insertar(vehiculo);
				} catch (OperationNotSupportedException | NullPointerException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	private Vehiculo getVehiculo(Element elemento) {
		String marca = elemento.getAttribute(MARCA);
		String modelo = elemento.getAttribute(MODELO);
		String matricula = elemento.getAttribute(MATRICULA);
		Vehiculo vehiculo = (Vehiculo) elemento;
		if (vehiculo instanceof Turismo) {
			int cilindrada = Integer.parseInt(elemento.getAttribute(CILINDRADA));
			vehiculo = new Turismo(marca, modelo, cilindrada, matricula);
		} else if (vehiculo instanceof Furgoneta) {
			int pma = Integer.parseInt(elemento.getAttribute(PMA));
			int plazas = Integer.parseInt(elemento.getAttribute(PLAZAS));

			vehiculo = new Furgoneta(marca, modelo, pma, plazas, matricula);
		} else if (vehiculo instanceof Autobus) {
			int plazas = Integer.parseInt(elemento.getAttribute(PLAZAS));

			vehiculo = new Autobus(marca, modelo, plazas, matricula);
		}
		return vehiculo;

	}

	private Document crearDom() {
		DocumentBuilder constructor = UtilidadesXml.crearConstructorDocumentoXml();
		Document documentoXml = null;
		if (constructor != null) {
			documentoXml = constructor.newDocument();
			documentoXml.appendChild(documentoXml.createElement(RAIZ));
			for (Vehiculo vehiculo : coleccionVehiculo) {
				Element eVehiculo = getElemento(documentoXml, vehiculo);
				documentoXml.getDocumentElement().appendChild(eVehiculo);
			}
		}
		return documentoXml;
	}

	private Element getElemento (Document documentoXml, Vehiculo vehiculo) { // no esta terminado el metodo
		Element elementoVehiculo = documentoXml.createElement(vehiculo);
		elementoVehiculo.setAttribute(Integer.parseInt(CILINDRADA));
		elementoVehiculo.setAttribute(MARCA,vehiculo.getMarca());
		elementoVehiculo.setAttribute(TIPO,vehiculo);
		return elementoVehiculo;
	}

	private List<Vehiculo> coleccionVehiculo;

	public Vehiculos() {
		coleccionVehiculo = new ArrayList<>();
	}

	@Override
	public List<Vehiculo> get() {
		return coleccionVehiculo;
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

	@Override
	public void comenzar() {
		Document documento = UtilidadesXml.leerXmlDeFichero(FICHEROS_VEHICULOS);
		if (documento != null) {
			System.out.println("Fichero XML leído correctamente.");
			leerDom(documento);
		} else {
			System.out.println("No se ha podido leer el fichero XML.");
		}
	}
		
	

	@Override
	public void terminar() {
		UtilidadesXml.escribirXmlAFichero(crearDom(), FICHEROS_VEHICULOS);
		
	}

}
