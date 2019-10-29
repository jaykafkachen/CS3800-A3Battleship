import java.io.Serializable;
public class BattleShipTable implements Serializable
{ 
	/* Constants*/
	//Size of each type of ship
	static final int AIRCRAFT_CARRIER_SIZE = 5;
	static final int DESTROYER_SIZE = 3;
	static final int SUBMARINE_SIZE = 1;
	
	//symbols use on the board
	/*
	   "A": Aircraft
	   "D": Destroyer
	   "S": Submarine
	   
	   "X": Hit
	   "O": Miss
	   "-": default value
	*/
	
	final String AIRCRAFT_CARRIER_SYMBOL = "A";
	final String DESTROYER_SYMBOL = "D";
	final String SUBMARINE_SYMBOL = "S";
	final String HIT_SYMBOL = "X";
	final String MISS_SYMBOL = "O";
	final String DEFAULT_SYMBOL = "-";
	
	String [][]table = null;


	// constructor 
	public BattleShipTable() 
	{ 
		//System.out.println("create table");
		this.table = new String[10][10];
		//set default values
		for(int i=0;i<10;++i){
			for(int j=0;j<10;++j){
				this.table[i][j] = DEFAULT_SYMBOL;
			}		
		}		
	} 

	public char shipType(String x1)
	{
		int[] c = this.AlphaNumerictoXY(x1);
		return table[c[0]][c[1]].charAt(0);
	}
	
	//parameters are alpha coord (A1), 
	//returns hit t/f and marks this board with X if hit 
	//called by the opponent
	public boolean insertHit(String x1)
	{
		int[] c = this.AlphaNumerictoXY(x1);
		if(!table[c[0]][c[1]].equals(DEFAULT_SYMBOL))
		{
			table[c[0]][c[1]] = "X";
			return true;
		}
		return false;
	}

	//called by player if the coord they chose is projected to hit/miss
	public void setHitMiss(String x1, boolean hit)
	{
		int[] c = this.AlphaNumerictoXY(x1);
		if(hit)
		{
			table[c[0]][c[1]] = "X";
		}
		else
		{
			table[c[0]][c[1]] = "O";
		}
	}

	public boolean allSunk()
	{
		for(int r=0;r<10;r++)
		{
			for(int c=0;c<10;c++)
			{
				char value = table[r][c].charAt(0);
				if(value!=DEFAULT_SYMBOL.charAt(0) && value!='X')
					return false;
			}
		}
		return true;
	}

	//checks if entire ship is sunk and returns true/false
	public boolean checkSunk(String x1, char type)
	{
		int[] xy = this.AlphaNumerictoXY(x1);
		
		int[] up 	= { xy[0], xy[1]-1 };
		int[] up2	= { xy[0], xy[1]-2 };
		int[] up3	= { xy[0], xy[1]-3 };
		int[] up4	= { xy[0], xy[1]-4 };
		int[] up5	= { xy[0], xy[1]-5 };
		
		int[] down   = { xy[0], xy[1]+1 };
		int[] down2  = { xy[0], xy[1]+2 };
		int[] down3  = { xy[0], xy[1]+3 };
		int[] down4  = { xy[0], xy[1]+4 };
		int[] down5  = { xy[0], xy[1]+5 };
		
		int[] left   = { xy[0]-1, xy[1] };
		int[] left2  = { xy[0]-2, xy[1] };
		int[] left3  = { xy[0]-3, xy[1] };
		int[] left4  = { xy[0]-4, xy[1] };
		int[] left5  = { xy[0]-5, xy[1] };


		int[] right  = { xy[0]+1, xy[1] };
		int[] right2 = { xy[0]+2, xy[1] };
		int[] right3 = { xy[0]+3, xy[1] };
		int[] right4 = { xy[0]+4, xy[1] };
		int[] right5 = { xy[0]+5, xy[1] };


		int[][] Acoords = { up,up2,up3,up4,up5, down,down2,down3,down4,down5, left,left2,left3,left4,left5, right,right2,right3,right4,right5 };
		int[][] Dcoords = { up,up2,up3, down,down2,down3, left,left2,left3, right,right2,right3 };
		
		int[][] coords = (type=='D') ? Dcoords : Acoords;
		
		for(int[] adj:coords)
		{
			try
			{
				char value = table[adj[0]][adj[1]].charAt(0);
				if(value=='D' || value=='A') //if theres more destroyer or aircraft 2 hit
					return false;
			} catch(IndexOutOfBoundsException e){ /*System.out.println("coord ignored bc off board")*/;}
		}
		return true;
	}

