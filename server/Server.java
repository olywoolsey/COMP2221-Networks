/ file: Server.java
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class Server
{
	// Define constants for port number, server directory, and log file
	private static final int PORT = 9888;
	private static final File SERVER_DIR = new File("serverFiles");
	private static final File LOG_FILE = new File("log.txt");

	public static void main(String[] args) throws IOException
	{
		// Create pool with 20 threads
		ExecutorService executor = Executors.newFixedThreadPool(20);
		// Create log file if it does not exist
		if (LOG_FILE.createNewFile())
		{
			System.out.println("log file created");
		} else
		{
			System.out.println("log file already exist");
		}
		// Create a server socket and continuously accept client connections
		try (ServerSocket serverSocket = new ServerSocket(PORT))
		{
			while (true)
			{
				// Accept client connection
				Socket clientSocket = serverSocket.accept();
				// Handle each client connection in a separate thread
				executor.submit(() -> handleClient(clientSocket));
			}
		}
	}

	private static void handleClient(Socket clientSocket)
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true))
		{
			// Read the request from the client
			String request = in.readLine();
			// Process the request
			switch (request)
			{
				case "list":
					out.println(listFiles());
					break;
				case "put":
					out.println(putFile(in, clientSocket));
					break;
				default:
					// If request is not list or put, send an error message
					out.println("Invalid request");
			}
			// log the request in log.txt
			logRequest(clientSocket.getInetAddress().getHostAddress(), request);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static String listFiles()
	{
		// list all files in the server directory with line breaks
		if (SERVER_DIR.exists() && SERVER_DIR.isDirectory()) {
			String[] files = SERVER_DIR.list();
			if (files != null && files.length > 0) {
				return "Listing " + files.length + " file(s):\n" + String.join("\n", files);
			} else {
				return "No files found in the server directory.";
			}
		} else {
			return "Server directory does not exist.";
		}
	}

	private static String putFile(BufferedReader in, Socket clientSocket)  throws IOException
	{
		// Read the file name from the client then create the file
		String fileName = in.readLine();
		File file = new File(SERVER_DIR, fileName);
		if (file.exists()) {
			// Discard the file data from the client if file is already on server
			byte[] buffer = new byte[8192];
			InputStream is = clientSocket.getInputStream();
			int count;
			while ((count = is.read(buffer)) > 0) {
				if (new String(buffer, 0, count).contains("END_OF_FILE")) {
					break;
				}
			}
			return "Error: Cannot upload file ’" + file.getName() + "’; already exists on server";
		} else {
			try (FileOutputStream fos = new FileOutputStream(file))
			{
				// Write the file data from the client to the server
				byte[] buffer = new byte[8192];
				InputStream is = clientSocket.getInputStream();
				int count;
				// While there is data to read from the client
				while ((count = is.read(buffer)) > 0)
				{
					if (new String(buffer, 0, count).contains("END_OF_FILE"))
					{
						// Remove the "END_OF_FILE" string from the buffer
						fos.write(buffer, 0, count - "END_OF_FILE".length());
						break;
					}
					// Write the buffer to the file all at once
					fos.write(buffer, 0, count);
				}
				return "Uploaded file " + file.getName();
			}
		}
	}

	private static void logRequest(String clientIP, String request)
	{
		try (PrintWriter logWriter = new PrintWriter(new FileWriter(LOG_FILE, true)))
		{
			// Find time of request
			String timestamp = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss").format(new Date());
			// Write the request to log file
			logWriter.println(timestamp + "|" + clientIP + "|" + request);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}