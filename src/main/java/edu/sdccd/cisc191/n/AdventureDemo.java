package edu.sdccd.cisc191.n;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.io.FileWriter;
import java.util.concurrent.atomic.AtomicInteger;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//this thread makes a log that records certain events in the game in a text file
//by Gene P.
class LogWriter extends Thread{
	private Thread t;
	private String threadName;
	final AtomicInteger atomicInteger;
	
	LogWriter(String name, AtomicInteger aInteger){
		atomicInteger = aInteger;
		threadName = name;
	}
	
	//start the thread
	public void run(){
		try{
			FileWriter fileWriter = new FileWriter("event-log.txt", true);
			fileWriter.write("Event Log Started at: " + System.currentTimeMillis() + "(Current time in milliseconds)\n");
			fileWriter.flush();
			while(true){
				if(atomicInteger.get() == 1){
					fileWriter.write("Player lost game at: " + System.currentTimeMillis() + "(Current time in milliseconds)\n");
					fileWriter.flush();
				}
			}
		} catch(IOException e){
			System.out.println("Failed to create log file.");
			e.printStackTrace();
		}
	}
	
	public void start(){
		if(t == null){
			t = new Thread(this, threadName);
			t.start();
		}
	}
}

public class AdventureDemo{
	
	//atomic integers help transfer data between the two threads
	final AtomicInteger atomicInteger = new AtomicInteger(0);
	
	Scanner scr = new Scanner(System.in);
	
	ArrayList<String> eventBuffer = new ArrayList<String>();
	
	//GUI title screen by Gene Patay
	JFrame gameWindow;
	Container con;
	JPanel titlePanel;
	JPanel startPanel;
	JPanel startButtonPanel;
	JLabel titleLabel;
	JButton startButton;
	Font titleFont = new Font("Cambria", Font.PLAIN, 50);

	//Player class made by Gene P, Jonathan L, and Victor S.
	public static class Player {
		String Name;
		int PlayerType = -1;

		/*
		 * Inventory
		 * Canned Food - 0
		 * Sticks - 1
		 * Firearms - 2
		 * Phones - 3
		 * */

		int[] Inventory = {0, 0, 0, 0};

		//inventory classes made by Matthew B and Victor S.
		public void SetInventory(int cannedFood, int stick, int fireArm, int phone){
			Inventory[0] = cannedFood;
			Inventory[1] = stick;
			Inventory[2] = fireArm;
			Inventory[3] = phone;
		}

		public void DescribeInventory(){
			System.out.println("-------------------------------");
			System.out.println("Your Inventory:");
			System.out.println("1: Canned Food: " + Inventory[0]);
			System.out.println("2: Stick: " + Inventory[1]);
			System.out.println("3: Firearm: " + Inventory[2]);
			System.out.println("4: Phone: " + Inventory[3]);
			System.out.println("-------------------------------");
		}

		public boolean ItemExists(int itemID){
			return Inventory[itemID] > 0;
		}

		public void Reset(){
			Name = "";
			PlayerType = -1;
			SetInventory(0, 0, 0, 0);
		}
	}

	public static void main(String args[]){	
		AdventureDemo adventureDemo = new AdventureDemo();
		
		LogWriter logWriter = new LogWriter("gameLog-1", adventureDemo.atomicInteger);
		logWriter.start();
			System.out.println("City Adventures");
			        
			
			Player player = new Player();
			
			adventureDemo.setPlayer(player);
			adventureDemo.FlushSpace();
			adventureDemo.preparePlayer(adventureDemo, player);
			adventureDemo.startSequence(adventureDemo, player);

	}
	
	//this closes out the game when a game session ends
	//endGame and completeGame by Gene P.
	public void endGame(Player player){
		System.out.println("-----------GAME OVER-----------");
		System.out.println("Press any key to exit.");
		atomicInteger.set(1);
		try{System.in.read();}
		catch(Exception e){}
		System.exit(0);
	}
	
	public void gameComplete(Player player){
		System.out.println("-----------YOU WIN!-----------");
		System.out.println("Press any key to exit.");
		atomicInteger.set(1);
		try{System.in.read();}
		catch(Exception e){}
		System.exit(0);
	}
	
	//asks for player name and character to play as
	//following player classes by Gene P. and Jonathan L.
	public void setPlayer(Player player){
		System.out.println("Your name son, give me your name");
		player.Name = scr.nextLine();
		System.out.println("What are you, " + player.Name);
		while(player.PlayerType <= 0){
			System.out.println("Choose your class: Civilian (1), Bum (2), or Businessman (3)");
			
			int playerChoice = scr.nextInt();
			
			//tried to use switch method as alternative to if/else
			switch(playerChoice){
				case 1:
					player.PlayerType = 1;
					break;
				case 2:
					player.PlayerType = 2;
					break;
				case 3:
					player.PlayerType = 3;
					break;
				default:
					System.out.println("Please select one of the specified classes.");
			}
		}
	}
	
	public void preparePlayer(AdventureDemo adventureDemo, Player player){
		switch(player.PlayerType){
			case 1:
				System.out.println("You cannot defend yourself, but you have a phone");
				System.out.println("You are an average Joe who is afraid of things most people are afraid of, like conflict, zombies, and communism");
				player.SetInventory(1, 0, 0, 1);
				player.DescribeInventory();
				break;
			case 2:
				System.out.println("You have some sticks to defend yourself, and some canned food for backup");
				System.out.println("Although you do have some money saved for a good meal once in a while");
				System.out.println("Your life on the streets has made you tougher and smellier than most");
				player.SetInventory(1, 1, 0, 0);
				player.DescribeInventory();
				break;
			case 3:
				System.out.println("You have purchased a firearm in order to stay safe on the streets, but pray you should not be forced to use it");
				System.out.println("Your cozy city life has made you fearful and cautious of the lower income communities");
				player.SetInventory(0, 0, 1, 1);
				player.DescribeInventory();
				break;
		}
	}
	
	//frees space on certain prompts so the text isn't squished together
	public void FlushSpace(){
		for(int i = 0; i < 50; i++){
			System.out.println("");
		}
	}
	
	//useItem by Matthew B.
	public int useItem(Player player){
		while(true){
			System.out.println("What item would you like to use?");
			player.DescribeInventory();
			int playerChoice = scr.nextInt();
			if(player.ItemExists(playerChoice - 1)){
				return playerChoice;
			} else{
				System.out.println("You don't own this item.");
			}
		}
	}
	
	//starts the game
	//GUI by Gene P.
	public void startSequence(AdventureDemo adventureDemo, Player player){
		gameWindow = new JFrame();
		gameWindow.setSize(800, 600);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindow.getContentPane().setBackground(Color.black);
		gameWindow.setLayout(null);
		gameWindow.setVisible(true);
		con = gameWindow.getContentPane();
		
		titlePanel = new JPanel();
		titlePanel.setBounds(100, 150, 600, 75);
		titlePanel.setBackground(Color.red);
		titleLabel = new JLabel("CITY ADVENTURES");
		titleLabel.setForeground(Color.white);
		titleLabel.setFont(titleFont);
		titlePanel.setVisible(true);
		
		
		
		startButtonPanel = new JPanel();
		startButtonPanel.setBounds(300, 350, 150, 100);
		startButtonPanel.setBackground(Color.orange);
		startButtonPanel.setVisible(true);
		
		
		startButton = new JButton("START");
		startButton.setBackground(Color.orange);
		startButton.setBackground(Color.green);
		startButton.addActionListener(new ActionListener(){

public void actionPerformed(ActionEvent e){
            gameWindow.dispose();
        }
    });
		startButton.setVisible(true);
		
		titlePanel.add(titleLabel);
		startButtonPanel.add(startButton);
		
		con.add(titlePanel);
		con.add(startButtonPanel);
		
		adventureDemo.kittenEvent(adventureDemo, player);
	}
	
