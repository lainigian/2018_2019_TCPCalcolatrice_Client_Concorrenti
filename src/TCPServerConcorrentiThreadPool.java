
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class TCPServerConcorrentiThreadPool extends Thread
{

	private ServerSocket server;
	private final int SERVER_PORT=2000;
	private ExecutorService threads;
	private final int NUM_MAX_TREADS=3;
	
	public TCPServerConcorrentiThreadPool() throws IOException
	{
		server= new ServerSocket(SERVER_PORT);
		server.setSoTimeout(1000);
		threads=Executors.newFixedThreadPool(NUM_MAX_TREADS);
	}
	
	public void run()
	{
		Socket connection = null;
		while(!interrupted())
		{
			try 
			{
				connection=server.accept();  //rimane in ascolto di nuve richiese di connessione da parte di client
				//genera un thread ClientConnection per la gestine di ogni richiesta di connessione
				TCPClientThread clientThread= new TCPClientThread(connection);	
				//INVIA IL THREAD AL THREAD POOL
				
				try
				{
					threads.submit(clientThread);
				}
				catch (RejectedExecutionException e)
				{
					connection.close();
				}
				
				
			}
			catch (SocketTimeoutException e) 
			{
				System.err.println("Timeout");
			}
			catch (IOException e) 
			{
			
				e.printStackTrace();
			}
			
			
			
		}
		
		
	}
	
	
	
	public static void main(String[] args) 
	{
		ConsoleInput tastiera= new ConsoleInput();
		try 
		{
			TCPServerConcorrentiThreadPool server= new TCPServerConcorrentiThreadPool();
			server.start();
			tastiera.readLine();
			server.interrupt();
			server.join();
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

}
