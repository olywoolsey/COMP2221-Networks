/* Requirements
- Accepts one of the following commands as command line arguments, and performs the stated task:
	- list, which lists all the files on the server’s folder serverFiles.
	- put name, which uploads the file name to the server to be added to serverFiles(see above), or returns an error
	  message to say that this file already exists.
- Exits after completing each command
 */

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
					out.println(command);
					break;
				case "put":
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
		System.out.println("Sending file " + fileName + " to server...");
		File file = new File(fileName);
		System.out.println("File exists: " + file.exists());
		if (file.exists())
		{
			System.out.println("File exists...");
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
		}
		else
		{
			System.out.println("File does not exist...");
		}
	}
}