	//Dialogue and choices by Gene P. and Victor S.
	public void kittenEvent(AdventureDemo adventureDemo, Player player){
		boolean kittenComplete = false;
		while(!kittenComplete){
			System.out.println("-------------------------------------------------------------------");
			System.out.println("You're walking down a sidewalk and see a small kitten under a box");
			System.out.println("He looks tired and scared");
			System.out.println("What do you want to do?");
			System.out.println("1: Observe the kitten");
			System.out.println("2: Pick up the kitten");
			System.out.println("3: Kick the kitten's box");
			System.out.println("4: Check Items");
			System.out.println("-------------------------------------------------------------------");
			
			int playerChoice = scr.nextInt();
			
			switch(playerChoice){
				case 1:
					adventureDemo.FlushSpace();
					System.out.println("You observe the kitten and see that he is also hungry.");
					kittenComplete = false;
					break;
				case 2:
					adventureDemo.FlushSpace();
					System.out.println("You pick up the poor kitten and conclude that he needs immediate care.");
					System.out.println("1: Take him home");
					System.out.println("2: Put him back down.");
					int actionChoice = scr.nextInt();
					switch(actionChoice){
						case 1:
							adventureDemo.FlushSpace();
							System.out.println("You take the kitten home, and nurse him back to health.");
							System.out.println("Your adventure has been cut short, but now you have a new friend.");
							System.out.println("Press ENTER to continue.");
							try{System.in.read();}
							catch(Exception e){}
							adventureDemo.endGame(player);
							kittenComplete = true;
							break;
						case 2:
							adventureDemo.FlushSpace();
							System.out.println("You put the kitten back in his box");
							kittenComplete = false;
							break;
						default:
							kittenComplete = false;
					}
					break;
				case 3:
					adventureDemo.FlushSpace();
					System.out.println("Being the jerk you apparently are, you kick the kitten's box.");
					System.out.println("Enraged, the kitten lets out a Bruce Lee yell and kung fu kicks you in your soft spot.");
					System.out.println("Humiliated, you fall to the floor and cry yourself to sleep.");
					System.out.println("Press ENTER to continue.");
					try{System.in.read();}
					catch(Exception e){}
					adventureDemo.endGame(player);
					kittenComplete = true;
					break;
				case 4:
					adventureDemo.FlushSpace();
					int itemChoice = adventureDemo.useItem(player);
					switch(itemChoice){
						case 1: 
							adventureDemo.FlushSpace();
							System.out.println("You give the cat food, he gobbles it up and lets out a happy meow.");
							System.out.println("Satisfied, you move on to your next location.");
							System.out.println("Press ENTER to continue.");
							try{System.in.read();}
							catch(Exception e){}
							adventureDemo.streetEvent(adventureDemo, player);
							kittenComplete = true;
							break;
						case 2:
							adventureDemo.FlushSpace();
							System.out.println("You point the stick towards the kitten.");
							System.out.println("Scared, he claws it up and destroys the stick.");
							kittenComplete = false;
							break;
						case 3:
							adventureDemo.FlushSpace();
							System.out.println("For some reason you decided to pull out a GUN on a helpless kitten.");
							System.out.println("Unfortunately for you, this kitten knows 50 different forms of jiujitsu.");
							System.out.println("The kitten gives you a beating and you end up in the hospital for a lengthy and painful recovery.");
							System.out.println("Press ENTER to continue.");
							try{System.in.read();}
							catch(Exception e){}
							adventureDemo.endGame(player);
							kittenComplete = true;
							break;
						case 4:
							adventureDemo.FlushSpace();
							System.out.println("You get out your phone and call animal services.");
							System.out.println("They soon arrive but alongside them are the FBI.");
							System.out.println("Your description of the kitten has brought up red flags across the country.");
							System.out.println("This kitten was apparently wanted in 10 states for aggravated assault.");
							System.out.println("You receive praise for your discovery and become an instant local celebrity.");
							System.out.println("You did not expect such an outcome, but you go with it and move along.");
							System.out.println("Press  ENTER to continue.");
							try{System.in.read();}
							catch(Exception e){}
							adventureDemo.streetEvent(adventureDemo, player);
							kittenComplete = true;
							break;
					}
					break;
			}
		}
	}
	public void streetEvent(AdventureDemo adventureDemo, Player player){
		boolean streetComplete = false;
		while(!streetComplete){
			System.out.println("---------------------------------------------------------------------------------------------------");
			System.out.println("You have now come across a bustling busy street");
			System.out.println("Although it is nighttime, there are still lights and people out and open stores and restaurants");
			System.out.println("Danger is very unlikely to reach you here, What would you like to do?");
			System.out.println("1: Eat at a restaurant");
			System.out.println("2: Go to the store");
			System.out.println("3: Socialize with the citizens");
			System.out.println("4: Make your way home");
			System.out.println("----------------------------------------------------------------------------------------------------");
			
			int streetChoice = scr.nextInt();
			
			switch(streetChoice){
				case 1:
				adventureDemo.FlushSpace();
				System.out.println("Which restaurant would you like to go to?");
				System.out.println("1: Donatello's Pizzaria");
				System.out.println("2: Ali's Cuisine of the Middle East");
				System.out.println("3: Saturday's All American Diner");
				System.out.println("4: Beijing Express");
				
				int restaurantChoice = scr.nextInt();
				
				switch(restaurantChoice){
					case 1:
					adventureDemo.FlushSpace();
					System.out.println("You enter one of the most famous Italian restaurants in town");
					System.out.println("The smell of fresh spaghetti and pizza entices your appetite");
					System.out.println("What would you like to order?");
					System.out.println("1: Meat Lover's Pizza");
					System.out.println("2: Vegetarian Pizza");
					System.out.println("3: Spaghetti and Meat Sauce");
					System.out.println("4: Meatball Sandwich");
					System.out.println("5: Torpedo Sandwich");
					
					int italFoodChoice = scr.nextInt();
					
					switch(italFoodChoice){
						case 1, 2, 3, 4, 5:
						adventureDemo.FlushSpace();
						System.out.println("Your meal has arrived");
						System.out.println("But as you are about to indulge, something interesting occurs");
						System.out.println("A group of men in black enter the establishment, asking to see the owner");
						System.out.println("As they are escorted, one henchman stays behind and catches your eye");
						System.out.println("Hungry and angry, he demands to have your meal");
						System.out.println("What do you do?");
						System.out.println("1: Give the man your meal");
						System.out.println("2: Check Items");
						System.out.println("3: Call him fat");
						
						int thugChoice = scr.nextInt();
						
						switch(thugChoice){
							case 1:
							adventureDemo.FlushSpace();
							System.out.println("You give the thug your meal");
							System.out.println("Satisfied, the thug leaves you alone and joins his friends in the back");
							System.out.println("You leave the restaurant, hungry, but safe");
							streetComplete = false;
							break;
							case 2:
							adventureDemo.FlushSpace();
							int itemChoice = adventureDemo.useItem(player);
					        switch(itemChoice){
								case 1:
								adventureDemo.FlushSpace();
								System.out.println("You offer a piece of canned food to the henchman");
								System.out.println("Confused and insulted, he smacks the food out of your hand and demands the meal on your table");
								System.out.println("Give the man your meal? Y/N?");
								
							    char giveMeal = scr.next().charAt(0);
								
								switch(giveMeal){
									case 'Y', 'y':
									adventureDemo.FlushSpace();
									System.out.println("You give the thug your meal");
							        System.out.println("Satisfied, the thug leaves you alone and joins his friends in the back");
							        System.out.println("You leave the restaurant, hungry, but safe");
							        streetComplete = false;
							        break;
							        case 'N', 'n':
							        adventureDemo.FlushSpace();
							        System.out.println("Tired of bargaining, the man takes a swing at you");
							        System.out.println("However, before he lands the blow, a voice call out to him");
							        System.out.println("His friend is telling him to come to the back with the others");
							        System.out.println("Frustrated, he leaves, and you finish your meal, pay, and go back out to the street");
							        streetComplete = false;
							        break;
								}
								break;
							
								case 2:
								adventureDemo.FlushSpace();
								System.out.println("You pull out your stick against the man");
								System.out.println("You poke him with the stick, angering him");
								System.out.println("The following 5 minutes involve a brutal beating performed on you, followed by an ambulance immediatey taking you to the ICU");
								System.out.println("Now you are defeated and hungry");
								System.out.println("Press ENTER to continue");
								try{System.in.read();}
							    catch(Exception e){}
							    adventureDemo.endGame(player);
							    break;
							    case 3:
							    adventureDemo.FlushSpace();
							    System.out.println("You pull out your firearm against the thug");
							    System.out.println("Surprised, the thug puts both his hands in the air");
							    System.out.println("Scared, he says he doesn't want any trouble and runs out the back");
							    System.out.println("The owner of the restaurant notices the commotion and goes to see you");
							    System.out.println("Impressed by your comnbat skill, he informs you of his dilemma");
							    System.out.println("");			
							    System.out.println("He tells you that the group that showed up was a group known as the Bratoni Family");
							    System.out.println("They have been demanding protection money from the owner but he is out of money");
							    System.out.println("He asks if you can go to their hideout and convince the boss to leave him alone");
							    System.out.println("Will you accept the quest? Y/N?");
							    
							    char mafiaQuest = scr.next().charAt(0);
							    switch(mafiaQuest){
									case 'Y', 'y':
									adventureDemo.FlushSpace();
									System.out.println("You vow to the kind man that you will return to him with news that he is debt free");
									System.out.println("You ask him where their base is and you go off to convince the boss");
									System.out.println("Press ENTER to continue");
									try{System.in.read();}
						            catch(Exception e){}
						            adventureDemo.bratoniQuest(adventureDemo, player);
						            streetComplete = true;
									break;
									case 'N', 'n':
									adventureDemo.FlushSpace();
									System.out.println("Fearing for your safety, you call the man crazy and rush out back to the street");
									streetComplete = false;
									break;
								}																								
						        break;
						        case 4:
						        adventureDemo.FlushSpace();
						        System.out.println("You pull out your phone, but as you frantically dial for help, the man realizes what you're doing and snatches the phone");
						        System.out.println("He proceeds to beat you into unconsciousness and take your meal by force");
						        System.out.println("That's gonna hurt in the morning");
						        adventureDemo.endGame(player);
						        break;
						        }      
						   break;
						   case 3:
						   adventureDemo.FlushSpace();
						   System.out.println("In your infinite wisdom you decide to insult the man who is twice your height and weight");		
						   System.out.println("What follows is a beating so horrific that it seems likely your journey has come to an end...");
						   adventureDemo.endGame(player);
						   break;				   
					   }						   
				    }
				     break;				   				    														
	                 case 2:
	                 adventureDemo.FlushSpace();
	                 System.out.println("A renowned restaurant celebrating the food of the Middle East, the scent of spices and exotic meats entice your sense of smell");
	                 System.out.println("It is not a cuisine you are used to, but you'll try anything once");
	                 System.out.println("What would you like to order?");
	                 System.out.println("1: Falafel with Fresh Hummus");
	                 System.out.println("2: Fish curry with tilapia");
	                 System.out.println("3: Lamb with Turkish coffee");
	              
	                 int ArabFoodChoice = scr.nextInt();
	              
	                 switch(ArabFoodChoice){
					  case 1, 2:
					  adventureDemo.FlushSpace();
					  System.out.println("Your meal has arrived");
					  System.out.println("You chow down and have one of the most enjoyable meals of your life");
					  System.out.println("You feel the need to go to the owner himself and thank him");
					  System.out.println("However, he is not present at the moment, so you pay with the momey you have and leave");
					  streetComplete = false;
					  break;
					  case 3:
					  adventureDemo.FlushSpace();
					  System.out.println("The waiter regretfully informs you that they are out of lamb");
					  System.out.println("He asks if you want to wait until they receive some from the owner, who is out shopping");
					  System.out.println("Wait for the lamb? Y/N?");
					  
					  char lambWait = scr.next().charAt(0);
					  
					  switch(lambWait){
						  case 'Y', 'y':
						  adventureDemo.FlushSpace();
						  System.out.println("You decide to stay and wait for the lamb");
						  System.out.println("After a few minutes you see the owner return with shopping bags");
						  System.out.println("Minutes later the waiter arrives with your meal, and thanks you for waiting");
						  System.out.println("It is the most delicious meal you've had in a long time, and feel the need to thank the owner for bringing it");
						  System.out.println("When you enter the kitchen, you see the owner in the middle of a phone call");
						  System.out.println("He looks frustrated and distressed");
						  System.out.println("After he is done, you ask him what is the matter");
						  System.out.println("He says that his son had disappeared the night before, and has been unable to receive word from the police");
						  System.out.println("He then asks with guilt if you may go out and search for his son");
						  System.out.println("Search for the son? Y/N");
						  
						  char sonQuest = scr.next().charAt(0);	
						  
						  switch(sonQuest){
							  case 'Y', 'y':
							  adventureDemo.FlushSpace();
							  System.out.println("You offer to help and tell the man you will look for his son");
							  System.out.println("He thanks you and you set off to look for the young man");
							  System.out.println("However, you realize you know nothing about his disappearance");
							  System.out.println("Hopefully you will find clues by exploring the streets");
							  streetComplete = false;
							  break;
							  case 'N', 'n':
							  adventureDemo.FlushSpace();
							  System.out.println("You feel bad for the poor man, but you decline, concerned about your own well-being");
							  System.out.println("You thank him for the lamb and leave the restaurant, back into the street");
							  streetComplete = false;
							  break;
						  }
						  break;
						  case 'N', 'n':
						  adventureDemo.FlushSpace();
						  System.out.println("You decide to not wait for the lamb, and leave the establishment");
						  streetComplete = false;
						  break;
					   }		              	              				    	          	             	       						
				}	         	 
                   break;
                   case 3:
                   adventureDemo.FlushSpace();
                   System.out.println("One of the most beloved restaurants in town, this classic diner contains everyone's favorites!");
                   System.out.println("What would you like to order?");
                   System.out.println("1: Cheeseburger with fries");
                   System.out.println("2: Country Fried Steak");
                   System.out.println("3: Hearty Sausage Skillet with Eggs");
                
                   int USFoodChoice = scr.nextInt();
                
                   switch(USFoodChoice){
					case 1, 2, 3:
					adventureDemo.FlushSpace();
					System.out.println("As you are waiting for your order you hear a commotion behind you");
					System.out.println("You turn around and see that a man is arguing with a waitress");
					System.out.println("The argument appears to be about a wrong order, but it looks like it is getting heated");
					System.out.println("Even the man's dog is getting riled up");
					System.out.println("What do you want to do?");
					System.out.println("1: Approach the two and tell the man to stop");
					System.out.println("2: Ignore it and wait for your food");
					
					int customerChoice = scr.nextInt();
					
					switch(customerChoice){
						case 1:
						adventureDemo.FlushSpace();
						System.out.println("You approach the man and ask him to tone it down a bit");
						System.out.println("He gets up and asks you if you want a piece of him");
						System.out.println("What now?");
						System.out.println("1: Apologize and return to your table");
						System.out.println("2: Check Items");
						System.out.println("3: Call him fat");
						
						int angryManChoice = scr.nextInt();
						
						switch(angryManChoice){
							case 1:
							adventureDemo.FlushSpace();
							System.out.println("Scared, you get on your knees and apologize frantically to the man");
							System.out.println("Seeing you shaking and crying, the man feels bad for you and says to just forget it");
							System.out.println("You return to your table, put on your bib and eat your meal");
							System.out.println("Afterwards, you pay and leave, going back to the streets");
						    streetComplete = false;
						    break;
						    case 2:
						    adventureDemo.FlushSpace();
							int itemChoice = adventureDemo.useItem(player);
					        switch(itemChoice){
								case 1:
								adventureDemo.FlushSpace();
								System.out.println("You pull out a piece of canned food");
								System.out.println("Confused, the angry man feels insulted and breaks open the can");
								System.out.println("He then proceeds to force feed the contents to you in a cartoonish and humorous fashion");
								System.out.println("Humiliated, you break into a fetal position on the floor and start sobbing quietly to yourself");
								System.out.println("Yeah, it looks like you're not recovering from that, physically and mentally");
								System.out.println("Press ENTER to continue.");
					            try{System.in.read();}
					            catch(Exception e){}
					            adventureDemo.endGame(player);
					            break;
					            case 2:
					            adventureDemo.FlushSpace();
					            System.out.println("You take your stick and throw it at the man");
					            System.out.println("You miss and it goes out the window into the street");
					            System.out.println("The man's dog barks excitedly and chases after the stick");
					            System.out.println("The man immediately runs out of the restaurant to chase after the dog");
					            System.out.println("That should not have worked, but you've chased the disgruntled officer away");
					            System.out.println("You then return to your table to finish your meal");
					            System.out.println("");
					            System.out.println("Suddenly a man walks up to you");
					            System.out.println("He introduces himself as the commanding officer of the soldier you just chased off");
					            System.out.println("Impressed by your combat skill, he makes you an offer");				           
					            System.out.println("He challenges you to catch a drug lord his unit had been pursuing in South America and has fled to the city");
					            System.out.println("He says if you succeed, you will be rewarded handsomely");
					            System.out.println("Accept quest? Y/N?");
					            
					            char bountyQuest = scr.next().charAt(0);
					            
					            switch(bountyQuest){
									case 'Y', 'y':
									adventureDemo.FlushSpace();
									System.out.println("You accept the assignment and are escorted by helicopter to the culprit's last known location");
									System.out.println("Press ENTER to comtinue");
									try{System.in.read();}
							        catch(Exception e){}
							        adventureDemo.drugBustQuest(adventureDemo, player);
									streetComplete = true;
									break;
									case 'N', 'n':
									adventureDemo.FlushSpace();
									System.out.println("You politely decline and leave the restaurant");
									System.out.println("You return to the street");
									streetComplete = false;
									break;																					    							
				                    }				    
				                break;
				                case 3:
				                adventureDemo.FlushSpace();
				                System.out.println("You pull out your firearm on the man");
				                System.out.println("This might've been a good idea to intimidate the man...");
				                System.out.println("...If he wasn't a Navy SEAL");
				                System.out.println("You are laid out faster than you can count to one");
				                System.out.println("You lapse into a coma, nobody knowing if you will recover from your epic beating");
				                System.out.println("Press ENTER to continue");			    
					            adventureDemo.endGame(player);
					            break;
					            case 4:
					            adventureDemo.FlushSpace();
					            System.out.println("You pull out your phone and call the police");
					            System.out.println("The cops arrive at the scene and attempt to arrest the man for public disturbance");
					            System.out.println("The agitated customer proceeds to use his Navy SEAL training to vanquish the officers");
					            System.out.println("Scared, you make a run for it in the commotion, not looking back, going home and crying yourself to sleep that night");
					            System.out.println("Never again will you involve yourself in conflict");
					            System.out.println("Your days as a protagonist are over");
					            System.out.println("Press ENTER to continue");		            
					            adventureDemo.endGame(player);
					            break;				                
                                }
                            break;
                            case 3:
                            adventureDemo.FlushSpace();
                            System.out.println("Being the tactical genius and great compromiser that you are, you call this hulking man's appearance");
                            System.out.println("Prous of yourself, you are then immediately knocked out by probably the angriest punch this man has ever thrown");
                            System.out.println("Your backwards logic has once again become your hubris");
                            System.out.println("Press ENTER to continue");
				            try{System.in.read();}
					        catch(Exception e){}
					        adventureDemo.endGame(player);}
					        break;
					     case 2:
                         adventureDemo.FlushSpace();
                         System.out.println("You ignore the commotion and return to your table");
                         System.out.println("You enjoy your All American meal, pay and leave, not looking back at the noisy commotion inside");
                         System.out.println("As you return to the streets, you wonder if you could've done anything");
                         streetComplete = false;
                         break;
	                     }                     
                        }
                   break;
                   case 4:
                   adventureDemo.FlushSpace();
                   System.out.println("Chinese is one of your favorite cuisines, so it's no wonder you would choose this place");
                   System.out.println("You love for sweet and spicy meat, and favorites such as fried rice get you excited for your meal");
                   System.out.println("What would you like to order?");
                   System.out.println("1: Orange Chicken with Egg Fried Rice");
                   System.out.println("2: Fried Pork with Vegetbale Lo Mein");
                   System.out.println("3: Chop Suey with Iced Tea");
                
                   int chinaFoodChoice = scr.nextInt();
                
                   switch(chinaFoodChoice){
					case 1, 2, 3:
					adventureDemo.FlushSpace();
					System.out.println("As you wait for your meal, you hear a commotion in the back");
					System.out.println("You peek over the kitchen and see a fight going on");
					System.out.println("One of the chefs is being attacked by his co workers");
					System.out.println("One of the attackers holds a meat cleaver in his hand");
					System.out.println("What will you do?");
					System.out.println("1: Interfere");
					System.out.println("2: Ignore it and wait for your meal");
					
					int chinaConflictChoice = scr.nextInt();
					
					switch(chinaConflictChoice){
						case 1:
						adventureDemo.FlushSpace();
						System.out.println("You decide to rush into the kitchen hoping to end the conflict");
						System.out.println("As you enter, the attackers look towards you with hostility");
						System.out.println("What will you do?");
						System.out.println("1: Try to compromise with the men");
						System.out.println("2: Check Items");
						System.out.println("3: Call them fat");
						
						int chinaConflictTwo = scr.nextInt();
						
						switch(chinaConflictTwo){
							 case 1:
							 adventureDemo.FlushSpace();
							 System.out.println("You attempt to reason with the hostile men");
							 System.out.println("You try to talk them out of their actions and work out an alternative solution");
							 System.out.println("What shall you propose as the alternative solution?");
							 System.out.println("1: Ask why they are attacking the man to determine their motive and come up with a more verbal solution");
							 System.out.println("2: \"Let's all sing our favorite song from Animaniacs!\" ");
							 
							 int chinaSolution = scr.nextInt();
							 
							 switch(chinaSolution){
								 case 1:
								 adventureDemo.FlushSpace();
					             System.out.println("You ask why the men are doing this and ask if they can just talk about it");
					             System.out.println("The leader of the pack is having none of it and orders the men to attack");
					             System.out.println("Before you can defend yourself, the men are quickly on you, beeating you until you cry for your mommy");
					             System.out.println("They leave your battered self on the floor, and begin to regret your life choices as you suck on your thumb");
					             System.out.println("Press ENTER to continue");
					             try{System.in.read();}
					             catch(Exception e){}
					             adventureDemo.endGame(player);
					             break;
					             case 2:
					             adventureDemo.FlushSpace();
					             System.out.println("Believing that this will actually work, you put on the dumbest smile as you are beaten senselessly into the floor");
					             System.out.println("The assailants beat you into unconsciousness while one stays behind and recites \"Wakko's America\"");
					             System.out.println("Press ENTER to continue");
					             try{System.in.read();}
					             catch(Exception e){}
					             adventureDemo.endGame(player);
					             break;		
							 }			
                              case 2:
                              adventureDemo.FlushSpace();
                              int itemChoice = adventureDemo.useItem(player);
					          switch(itemChoice){
								  case 1:
								  adventureDemo.FlushSpace();
								  System.out.println("You pull out the canned food on the men");
								  System.out.println("Confused, they smack it out of your hand and beat the daylights out of you");
								  System.out.println("Worst of of all, after they're done, they set off with your canned food");
								  System.out.println("Now you are defeated and starved");
								  System.out.println("Press ENTER to continue");
								  try{System.in.read();}
					              catch(Exception e){}
					              adventureDemo.endGame(player);
					              break;					              
					              case 2:
					              adventureDemo.FlushSpace();
					              System.out.println("You pull out your trusty stick in defiance against the group of hostiles");
					              System.out.println("It is immediately broken by the men");
					              System.out.println("I don't think you need to be told what happens next");
					              System.out.println("Once again your tactical genius has led to your downfall");
					              System.out.println("Press ENTER to continue");
					              try{System.in.read();}
					              catch(Exception e){}
					              adventureDemo.endGame(player);
					              break;		
					              case 3:
					              adventureDemo.FlushSpace();
					              System.out.println("You pull out your firearm on the men, believing they will back down at the sight of the weapon");
					              System.out.println("However, in a split second, one of the men crane kicks your hand with lightning speed");
					              System.out.println("The firearm is knoecked off and falls to the floor");
					              System.out.println("Your response to this unexpected event is a simple \"Oh.\" followed by a merciless beating");
					              System.out.println("Press ENTER to continue");
					              try{System.in.read();}
					              catch(Exception e){}
					              adventureDemo.endGame(player);
					              break;
					              case 4:
					              adventureDemo.FlushSpace();
					              System.out.println("You pull out your phone and threaten to call the police on the men");
					              System.out.println("Although it seems that they can beat you before you call, they seem to look very worried");
					              System.out.println("Immdediately, the assailants flee the restaurant, and you help the victim to his feet.");
					              System.out.println("He thanks you, but informs you that now neither of you two are now safe");
					              System.out.println("He informs you that the attackers were not chefs but members of the Pink Tiger Clan");
					              System.out.println("He tells you they are a ruthless organization that is very secretive of their identities");
					              System.out.println("This is why they were concerned when you threatened to bring police/witnesses to the scene");
					              System.out.println("Impressed by your combat skills and tactical aptitude, he asks if you can perhaps do something about them");
					              System.out.println("He asks that you find a way to expose their identities and force them to retreat to the shadows once and for all");
					              System.out.println("Will you accept this quest? Y/N");
					              
					              char clanQuest = scr.next().charAt(0);
					              
					              switch(clanQuest){
									case 'Y', 'y':
									adventureDemo.FlushSpace();
									System.out.println("You offer your help and set out to find more about the Pink Tiger Clan");
									System.out.println("Because they are so secretive, you do not have any info on them");
									System.out.println("Perhaps asking around will do you some luck");
									streetComplete = false;
									break;
									case 'N', 'n':
									adventureDemo.FlushSpace();
									System.out.println("You decline, fearing for your safety");
									System.out.println("You leave the restaurant in a hurry and hope the Pink Tiger Clan will forget about you soon");
									streetComplete = false;
									break; 
									}                        
                                   }
                             break;
                             case 3:
                             adventureDemo.FlushSpace();
                             System.out.println("Boldly, you insult the appearance of one of the more heavy attackers");
                             System.out.println("Unexpectedly, the man panicks and runs out of the room");
                             System.out.println("His cohorts rush out of the restaurant to chase after him");
                             System.out.println("You help the victim up to his feet");
                             System.out.println("He thanks you, but informs you that now neither of you two are now safe");
					         System.out.println("He informs you that the attackers were not chefs but members of the Pink Tiger Clan");
					         System.out.println("He tells you they are a ruthless organization that is very secretive of their identities");
					         System.out.println("This is why they were concerned when you insulted a distinguishable part of the heavy man's identity");
					         System.out.println("Impressed by your combat skills and tactical aptitude, he asks if you can perhaps do something about them");
					         System.out.println("He asks that you find a way to expose their identities and force them to retreat to the shadows once and for all");
					         System.out.println("Will you accept this quest? Y/N");
					         
					         char clanQuestTwo = scr.next().charAt(0);
					         
					         switch(clanQuestTwo){
								case 'Y', 'y':
							    adventureDemo.FlushSpace();
								System.out.println("You offer your help and set out to find more about the Pink Tiger Clan");
								System.out.println("Because they are so secretive, you do not have any info on them");
								System.out.println("Perhaps asking around will do you some luck");
								streetComplete = false;
								break;
								case 'N', 'n':
								adventureDemo.FlushSpace();
								System.out.println("You decline, fearing for your safety");
								System.out.println("You leave the restaurant in a hurry and hope the Pink Tiger Clan will forget about you soon");
								streetComplete = false;
								break;								  
								}
                               }
                        break;
                        case 2:
                        adventureDemo.FlushSpace();
                        System.out.println("Scared for your life, you try your best to ignore the commotion and patiently wait for your meal");
                        System.out.println("The man in the back continues to be attacked, but you eat your meal in silence, pay, and leave");
                        System.out.println("As you wander back into the street, you are overcome with guilt, and feel terribly about the man you left behind");
                        streetComplete = false;
                        break;
                        }
                       }
                      }
                break;
                case 2:
                adventureDemo.FlushSpace();
                System.out.println("You arrive at the only store that is open this late");
                System.out.println("The store owner greets you and asks if you need anything");
                System.out.println("He says he's closed up shop for the night but asks if there is anything else he can do for you");
                System.out.println("Do you need anything? Y/N");
                  
                char storeHelp = scr.next().charAt(0);
                    
                switch(storeHelp){
			       case 'Y', 'y':
				   adventureDemo.FlushSpace();
				   System.out.println("He asks what do you want to know about");
				   System.out.println("");
				   System.out.println("(Use _ instead of space for your entries and capitalize the first letter of your words)");
						
				   String storeTopic = scr.next();
						
						switch(storeTopic){
							case "Bratoni_Family":
							case "The_Bratonis":
							case "The_Bratoni_Family":
							adventureDemo.FlushSpace();
							System.out.println("\"Ah, if you ain't the first to ask about those Bratonis\"");
							System.out.println("\"They're known to cause a lot of trouble\"");
							System.out.println("\"But I've got a nephew in cohorts with em, so I know a few things\"");
							System.out.println("\"Their boss's name is Stimpy, and the code to get into their hideout is Prosciutto\"");
							System.out.println("\"That's all I know about em\"");
							System.out.println("You thank him for his time and leave the store");
							System.out.println("Hopefully you gained some important info");
							streetComplete = false;
							break;
							case "Missing_Boy":
							case "Missing_Son":
							adventureDemo.FlushSpace();
							System.out.println("\"Ah, I've heard about a missing son of a local cook, sweet man, but what was his name again?\"");
							
							String fatherOfBoy = scr.next();
							
							switch(fatherOfBoy){
								case "Ali":
								case "ali:":
								adventureDemo.FlushSpace();
							    System.out.println("\"Yeah that was it! Ali, nice guy, great cook as well\"");
							    System.out.println("\"Yeah I heard about his son missing, but I'm no detective, so I don't have any info about the boy's disappearance");
							    System.out.println("\"Well  actually I do know that the lad's name is Mason\"");
							    System.out.println("You thank him for his time and leave the store");
							    System.out.println("Hopefully you gained some important info");
							    streetComplete = false;
							    break;
							}
							break;
							case "Pink_Tiger_Clan":
							case "The_Pink_Tiger_Clan":
							adventureDemo.FlushSpace();
							System.out.println("The man's eyes widen and looks around in a paranoid fashion");
							System.out.println("\"I suggest you ask somewhere else boy, I'd like to keep my mouth attatched to my face thank you very much");
							System.out.println("He hurries you out of the store, back into the street");
							System.out.println("Looks like you couldn't get any useful info from the store");
							streetComplete = false;
							break;
							default:
							adventureDemo.FlushSpace();
							System.out.println("\"Sorry son, I don't know anything about that\"");
							System.out.println("You thank him for his time and leave the store");							
							break;
                           }
                   break;
                   case 'N', 'n':
                   adventureDemo.FlushSpace();
                   System.out.println("You say you don't need to ask for any info and leave, returning to the streets");
                   }
               break;
               case 3:
               adventureDemo.FlushSpace();
               System.out.println("Out this late there are few people out along with you");
               System.out.println("1: A young lady walking her dog");
               System.out.println("2: A thuggish looking young man in a leather jacket");
               System.out.println("3: A hobo in a giant chicken suit");
               System.out.println("Who will you talk to?");
               
               int whichPerson = scr.nextInt();
               
               switch(whichPerson){
				   case 1:
				   adventureDemo.FlushSpace();
				   System.out.println("You approach the young lady, asking her if she can help you with something");
				   System.out.println("She asks you what is it you need help with");
				   System.out.println("");
				   System.out.println("(Use _ instead of space for your entries and capitalize the first letter of your words)");
				   
				   String girlTopic = scr.next();
				   
				   switch(girlTopic){
					  case "Pink_Tiger_Clan":
					  case "The_Pink_Tiger_Clan":
					  adventureDemo.FlushSpace();
					  System.out.println("\"Oh, I think I heard my boyfriend mention that name\"");
					  System.out.println("\"He's always going out with some friends he never tells me about\"");
					  System.out.println("\"One day I follwed him, and saw him go to an old playground at night\"");
					  System.out.println("\"It almost looked like he lifted the slide and just went underground\"");
					  System.out.println("Ask her more about the organization? Y/N");
					  
					  char moreGirlInfo = scr.next().charAt(0);
					  
					  switch(moreGirlInfo){
						   case 'Y', 'y':
						   adventureDemo.FlushSpace();
						   System.out.println("\"I don't think I'm supposed to tell you more, but I don't see what could go wrong\"");
						   System.out.println("\"One of his friends that I have seen appeared to be quite heavy and pretty senstive\"");
						   System.out.println("\"It looked like he was a real scary guy, so I backed off\"");
						   System.out.println("\"Well that's all I got to tell you\"");
						   System.out.println("You thank her for her help and leave");
						   System.out.println("Investigate the playground? Y/N?");
						   
						   char beginClanQuest = scr.next().charAt(0);
						   
						   switch(beginClanQuest){
							   case 'Y', 'y':
							   adventureDemo.FlushSpace();
							   System.out.println("You thank her for the info and make your way to the old playground to investigate");
							   System.out.println("Press ENTER to continue");
							   try{System.in.read();}
					           catch(Exception e){}
							   adventureDemo.pinkTigerQuest(adventureDemo, player);
							   break;
							   case 'N', 'n':
							   adventureDemo.FlushSpace();
							   System.out.println("Thinking perhaps you can get more info, you choose not to go to the playground just yet");
							   System.out.println("You thank her for he time and return to the streets");
							   streetComplete = false;
							   break;
						   }
						   break;			
						   case 'N', 'n':
						   adventureDemo.FlushSpace();
						   System.out.println("You thank her for your time and return to the streets");
						   streetComplete = false;
						   break;
					   }
					   break;
				       default:
				       adventureDemo.FlushSpace();
				       System.out.println("Sorry hon, I don't know anything about that");
				       System.out.println("You apologize and thank her for her time, you then proceed back to the streets");
				       streetComplete = false; 
				       break;
				       }
                   break;
                   case 2:
                   adventureDemo.FlushSpace();
                   System.out.println("\"What do you want dweeb?\"");
                   System.out.println("(Use _ instead of space for your entries and capitalize the first letter of your words)");
                      
                   String punkTopic = scr.next();
                    
                    switch(punkTopic){
						case "Bratoni_Family":
						case "The_Bratoni_Family":
						case "The_Bratonis":
						adventureDemo.FlushSpace();
						System.out.println("\"Oh those guys? Heck if I know about them, but their boss likes musicals I heard, so they can't be a tough bunch\"");
						System.out.println("\"Now get out of my face shrimp, if you know what's good for ya\"");
						System.out.println("You leave the young man alone and wander back into the streets");
						System.out.println("Hopefully what he told you will be useful in your journey");
						break;
						default:
						adventureDemo.FlushSpace();
						System.out.println("\"I don't know squat about that, now leave me alone before I get angry...and you wouldn't like me when I'm angry\"");
						System.out.println("You hurry out and return to the streets");
						streetComplete = false;
						break; 
						}
                   break;
                   case 3:
                   adventureDemo.FlushSpace();
                   System.out.println("You are perplexed by the appearance of this man, but you consider he may have good info since he spends the day wandering the streets");
                   System.out.println("\"Hey Sonny! It is I, the smartest man in the world! The Trivia Master! I can tell you anything!\"");
                   System.out.println("\"What would you like to know about Sonny?\"");
                   
                   String hoboTopic = scr.next();
                   
                   switch(hoboTopic){
					   case "Bratoni_Family":
					   case "The_Bratoni_Family":
					   case "The_Bratonis":
					   case "Missing_Boy":
					   case "Missing_Son":
					   case "Pink_Tiger_Clan":
					   case "The_Pink_Tiger_Clan":
					   adventureDemo.FlushSpace();
					   System.out.println("\"Sonny, you seem intent on getting some info\"");
					   System.out.println("\"My knowledge doesn't come for free lad\"");
					   System.out.println("\"Answer my questions and I shall answer yours!\"");
					   System.out.println("\"First: The Investiture Controversy was a disagreement between what two medieval European leaders?\"");
					   System.out.println("Hint: one religious leader and one monarch");
					   System.out.println("");
					   System.out.println("Capitalize all the first letters of each word, including words like and, or, if, and use _ instead of space, use I and V for roman numerals");
					   
					   String questionOne = scr.next();
					   
					   switch(questionOne){
						   case "Pope_Gregory_VII_And_Henry_IV":
						   case "Gregory_VII_And_Henry_IV":
						   case "Henry_IV_And_Pope_Gregory_VII":
						   case "Henry_IV_And_Gregory_VII":
						   adventureDemo.FlushSpace();
						   System.out.println("\"Correct!\"");
						   System.out.println("\"Second: Which U.S. president had the shortest term in his country's history?\"");
						   
						   String questionTwo = scr.next();
						   
						   switch(questionTwo){
							   case "William_Henry_Harrison":
							   case "President_William_Henry_Harrison":
							   adventureDemo.FlushSpace();
							   System.out.println("\"Correct!\"");
							   System.out.println("\"Final Question: What is the third song played and sung in the movie Rocketman?\"");
							   
							   String questionThree = scr.next();
							   
							   switch(questionThree){
								    case "Saturday_Night's_All_Right_For_Fighting":
								    case "saturday_night's_all_right_for_fighting":
								    adventureDemo.FlushSpace();
								    System.out.println("\"You did it sonny! Now as promised, what I do know is...\"");
								    System.out.println("\"wait I think I'm having a stroke\"");
								    System.out.println("The man falls to the floor immediately, the few onlookers rush and call for help");
								    System.out.println("Before the hobo is swarmed, a pice of paper fallf out of his pocket");
								    System.out.println("You pick up the paper and notice it reads: 137618666");
								    System.out.println("You return to the streets");
								    streetComplete = false;
								    break;
								    default:
								    adventureDemo.FlushSpace();
								    System.out.println("\"Wrong sonny!\"");
								    System.out.println("\"Now go on, get, you can't have any of my info sonny, you ain't smart enough\"");
								    System.out.println("Disappointed, you return to the streets with no new info");
								    streetComplete = false;
								    break;
								    }
                                break;
                                default:
								adventureDemo.FlushSpace();
								System.out.println("\"Wrong sonny!\"");
								System.out.println("\"Now go on, get, you can't have any of my info sonny, you ain't smart enough\"");
								System.out.println("Disappointed, you return to the streets with no new info");
								streetComplete = false;
								break;  
								}  
                           break;
                           default:
                           adventureDemo.FlushSpace();
                           System.out.println("\"Wrong sonny!\"");
				           System.out.println("\"Now go on, get, you can't have any of my info sonny, you ain't smart enough\"");
					       System.out.println("Disappointed, you return to the streets with no new info");
						   streetComplete = false;
						   break;  
						   }
                       break;
                       default:
                       adventureDemo.FlushSpace();
                       System.out.println("\"Ok I don't really know EVERYTHING sonny, try asking me something about the streets\"");
                       System.out.println("You tell him to just forget it and you retirn to the streets");
                       streetComplete = false;
                       break;
                       }
                   break;
                   default:
                   adventureDemo.FlushSpace();
                   System.out.println("Deciding not to talk to one of the people, you return to the streets");
                   streetComplete = false;
                   break;
                   }
               break;
               case 4:
               adventureDemo.FlushSpace();
               System.out.println("Why would you want to go home? Adventure awaits you!");
               streetComplete = false;
               break;
               }
  }
 }
    public void drugBustQuest(AdventureDemo adventureDemo, Player player){
      System.out.println("You are taken to the front of an abandoned warehouse");
      System.out.println("It seems like nobody is inside, but you hear some noise coming from the building");
      System.out.println("Enter the building from the Front or Back?");
      
      String warehouseEntrance = scr.next();
      
      switch(warehouseEntrance){
          case "Front":
		  case "The_Front":
		  adventureDemo.FlushSpace();
		  System.out.println("You approach the main entrance to see two big guard dogs");
		  System.out.println("The two dogs seem vicious and eager to attack anyone who approaches");
		  System.out.println("Like a weird earthly cerberus or something like that");
		  System.out.println("What will you do?");
		  System.out.println("1: Try to go around them");
		  System.out.println("2: Check Items");
		  
		  int dogsChoice = scr.nextInt();
		  
		  switch(dogsChoice){
			  case 1:
			  adventureDemo.FlushSpace();
			  System.out.println("You attempt to go around the guard dogs");
			  System.out.println("However, they immediately follow and cut you off, now even closer to you");
			  System.out.println("They seem prine and ready to attack");
			  System.out.println("What will you do now?");
			  System.out.println("1: Punch the dogs");
			  System.out.println("2: Back off");
			  
			  int dogsChoiceTwo = scr.nextInt();
			  
			  switch(dogsChoiceTwo){
				  case 1:
				  adventureDemo.FlushSpace();
				  System.out.println("You throw a blind punch at one of the dogs");
				  System.out.println("In a lucky shot you bop him right on the snout");
				  System.out.println("Hit in a quite sensitive spot, the dog begins to whimper and runs off");
				  System.out.println("His loyal friend follows him as well");
				  System.out.println("Flabbergasted at how that worked, you proceed to enter the warehouse and confront the crime boss");
				  System.out.println("Press ENTER to continue");
				  try{System.in.read();}
	              catch(Exception e){}
				  adventureDemo.warehouse(adventureDemo, player);
				  break;
				  case 2:
				  adventureDemo.FlushSpace();
				  System.out.println("You try to slowly back away and avoid the dogs");
				  System.out.println("However, their bloodlust has already reached its height");
				  System.out.println("As you back away, the dogs jump and maul you");
				  adventureDemo.endGame(player);
			      break;
			      }
                 break;
                 case 2:
                 adventureDemo.FlushSpace();
				 int itemChoice = adventureDemo.useItem(player);
				 switch(itemChoice){
					 case 1:
					 adventureDemo.FlushSpace();
					 System.out.println("You throw a piece of canned food on the floor hoping to distract the dogs");
					 System.out.println("It works! Soon the dogs are distracted fighting over who gets the canned food");
					 System.out.println("Maneuvering around the fighting dogs, you enter the warehouse");
					 System.out.println("Press ENTER to continue");
					 try{System.in.read();}
	                 catch(Exception e){}
				     adventureDemo.warehouse(adventureDemo, player);
				     break;
				     case 2:
				     adventureDemo.FlushSpace();
				     System.out.println("You throw your stick at the dogs");
				     System.out.println("You think to yourself \"Dogs love sticks! They'll be so distracted by this I'll be able to sneak past them\"");
				     System.out.println("They are not those kind of dogs");
				     System.out.println("They look down at the stick for no more than a second, and then maul you");
				     adventureDemo.endGame(player);
			         break;
			         }
                    }
          break;
          case "Back":
          case "The_Back":
          adventureDemo.FlushSpace();
          System.out.println("You attempt to enter the warehouse from its back entrance");
          System.out.println("Nobody is there, but there is a lock on the door");
          System.out.println("It requires a combination of some sort to open");
          
          int lockComb = scr.nextInt();
          
          switch(lockComb){
			  case 137618666:
			  adventureDemo.FlushSpace();
			  System.out.println("You enter the correct combination, unlock the door and enter the warehouse");
			  System.out.println("Press ENTER to continue");
			  try{System.in.read();}
			  catch(Exception e){}
			  adventureDemo.warehouse(adventureDemo, player);
			  break;
			  default:
			  adventureDemo.FlushSpace();
			  System.out.println("Entering the wrong combination, you are electrocuted by the door");
			  System.out.println("As you lay shocked and paralyzed on the floor, an alarm goes off and dozens of armed men rush to your position");
			  System.out.println("There is nothing more you can do");
			  adventureDemo.endGame(player);
			  break;
			  }
             }
 
 }
 public void warehouse(AdventureDemo adventureDemo, Player player){
	  System.out.println("You enter the warehouse, but it is pitch black with no light whatsoever");
	  System.out.println("Suddenly the lights flash in a blinding light and all in the warehouse is made visible");
	  System.out.println("You see the crime boss, whose name--Mucho, provided to you by your recruiter, is on top of a balcony overlooking you");
	  System.out.println("With him are his henchmen, all pointing their weapons at you");
	  System.out.println("He yells \"I've been expecting you! The army thinks they can send one measly errand boy to take ME down?\"");
	  System.out.println("\"What will you do now errand boy?\"");
	  System.out.println("1: Run to the corner of the room");
	  System.out.println("2: Run towards the balcony");
	  
	  int warehouseBattle = scr.nextInt();
	  
	  switch(warehouseBattle){
		  case 1:
		  adventureDemo.FlushSpace();
		  System.out.println("You run toward the corner of the building, dodging the shots of the henchmen and see a rope connecting the balcony");
		  System.out.println("What will you use?");
		  adventureDemo.FlushSpace();
		  int itemChoice = adventureDemo.useItem(player);
		  switch(itemChoice){
			  case 1:
			  adventureDemo.FlushSpace();
			  System.out.println("You are not sure what to do with this");
			  System.out.println("While you stand there confused, you are shot down by the henchmen");
			  adventureDemo.endGame(player);
			  break;
			  case 2:
			  adventureDemo.FlushSpace();
			  System.out.println("You try to use the stick to cut the rope");
			  System.out.println("However the stick is not sharp enough");
			  System.out.println("What will you use?");
			  adventureDemo.FlushSpace();
		      int itemChoice2 = adventureDemo.useItem(player);
		      switch(itemChoice2){
				  case 1:
				  adventureDemo.FlushSpace();
				  System.out.println("You poke a hole in the can's tin top to make a sharp edge");
				  System.out.println("You use the edge to sharpen the stick and cut the rope");
				  System.out.println("The balcony falls along with Mucho and his men");
				  System.out.println("The henchmen are unconscious, but Mucho is still standing");
				  System.out.println("\"So you are a crafty adversary, very well, now let us see your physical aptitude!\"");
				  System.out.println("He pulls out a large hunting knife and looks prone to attack you");
				  System.out.println("The final battle is now at hand");
				  System.out.println("Press ENTER to continue");
				  try{System.in.read();}
			      catch(Exception e){}
			      adventureDemo.muchoBattle(adventureDemo, player);
			      break;
			      default:
			      adventureDemo.FlushSpace();
			      System.out.println("You are not sure how this item can help sharpen the stick");
			      System.out.println("While you stand there confused, you are shot down by the henchmen");
			      adventureDemo.endGame(player);
			      break;
			      }
                 }
          break;
          case 2:
          adventureDemo.FlushSpace();
          System.out.println("You run straigh towards the balcony");
          System.out.println("However, you are now an easy shot for the henchmen to hit by running straight at them");
          System.out.println("Without hesitation they immediately gun you down");
          adventureDemo.endGame(player);
          break;
          }
       }

