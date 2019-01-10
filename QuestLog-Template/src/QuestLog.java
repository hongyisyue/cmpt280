import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.opencsv.CSVReader;

import lib280.base.Pair280;
import lib280.exception.ItemNotFound280Exception;
import lib280.hashtable.KeyedChainedHashTable280;
import lib280.list.ArrayedList280;
import lib280.list.ArrayedListIterator280;
import lib280.list.LinkedIterator280;
import lib280.tree.OrderedSimpleTree280;
import java.util.Arrays;

// This project uses a JAR called opencsv which is a library for reading and
// writing CSV (comma-separated value) files.
// 
// You don't need to do this for this project, because it's already done, but
// if you want to use opencsv in other projects on your own, here's the process:
//
// 1. Download opencsv-3.1.jar from http://sourceforge.net/projects/opencsv/
// 2. Drag opencsv-3.1.jar into your project.
// 3. Right-click on the project in the package explorer, select "Properties" (at bottom of popup menu)
// 4. Choose the "Libraries" tab
// 5. Click "Add JARs"
// 6. Select the opencsv-3.1.jar from within your project from the list.
// 7. At the top of your .java file add the following imports:
//        import java.io.FileReader;
//        import com.opencsv.CSVReader;
//
// Reference documentation for opencsv is here:  
// http://opencsv.sourceforge.net/apidocs/overview-summary.html



public class QuestLog extends KeyedChainedHashTable280<String, QuestLogEntry> {

	public QuestLog() {
		super();
	}
	
	/**
	 * A method that obtains an array of the keys (quest names) from the quest log.
	 * Note: There is no particular ordering of the keys.
	 * 
	 * @return The array of keys (quest names) from the quest log.
	 */
	public String[] keys() {
		// TODO Implement this method.
		ArrayedList280 n = new ArrayedList280(this.hashArray.length);
		for(int i=0; i<this.hashArray.length; i++){
			if(this.hashArray[i] != null) {
				LinkedIterator280 h = this.hashArray[i].iterator();
						h.goFirst();
				while(!h.after() && h.itemExists()) {
					n.insertFirst(((QuestLogEntry)h.item()).getQuestName());
					h.goForth();
				}
			}
		}
		String[] m = new String[n.count()];
		ArrayedListIterator280 h = n.iterator();
		h.goFirst();
		for(int i=0; i<n.count(); i++){
			if(h.item() != null) {
				m[i]=h.item().toString();
				h.goForth();
			}
		}
		return m;
	}
	/**
	 * A method that formats the quest log as a string which displays the quests in the log
	 * Note: in alphabetical order by name.
	 * 
	 * @return A nicely formatted quest log.
	 */
	public String toString() {
		// TODO Implement this method.
		String[] keyHelper = this.keys();           /**take advantage of keys() as requirement*/
		Arrays.sort(keyHelper);

		for(int i=0; i<keyHelper.length; i++){
			keyHelper[i] = this.obtain(keyHelper[i]).toString();
		}

		String allQuests=String.join("\n", keyHelper);

		return allQuests;
	}
	
