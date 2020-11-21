import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.*;

public class server{

	public static void main(String args[]) throws Exception{
		try(ServerSocket serverSocket = new ServerSocket(8080)){
			while(true){
				try(Socket client = serverSocket.accept()){
					handleClient(client);
				}
			}
		}
	}

	private static void handleClient(Socket client) throws IOException{
		
		//(1)recieve data
		
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));

		String request = inFromClient.readLine();
		System.out.println(request);
		
		//(2)parse data
		StringTokenizer tokens = new StringTokenizer(request," ");
		String method = tokens.nextToken();
		String path = tokens.nextToken();
	       	String version = tokens.nextToken();
		
		System.out.println(" method: " + method + " path: " + path + " version: " + version);

		//(3) response + close
		Path filepath = getFilePath(path);
		System.out.println(filepath);	
		if(Files.exists(filepath)){
			String contentType = guessContentType(filepath);
			sendResponse(client,"200 OK", contentType, Files.readAllBytes(filepath));
		}
		else{
			byte[] notFoundContent = "<h1>Not found</h1>".getBytes();
			sendResponse(client,"404 Not Found","text/html",notFoundContent);
		}
	}

	private static void sendResponse(Socket client, String status, String ContentType, byte[] content)throws IOException{
	
		OutputStream ClientOutput = client.getOutputStream();
		ClientOutput.write(("HTTP/1.1" + status + "\r\n").getBytes());
		ClientOutput.write(("ContentType: " + ContentType + "\r\n").getBytes());
		ClientOutput.write("\r\n".getBytes());
		ClientOutput.write(content);
		ClientOutput.write("\r\n\r\n".getBytes());
		ClientOutput.flush();
		client.close();
	}
	
	private static Path getFilePath(String path){
		if(path.equals("/"))
			path = "/index.html";
		
		return Paths.get("/home/shiwulo/createServer/www",path);
	}

	private static String guessContentType(Path filePath) throws IOException{
		return Files.probeContentType(filePath);
	}
}
