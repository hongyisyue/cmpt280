import lib280.graph.Vertex280;
import lib280.graph.WeightedGraphMatrixRep280;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;


/**
 * A non-public class that stores an item's value and weight
 */
class Item implements Comparable<Item> {

	protected Double value;
	protected Double weight;
	
	Item(Double v, Double w) {
		value = v;
		weight = w;
	}

	@Override
	public int compareTo(Item o) {
		return this.value.compareTo(o.value);
	}
	
	/**
	 * @return the value
	 */
	public Double value() {
		return value;
	}

	/**
	 * @return the weight
	 */
	public Double weight() {
		return weight;
	}
	
}

/**
 * A non-public class that stores an instance of Knapsack.
 */
class KnapsackInstance {
	/**
	 * The number of items in the problem instance.
	 */
	protected Integer numItems;
	
	/**
	 * The items to be considered.
	 */
	Item items[];
	
	/**
	 * The capacity of the knapsack in the problem instance.
	 */
	protected Double W;
	
	/**
	 * Initialize a knapsack instance.
	 * @param numItems Number of items in the problem instance
	 * @param W Capacity of the backpack.
	 */
	KnapsackInstance(Integer numItems, Double W) {
		this.numItems = numItems;
		this.W = W;
		this.items = new Item[this.numItems];
	}
	
	/** 
	 * @return The number of items in the problem instance.
	 */
	public Integer numItems() { return this.numItems; }
	
	/**
	 * Set the value and weight of the id-th item.
	 */
	public void setItem(Double value, Double weight, Integer id) {
		this.items[id] = new Item(value, weight);
	}
	
	/**
	 * Obtain an item's value
	 */
	public Double value(int i) { return this.items[i].value(); }
	
	/**
	 * Obtain and item's weight
	 */
	public Double weight(int i) { return this.items[i].weight(); }

	/**
	 * Obtain the knapsack's capacity
	 */
	public Double capacity() { return this.W; }
	
	/** 
	 * Obtain the array of items.
	 */
	public Item[] items() { return this.items; }

	/**
	 * Printable representation of the problem instance.
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append("W = " + this.W + "\n");
		for(int i=0; i < this.numItems; i++) {
			result.append(this.items[i].value + ", " + this.items[i].weight + "\n");
		}
		return result.toString();
	}
	
}


public class Knapsack {

	public static KnapsackInstance readKnapsackInstance(String filename) {
		
		Scanner infile = null;
		try {
			infile = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			System.out.println("Error: " + filename + " could not be opened.");			
		}
		
		// Try to read the knapsack capacity and the number of items.
		if (!infile.hasNextDouble()) 
			throw new RuntimeException("Error: expected knapsack weight as a real number");
		Double W = infile.nextDouble();
		
		if (!infile.hasNextInt()) 
			throw new RuntimeException("Error: expected integer number of items.");
		Integer numItems = infile.nextInt();
		
		// Create a knapsack instance for the given number of items.
		KnapsackInstance K = new KnapsackInstance(numItems, W);
		
		// Read each value-weight pair.
		for(int i=0; i < numItems; i++) {
			if(!infile.hasNextDouble()) 
				throw new RuntimeException("Error: expected a value while reading item " + i +".");
			Double v = infile.nextDouble();
			if(!infile.hasNextDouble()) 
				throw new RuntimeException("Error: expected a weight while reading item " + i +".");
			Double w = infile.nextDouble();
			
			// Store the value-weight pair in the problem instance.
			K.setItem(v,w,i);				
		}
		
		infile.close();
		
		return K;
	}

	// TODO Write your backtracking and greedy solutions to Knapsack here.
	public int sequence[];
	public int bestSequence[];
	public int extensionsTried;
	public double currentWeight;
	public double currentValue;
	public double bestValue;

	public double TSPPruningWhenCostExceedsBest(KnapsackInstance K, int startItem) {
		// Initialize everything
		currentWeight= 0;
		currentValue= 0;
		bestValue = 0;
		extensionsTried = 0;
		sequence = new int[K.numItems()];
		bestSequence = new int[K.numItems()];

		sortKnapsack(K); // sort the knapsack items to make it easier to run the algorithm
		KnapsackTSPHelper(K, startItem);
		return bestValue;
	}

	/**
	 * A method that sort the Knapsack items by their value/weight
	 * @param K
	 */
	public void sortKnapsack (KnapsackInstance K){
		for(int j=0; j< K.numItems()-1; j++) {
			for (int i = 0; i <K.numItems()-1; i++) {
				if ((K.items[i].value() / K.items[i].weight()) < (K.items[i + 1].value() / K.items[i + 1].weight())) {
					Item temp = K.items[i];
					K.items[i] = K.items[i + 1];
					K.items[i + 1] = temp;
				}
			}
		}
	}
	protected void KnapsackTSPHelper(KnapsackInstance K, int currentItem) {
		//reach the edge, save the sequence.
		if( currentItem == K.numItems()-1 && currentValue>bestValue) {
				bestValue = currentValue;
				bestSequence = Arrays.copyOf(sequence, sequence.length);
				return;
		}

		// Otherwise, try to extend the path, as long as we haven't exceeded the capacity.
		else if( currentWeight+K.items[currentItem].weight() <= K.capacity()){

			sequence[currentItem] = 1;
			currentWeight = currentWeight+K.items[currentItem].weight();
			currentValue = currentValue+K.items[currentItem].value();

			KnapsackTSPHelper(K, currentItem+1);

			currentWeight = currentWeight-K.items[currentItem].weight(); //backtracking
			currentValue = currentValue-K.items[currentItem].value(); // backtracking

		}

		//if the max value is larger than the nest value so far, we start search on next position
		//Otherwise we will ignore this step
		if(maxValue(K, currentItem+1) > bestValue){
			sequence[currentItem] = 0;
			KnapsackTSPHelper(K, currentItem+1);
		}

	}

