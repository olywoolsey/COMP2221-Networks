/* Requirements
- Run continuously
- Use an Executor to manage a fixed thread-pool with 20 connections.
- Following a request by a client, query the local folder serverFiles and return a list of the files found there to the
  same client.•
- Receive a request from a client to upload a new file to serverFiles.
- If a file with the same name already exists, return an error to the client; otherwise transfer the file from the
  client and save to serverFiles.
- Create the file log.txt on the server directory and log every valid client request, with one line per request, in the
  following format:date|time|client IP address|request where request is one of list or put, i.e. you do not need to log
  the filename for put operations. Do not add other rows (e.g. headers) to the log file.
 */
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class Server
{
	private static final int PORT = 9888;
	private static final File SERVER_DIR = new File("serverFiles");
	private static final File LOG_FILE = new File("log.txt");

	public static void main(String[] args) throws IOException
	{
		ExecutorService executor = Executors.newFixedThreadPool(20);
		if (SERVER_DIR.mkdirs() && LOG_FILE.createNewFile())
		{
			System.out.println("Server directory and log file created");
		} else
		{
			System.out.println("Server directory and log file already exist");
		}
		try (ServerSocket serverSocket = new ServerSocket(PORT))
		{
			while (true)
			{
				System.out.println("Client connected");
				Socket clientSocket = serverSocket.accept();
				executor.submit(() -> handleClient(clientSocket));
			}
		}
	}

	private static void handleClient(Socket clientSocket)
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true))
		{
			String request = in.readLine();
			String response;
			switch (request)
			{
				case "list":
					response = String.join("\n", Objects.requireNonNull(SERVER_DIR.list()));
					break;
				case "put":
					String fileName = in.readLine();
					File file = new File(SERVER_DIR, fileName);
					if (file.exists())
					{
						response = "Error: File already exists";
					} else {
						Files.copy((Path) in, file.toPath());
						response = "File uploaded successfully";
					}
					break;
				default:
					response = "Invalid request";
			}

			out.println(response);
			logRequest(clientSocket.getInetAddress().getHostAddress(), request);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void logRequest(String clientIP, String request)
	{
		try (PrintWriter logWriter = new PrintWriter(new FileWriter(LOG_FILE, true)))
		{
			String timestamp = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss").format(new Date());
			logWriter.println(timestamp + "|" + clientIP + "|" + request);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}