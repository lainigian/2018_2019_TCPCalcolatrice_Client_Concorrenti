
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPClient 
{
	private String server_address;
	private int server_port;

	public TCPClient(String server, int port)
	{
		server_address=server;
		server_port=port;
	}
	
	public double sendAndReceive(String operazione, double x, double y) throws IOException, EccezioneErrore
	{
		Socket clientSocket= new Socket();
		InetSocketAddress server=new InetSocketAddress(server_address, server_port);
		InputStream input;
		OutputStream out;
		int n;
		String messaggioRicevuto;
		String request;
		byte[] bufferInput= new byte[1024];
		//byte[] bufferOutput= new byte[1024];
		
		clientSocket.connect(server, 1000);
		clientSocket.setSoTimeout(1000);
		
		request=operazione+","+Double.toString(x)+","+Double.toString(y)+"\n";
		
		out=clientSocket.getOutputStream();
		out.write(request.getBytes("ISO-8859-1"));
		out.flush();
		
		input= clientSocket.getInputStream();
		
		while ((n=input.read(bufferInput))!=-1)
		{
			if (n>0)
			{
				for (int i = 0; i < bufferInput.length; i++) 
				{
					if (bufferInput[i]=='\n' || bufferInput[i]=='\r')
					{
						messaggioRicevuto= new String(bufferInput,"ISO-8859-1");
						clientSocket.shutdownInput();
						clientSocket.close();
						messaggioRicevuto=new String(bufferInput,"ISO-8859-1");
						try
						{
							return Double.parseDouble(messaggioRicevuto);
						}
						catch (NumberFormatException e)
						{
							throw new EccezioneErrore();
						}
						
						
					}
				}
			}		
		}
		
		//in caso di errore degli stream di I/O
		clientSocket.shutdownInput();
		clientSocket.close();
		messaggioRicevuto=new String(bufferInput,"ISO-8859-1");
		throw new IOException();
		
	}
	
	private double add(double x, double y) throws IOException, EccezioneErrore
	{
		return sendAndReceive("ADD", x, y);
	}
	
	private double sub(double x, double y) throws IOException, EccezioneErrore
	{
		return sendAndReceive("SUB", x, y);
	}
	
	private double mul(double x, double y) throws IOException, EccezioneErrore
	{
		return sendAndReceive("MUL", x, y);
	}
	private double div(double x, double y) throws IOException, EccezioneErrore
	{
		return sendAndReceive("DIV", x, y);
	}
	
	public static void main(String[] args) 
	{
		String server="127.0.0.1";
		int port=2000;
		double risultato;

		
		TCPClient client= new TCPClient(server, port);
		
		try 
		{
			risultato=client.add(10, 2);
			System.out.println(risultato);
			
			risultato=client.sub(10, 2);
			System.out.println(risultato);
			
			risultato=client.mul(10, 2);
			System.out.println(risultato);
			
			risultato=client.div(10, 2);
			System.out.println(risultato);
			
			risultato=client.div(2, 0);
			System.out.println(risultato);
		} 
		catch (EccezioneErrore e) 
		{
			System.err.println("Calcolo impossibile");
		}
		catch (SocketTimeoutException e) 
		{
			System.err.println("Il server non riponde");
		}
		
		catch (IOException e) 
		{
			e.printStackTrace();
			System.err.println("Errore di I/O");
		}
		
	}

}