 public void muchoBattle(AdventureDemo adventureDemo, Player player){
	  adventureDemo.FlushSpace();
	  System.out.println("Poised and ready for battle, he takes a swing at you, swiping at from your left side");
	  System.out.println("1: Block");
	  System.out.println("2: Dodge");
	  System.out.println("3: Counter");
	  
	  int actionOne = scr.nextInt();
	  
	  switch(actionOne){
		  case 1:
		  adventureDemo.FlushSpace();
		  System.out.println("You block his extended arm with yours, preventing the knife from reaching you");
		  System.out.println("He yanks back and attacks with a stabbing motion");
		  System.out.println("1: Block");
	      System.out.println("2: Dodge");
	      System.out.println("3: Counter");
	      
	      int actionTwo = scr.nextInt();
	      
	      switch(actionTwo){
			  case 2:
			  adventureDemo.FlushSpace();
			  System.out.println("You move to the side and easily dodge his straight stab");
			  System.out.println("He keeps his arm extended from the stab and attempts to swipe once again while you are suspended in a dodge movement");
			  System.out.println("1: Block");
	          System.out.println("2: Dodge");
	          System.out.println("3: Counter");
	          
	          int actionThree = scr.nextInt();
	          
	          switch(actionThree){
				  case 3:
				  adventureDemo.FlushSpace();
				  System.out.println("You use your free hands to catch the knife and hold it so Mucho cannot pull it out");
				  System.out.println("He is now stuck trying to pull the knife out");
				  System.out.println("1: Block");
	              System.out.println("2: Dodge");
	              System.out.println("3: Counter");
	              
	              int actionFour = scr.nextInt();
	              
	              switch(actionFour){
					  case 3:
					  adventureDemo.FlushSpace();
					  System.out.println("While he is stuck you take a short leap up and deliver a crushing crane kick straight to his face");
					  System.out.println("Mucho falls to the floor, defeated");
					  System.out.println("Soon after, the army and the man who recruited you in the restaurant shoe up to arrest the crime boss");
					  System.out.println("You are made into a national hero, and live the rest of your life in fame and luxury");
					  System.out.println("Yeah, life is good");
					  adventureDemo.gameComplete(player);
					  break;
					  default:
					  adventureDemo.FlushSpace();
					  System.out.println("Suspended by your own counter, you cannot do this");
					  System.out.println("Mucho pulls the knife out and cuts you up in your shock");
					  adventureDemo.endGame(player);
					  break;
					  }
                   break;
                   default:
                   adventureDemo.FlushSpace();
                   System.out.println("You are unable to perform this as you are already moving to dodge his stab");
                   System.out.println("His slash reaches you, and you fall to the ground");
                   System.out.println("Mucho takes advantage of this moment of weakness to finish you off");
                   adventureDemo.endGame(player);
                   break;
                   }
              break;
              default:
              adventureDemo.FlushSpace();
              System.out.println("This action prevents you from getting out of the way of his stab");
              System.out.println("His blade reaches your inside and Mucho uses this show of weakness to finish you off");
              adventureDemo.endGame(player);
              break;
              }
          break;
          default:
          adventureDemo.FlushSpace();
          System.out.println("His slash prevents you from dodging to the side or countering from the front, and his blade reaches your face");
          System.out.println("You fall to the floor, writhing in pain");
          System.out.println("Mucho uses this show of weakness to finish you off");
          adventureDemo.endGame(player);
          break;
          }
         }
 
