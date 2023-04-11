package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.DocumentBuilder;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IClientes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Clientes implements IClientes {
	private static final  File FICHEROS_CLIENTES = new File("datos" + File.separator + "clientes.xml");
	private static final  String RAIZ = "raiz";
	private static final  String CLIENTE = "cliente";
	private static final  String DNI = "dni";
	private static final  String TELEFONO = "telefono";
	private static final String NOMBRE ="nombre";
	private List<Cliente> coleccionClientes;

	private static Clientes instancia;

	static Clientes getInstancia() {
		if (instancia == null) {
			instancia = new Clientes();

		}
		return instancia;
	}

	private void leerDom(Document documentoXml) {
		NodeList clientes = documentoXml.getElementsByTagName(CLIENTE);
		for (int i = 0; i < clientes.getLength(); i++) {
			Node nCliente = clientes.item(i);
			if (nCliente.getNodeType() == Node.ELEMENT_NODE) {
				Cliente cliente = getCliente((Element)nCliente);
				try {
					insertar(cliente);
				} catch (OperationNotSupportedException | NullPointerException e) {
					System.out.println(e.getMessage());
				}
				}
		}
		
	}

	private Cliente getCliente(Element elemento) {
		String telefono = elemento.getAttribute(TELEFONO);
		String dni = elemento.getAttribute(DNI);
		String nombre = elemento.getAttribute(NOMBRE);
		return new Cliente(nombre, dni, telefono);
	}

	private Document crearDom() {
		DocumentBuilder constructor = UtilidadesXml.crearConstructorDocumentoXml();
		Document documentoXml = null;
		if (constructor != null) {
			documentoXml = constructor.newDocument();
			documentoXml.appendChild(documentoXml.createElement(RAIZ));
			 for (Cliente cliente : coleccionClientes) {
				Element eCliente = getElemento(documentoXml, cliente);
				documentoXml.getDocumentElement().appendChild(eCliente);
			}
		}
		return documentoXml;
	}

	

	private Element getElemento(Document documentoXml, Cliente cliente) {
		Element elementoCliente = documentoXml.createElement(CLIENTE);
		elementoCliente.setAttribute(NOMBRE,cliente.getNombre() );
		elementoCliente.setAttribute(DNI,cliente.getDni());
		elementoCliente.setAttribute(TELEFONO,cliente.getTelefono());
		return elementoCliente;
	}

	private Clientes() {
		coleccionClientes = new ArrayList<>();
	}

	@Override
	public List<Cliente> get() {
		return new ArrayList<>(coleccionClientes);
	}

	@Override
	public void insertar(Cliente cliente) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede insertar un cliente nulo.");
		}
		if (!coleccionClientes.contains(cliente)) {
			coleccionClientes.add(cliente);
		} else {
			throw new OperationNotSupportedException("ERROR: Ya existe un cliente con ese DNI.");
		}
	}

	@Override
	public Cliente buscar(Cliente cliente) {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede buscar un cliente nulo.");
		}
		int indice = coleccionClientes.indexOf(cliente);
		if (indice == -1) {
			cliente = null;
		} else {
			cliente = coleccionClientes.get(indice);
		}
		return cliente;
	}

	@Override
	public void borrar(Cliente cliente) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede borrar un cliente nulo.");
		}
		if (!coleccionClientes.contains(cliente)) {
			throw new OperationNotSupportedException("ERROR: No existe ningún cliente con ese DNI.");
		}
		coleccionClientes.remove(cliente);

	}

	@Override
	public void modificar(Cliente cliente, String nombre, String telefono) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede modificar un cliente nulo.");
		}
		Cliente clienteEncontrado = buscar(cliente);
		if (clienteEncontrado == null) {
			throw new OperationNotSupportedException("ERROR: No existe ningún cliente con ese DNI.");
		}
		if (nombre != null && !nombre.isBlank()) {
			clienteEncontrado.setNombre(nombre);
			System.out.println("Se ha modificado el nombre correctamente.");
		}
		if (telefono != null && !telefono.isBlank()) {
			clienteEncontrado.setTelefono(telefono);
			System.out.println("Se ha modificado el teléfono correctamente.");
		}

	}

	@Override
	public void comenzar() {
		Document documento = UtilidadesXml.leerXmlDeFichero(FICHEROS_CLIENTES);
		if (documento != null) {
			System.out.println("Fichero XML leído correctamente.");
			leerDom(documento);
		} else {
			System.out.println("No se ha podido leer el fichero XML.");
		}
	}

	@Override
	public void terminar() {
		UtilidadesXml.escribirXmlAFichero(crearDom(), FICHEROS_CLIENTES);
	}
}
