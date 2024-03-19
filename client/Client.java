// file: Client.java
import java.io.*;
import java.net.*;

public class Client
{
	private static final int PORT = 9888;
	private static final String SERVER_IP = "localhost";
	public static void main(String [] args) throws IOException
	{
		// command(args);
		if (args.length == 0)
		{
			System.out.println("Please enter a command...");
			return;
		}
		String command = args[0];
		try (Socket socket = new Socket(SERVER_IP, PORT);
			 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
		{
            switch (command) {
				case "list":
					// send the command to the server
					out.println(command);
					break;
				case "put":
					// send the command to the server
					out.println(command);
					// check if a file was specified
					if (args.length < 2)
					{
						System.out.println("Error: No file specified");
						return;
					}
					// send the filename to the server
					out.println(args[1]);
					// send the file to the server
					sendFile(args[1], socket);
					break;
				default:
					System.out.println(command + " not found...");
					return;
            }
			String response;
			while ((response = in.readLine()) != null)
			{
				System.out.println(response);
			}
		}
	}

	// subroutine that will send a file to the server
	private static void sendFile(String fileName, Socket socket) throws IOException
	{
		File file = new File(fileName);
		if (file.exists())
		{
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
			System.out.println("Error: File does not exist...");
			System.exit(1);
		}
	}
}