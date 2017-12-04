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
		help();
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
		print("\t6. exit\n\t\tThis will exit the game.\n");
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
			Room currentRoom = rooms[getPlayerLocation(player)];

			String turn = turn();
			if(turn.toLowerCase().contains("move")) {
				String direction = turn.split("move")[1].trim();
				if(stringArrayContains(getRoomDirections(currentRoom), direction)) {
					move(player, rooms, direction);
				} else {
					print("You can't go that way! Go again.");
				}
			} else if(turn.toLowerCase().contains("eat")) {
				String item = turn.split("eat")[1].trim();
				if(stringArrayContains(getPlayerInventory(player), item)) {
					eat(player, item);
				} else {
					print("You don't have that in your inventory. Please take your turn again.");
				}
			} else if(turn.trim().equalsIgnoreCase("attack")) {
				if(getPlayerLocation(player) == 13) {
					attack(player);
				}
			} else if(turn.trim().equalsIgnoreCase("inventory")) {
				printInventory(player);
			} else if(turn.trim().equalsIgnoreCase("help")) {
				help();
			} else if(turn.trim().equalsIgnoreCase("exit")) {
				print("Thank you for playing!");
				System.exit(0);
			} else {
				print("I'm not quite sure what you want to do...");
				print("Take your turn again.");
			}
		}
	}

	//User picks a direction to move
	public static String turn() {
		String direction = input("What move would you like to make?");
		return direction.trim();
	}

	//Move the player in the direction chosen
	public static void move(Player p, Room[] rooms, String dir) {
		int location = getPlayerLocation(p);
		System.out.println(location);
		if(dir.equalsIgnoreCase("N")) {
			setPlayerLocation(p, location - 5);
		} else if(dir.equalsIgnoreCase("S")) {
			setPlayerLocation(p, location + 5);
		} else if(dir.equalsIgnoreCase("E")) {
			setPlayerLocation(p, location + 1);
		} else if(dir.equalsIgnoreCase("W")) {
			setPlayerLocation(p, location - 1);
		}
		roomDescription(p, rooms[getPlayerLocation(p)]);
	}

	//Eat an item of food in inventory to heal health
	public static String eat(Player p, String item) {
		String[] inv = getPlayerInventory(p);
		String[] newInventory = new String[inv.length - 1];
		int index = 0;
		for(int i = 0;i<inv.length;i++) {
			if(inv[i].equalsIgnoreCase(item)) {
				index = i;
				break;
			} else {
				newInventory[i] = inv[i];
			}
		}
		for(int x = index + 1;x < inv.length;x++) {
			newInventory[x] = inv[x];
		}
		setPlayerInventory(p, newInventory);
		Random r = new Random();
		int heals = r.nextInt(10);
		int totalHealth = getPlayerHealth(p);
		totalHealth = (totalHealth + heals > 100) ? 100 : (totalHealth + heals);
		setPlayerHealth(p, totalHealth);
		return ("You have eaten " + item + ". It heals " + heals + " health.");
	}

	//Attack the monster if you're in the correct room
	public static void attack(Player p) {

	}

	//Print the player's current inventory
	public static void printInventory(Player p) {
		String[] inv = getPlayerInventory(p);
		print("*******************");
		print("Your inventory contains: ");
		for(int i = 0;i < inv.length;i++) {
			print(i + ": " + inv[i]);
		}
		print("*******************");
	}

	//Take the items in the room and put them in the player's inventory
	public static void takeItems(Player p, Room r) {
		String[] objects = getRoomObjects(r);
		for(int i = 0;i < objects.length;i++) {
			setPlayerInventory(p, objects[i]);
		}
	}

	//Decide how many points the user receives
	public static int score(int highest) {
		Random score = new Random();
		return score.nextInt(highest) + 1;
	}

	//Print description of the room just entered
	public static void roomDescription(Player p, Room r) {
		System.out.println(getRoomMessage(r));
		int roomScore = getRoomMaxScore(r);
		setPlayerScore(p, score(roomScore));
		print("Your score is now: " + getPlayerScore(p));
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
			p.inventory = new String[temp.length + 1];
			p.inventory = temp;
			p.inventory[temp.length - 1] = object;
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
	String[] inventory = new String[100];
	int inventoryItems = 0;
	int location = 12;
}

class Room {
	String message;
	String[] directions;
	String[] objects;
	int maxScore;
}
