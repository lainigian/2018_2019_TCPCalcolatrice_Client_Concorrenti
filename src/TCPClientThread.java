
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClientThread implements Runnable
{
	private Socket connection;
	private InputStream input;
	private OutputStream out;
	
	public TCPClientThread(Socket connection) throws IOException
	{
		this.connection=connection;
		input=this.connection.getInputStream();
		out=this.connection.getOutputStream();
	}
	
	public void run()
	{
		byte[] bufferInput= new byte[1024];
		byte[] bufferOutput=new byte[1024];
		int n;
		String request;
		
		try 
		{
			//devi fare il ciclo di lettura in questo modo, altrimenti, se leggessi tutti i byte solamente quando
			//arriva -1, essi verrebbero letti solo alla chiusura della connessione da parte del client!
			while ((n=input.read(bufferInput))!=-1)   
			{
				if (n>0)
				{
					for (int i = 0; i < bufferInput.length; i++) 
					{
						if (bufferInput[i]=='\n'|| bufferInput[i]=='\r')
						{
							
							request=new String(bufferInput,"ISO-8859-1");
							String risultato=calcola(request);
							risultato+="\n";
							out.write(risultato.getBytes("ISO-8859-1"));
							out.flush();
						}
							
					}
				}
			}
			
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try 
		{
			input.close();
			out.close();
			connection.shutdownInput();
			connection.shutdownOutput();
			connection.close();
			System.out.println("connessione chiusa");
		} 
		catch (IOException e) 
		{
		
		}
	}
	
private String calcola(String messaggio)
	
	{
		double risultato = 0;
		double x;
		double y;
		String operazione;
		
		String[] elementiMessaggio;
		
		elementiMessaggio=messaggio.split(",");
		operazione=elementiMessaggio[0];
		x=Double.parseDouble(elementiMessaggio[1]);
		y=Double.parseDouble(elementiMessaggio[2]);
		if (operazione.compareTo("ADD")==0)
			risultato=x+y;
		else if (operazione.compareTo("SUB")==0)
			risultato=x-y;
		else if (operazione.compareTo("MUL")==0)
			risultato=x*y;
		else if (operazione.compareTo("DIV")==0)
		{
			if (y==0)
			{
				return "ERROR";
			}
			else
			risultato=x/y;
		}
		else
			return "ERROR";
			
		return Double.toString(risultato);
	}


}
