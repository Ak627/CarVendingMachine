import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

// Main class for the Car Vending Machine program
public class AlexanderKindallCarVendingMachine{

public static void main(String [] args) throws FileNotFoundException, InputMismatchException{
	try {
	Scanner UserInput = new Scanner(System.in); // Scanner for user input
	System.out.println("Enter the number of floors for the car vending machine: ");
	int rows = UserInput.nextInt();
	System.out.println("Enter the number of spaces for the car vending machine: ");
	int columns = UserInput.nextInt();
	
	VendingMachine VM = new VendingMachine(); // Create vending machine instance
	CarWash CW = new CarWash(); // Create car wash queue instance
	boolean exit = false; // Flag for exiting the menu
	while(exit != true) {
		// Display menu
		System.out.println("=== Car Vending Machine ===");
		System.out.println("1. Load Car Data from File");
		System.out.println("2. Display Vending Machine");
		System.out.println("3. Retrieve a Car by Location(Floor and Space)");
		System.out.println("4. Print Sorted Inventory (Price)");
		System.out.println("5. Print Sorted Inventory (Year)");
		System.out.println("6. Search for Cars(Manufacturer and Type)");
		System.out.println("7. Add Car to Wash Queue");
		System.out.println("8. Process Car Wash Queue");
		System.out.println("9. Sell a Car");
		System.out.println("10. Exit");
		System.out.println("Enter your Choice: ");
		
		int choice = UserInput.nextInt();
		UserInput.nextLine(); // Consume newline

		switch(choice) {
		case 1:
			// Load cars from a file
			System.out.println("Enter the file name: ");
			try {
				String fileName = UserInput.nextLine();
				Scanner fileScanner = new Scanner(new File(fileName));
				while (fileScanner.hasNextLine()) {
					String line = fileScanner.nextLine();
					if(line.isEmpty()) {
						continue; // Skip blank lines
					}
					String[] data = line.split("\\s+");
					String quality = data[0];
					int x = Integer.parseInt(data[1]);
					int y = Integer.parseInt(data[2]);
					int year = Integer.parseInt(data[3]);
					double price = Double.parseDouble(data[4]);
					String brand = data[5];
					String type = data[6];

					// Check if the position is out of bounds
					if(x > rows || y > columns) {
						System.out.println("Car exceeds number of rows or columns");
					}
					else {
						Car newCar;
						if(quality.equals("B")) { // Basic Car
							newCar = new BasicCar(x,y, brand, type, year, price);
							VM.addCar(new Node(newCar));
						}
						else if(quality.equals("P")) { // Premium Car
							newCar = new PremiumCar(x,y, brand, type, year, price);
							VM.addCar(new Node(newCar));
						}
					}
				}
				fileScanner.close();
			}catch(FileNotFoundException e) {
				System.out.println("File does not exist! Please try this again!");
			}
			break;
		case 2:
			// Show vending machine cars
			VM.showVendingMachine();
			break;
		case 3:
			// Retrieve car at given location
			System.out.println("Where would you like to retrieve the car from?");
			int posX = UserInput.nextInt();
			int posY = UserInput.nextInt();
			UserInput.nextLine();
			Car foundCar = VM.retrieveCar(posX, posY);
			if(foundCar != null) {
				System.out.println("Car retrieved: " + foundCar.displayInfo() + " at position " + posX + "," + posY);
			}
			else {
				System.out.println("No Car exists in position " + posX + "," + posY);
			}
			break;
		case 4:
			// Sort cars by price
			VM.sortCar(1);
			break;
		case 5:
			// Sort cars by year
			VM.sortCar(0);
			break;
		case 6:
			// Search for cars by manufacturer and type
			System.out.println("Enter Manufacturer: ");
			String manufacturer = UserInput.nextLine();
			System.out.println("Enter Type(Basic/Premium)");
			String Type = UserInput.nextLine();
			if(Type.equals("Basic") || Type.equals("Premium")) {
				VM.carSearch(manufacturer, Type);
			}
			else {
				System.out.println("TYPE NOT INPUTTED CORRECTLY! TRY AGAIN!");
			}
			break;
		case 7:
			// Add a car to the car wash queue
			System.out.println("Enter the Floor position of the car you would like to wash: ");
			posX = UserInput.nextInt();
			System.out.println("Enter the Space position of the car you would like to wash: ");
			posY = UserInput.nextInt();
			CW.enQueue(VM.retrieveCar(posX, posY));
			break;
		case 8:
			// Process all cars in the wash queue
			System.out.println("Processing car wash!");
			CW.processQueue();
			break;
		case 9:
			// Sell a car (remove it from vending machine)
			System.out.println("Enter the Floor position of the car you would like to buy: ");
			posX = UserInput.nextInt();
			System.out.println("Enter the Space position of the car you would like to buy: ");
			posY = UserInput.nextInt();
			VM.sellCar(posX,posY);
			break;
		case 10:
			// Exit the program
			System.out.println("GoodBye!");
			exit = true;
			break;
		default:
			// Handle invalid menu choices
			System.out.println("Invalid input! Try again!");
			break;
		}
	}
	UserInput.close();
	}catch(InputMismatchException e) {
		System.out.println("InputMisMatched! " + e);
	}

}
}