 public void bratoniQuest(AdventureDemo adventureDemo, Player player){
	 adventureDemo.FlushSpace();
	 System.out.println("You wander the alleys thinking this is the most likely place to find the Bratonis");
	 System.out.println("Suddenly you are jumped by a mysterious man in black");
	 System.out.println("He demands everything in your pocket and pulls out a pocket knife");
	 System.out.println("What will you do?");
	 System.out.println("1: Fight back");
	 System.out.println("2: Run away");
	 System.out.println("3: Check Items");
	 
	 int mugChoice = scr.nextInt();
	 
	 switch(mugChoice){
		 case 1:
		 adventureDemo.FlushSpace();
		 System.out.println("You rush towards the mugger and grab a hold of his knife");
		 System.out.println("As you grab the knife, you and him struggle for control of it");
		 System.out.println("And it looks like he's winning");
		 System.out.println("What will you do now?");
		 System.out.println("1: Headbutt him");
		 System.out.println("2: Kick him in his gonads");
		 
		 int mugChoiceTwo = scr.nextInt();
		 
		 switch(mugChoiceTwo){
			 case 1:
			 adventureDemo.FlushSpace();
			 System.out.println("You attempt to use a part of your body not being used in the struggle and headbutt the mugger");
			 System.out.println("However, his head is very hard and you pull you head back in pain");
			 System.out.println("Unfortunately people who make a living mugging others probably aren't the smartest people");
			 System.out.println("And a dumb assailant equals hard heads");
			 System.out.println("While you writhe in pain, the angered mugger drives his knife into you");
			 System.out.println("He leaves you on the ground and runs off with all your belongings");
			 adventureDemo.endGame(player);
			 break;
			 case 2:
			 adventureDemo.FlushSpace();
			 System.out.println("With your hands being used to grab the knife, you use you lower half to fight back");
			 System.out.println("You deliver a swift knee to the man's groin, he lets out high pitched moan of pain, and falls to the floor");
			 System.out.println("You grab his knife and hold it up to him, asking if he knows anything about the Bratoni Family");
			 System.out.println("He says that he used to work for them, but was kicked out for being too aggressive with civilians");
			 System.out.println("He says he remembers the top brass hung out at a small bar in the basement of a family restaurant at the end of the alley");
			 System.out.println("But he says he was low level when he was with the family, so he doesn't know the important stuff like the code to get in");
			 System.out.println("You leave the man on the floor and proceed to the restaurant to confront the Bartonis");
			 System.out.println("Press ENTER to continue");
			 try{System.in.read();}
			 catch(Exception e){}
			 adventureDemo.bratoniBar(adventureDemo, player);
			 break;
		 }
            break;
            case 2:
            adventureDemo.FlushSpace();
            System.out.println("You attempt to run away thinking this man is too dangerous for you to handle");
            System.out.println("However, this mugger is used to dealing with cowards");
            System.out.println("He easily catches up with almost athletic speed and knocks you out on the back of your head");
            System.out.println("He leaves you on the floor and takes all your belongings");
            adventureDemo.endGame(player);
            break;
            case 3:
            int itemChoice = adventureDemo.useItem(player);
		    switch(itemChoice){
				case 1, 2:
				adventureDemo.FlushSpace();
				System.out.println("This are not sure how this item can help you in a mugging situation");
				System.out.println("In your confusion the mugger you and cuts you up, taking your belongings afterwards");
				adventureDemo.endGame(player);
				break;
				case 3:
				adventureDemo.FlushSpace();
				System.out.println("You pull out your firearm to use against the man");
				System.out.println("The man's eyes widen, and he begins to sweat profusely");
				System.out.println("It seems you've got him in a corner");
				System.out.println("However, in you blind moment of glory, in a split second, he pulls out his own firearm and guns you down before you can retaliate");
				adventureDemo.endGame(player);
				break;
				case 4:
				adventureDemo.FlushSpace();
				System.out.println("You pull out your phone and threaten to call the cops on the man");
				System.out.println("You remind him that you're a fast talker and can stay alive long enough to give the cops an accurate description of the man");
				System.out.println("The mans tells you he's already got warrants out for him and tries to compromise");
				System.out.println("You ask for info about the Bratonis and you will let him go if he complies");
				System.out.println("He says that he used to work for them, but was kicked out for being too aggressive with civilians");
			    System.out.println("He says he remembers the top brass hung out at a small bar in the basement of a family restaurant at the end of the alley");
			    System.out.println("But he says he was low level when he was with the family, so he doesn't know the important stuff like the code to get in");
			    System.out.println("He then leaves and you continue down the alley to the restaurant to confront the Bratonis");
			    System.out.println("Press ENTER to continue");
			    try{System.in.read();}
			    catch(Exception e){}
			    adventureDemo.bratoniBar(adventureDemo, player);
			    break;
			    }
               }
              }
		     
