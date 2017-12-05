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
		loadSave(player);
		help();
		introduction();
		playGame(player, rooms);
	}

	public static void loadSave(Player p) {
		final String fileName = "data/save.csv";
		String line = null;
		String[] lines = new String[4];

		try{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int count = 0;

			Scanner scanner = new Scanner(System.in);
			System.out.print("There is a saved game available. Would you like to load it? Y/N ");
			String response = scanner.nextLine();

			if(response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes")) {
				while((line = bufferedReader.readLine()) != null) {
					//health score inventory location
					lines[count] = line;
					print(line);
					count++;
				}

				if(count > 0) {
					int health = Integer.parseInt(lines[0]);
					int score = Integer.parseInt(lines[1]);
					String[] inventory = convertToArray(lines[2]);
					int location = Integer.parseInt(lines[3]);

					setPlayerHealth(p, health);
					setPlayerScore(p, score);
					setPlayerInventory(p, inventory);
					setPlayerLocation(p, location);
				}
			} else {
				print("Okay. Starting new game...");
			}
		} catch(IOException e) {
			System.out.println(e);
		}
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
		print("\t5. save\n\t\tThis will save the current game state so you can resume playing at a later date.");
		print("\t6. help\n\t\tIf you need to see these instructions again typing help will display them.");
		print("\t7. exit\n\t\tThis will exit the game.\n");
	}

	//Initialise Array List of all the rooms
	public static Room[] initRooms(Room[] rooms) {
		final String fileName = "data/data.csv";
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
				setRoomObject(r, data[2]);
				setRoomMaxScore(r, calculateMaxScore(r));
				rooms[count] = r;
				count++;
			}
		} catch(IOException e) {
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
					currentRoom = move(player, rooms, direction);
					takeItems(player, currentRoom);
					roomDescription(player, currentRoom);
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
				if(monsterInRoom(player)) {
					attack(player);
				} else {
					print("The monster doesn't appear to be in this room, there is nothing to attack.");
				}
			} else if(turn.trim().equalsIgnoreCase("inventory")) {
				printInventory(player);
			} else if(turn.trim().equalsIgnoreCase("save")) {
				save(player);
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
	public static Room move(Player p, Room[] rooms, String dir) {
		int location = getPlayerLocation(p);
		if(dir.equalsIgnoreCase("N")) {
			setPlayerLocation(p, location - 5);
		} else if(dir.equalsIgnoreCase("S")) {
			setPlayerLocation(p, location + 5);
		} else if(dir.equalsIgnoreCase("E")) {
			setPlayerLocation(p, location + 1);
		} else if(dir.equalsIgnoreCase("W")) {
			setPlayerLocation(p, location - 1);
		}
		return rooms[getPlayerLocation(p)];
	}

	//Eat an item of food in inventory to heal health
	public static void eat(Player p, String item) {
		int health = getPlayerHealth(p);
		if(health != 100) {
			if(item.contains("golden")) {
				print("You cannot eat a " + item + "! It's an object, not food!");
			} else {
				String[] inventory = getPlayerInventory(p);
				if(inventory.length > 1) {
					int itemIndex = 0;
					for(int i =0; i < inventory.length; i++) {
						if(inventory[i].equalsIgnoreCase(item)) {
							itemIndex = i;
						}
					}
					String[] newInventory = new String[inventory.length - 1];
					for(int j = 0;j<itemIndex;j++) {
						newInventory[j] = inventory[j];
					}
					for(int k = itemIndex + 1;k<inventory.length;k++) {
						newInventory[itemIndex] = inventory[k];
						itemIndex++;
					}
					setPlayerInventory(p, newInventory);
				} else {
					String[] newInventory = null;
					setPlayerInventory(p, newInventory);
				}
				print("You have eaten some " + item);
			}
		} else {
			print("You have full health, you don't need to eat anything!");
		}
	}

	//Attack the monster if you're in the correct room
	public static void attack(Player p) {

	}

	//Saves the current game state so the user can come back at a later time
	public static void save(Player p) {
		String health = Integer.toString(getPlayerHealth(p)) + "\n";
		String score = Integer.toString(getPlayerScore(p)) + "\n";
		String inventory = convertToString(getPlayerInventory(p)) + "\n";
		String location = Integer.toString(getPlayerLocation(p));

		final String fileName = "data/save.csv";

		try {
			FileWriter fileWriter = new FileWriter(fileName);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write(health);
			bufferedWriter.write(score);
			bufferedWriter.write(inventory);
			bufferedWriter.write(location);

			bufferedWriter.close();
		} catch(IOException e) {
			System.out.println(e);
		}
	}

	//Print the player's current inventory
	public static void printInventory(Player p) {
		String[] inv = getPlayerInventory(p);
		print("*******************");
		print("Your inventory contains: ");
		for(int i = 0;i < inv.length;i++) {
			print((i+1) + ": " + inv[i]);
		}
		print("*******************");
	}

	//@TODO Remove item from room once taken
	//Take the items in the room and put them in the player's inventory
	public static void takeItems(Player p, Room r) {
		String object = getRoomObject(r);
		if(!object.equalsIgnoreCase("nothing")) {
			String[] inventory = getPlayerInventory(p);
			String[] newInventory;

			if(inventory == null) {
				newInventory = new String[1];
				newInventory[0] = object;
			} else {
				newInventory = new String[inventory.length + 1];
				for(int i = 0;i < inventory.length;i++) {
					newInventory[i] = inventory[i];
				}
				newInventory[inventory.length] = object;
			}
			newInventory = sortInventory(newInventory);
			setPlayerInventory(p, newInventory);
			print(object + " has been added to your inventory.");
			setRoomObject(r, "nothing");
		}
	}

	//Decide how many points the user receives
	public static int score(Player p, int highest) {
		Random score = new Random();
		int multiplier = 1;
		if(getPlayerScore(p) >= 20) {
			multiplier = getPlayerScore(p)/10;
		}
		return (score.nextInt(highest) + 1)*multiplier;
	}

	//Print description of the room just entered
	public static void roomDescription(Player p, Room r) {
		System.out.println(getRoomMessage(r));
		int roomScore = getRoomMaxScore(r);
		int score = score(p, roomScore);
		setPlayerScore(p, score);
		print("Your score is now: " + getPlayerScore(p));
	}

	//Calculate the maximum score a player can receive from a room
	public static int calculateMaxScore(Room r) {
		String roomObject = getRoomObject(r);
		int score = 1;
		if(roomObject.contains("golden")) {
			score = 3;
		} else if(roomObject.contains("food")) {
			score = 2;
		}
		return score;
	}

	//Returns a boolean as to whether the monster is in the current room
	public static boolean monsterInRoom(Player p) {
		if(getPlayerLocation(p) == 13) {
			return true;
		}
		return false;
	}

	//Sort the players inventory in to alphabetical order
	public static String[] sortInventory(String[] inventory) {
		if(inventory.length > 1) {
			for(int pass = 0;pass < inventory.length - 1; pass++) {
				for(int i = 0;i < inventory.length - 1;i++) {
					if(inventory[i].compareTo(inventory[i + 1]) > 0) {
						String tmp = inventory[i + 1];
						inventory[i + 1] = inventory[i];
						inventory[i] = tmp;
					}
				}
			}
		}
		return inventory;
	}


	//Convert a String array to a comma separated string
	public static String convertToString(String[] arr) {
		String arrString = "";
		for(int i = 0;i<arr.length;i++) {
			if(i != (arr.length - 1) ) {
				arrString = arrString + arr[i] + ",";
			} else {
				arrString = arrString + arr[i];
			}
		}
		return arrString;
	}

	//Convert a comma separated string to a String array
	public static String[] convertToArray(String s) {
		String[] arr = s.split(",");
		return arr;
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
		public static Player setPlayerInventory(Player p, String[] objects) {
			p.inventory = objects;
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

		public static String getRoomObject(Room r) {
			return r.object;
		}
		public static Room setRoomObject(Room r, String object) {
			r.object = object;
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
	String object;
	int maxScore;
}