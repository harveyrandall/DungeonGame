import java.util.*;
import java.io.*;

/*

	Author: Harvey Randall
	Date: 17/10/17

	Dungeon game
	The user can explore the dungeon collecting objects
	and searching for a monster to defeat

*/

class Game {

	public static void main(String[] args) {
		startGame();
	}

	//Start the game and initialise
	public static void startGame() {
		Player player = new Player();
		Room[] rooms = new Room[25];
		rooms = initRooms(rooms);

		introduction();
		playGame(player, rooms);
	}

	//Print introduction to the game
	public static void introduction() {
		print("You find yourself in a giant entrance hall.\nYou can see four doors marked N,S,E, and W.");
	}

	//Print commands user can type to control the game
	public static void help() {
		print("To play the game you can type one of these commands each time you come to a room:");
		print("\t1. move <direction>\n\t\tWhere direction is one of N,S,E, or W.");
		print("\t2. eat <food>\n\t\tWhere food is one of the pieces of food you have in your inventory.");
		print("\t3. attack\n\t\tYou can use this command to attack the monster when in the same room.");
		print("\t4. inventory\n\t\tThis will display the contents of your inventory and the amount you have.");
		print("\t5. help\n\t\tIf you need to see these instructions again typing help will display them.");
		print("\t6. exit\n\t\tThis will exit the game.");
	}

	//Initialise Array List of all the rooms
	public static Room[] initRooms(Room[] rooms) {
		String fileName = "data/data.csv";
		String line = null;

		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int count = 0;

			while((line = bufferedReader.readLine()) != null) {
				Room r = new Room();
				String[] data = line.split(",");
				setRoomMessage(r, data[1]);
				setRoomDirections(r, data[0].split("|"));
				setRoomObjects(r, data[2].split("|"));
				setRoomMaxScore(r, calculateMaxScore(r));
				rooms[count] = r;
				count++;
			}
		} catch(Exception e) {
			System.out.println(e);
		}
		return rooms;
	}

	//Controls gameplay and handling of user made moves
	public static void playGame(Player player, Room[] rooms) {
		while(getPlayerHealth(player) > 0) {
			roomDescription(rooms);
			String turn = turn();
			//validateMove(turn, rooms[getPlayerLocation(player)], player);
			if(turn.toLowerCase().contains("move")) {
				String direction = turn.split("move");
				direction = direction[1].trim();
				if(stringArrayContains(getRoomDirections(r), direction)) {
					move(direction);
				} else {
					print("That's not a valid move for the room you're currently in. Please take your turn again.");
				}
			} else if(turn.toLowerCase().contains("eat")) {
				String item = turn.split("eat");
				item = item[1].trim();
				if(stringArrayContains(getPlayerInventory(p), item)) {
					eat(item);
				} else {
					print("You don't have that in your inventory. Please take your turn again.")
				}
			} else if(turn.trim().equalsIgnoreCase("attack")) {
				if(getPlayerLocation(p) == 13) {
					attack(player);
				}
			} else if(turn.trim().equalsIgnoreCase("inventory")) {
				printInventory(player);
			} else if(turn.trim().equalsIgnoreCase("help")) {
				help();
			} else {
				print("I'm not quite sure what you want to do...");
				print("Take your turn again.");
				return false;
			}
		}
	}

	//User picks a direction to move
	public static String turn() {
		String direction = input("What move would you like to make?");
		return direction.trim();
	}

	//Move the player in the direction chosen
	public static void move(String dir) {

	}

	//Eat an item of food in inventory to heal health
	public static String eat(String item) {

	}

	//Attack the monster if you're in the correct room
	public static void attack(Player p) {

	}

	//Print the player's current inventory
	public static void printInventory(Player p) {
		String[] inv = getPlayerInventory(p);

		print("")
		for(int i = 0;i < inv.length;i++) {

		}
	}

	//Decide how many points the user receives
	public static int score() {
		Random score = new Random();
		return score.nextInt(4) + 1;
	}

	//Print description of the room just entered
	public static void roomDescription(Room r) {
		System.out.println(getRoomMessage(r));
	}

	//Calculate the maximum score a player can receive from a room
	public static int calculateMaxScore(Room r) {
		String[] roomObjects = getRoomObjects(r);
		int score = roomObjects.length;
		int multiplier = 1;
		for(int i = 0; i < roomObjects.length;i++) {
			if(roomObjects[i].contains("golden")) {
				multiplier = 3;
			} else if(roomObjects[i].contains("food")) {
				multiplier = 2;
			}
		}
		return (score * multiplier);
	}

	//Check whether a given string array contains a given value
	//ie for making a move whether the direction given is possible
	public static Boolean stringArrayContains(String[] arr, String str) {
		for(int i = 0;i < arr.length;i++) {
			if(arr[i].equalsIgnoreCase(str)) {
				return true;
			}
		}
		return false;
	}

	//Receive user input as String
	public static String input(String text) {
		Scanner scanner = new Scanner(System.in);
		print(text);
		return scanner.nextLine();
	}

	//Prints output to command line
	public static void print(String text) {
		System.out.println(text);
	}

	/*
		Getter and Setter methods for the
		Player record
	*/
		public static int getPlayerHealth(Player p) {
			return p.health;
		}
		public static Player setPlayerHealth(Player p, int health) {
			p.health = health;
			return p;
		}

		public static int getPlayerScore(Player p) {
			return p.score;
		}
		public static Player setPlayerScore(Player p, int score) {
			p.score = p.score + score;
			return p;
		}

		public static String[] getPlayerInventory(Player p) {
			return p.inventory;
		}
		public static Player setPlayerInventory(Player p, String object) {
			String[] temp = p.inventory;
			p.inventory = new String[p.inventory.length + 1];
			p.inventory = temp;
			p.inventory[p.inventory.length - 1] = object;
			return p;
		}

		public static int getPlayerLocation(Player p) {
			return p.location;
		}
		public static Player setPlayerLocation(Player p, int location) {
			p.location = location;
			return p;
		}

	/*
		Getter and Setter methods for Room record
	*/
		public static String getRoomMessage(Room r) {
			return r.message;
		}
		public static Room setRoomMessage(Room r, String message) {
			r.message = message;
			return r;
		}

		public static String[] getRoomDirections(Room r) {
			return r.directions;
		}
		public static Room setRoomDirections(Room r, String[] directions) {
			r.directions = directions;
			return r;
		}

		public static String[] getRoomObjects(Room r) {
			return r.objects;
		}
		public static Room setRoomObjects(Room r, String[] objects) {
			r.objects = objects;
			return r;
		}

		public static int getRoomMaxScore(Room r) {
			return r.maxScore;
		}
		public static Room setRoomMaxScore(Room r, int score) {
			r.maxScore = score;
			return r;
		}


}

class Player {
	int health = 100;
	int score;
	String[] inventory;
	int location = 12;
}

class Room {
	String message;
	String[] directions;
	String[] objects;
	int maxScore;
}