//Node class represents each node in the linked list, holding a Car object
class Node {
 Car carObj; // Car object stored in the node
 Node next;  // Pointer to the next node

 public Node(Car carObj) {
     this.carObj = carObj;
     next = null;
 }
}

//Abstract Car class (parent class for BasicCar and PremiumCar)
abstract class Car {
 String CarBrand;  // Car brand (e.g., Toyota)
 String CarType;   // Car model/type (e.g., Prius)
 double price;     // Price of the car
 int year;         // Manufacture year
 int x;            // Floor position in vending machine
 int y;            // Space position on the floor

 public Car(int x, int y, String CarBrand, String CarType, int year, double price) {
     this.x = x;
     this.y = y;
     this.CarBrand = CarBrand;
     this.CarType = CarType;
     this.price = price;
     this.year = year;
 }

 // Getter methods
 public String getBrand() { return CarBrand; }
 public String getType() { return CarType; }
 public double getPrice() { return price; }
 public int getYear() { return year; }

 // Abstract method to be implemented differently by BasicCar and PremiumCar
 public abstract String displayInfo();
}

//BasicCar class extends Car, representing a basic car
class BasicCar extends Car {
 public BasicCar(int x, int y, String CarBrand, String CarType, int year, double price) {
     super(x, y, CarBrand, CarType, year, price);
 }

 @Override
 public String displayInfo() {
     return "Basic Car: " + CarBrand + " " + CarType + " (" + year + ") $" + price;
 }
}

//PremiumCar class extends Car, representing a premium car
class PremiumCar extends Car {
 public PremiumCar(int x, int y, String CarBrand, String CarType, int year, double price) {
     super(x, y, CarBrand, CarType, year, price);
 }

 @Override
 public String displayInfo() {
     return "Premium Car: " + CarBrand + " " + CarType + " (" + year + ") $" + price;
 }
}

//VendingMachine class manages the linked list of cars
class VendingMachine {
 Node head; // Head node of the car list

 public VendingMachine() {
     head = null;
 }

 // Add a car to the vending machine
 public void addCar(Node newNode) {
     if (head == null) {
         head = newNode; // First car becomes the head
     } else {
         Node current = head;
         while (current != null) {
             // Check for duplicate position
             if (current.carObj.x == newNode.carObj.x && current.carObj.y == newNode.carObj.y) {
                 System.out.println(current.carObj.getBrand() + " " + current.carObj.getType() + " is already in this slot, cannot add car to this area!");
                 return;
             }
             if (current.next == null) { // Last node found, attach new node
                 current.next = newNode;
                 return;
             }
             current = current.next;
         }
     }
 }