	/**
	 * Obtain the quest with name k, while simultaneously returning the number of
	 * items examined while searching for the quest.
	 * @param k the quest name to obtain.
	 * @return A pair in which the first item is the QuestLogEntry for the quest named k, and the
	 *         second item is the number of items examined during the search for the quest named k.
	 *
	 *         Note: if no quest named k is found, then the first item of the pair will be null.
	 */
	public Pair280<QuestLogEntry, Integer> obtainWithCount(String k) throws ItemNotFound280Exception {
		// TODO Implement this method.
		// Write a method that returns a Pair280 which contains the quest log entry with name k, 
		// and the number QuestLogEntry objects that were examined in the process.  You need to write
		// this method from scratch without using any of the superclass methods (mostly because 
		// the superclass methods won't be terribly useful unless you can modify them, which you
		// aren't allowed to do!).

		//not sure if we need to throw exception so I comment it out.
		//if(!this.has(k)) throw new ItemNotFound280Exception("Sorry, Item not found!");

		int position = this.hashPos(k);

			if(this.hashArray[position] != null) {
				int count=0;
				LinkedIterator280 h = this.hashArray[position].iterator();
				h.goFirst();
				while(!h.after() && h.itemExists()) {
					if (((QuestLogEntry)h.item()).getQuestName().equals(k)) {
						count++;
						return new Pair280 <> ((QuestLogEntry)h.item(), count);
					}
					count++;
					h.goForth();
				}
			}
		return null;
	}
	
	
	public static void main(String args[])  {
		// Make a new Quest Log
		QuestLog hashQuestLog = new QuestLog();
		
		// Make a new ordered binary lib280.tree.
		OrderedSimpleTree280<QuestLogEntry> treeQuestLog =
				new OrderedSimpleTree280<QuestLogEntry>();
		
		
		// Read the quest data from a CSV (comma-separated value) file.
		// To change the file read in, edit the argument to the FileReader constructor.
		CSVReader inFile;
		try {
			//NOTE: if you are using ECLIPSE, remove the 'QuestLog/' portion of the
			//input filename on the next line.
			inFile = new CSVReader(new FileReader("/Users/homeyxue/Desktop/cmpt280/QuestLog-Template/quests16.csv"));
		} catch (FileNotFoundException e) {
			System.out.println("Error: File not found.");
			return;
		}
		
		String[] nextQuest;
		try {
			// Read a row of data from the CSV file
			while ((nextQuest = inFile.readNext()) != null) {
				// If the read succeeded, nextQuest is an array of strings containing the data from
				// each field in a row of the CSV file.  The first field is the quest name,
				// the second field is the quest region, and the next two are the recommended
				// minimum and maximum level, which we convert to integers before passing them to the
				// constructor of a QuestLogEntry object.
				QuestLogEntry newEntry = new QuestLogEntry(nextQuest[0], nextQuest[1], 
						Integer.parseInt(nextQuest[2]), Integer.parseInt(nextQuest[3]));
				// Insert the new quest log entry into the quest log.
				hashQuestLog.insert(newEntry);
				treeQuestLog.insert(newEntry);
			}
		} catch (IOException e) {
			System.out.println("Something bad happened while reading the quest information.");
			e.printStackTrace();
		}
		
		// Print out the hashed quest log's quests in alphabetical order.
		// COMMENT THIS OUT when you're testing the file with 100,000 quests.  It takes way too long.
		System.out.println(hashQuestLog);
		
		// Print out the lib280.tree quest log's quests in alphabetical order.
		// COMMENT THIS OUT when you're testing the file with 100,000 quests.  It takes way too long.
	    System.out.println(treeQuestLog.toStringInorder());
		

		// TODO Determine the average number of elements examined during access for hashed quest log.
	    // (call hashQuestLog.obtainWithCount() for each quest in the log and find average # of access)
		int totalTime =0;
		for(int i=0; i<hashQuestLog.keys().length; i++){
			totalTime = totalTime + hashQuestLog.obtainWithCount(hashQuestLog.keys()[i]).secondItem();
		}

		System.out.println("Avg . # of items examined per query in the hashed quest log with "
				+ hashQuestLog.keys().length
				+ " entries : "
				+ ((double)totalTime/((double) hashQuestLog.keys().length)));

		// TODO Determine the average number of elements examined during access for lib280.tree quest log.
	    // (call treeQuestLog.searchCount() for each quest in the log and find average # of access)

		int totalTime2 =0;
		for(int i=0; i<hashQuestLog.keys().length; i++){
			totalTime2 = totalTime2 + treeQuestLog.searchCount(hashQuestLog.obtain(hashQuestLog.keys()[i]));
		}

		System.out.println("Avg . # of items examined per query in the tree quest log with "
				+ hashQuestLog.keys().length
				+ " entries : "
				+ ((double)totalTime2/((double) hashQuestLog.keys().length)));
	}
	
	
}
