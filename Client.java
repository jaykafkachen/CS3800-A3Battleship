import java.io.*; 
import java.net.*; 
class Client 
{ 
	static BattleShipTable myboard = new BattleShipTable();
	static BattleShipTable oppboard = new BattleShipTable();

	
	static BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));
	static BufferedReader inServer;
	static DataOutputStream outServer;
	public static void main(String argv[]) throws Exception 
    { 
		System.out.println("New client connecting to Server on port 5000...");
		Socket clientSocket = new Socket("localhost", 5000);
		inServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		outServer = new DataOutputStream(clientSocket.getOutputStream());
		setup();
		String input, answer, coord="";
		boolean won = false;
		while(true)
		{
			input = inServer.readLine();
			if(input.charAt(0)=='o') //offense
			{
				System.out.println("Player on offense");
				input = inServer.readLine();
				if(input.charAt(0) == 'p') //start play
				{
					coord = inputGuess();
					if(coord!=null)
					{
						System.out.println("You guessed: " + coord);
						sendGuess(coord); //send guess to server
					}
				}
				System.out.println("Waiting for opponent response...");
				input = inServer.readLine();
				
				if(input.charAt(0) == 'm') //mark hits on the board
				{ 
					answer = inServer.readLine(); //accept answer string from server
					won = markHit(coord, answer);
					System.out.println("did i win?: " + won);
				}
				if(won)
				{
					outServer.writeBytes("w"+'\n');
					System.out.println("My Board:\n" + myboard.toString());
		System.out.println("Opponent's Board:\n" + oppboard.toString());
					System.out.println("GAME OVER: WIN\nYou sunk all opponent ships.");
					break;
				}
			}
			else 
			{
				System.out.println("Player on defense\nWaiting for opponent's guess...");
				input = inServer.readLine();
				if(input.charAt(0) == 'k') //check hit
				{
					answer = inServer.readLine(); //get coords from server
					System.out.println("Opponent guessed: "+ answer);
					sendHit(answer);
				}
				if(myboard.allSunk())
				{
					System.out.println("My Board:\n" + myboard.toString());
		System.out.println("Opponent's Board:\n" + oppboard.toString());
					System.out.println("GAME OVER: LOSE\nAll your ships sunk.");
					break;
				}
			}
			outServer.writeBytes("t"+'\n');
		}
		clientSocket.close();
	} 

	public static String inputGuess()
	{
		System.out.println("My Board:\n" + myboard.toString());
		System.out.println("Opponent's Board:\n" + oppboard.toString());
		System.out.print("Select a tile to bomb:\n-->");
		try {
		return inUser.readLine();
		} 
		catch(Exception e) {	System.out.println("problem with user input"); }
		return null;
	}

	public static void sendGuess(String coord)
	{
		try {
			outServer.writeBytes(coord + '\n');
		} 
			catch(Exception e) {	System.out.println("problem with sending guessed coords to server"); }
	}

	public static void sendHit(String coord)
	{
		char ship = myboard.shipType(coord);
		int hit = myboard.insertHit(coord)  ? 1 : 0;
		int sunk = 0;
		if(hit==1) 
			sunk = myboard.checkSunk(coord,ship) ? 1 : 0;
		int all = myboard.allSunk()         ? 1 : 0;
		String hitsunk = hit + "/" + sunk + "/" + all + "/" + ship + "\n";
		try {
		outServer.writeBytes(hitsunk);
		} 
		catch(Exception e) {	System.out.println("problem with sending hit/miss/sunk/ship results to server"); }
	}

	public static boolean markHit(String coord, String response) //returns if game over
	{	//				 hit/sunk/all/shiptype
		//response output: "1/0/0/D"
		boolean hit = ( response.charAt(0) == '1' ); 
		boolean sunk =( response.charAt(2) == '1' );
		boolean all = ( response.charAt(4) == '1' );
		char shiptype = response.charAt(6);
		String ship = "You sunk opponent's ";
		oppboard.setHitMiss(coord, hit);
		System.out.println("Ship hit?: " + hit);
		System.out.println("Ship sunk?: " + sunk);
		if(sunk)
		{
			if(shiptype=='S')
				System.out.println(ship+"submarine!");
			else if(shiptype=='D')
				System.out.println(ship+"destroyer!");
			else if(shiptype=='A')
				System.out.println(ship+"aircarrier!");
		}
		return all;
	}

	public static void setup()
	{
		//2 of each ship
		try {
		System.out.println(myboard.toString());
		System.out.println("All coordinates should be letter-number format, example: A1\n");
		String coord1, coord2;
		System.out.println("Select submarine coordinates: (1x1)");
		System.out.print("Submarine 1-->");
		coord1 = inUser.readLine();
		myboard.insertSubmarine(coord1);
		System.out.print("Submarine 2-->");
		coord1 = inUser.readLine();
		myboard.insertSubmarine(coord1);
		System.out.println(myboard.toString());
		
		System.out.println("For destroyer/aircraft, select 2 coordinates per ship to determine direction\n");
		System.out.println("Select destroyer coordinates: (3x1)");
		System.out.print("Destroyer 1:\n-->");
		coord1 = inUser.readLine();
		System.out.print("-->");
		coord2 = inUser.readLine();
		myboard.insertDestroyer(coord1, coord2);
		System.out.print("Destroyer 2:\n-->");
		coord1 = inUser.readLine();
		System.out.print("-->");
		coord2 = inUser.readLine();
		myboard.insertDestroyer(coord1, coord2);
		System.out.println(myboard.toString());
		
		System.out.println("Select aircraft coordinates: (3x1)");
		System.out.print("Aircraft 1:\n-->");
		coord1 = inUser.readLine();
		System.out.print("-->");
		coord2 = inUser.readLine();
		myboard.insertAirCarrier(coord1, coord2);
		System.out.print("Aircraft 2:\n-->");
		coord1 = inUser.readLine();
		System.out.print("-->");
		coord2 = inUser.readLine();
		myboard.insertAirCarrier(coord1, coord2);
		System.out.println(myboard.toString());
		
		outServer.writeBytes("r" + '\n'); //tell server ready to play
		System.out.println("Waiting for opponent setup...");
		} catch(IOException e) {}
	}


	
}