 // Retrieve a car by position using a HashMap
 public Car retrieveCar(int x, int y) {
     HashMap<String, Car> map = new HashMap<>();
     Node current = head;
     while (current != null) {
         String key = current.carObj.x + "," + current.carObj.y;
         map.put(key, current.carObj);
         current = current.next;
     }
     String SearchKey = x + "," + y;
     return map.getOrDefault(SearchKey, null); // Return the car if found, otherwise null
 }

 // Display all cars in the vending machine
 public void showVendingMachine() {
     Node current = head;
     while (current != null) {
         System.out.println("Floor " + current.carObj.x + " Space " + current.carObj.y + ": " + current.carObj.displayInfo());
         current = current.next;
     }
     if (head == null) {
         System.out.println("There are no cars yet!");
     }
 }

 // Sort the cars by price or year
 public void sortCar(int choice) {
     List<Car> carList = new ArrayList<>();
     Node current = head;
     while (current != null) {
         carList.add(current.carObj);
         current = current.next;
     }

     if (choice == 1) {
         Collections.sort(carList, Comparator.comparing(Car::getPrice)); // Sort by price
         System.out.println("Sorted cars by Price:");
     } else if (choice == 0) {
         Collections.sort(carList, Comparator.comparing(Car::getYear)); // Sort by year
         System.out.println("Sorted cars by Year:");
     }

     for (Car car : carList) {
         System.out.println(car.getBrand() + " " + car.getType() + " - $" + car.getPrice() + " (" + car.getYear() + ")");
     }
 }

 // Search cars by brand and type
 public void carSearch(String Brand, String Quality) {
     Node current = head;
     while (current != null) {
         if (Quality.equals("Basic")) {
             if (current.carObj instanceof BasicCar && current.carObj.getBrand().equals(Brand)) {
                 System.out.println(current.carObj.displayInfo());
             }
         } else if (Quality.equals("Premium")) {
             if (current.carObj instanceof PremiumCar && current.carObj.getBrand().equals(Brand)) {
                 System.out.println(current.carObj.displayInfo());
             }
         }
         current = current.next;
     }
 }

 // Sell (delete) a car by position
 public void sellCar(int x, int y) {
     if (head == null) {
         System.out.println("No Cars in the list!");
         return;
     }

     if (head.carObj.x == x && head.carObj.y == y) {
         head = head.next; // Car is at the head
         return;
     }

     Node current = head;
     Node prev = null;
     while (current != null) {
         if (current.carObj.x == x && current.carObj.y == y) {
             System.out.println(current.carObj.displayInfo() + " has been sold!");
             prev.next = current.next; // Bypass the current node
             return;
         }
         prev = current;
         current = current.next;
     }
     System.out.println("Car not found!");
 }
}

//CarWash class manages a separate linked list queue for washing cars
class CarWash {
 Node head; // Head node of the car wash queue

 public CarWash() {
     this.head = null;
 }

 // Add a car to the end of the queue
 public void enQueue(Car newCar) {
     if (newCar != null) {
         Node newNode = new Node(newCar);
         if (head == null) {
             head = newNode;
         } else {
             Node current = head;
             while (current.next != null) {
                 current = current.next;
             }
             current.next = newNode;
         }
         System.out.println(newCar.displayInfo() + " enQueued!");
         displayQueue();
     } else {
         System.out.println("No Car at this position!");
     }
 }

 // Process (dequeue) the entire car wash queue
 public void processQueue() {
     while (head != null) {
         System.out.println("Dequeued: " + head.carObj.CarBrand + " " + head.carObj.CarType);
         head = head.next; // Remove from queue
         displayQueue();   // Show current state after each removal
     }
 }

 // Helper method to display the car wash queue
 private void displayQueue() {
     Node current = head;
     while (current != null) {
         System.out.print(current.carObj.displayInfo() + " -> ");
         current = current.next;
     }
     System.out.println("null"); // End of the queue
     System.out.println();
 }
}
