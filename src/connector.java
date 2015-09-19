import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class connector
 */
@WebServlet(description = "Enables to connect with Arduino", urlPatterns = { "/connector" })
public class connector extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String serialReturn = "no data";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public connector() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("In Get");
		// Set response content type
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Comunicator test = new Comunicator();
		if (test.initialize()) {
			test.sendData(request.getParameter("data"));
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
				System.out.println("Exception: " + ie.toString());
				out.println("Exception: " + ie.toString());

			}			
			out.println(serialReturn);
			test.close();

		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {
			System.out.println("Exception: " + ie.toString());
			out.println("Exception: " + ie.toString());

		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("In POST");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Comunicator test = new Comunicator();
		if (test.initialize()) {
			test.sendData(request.getParameter("data"));
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
				System.out.println("Exception: " + ie.toString());
				out.println("Exception: " + ie.toString());

			}			
			out.println(serialReturn);
			test.close();

		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {
			System.out.println("Exception: " + ie.toString());
			out.println("Exception: " + ie.toString());

		}
	}

	public class Comunicator implements SerialPortEventListener {

		SerialPort serialPort = null;
		private String appName;
		private BufferedReader input;
		private OutputStream output;

		private static final int TIME_OUT = 1000; // Port open timeout
		private static final int DATA_RATE = 9600; // Arduino serial port

		private final String PORT_NAMES[] = {
		// "/dev/tty.usbmodem", // Mac OS X
		// "/dev/usbdev", // Linux
		// "/dev/tty", // Linux
		// "/dev/serial", // Linux
		"COM3", // Windows
		};

		public boolean initialize() {
			// TODO Auto-generated method stub

			try {
				CommPortIdentifier portId = null;
				Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

				// Enumerate system ports and try connecting to Arduino over
				// each
				//
				System.out.println("Trying:");
				while (portId == null && portEnum.hasMoreElements()) {
					// Iterate through your host computer's serial port IDs
					//
					CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
							.nextElement();
					System.out.println("   port " + currPortId.getName());
					for (String portName : PORT_NAMES) {
						if (currPortId.getName().equals(portName)
								|| currPortId.getName().startsWith(portName)) {

							// Try to connect to the Arduino on this port
							//
							// Open serial port
							serialPort = (SerialPort) currPortId.open(appName,
									TIME_OUT);
							portId = currPortId;
							System.out.println("Connected on port "
									+ currPortId.getName());
							break;
						}
					}
				}

				if (portId == null || serialPort == null) {
					System.out.println("Oops... Could not connect to Arduino");
					return false;
				}

				// set port parameters
				serialPort.setSerialPortParams(DATA_RATE,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				// add event listeners
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);

				// Give the Arduino some time
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ie) {
				}

				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public void serialEvent(SerialPortEvent evt) {
			// TODO Auto-generated method stub
			try {
				switch (evt.getEventType()) {
				case SerialPortEvent.DATA_AVAILABLE:
					if (input == null) {
						input = new BufferedReader(new InputStreamReader(
								serialPort.getInputStream()));
					}
					String inputLine = input.readLine();
					System.out.println(inputLine);
					serialReturn = inputLine;
					break;

				default:
					break;
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}

		}

		public Comunicator() {
			appName = getClass().getName();
		}

		private void sendData(String data) {
			// TODO Auto-generated method stub
			try {
				System.out.println("Sending data: '" + data + "'");

				// open the streams and send the "y" character
				output = serialPort.getOutputStream();
				output.write(data.getBytes());
			} catch (Exception e) {
				System.err.println(e.toString());
				System.exit(0);
			}

		}

		private void close() {
			// TODO Auto-generated method stub
			if (serialPort != null) {
				serialPort.removeEventListener();
				serialPort.close();
			}
		}

	}

}
