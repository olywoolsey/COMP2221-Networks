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
			System.out.println("Usage: java Client <command> [args]");
			return;
		}
		String command = args[0];
		try (Socket socket = new Socket(SERVER_IP, PORT);
			 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
		{
            switch (command) {
				case "list":
					System.out.println("list");
					// send the command to the server
					out.println(command);
					break;
				case "put":
					System.out.println("put");
					System.out.println(args[1]);
					break;
				default:
					System.out.println(command + " not found...");
					return;
            }
			String response = in.readLine();
			System.out.println(response);
		}
	}
}