 public void bratoniBar(AdventureDemo adventureDemo, Player player){
	 adventureDemo.FlushSpace();
	 System.out.println("You see the restaurant at the end of the alley and enter it");
	 System.out.println("The restaurant is closed, but you can hear some talking below the floor");
	 System.out.println("After doing some investigating, you find a metal door behind the cupboards in the kitchen");
	 System.out.println("It is locked, but suddenly you hear a voice coming from behind the door");
	 System.out.println("\"What's the password?\"");
	 
	 String bratoniPW = scr.next();
	 
	 switch(bratoniPW){
		 case "Prosciutto":
		 case "prosciutto":
		 adventureDemo.FlushSpace();
		 System.out.println("\"Alright, come on in\"");
		 System.out.println("The door opens and you are escorted through down a flight of stairs to the basement");
		 System.out.println("When you reach the bottom there are multicolored party lights everywhere");
		 System.out.println("Sharply dressed men are hanging out, drinking, and talking");
		 System.out.println("There is an older man in the corner of the room smoking a cigar, guarded by multiple other men");
		 System.out.println("Of of the men gets up and says \"Hey who's this poser?\" pointing at you");
		 System.out.println("Recognizing you as not one of their own, the men in the room all pull out their weapons and point their firearms at you");
		 System.out.println("The older man gets up and goes \"Now hold on a minute everyone\"");
		 System.out.println("\"This could be the delivery boy, remember we can't have a party without the edibles now can we?\"");
		 System.out.println("\"But just to make sure, what name was the order placed to again?\"");
		 
		 String caterName = scr.next();
		 
		 switch(caterName){
			 case "Stimpy":
			 case "stimpy":
			 adventureDemo.FlushSpace();
			 System.out.println("\"Alright boys put yer guns down\"");
			 System.out.println("\"Alright kid, come to the back and lemme pay ya for your services\"");
			 System.out.println("He takes you to his personal office behind the bar");
			 System.out.println("\"Alright so that's 10 orders of fried zucchini, 8 orders of lemon chicken, 7 orders...wait, where's the food kid?\"");
			 System.out.println("1: I left it in the bar");
			 System.out.println("2: My boss forgot to give it to me");
			 System.out.println("3: \"Hey did you watch The Greatest Showman?\"");
			 
			 int coverUp = scr.nextInt();
			 
			 switch(coverUp){
				 case 1:
				 adventureDemo.FlushSpace();
				 System.out.println("\"I had my eyes on youse the whole time you were here, I didn't see you leave anything\"");
				 System.out.println("\"Actually you didn't seem to be CARRYING anything either\"");
				 System.out.println("\"BOYS GET IN HERE!\"");
				 System.out.println("The boss's men rush in and beat you savagely");
				 System.out.println("They bring you into a car in the alley and say they're taking you somewhere where you cant bother anyone no more");
				 System.out.println("The car speeds off, with your chances of survival looking like nothing at this point...");
				 adventureDemo.endGame(player);
				 break;
				 case 2:
				 adventureDemo.FlushSpace();
				 System.out.println("\"You're not the delivery boy are ya?\"");
				 System.out.println("\"Actually you didn't seem to be CARRYING anything either\"");
				 System.out.println("\"BOYS GET IN HERE!\"");
				 System.out.println("The boss's men rush in and beat you savagely");
				 System.out.println("They bring you into a car in the alley and say they're taking you somewhere where you cant bother anyone no more");
				 System.out.println("The car speeds off, with your chances of survival looking like nothing at this point...");
				 adventureDemo.endGame(player);
				 break;
				 case 3:
				 adventureDemo.FlushSpace();
				 System.out.println("\"Did I see it? You're trying to ask if I saw the greatest musical in this tone-deaf generation?\"");
				 System.out.println("\"Of course I saw it!\"");
				 System.out.println("\"Hey what was your favorite song kid?\"");
				 
				 String favShowmanSong = scr.next();
				 
				 switch(favShowmanSong){
					 case "The_Greatest_Show":
					 case "A_Million_Dreams":
					 case "Come_Alive":
					 case "Never_Enough":
					 case "This_Is_Me":
					 case "Rewrite_The_Stars":
					 case "Tightrope":
					 case "From_Now_On":
					 adventureDemo.FlushSpace();
					 System.out.println("\"Haha, good to know there's another youngster in this world that ain't into that loud pop, hip hop junk\"");
					 System.out.println("\"I mean, how can anyone be into that? Sounds like a broken record");
					 System.out.println("\"Alright here's your pay kid\"");
					 System.out.println("You manage to distract the boss from the fact that you didn't bring any food");
					 System.out.println("You leave in a hurry but realize you never talked about lifting the pressure off the owner of Donatello's");
					 System.out.println("You return to Donatello's and give the owner the money the Bratoni boss paid you");
					 System.out.println("He is insured for a bit, but you can't help but feel that this solution was only temporary...");
					 System.out.println("Maybe there was something else you could've said to the boss, something to resonate with him...");
					 adventureDemo.gameComplete(player);
					 break;
					 case "The_Other_Side":
					 adventureDemo.FlushSpace();
					 System.out.println("The boss's jaw drops and his cigar falls from his mouth");
					 System.out.println("He gets up and gives you a big hug");
					 System.out.println("\"That's my favorite too kid!\"");
					 System.out.println("\"Man kid, your boss, he must have a good taste in personality when hiring\"");
					 System.out.println("This reminds you of the plight of the owner of Donatello's");
					 System.out.println("You tell the boss the owner from Donatello's is your boss");
					 System.out.println("\"That old man? He's got more pizzazz then I thought");
					 System.out.println("\"I guess I can lift off the protection charges for him, just cause he has good taste\"");
					 System.out.println("You manage to distract the boss from the fact that you didn't bring any food");
					 System.out.println("Press ENTER to continue");
			         try{System.in.read();}
			         catch(Exception e){}
			         System.out.println("You return to Donatello's and inform the owner of your success");
			         System.out.println("Tearing up, the man embraces you and thanks you for what you've done");
			         System.out.println("You've saved the life of a humble old man and succeeded in your quest");
			         System.out.println("To this humble Italian restaurant, you will forever be known as a great hero, and the reason it still stands today");
			         adventureDemo.gameComplete(player);
			         break;
                     default:
                     adventureDemo.FlushSpace();
                     System.out.println("\"That ain't a song from the movie, kid\"");
                     System.out.println("You stutter as you try to come up with an excuse, but it only confirms the boss's suspicions");
                     System.out.println("\"BOYS GET IN HERE!\"");
				     System.out.println("The boss's men rush in and beat you savagely");
				     System.out.println("They bring you into a car in the alley and say they're taking you somewhere where you cant bother anyone no more");
				     System.out.println("The car speeds off, with your chances of survival looking like nothing at this point...");
				     adventureDemo.endGame(player);
				     break;
				     }
                    }
                   break;
                   default:
                   adventureDemo.FlushSpace();
                   System.out.println("\"That ain't my name kid\"");
                   System.out.println("\"Take him down boys!\"");
                   System.out.println("You are gunned down immediately by the Bratoni henchmen");
                   adventureDemo.endGame(player);
                   break;
                   }
                  break;
                  default:
                  adventureDemo.FlushSpace();
                  System.out.println("There is no response from the voice and a long silence follows");
                  System.out.println("Suddenly you are ambushed from behind and knocked unconscious");       
				  System.out.println("The assailants bring you into a car in the alley and say they're taking you somewhere where you cant bother anyone no more");
				  System.out.println("The car speeds off, with your chances of survival looking like nothing at this point...");
				  adventureDemo.endGame(player);
				  break;
				  }
                 }
 public void pinkTigerQuest (AdventureDemo adventureDemo, Player player){
	 adventureDemo.FlushSpace();
	 System.out.println("You are now at the slide in the old playground the lady told you about");
	 System.out.println("Remembering what she said, you look for the slide and try to lift it");
	 System.out.println("However, it doesn't seem like the slide is lifting");
	 System.out.println("What now?");
	 System.out.println("1: Leave");
	 System.out.println("2: Check Items");
	 System.out.println("3: Lift harder");
	 
	 int slideChoice = scr.nextInt();
	 
	 switch(slideChoice){
		 case 1:
		 adventureDemo.FlushSpace();
		 System.out.println("You decide to leave the playground and see if there's any info you can gather about the slide or the Pink Tigers");
		 adventureDemo.streetEvent(adventureDemo, player);
		 break;
		 case 2:
		 adventureDemo.FlushSpace();
		 int itemChoice = adventureDemo.useItem(player);
		 switch(itemChoice){
			case 1:
			adventureDemo.FlushSpace();
			System.out.println("You consume the canned food and feel a burst of strength coursing through you");
			System.out.println("The nutrients of the can's contents empower you with stamina and strength");
			System.out.println("You still can't lift the slide though");
			try{System.in.read();}
			catch(Exception e){}
			adventureDemo.pinkTigerQuest(adventureDemo, player);
			break;
			case 2:
			adventureDemo.FlushSpace();
			System.out.println("This puny stick will not help you lift this slide");
			try{System.in.read();}
			catch(Exception e){}
			adventureDemo.pinkTigerQuest(adventureDemo, player);
			break;
			case 3:
			adventureDemo.FlushSpace();
			System.out.println("Yeah, shoot the slide why don't you?");
			try{System.in.read();}
			catch(Exception e){}
			adventureDemo.pinkTigerQuest(adventureDemo, player);
			break;
			case 4:
			adventureDemo.FlushSpace();
			System.out.println("Don't see how this phone can help you get around the slide...");
			try{System.in.read();}
			catch(Exception e){}
			adventureDemo.pinkTigerQuest(adventureDemo, player);
			break;
			}
         break;
         case 3:
         adventureDemo.FlushSpace();
         System.out.println("You decide to brute force it and see if you'll eventually lift the slide if you try hard enough");
         System.out.println("You spend about half an hour trying to lift the slide with no results");
         System.out.println("Finally you give up, but it looks like your time wasted might actually have had its benefits");
         System.out.println("Press ENTER to continue");
         try{System.in.read();}
		 catch(Exception e){} 
		 System.out.println("While you were trying to lift the slide, two young men appeared to approach the playground and notice you");
		 System.out.println("One of them looks threatening while the other appears to be unwilling to be here");
		 System.out.println("The threatening one demands to know why you are here and pulls out a firearm on you");
		 System.out.println("What will you do?");
		 System.out.println("1: Tell him you don't want any trouble");
		 System.out.println("2: Check Items");
		 
		 int playgroundEncounter = scr.nextInt();
		 
		 switch(playgroundEncounter){
			  case 1:
			  adventureDemo.FlushSpace();
			  System.out.println("You tell the man that you dont want any trouble");
			  System.out.println("He says that you shouldn't have tried to lift the slide if you didn't want any trouble");
			  System.out.println("He then fires his weapon at you");
			  System.out.println("Press ENTER to continue");
			  try{System.in.read();}
			  catch(Exception e){}
			  System.out.println("Suddenly, the other young man jumps at the armed one, and tries to wrestle the firearm from the threatening man");
			  System.out.println("It appears that he is not an ally of the thug, was he dragged here against his will?");
			  System.out.println("Shots wring out during the scuffle, and you back off from the two to gain some distance");
			  System.out.println("Eventually, the man who saved you successfully wrestles the weapon from the thug and knocks him to the floor");
			  System.out.println("Still confused, the young man dazes off, while the thug pulls out a knife and tries to attack him from behind");
			  System.out.println("You must save him, but how? You are too far to make it, so you must warn him");
			  System.out.println("You try to call his name to warn him, but you do not know it!");
			  System.out.println("Unless...");
			  
			  String allyName = scr.next();
			  
			  switch(allyName){
				case "Mason":
				case "mason":
				adventureDemo.FlushSpace();
				System.out.println("The young man snaps out of his daze upon hearing his name, and you gesture him of the assailant behind him");
				System.out.println("He immediately turns around and fires at the thug, who falls to the floor");
				System.out.println("Upon meeting him, he asks you how you kniw his name");
				System.out.println("You tell him of his father whom had told you about him, that he had been worrie sick about you ever since your disappearance");
				System.out.println("Mason explains he witnessed a meeting between Pink Tiger members when he took a shortcut through an alley while getting groceries for his father's restaurant");
				System.out.println("He says the group kidnapped him and was planning on bringing him to their hideout to face judgement from their leaders");
				System.out.println("You ensure him he is safe now and you both set off to return to Ali's restaurant");
				System.out.println("Press ENTER to continue");
				try{System.in.read();}
			    catch(Exception e){} 
			    System.out.println("Ali is overjoyed to be reunited with his son, and he thanks you for what you've done");
			    System.out.println("You leave the restaurant now a hero to this small family, but Mason's story reminds you of the dangerous group that still roams the streets...");
			    adventureDemo.gameComplete(player);
			    break; 
			    default:
			    adventureDemo.FlushSpace();
			    System.out.println("You fail to snap the young man out of it, and is attacked from behind by the thug, who immediately turns his attention to you");
			    System.out.println("Scared for your life, you book it as fast as you can");
			    System.out.println("This experience has traumatized you so horribly it is unlikely you will seek adventure in the future");
			    adventureDemo.endGame(player);
			    break;
			  }
             break;
             case 2:
             adventureDemo.FlushSpace();
		     int itemChoice2 = adventureDemo.useItem(player);
		     switch(itemChoice2){
				 case 1, 2, 4:
				 adventureDemo.FlushSpace();
				 System.out.println("This will not help you against an armed thug");
				 System.out.println("He fires his weapon at you, and you immediately fall to the floor, vanquished");
				 adventureDemo.endGame(player);
				 break;
				 case 3:
				 adventureDemo.FlushSpace();
				 System.out.println("You pull out your own firearm and just manage to fire before the thug");
				 System.out.println("He falls to the floor, and the encounter scares the other man, who runs off immediately");
				 System.out.println("It doesn't seem like he was an enemy though...");
				 System.out.println("You turn your attention to the wounded aggressor and ask him how to lift the slide");
				 System.out.println("He says that only those with special tattoos may enter, that the slide has a recognition scan that lightens the slide if a tattoo is detected");
				 System.out.println("How are you supposed to give yourself a tattoo?");
				 
		         int itemChoice3 = adventureDemo.useItem(player);
		         switch(itemChoice3){
					 case 4:
					 adventureDemo.FlushSpace();
					 System.out.println("At first you are not sure how this is supposed to help you with your current problem...");
					 System.out.println("Suddenly, you get the idea that the tattoo on your arm doesn't have to be real to be recognized");
	                 System.out.println("You snap a phot of the wounded thug's tattoo ans use the latest photo editing software to put it on your arm");
	                 System.out.println("You hold the edited photo up to the slide which begins to emit a transparent light over your phone");
	                 System.out.println("The slide makes a strange noise then goes silent");
	                 System.out.println("You once again attempt to lift the slide and succeed, revealing a passageway leading underground");
	                 System.out.println("You proceed to confront the Pink Tigers once and for all");
	                 System.out.println("Press ENTER to continue");
				     try{System.in.read();}
			         catch(Exception e){}
			         adventureDemo.pinkTigerFinal(adventureDemo, player);
			         break;
			         default:
			         adventureDemo.FlushSpace();
			         System.out.println("You are not sure how this will help you with this problem");
			         System.out.println("You decide to return to the street to see if you can get any info that may help you");
			         break;
			         }
                    }
                   }
                  }
                 }
public void pinkTigerFinal(AdventureDemo adventureDemo, Player player){
	adventureDemo.FlushSpace();
	System.out.println("As you navigate through the passageway you notice a light at the bottom of the stairs");
	System.out.println("You approach the light");
	System.out.println("At the end is a slightly open door that emitted the light coming from the other side.");
	System.out.println("You peek through the door and notice a group of men gathered around");
	System.out.println("They are each wearing masks, hiding their identities");
	System.out.println("As you lean forward to peek further, the door gives in and you fall forward into the room");
	System.out.println("Immediately all the men turn to you and pull out numerous kinds of weapons");
	System.out.println("What will you do?");
	System.out.println("1: Run Away");
	System.out.println("2: Try to Compromise");
	System.out.println("3: Check Items");
	
	int pinkTigerFight = scr.nextInt();
	
	switch(pinkTigerFight){
		case 1, 2, 3:
		adventureDemo.FlushSpace();
		System.out.println("Before you or the men can act, a man perched on top of a higher platform shouts something in a foreign language");
		System.out.println("Immediately the assailants lower their weapons");
		System.out.println("The man then begins to speak to you:");
		System.out.println("\"Now this doesn't seem like a fair fight now does it?\"");
		System.out.println("\"But you did rudely interrupt our little gathering, so maybe a beating is in order\"");
		System.out.println("\"How about a trial, to determine if you deserve fair treatment\"");
		System.out.println("\"I will line up three men in front of you, one of them is a member of ours, the other two are not\"");
		System.out.println("\"Determine which of the three is one of us, and I will make your punishment a bit more fair\"");
		System.out.println("Press ENTER to continue");
		try{System.in.read();}
	    catch(Exception e){}
	    System.out.println("Seeing this as your only chance of survival, you accept, and three men are lined up in front of you");
	    System.out.println("They are all wearing masks, and wearing long sleeved shirts to hide tattoos");
	    System.out.println("There is one muscular man, one with a knife scar on his neck, and a heavy, obese man");
	    System.out.println("Who is the Pink Tiger member?");
	    System.out.println("1: The Muscular Man");
	    System.out.println("2: The Man with the Scar");
	    System.out.println("3: the Heavy Man");
	    
	    int memberChoice = scr.nextInt();
	    
	    switch(memberChoice){
			case 3:
			adventureDemo.FlushSpace();
			System.out.println("Remembering the story you heard from the young woman, you choose the heavy man");
			System.out.println("\"Correct!\", yells the man on the platform");
			System.out.println("\"I suppose you deserve some fair treatment as a rewarrd");
			System.out.println("\"Now you will only have to defeat one opponent in order to be allowed to leave");
			System.out.println("\"And that would be the man you just identified\"");
			System.out.println("Without warning, the large man takes a slow, but heavy swing at you");
			System.out.println("1: Block");
			System.out.println("2: Dodge");
			System.out.println("3: Counter");
			
			int actionOne = scr.nextInt();
			
			switch(actionOne){
				case 2:
				adventureDemo.FlushSpace();
				System.out.println("You are barely able to dodge his slow strike");
				System.out.println("However instead of reeling back, the man pulls up his arms and swings them down vertically");
				System.out.println("1: Block");
			    System.out.println("2: Dodge");
			    System.out.println("3: Counter");
			    
			    int actionTwo = scr.nextInt();
			    
			    switch(actionTwo){
					case 2:
					adventureDemo.FlushSpace();
					System.out.println("You immediately move to the side and avoid his powerful axe handle swing");
					System.out.println("Frustrated, he reaches into his pocket and pulls out a firearm, pointing it at you");
					System.out.println("1: Block");
			        System.out.println("2: Dodge");
			        System.out.println("3: Counter");
			        
			        int actionThree = scr.nextInt();
			        
			        switch(actionThree){
						case 3:
						adventureDemo.FlushSpace();
						System.out.println("Unable to block or dodge a bullet, you grab the firearm with both hands and attempt to wrestle it from him");
						System.out.println("1: Block");
			            System.out.println("2: Dodge");
			            System.out.println("3: Counter");
			            
			            int actionFour = scr.nextInt();
			            
			            switch(actionFour){
							case 3:
							adventureDemo.FlushSpace();
							System.out.println("As he attempts to pull the firearm away you knee him in the groin");
							System.out.println("You successfully pull the firearm from him and point it at him");
							System.out.println("The man concedes");
							System.out.println("The leader claps in amusement");
							System.out.println("\"Well done, in accordance to our code of honor we shall grant a great warrior one request\"");
							System.out.println("Remembering the request of the chef from Beijing Express, you demand the men move their operations out of town");
							System.out.println("The leader looks hesitant, but says he cannot dishonor the code, and orders his men to pack up");
							System.out.println("As you leave, the leader reminds you that the Pink Tigers will live on, and all you did has only stalled them");
							System.out.println("Press ENTER to continue");
							try{System.in.read();}
	                        catch(Exception e){}
	                        System.out.println("You return to the restaurant and inform the troubled worker that you have driven the Pink Tigers out of town");
	                        System.out.println("He thanks you, saying that what you have done has probably saved the lives of millions");
	                        System.out.println("As you leave, you remember the Pink Tigers are still out there...");
	                        System.out.println("And what of the young man at the playground? Will you ever know the story behind him?");
	                        adventureDemo.gameComplete(player);
	                        break;
	                        default:
	                        adventureDemo.FlushSpace();
	                        System.out.println("You are unable to do this as you are stuck wrestling for the assailant's firearm");
	                        System.out.println("His physical build allows him to take the weapon back, and fires at you");
	                        System.out.println("You immediately fall to the floor");
	                        adventureDemo.endGame(player);
	                        break;
				            }
                        break;
                        default:
                        adventureDemo.FlushSpace();
                        System.out.println("Against a bullet this action is worthless");
                        System.out.println("You opponen'ts fire reaches its mark and you fall to the floor");
                        adventureDemo.endGame(player);
                        break;
                       }
                break;
                default:
                adventureDemo.FlushSpace();
                System.out.println("You are unable to stop his attack, his build renders him superior to you in strength");
                System.out.println("The weight of his blow is so great you are immediately knocked out");
                adventureDemo.endGame(player);
                break;
               }
                adventureDemo.FlushSpace();
                System.out.println("You are unable to stop his attack, his build renders him superior to you in strength");
                System.out.println("The weight of his blow is so great you are immediately knocked out");
                adventureDemo.endGame(player);
                break;
               }
            break;
            default:
            System.out.println("\"Sorry pal, but that's wrong, sorry.\"");
            System.out.println("\"Alright boys, you know what to do\"");
            System.out.println("The men attack you with all of their weapons, you have no chance of surviving their onslaught\"");
            adventureDemo.endGame(player);
            break;
           }
          }
         }
        }
