import com.sun.javafx.geom.Edge;
import lib280.graph.Vertex280;
import lib280.graph.WeightedEdge280;
import lib280.graph.WeightedGraphAdjListRep280;
import lib280.tree.ArrayedHeap280;
import lib280.tree.ArrayedMinHeap280;

public class Kruskal {
	
	public static WeightedGraphAdjListRep280<Vertex280> minSpanningTree(WeightedGraphAdjListRep280<Vertex280> G) {

		// TODO -- Complete this method.
		WeightedGraphAdjListRep280<Vertex280> minST = new WeightedGraphAdjListRep280<Vertex280>(G.capacity(),false);
		G.goFirst();
		while(!G.after()) {
			minST.addVertex(G.item().index());
			G.goForth();
		}

		UnionFind280 Uf = new UnionFind280(G.numVertices());
		ArrayedMinHeap280<WeightedEdge280<Vertex280>> H = new ArrayedMinHeap280(G.numEdges()*2);
		for(int i =1; i<=G.numVertices(); i++) {
			G.eGoFirst(G.vertex(i));
			while (!G.eAfter()) {
				H.insert(G.eItem());
				G.eGoForth();
			}
		}
		while (H.itemExists()) {
			if (Uf.find(H.item().firstItem().index()) != Uf.find(H.item().secondItem().index())) {

				minST.addEdge(H.item().firstItem(), H.item().secondItem());
				minST.setEdgeWeight(H.item().firstItem(), H.item().secondItem(), H.item().getWeight());
				Uf.union(H.item().firstItem().index(), H.item().secondItem().index());

			}
			H.deleteItem();
		}

		
		return minST;  // Remove this when you're ready -- it is just a placeholder to prevent a compiler error.
	}
	
	
	public static void main(String args[]) {
		WeightedGraphAdjListRep280<Vertex280> G = new WeightedGraphAdjListRep280<Vertex280>(1, false);
		// If you get a file not found error here and you're using eclipse just remove the 
		// 'Kruskal-template/' part from the path string.
		G.initGraphFromFile("/Users/homeyxue/Desktop/cmpt280/Kruskal-Template/mst.graph");
		System.out.println(G);
		
		WeightedGraphAdjListRep280<Vertex280> minST = minSpanningTree(G);
		
		System.out.println(minST);
	}
}


