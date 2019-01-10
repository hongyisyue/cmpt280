package QuestPrerequisites;

import java.io.*;
import java.util.Scanner;

import lib280.base.CursorPosition280;
import lib280.exception.InvalidArgument280Exception;
import lib280.graph.Edge280;
import lib280.graph.GraphMatrixRep280;
import lib280.list.LinkedList280;
import lib280.tree.ArrayedHeap280;
import lib280.tree.ArrayedMinHeap280;

public class QuestProgression {
	
	// File format for quest data:
	// First line: Number of quests N
	// Next N lines consist of the following items, separated by commas:
	//     quest ID, quest name, quest area, quest XP
	//     (Quest ID's must be between 1 and N, but the line for each quest IDs may appear in any order).
	// Remaining lines consist of a comma separated pair of id's i and j where i and j are quest IDs indicating
	// that quest i must be done before quest j (i.e. that (i,j) is an edge in the quest graph).
	
	/**
	 * Read the quest data from a text file and build a graph of quest prerequisites.
	 * @param filename Filename from which to read quest data.
	 * @return A graph representing quest prerequisites.  If quest with id i must be done before a quest with id j, then there is an edge in the graph from vertex i to vertex j.
	 */
	public static GraphMatrixRep280<QuestVertex, Edge280<QuestVertex>> readQuestFile(String filename) {
		Scanner infile;
		
		// Attempt to open the input filename.
		try {
			infile = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.out.println("Error: Unable to open" + filename);
			e.printStackTrace();
			return null;
		}
		
		// Set the delimiters for parsing to commas, and vertical whitespace.
		infile.useDelimiter("[,\\v]");

		// Read the number of quests for which there is data.
		int numQuests = infile.nextInt();
		
		// read the quest data for each quest.
		LinkedList280<Quest> questList = new LinkedList280<Quest>();
		for(int i=0; i < numQuests; i++) {
			int qId = infile.nextInt();
			String qName = infile.next();
			String qArea = infile.next();
			int qXp = infile.nextInt();		
			questList.insertLast(new Quest(qId, qName, qArea, qXp));
		}
	
		// Make a graph with the vertices we created from the quest data.
		GraphMatrixRep280<QuestVertex, Edge280<QuestVertex>> questGraph = 
				new GraphMatrixRep280<QuestVertex, Edge280<QuestVertex>> (numQuests, true, "QuestPrerequisites.QuestVertex", "lib280.graph.Edge280");
		
		// Add enough vertices for all of our quests.
		questGraph.ensureVertices(numQuests);
		
		// Store each quest in a different vertex.  The quest with id i gets stored vertex i.
		questList.goFirst();
		while(questList.itemExists()) {
			questGraph.vertex(questList.item().id()).setQuest(questList.item());
			questList.goForth();
		}
		
		// Continue reading the input file for the quest prerequisite informaion and add an edge to the graph
		// for each prerequisite.
		while(infile.hasNext()) {
			questGraph.addEdge(infile.nextInt(), infile.nextInt());
		}
				
		infile.close();
		
		return questGraph;
	}
	

	/**
	 * Test whether vertex v has incoming edges or not
	 * @param G A graph.
	 * @param v The integer identifier of a node in G (corresponds to quest ID)
	 * @return Returns true if v has no incoming edges.  False otherwise.
	 */
	public static boolean hasNoIncomingEdges(GraphMatrixRep280<QuestVertex,Edge280<QuestVertex>> G, int v) {
		
		// TODO Write this method
		for(int i=1; i<=G.numVertices();i++) {
			if (G.isAdjacent(i, v)) return false;
		}
			return true;   // replace this with your own return statement -- this is just a placeholder to prevent compiler errors.
	}
	
	
	/**
	 * Perform a topological sort of the quests in the quest prerequisite graph G, with priority given
	 * to the highest experience value among the available quests.
	 * @param G The graph on which to perform a topological sort.
	 * @return A list of quests that is the result of the topological sort, that is, the order in which the quests should be done if we always pick the available quest with the largest XP reward first.
	 */
	public static LinkedList280<Quest> questProgression(GraphMatrixRep280<QuestVertex,Edge280<QuestVertex>> G) {
	
		// TODO Write this method
		LinkedList280<Quest> L = new LinkedList280<>();
		ArrayedHeap280<Quest> H = new ArrayedHeap280<>(G.numVertices());
		G.goFirst();
		while(!G.after()) {
			if(hasNoIncomingEdges(G,G.item().index())) {
				H.insert(G.item().quest());
			}
			G.goForth();
		}

		while(!H.isEmpty()) {
			L.insertLast(H.item());
			H.deleteItem();
			for(int i=1; i<=G.numVertices();i++){
			if(G.isAdjacent(QVHelper(G, L.lastItem()),G.vertex(i))){
				G.eSearch(QVHelper(G, L.lastItem()),G.vertex(i));
				G.deleteEItem();

				if (hasNoIncomingEdges(G, G.vertex(i).index())) {
					H.insert(G.vertex(i).quest);
				}
			}

			}
		}

		if(G.numEdges()!=0) throw new InvalidArgument280Exception("the graph had at least one cycle");

		else return L;  // Replace this with your own return statement -- this is jsut a placeholder to prevent compiler errors.

	}

	/**
	 * A search method that takes in a graph and a quests, and get the quests's corresponding node in the given graph
	 * @param G the graph that you are working on
	 * @param q the quest which you want to find it's vertex
	 * @return the corresponding Vertex of the given quests
	 */
	public static QuestVertex QVHelper(GraphMatrixRep280<QuestVertex,Edge280<QuestVertex>> G, Quest q){
		G.goFirst();
		while(!G.after()){
			if (G.item().quest().compareTo(q) == 0) {
			return G.item();
			}
			G.goForth();
		}
		return null;
	}
	
	public static void main(String args[]) {
		// Read the quest data and construct the graph.
		
		// If you get an error reading the file here and you're using Eclipse, 
		// remove the 'QuestPrerequisites-Template/' portion of the filename.
		GraphMatrixRep280<QuestVertex,Edge280<QuestVertex>> questGraph = readQuestFile("/Users/homeyxue/Desktop/cmpt280/QuestPrerequisites-Template/quests16.txt");
		
		// Perform a topological sort on the graph.
		LinkedList280<Quest> questListForMaxXp = questProgression(questGraph);
		
		// Display the quests to be completed in the order determined by the topological sort.
		questListForMaxXp.goFirst();
		while(questListForMaxXp.itemExists()) {
			System.out.println(questListForMaxXp.item());
			questListForMaxXp.goForth();
		}
				
	}
}
