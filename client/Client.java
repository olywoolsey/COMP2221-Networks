// file: Client.java
import java.io.*;
import java.net.*;

public class Client
{
	// Define the port number and server IP
	private static final int PORT = 9888;
	private static final String SERVER_IP = "localhost";
	public static void main(String [] args) throws IOException
	{
		// Check if a command was entered
		if (args.length == 0)
		{
			System.out.println("Please enter a command...");
			return;
		}
		// The first argument is the command
		String command = args[0];
		// Create a socket and connect to the server
		try (Socket socket = new Socket(SERVER_IP, PORT);
			 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
		{
            switch (command) {
				case "list":
					// Send the command to the server
					out.println(command);
					break;
				case "put":
					// Send the command to the server
					out.println(command);
					// Check if a file was specified
					if (args.length < 2)
					{
						System.out.println("Error: No file specified");
						return;
					}
					// Send the file contents to the server
					sendFile(args[1], socket, out);
					break;
				default:
					System.out.println(command + " not found...");
					return;
            }
			// Read then print the server response line by line
			String response;
			while ((response = in.readLine()) != null)
			{
				System.out.println(response);
			}
		}
	}

	// subroutine that will send a file to the server
	private static void sendFile(String fileName, Socket socket, PrintWriter out) throws IOException
	{
		File file = new File(fileName);
		if (file.exists())
		{
			// Send the filename to the server
			out.println(file.getName());
			byte[] buffer = new byte[8192];
			InputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			OutputStream os = socket.getOutputStream();
			int count;
			while ((count = bis.read(buffer)) > 0)
			{
				os.write(buffer, 0, count);
			}
			os.write("END_OF_FILE".getBytes());
			os.flush();
		} else
		{
			System.out.println("Error: Cannot open local file ’" + file.getName() + "’ for reading.");
			System.exit(0);
		}
	}
}