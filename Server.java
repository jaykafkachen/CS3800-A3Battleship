import java.io.*; 
import java.net.*; 
import java.util.*;

class Server implements Runnable
{ 
	Socket p1, p2;
	BufferedReader inP1, inP2;  
	DataOutputStream outP1, outP2;
	
    public Server(Socket player1, Socket player2)
    {
		p1 = player1;
		p2 = player2;
		try {
		inP1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
		inP2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
		outP1 = new DataOutputStream(p1.getOutputStream()); 
		outP2 = new DataOutputStream(p2.getOutputStream()); 
		} catch(IOException e) {}
	}

	//implement run method
	public void run() 
	{
		String p1input, p2input;
		
		try
		{
		System.out.println("Waiting for player setup...");
		p1input = inP1.readLine();
		p2input = inP2.readLine();
		if(p1input.charAt(0) == 'r' && p2input.charAt(0) == 'r')
		{
			System.out.println("Both players are ready.");
			
			while(true)
			{
				p1input = playOffense(inP1, inP2, outP1, outP2); //player 1 guesses coords
				p2input = checkDefense(inP2, outP2, p1input,1); //write out p1's guess to p2, get boolean hit/miss
				markOffense(outP1, p2input); //tell p1 to mark hit or miss
				//get terminate turn flags from both
				p1input = inP1.readLine();
				p2input = inP2.readLine();
				if(p1input.charAt(0) == 'w')
				{
					System.out.println("Player 1 won!");
					break;
				}
				//otherwise flipflop turn 2 p2
				p2input = playOffense(inP2, inP1, outP2, outP1); 
				p1input = checkDefense(inP1, outP1, p2input,2); 
				markOffense(outP2, p1input); 
				//get terminate turn flags from both
				p1input = inP1.readLine();
				p2input = inP2.readLine();
				if(p2input.charAt(0) == 'w')
				{
					System.out.println("Player 2 won!");
					break;
				}
			}
			
		p1.close();
		p2.close();
		}
		
		} catch(Exception e) {}
	}

	public String playOffense(BufferedReader in1, BufferedReader in2, DataOutputStream out1, DataOutputStream out2) throws IOException
	{
		String input;
		out1.writeBytes("o" + '\n'); //p1 offense flag
		out2.writeBytes("d" + '\n'); //p2 defense flag
		out1.writeBytes("p" + '\n'); //tell p1 to play
		input = in1.readLine(); //get p1's guess coords
		return input;
	}

	public String checkDefense(BufferedReader in2, DataOutputStream out2, String input, int p) throws IOException
	{
		System.out.println("Player "+ p +" guessed: "+ input);
		out2.writeBytes("k"+'\n'); //tell p2 to get ready for coords
		out2.writeBytes(input+'\n'); //write coords to p2
		return in2.readLine(); //accept + return opp response string in format 0,0 (hit,sunk)
	}

	public void markOffense(DataOutputStream out1, String response) throws IOException
	{
		out1.writeBytes("m"+'\n'); //tell offense to await hit mark answer
		out1.writeBytes(response+'\n');
	}

  public static void main(String argv[])
    { 
      ServerSocket welcomeSocket; 
      Thread t = null;
      try
      {
		welcomeSocket = new ServerSocket(5000);
		System.out.println("Server running on port 5000");
        while(true) 
        { 
			Socket connect1 = welcomeSocket.accept();
			System.out.println("Player 1 Added. Waiting for Player 2...");
			Socket connect2 = welcomeSocket.accept();
			System.out.println("Player 2 Added. Starting game...");
			
			//create thread to service new connection
			t = new Thread(new Server(connect1, connect2));
			t.start(); 
        } 
      } catch(Exception e) {System.out.println("server socket creation error");}
    } 
} 
 