	/**
	 * A helper method that calculate the mathematical largest value that can fit the knapsack
	 * @param K the Knapsack data
	 * @param i the start position
	 * @return the mathematical largest value that can fit the knapsack
	 */
	protected double maxValue(KnapsackInstance K, int i){
		double leftWeight = K.capacity()-currentWeight;
		double maxV = currentValue;

		while (i<K.numItems() && K.items[i].weight()<= leftWeight){
			leftWeight = leftWeight - K.items[i].weight();
			maxV = maxV + K.items[i].value();
			i++;
		}

		if(i<K.numItems()){
			maxV = maxV + K.items[i].value()/K.items[i].weight()*leftWeight;//fill up the knapsack,
		}

		return maxV;
	}

		int count;// a variable to count how many items does greedy algorithm take

	/**
	 * The greedy algorithm
	 * @param K
	 * @return the best greedy value
	 */
		public double greedySolution(KnapsackInstance K) {
			sortKnapsack(K);
			double Cweight=0;
			double Cvalue=0;
			count = 0;

			for(int i=0; i<K.numItems(); i++){
				if(Cweight+ K.items[i].weight()<= K.capacity()){
					Cweight = Cweight + K.items[i].weight();
					Cvalue = Cvalue + K.items[i].value();
					count++;
				}
			}

			return Cvalue;
		}

	public static void main(String args[]) {
		KnapsackInstance K = readKnapsackInstance("/Users/homeyxue/Desktop/cmpt280/Knapsack-Template/knapsack-10.dat");
		
		// this line is mostly just to prevent a warning that K is unused.  You can
		// delete it when you're ready.  It has the added bonus of letting you see
		// the problem instance.

		// TODO Call your algorithms to solve the knapsack instance K here.
		//WeightedGraphMatrixRep280<Vertex280> G = new WeightedGraphMatrixRep280<Vertex280>(50, false);

		Knapsack tsp = new Knapsack();
		tsp.TSPPruningWhenCostExceedsBest(K, 0);
			System.out.println(K);
		System.out.println("testing "+K.numItems()+" items: ");
		System.out.println("Backtracking: ");
		System.out.println("Best value is: " + tsp.bestValue);
		System.out.print("Best choices are: ");
		for(int i=0; i < tsp.bestSequence.length; i++) {
			System.out.print(tsp.bestSequence[i]);
			if(i < tsp.bestSequence.length-1)
				System.out.print(", ");
		}
		System.out.println('\n' +"Greedy: ");

		double GV = tsp.greedySolution(K);
		System.out.println("the best value is: "+ GV);
		System.out.println("greedy algorithm takes the first " + tsp.count + " items of the all sorted items");
		System.out.println("The correctness of greedy algorithm is: "+GV/tsp.bestValue*100 +"%");


	}
}