	public static void main(String args[]) 
	{ 
		/*BattleShipTable t = new BattleShipTable();		
		/*t.insertAirCarrier("C5","C6");
		System.out.println(t.toString());
		if(!t.insertDestroyer("H9", "I9")){
			System.out.println("not able to insert");
		}
		*/
		/*t.insertAirCarrier("F5","G5");
		System.out.println(t.toString());
		
		BattleShipTable opp = new BattleShipTable();
		
		boolean shotthrutheheart = t.insertHit("F5");
		boolean sunk = opp.setHitMiss("F5", shotthrutheheart);
		System.out.println(opp.toString());
		System.out.println(t.toString());
		shotthrutheheart = t.insertHit("G5");
		sunk = opp.setHitMiss("G5", shotthrutheheart);
		shotthrutheheart = t.insertHit("H5");
		sunk = opp.setHitMiss("H5", shotthrutheheart);
		shotthrutheheart = t.insertHit("I5");
		sunk = opp.setHitMiss("I5", shotthrutheheart);
		shotthrutheheart = t.insertHit("J5");
		sunk = opp.setHitMiss("J5", shotthrutheheart);
		
		System.out.println(opp.toString());
		System.out.println(t.toString());


		if(sunk)
			System.out.println("AirCraft was sunk!");

		System.out.println("Are all ships sunk?: "+t.allSunk());*/
	} 

	//Utility methods, leave alone to fester in their circumlocuitous iniquity
	
	/*convert alpha_numeric to the X and Y coordinates*/
	private int[] AlphaNumerictoXY(String alpha_coordinates) throws NumberFormatException{
		//get the alpha part
		int []ret = new int[2];
		ret[0] = this.helperAlphaToX(alpha_coordinates.charAt(0));
		//get the numeric part
		ret[1] = Integer.parseInt(alpha_coordinates.substring(1));
		return ret;
	}
	private int helperAlphaToX(char alpha){
		return (int)alpha - (int)'A';
	}
	
	private String XYToAlphaNumeric(int []xy){
		return "" + ((char)(xy[0] + (int)'A')) + "" + xy[1];
	}
	//print out the table
	public String toString(){
		String ret = new String();
		System.out.println("    0   1   2   3   4   5   6   7   8   9  ");
		for(int i=0;i<10;++i){
		ret = ret + "" + (char)((int)'A' + i) + " | ";
			for(int j=0;j<10;++j){
			ret = ret + this.table[i][j] + " | ";
			}
			ret = ret + "\n";
		}
		return ret;
	}
	

	public boolean insertSubmarine(String x1){
		//check if it can be inserted
		if(this.insertSinglePoint(this.AlphaNumerictoXY(x1), "S"))
			return true;
		else
			return false;
	}	

	public boolean insertAirCarrier(String x1, String x2){
		//check if it can be inserted
		if(this.insertShip(x1, x2, BattleShipTable.AIRCRAFT_CARRIER_SIZE, "A"))
			return true;
		else
			return false;
	}
	
	public boolean insertDestroyer(String x1, String x2){
		//check if it can be inserted	
		if(this.insertShip(x1, x2, BattleShipTable.DESTROYER_SIZE, "D"))
			return true;
		else
			return false;
	}

	private boolean insertShip(String x1, String x2, int len, String s){
		int []xy1 = this.AlphaNumerictoXY(x1);
		int []xy2 = this.AlphaNumerictoXY(x2);
		if(!(xy1[0]>=0 && xy1[0]<=9 && xy1[1]>=0 && xy1[1]<=9)) return false;
		if(!(xy2[0]>=0 && xy2[0]<=9 && xy2[1]>=0 && xy2[1]<=9)) return false;
		
		if(xy1[0] == xy2[0] && (xy1[1]+1) == xy2[1]){// along the x axis
			if(checkAlongXAxis(this.AlphaNumerictoXY(x1),len)){//insert the battleship
				this.insertAlongXAxis(this.AlphaNumerictoXY(x1), len, s);
				return true;
			}else{//prompt the user again
				return false;
			}
		}else if(xy1[1] == xy2[1] && (xy1[0]+1) == xy2[0]){// along the y axis
			if(checkAlongYAxis(this.AlphaNumerictoXY(x1), len)){//insert the battleship
				this.insertAlongYAxis(this.AlphaNumerictoXY(x1), len, s);
				return true;
			}else{//prompt the user again
				return false;
			}
		}else
			return false;
	}
	
	private boolean insertSinglePoint(int[] xy, String s){
		if(this.table[xy[0]][xy[1]].equals(DEFAULT_SYMBOL)){
			this.table[xy[0]][xy[1]] = s;
			return true;
		}else
			return false;
	}

	private boolean checkAlongXAxis(int[] xy, int len){
		if(xy[1]+len > 10) return false;
		for(int j=xy[1];j<xy[1]+len;++j){
			if(!this.table[xy[0]][j].equals(DEFAULT_SYMBOL))
				return false;
		}
		return true;
	}
	
	private void insertAlongXAxis(int[] xy, int len, String s){
		for(int j=xy[1];j<xy[1]+len;++j){
			this.table[xy[0]][j] = s;
		}
	}
	
	private boolean checkAlongYAxis(int[] xy, int len){
		if(xy[0]+len > 10) return false;
		for(int i=xy[0];i<xy[0]+len;++i){
			if(!this.table[i][xy[1]].equals(DEFAULT_SYMBOL))
				return false;
		}
		return true;
	}
	
	private void insertAlongYAxis(int[] xy, int len, String s){
		for(int i=xy[0];i<xy[0]+len;++i){
			this.table[i][xy[1]] = s;				
		}		
	}	
	
} 
