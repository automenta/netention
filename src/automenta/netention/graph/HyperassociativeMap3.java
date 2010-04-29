/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.netention.graph;

import com.syncleus.dann.graph.Graph;
import com.syncleus.dann.graph.drawing.GraphDrawer;
import com.syncleus.dann.graph.drawing.hyperassociativemap.HyperassociativeMap;
import com.syncleus.dann.math.Vector;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javolution.context.ConcurrentContext;
import org.apache.log4j.Logger;

/**
 *
 * @author seh
 */
public class HyperassociativeMap3<G extends Graph<N, ?>, N> implements GraphDrawer<G, N> {

    private final G graph;
    private final int dimensions;
    private final Map<N, Vector> coordinates = Collections.synchronizedMap(new HashMap<N, Vector>());
    private final static Random RANDOM = new Random();
    private double equilibriumDistance;
    private double learningRate;
    private double repulsionForce;
    private double attractionForce;

    /**
     *
     * @param graph
     * @param dimensions 
     * @param learningRate  typical value: 0.004
     * @param equilibriumDistance typical value: 1.0
     * @param attractionForce typical value: 2.0
     * @param repulsionForce typical value: 4.0
     */
    public HyperassociativeMap3(G graph, int dimensions, double learningRate, double equilibriumDistance, double attractionForce, double repulsionForce) {
        super();
        this.graph = graph;
        this.dimensions = dimensions;
        this.learningRate = learningRate;
        this.equilibriumDistance = equilibriumDistance;
        this.repulsionForce = repulsionForce;
        this.attractionForce = attractionForce;

        //refresh all nodes
        randomizeAllCoordinates();
    }

    public void randomizeAllCoordinates() {
        for (N node : this.graph.getNodes()) {
            this.coordinates.put(node, randomCoordinates(this.dimensions));
        }
    }

    public G getGraph() {
        return this.graph;
    }

    public boolean isAlignable() {
        return false;
    }

    public boolean isAligned() {
        return false;
    }

    public void align() {
        //refresh all nodes
        final Map<N, Vector> newCoordinates = new HashMap<N, Vector>();
        for (N node : this.graph.getNodes()) {
            if (this.coordinates.containsKey(node)) {
                newCoordinates.put(node, this.coordinates.get(node));
            } else {
                newCoordinates.put(node, randomCoordinates(this.dimensions));
            }
        }
        this.coordinates.clear();
        this.coordinates.putAll(newCoordinates);

        final Vector center = new Vector(this.dimensions);

        //align all nodes in parallel
        ConcurrentContext.enter();
        for (final N n : graph.getNodes()) {
            ConcurrentContext.execute(new Runnable() {
                @Override public void run() {
                    Vector v = align(n);
                    center.add(v);
                }
            });
        }
        ConcurrentContext.exit();


        //divide each coordinate of the sum of all the points by the number of
        //nodes in order to calulate the average point, or center of all the
        //points
        center.multiply(1.0 / ((double) this.graph.getNodes().size()));

        this.recenterNodes(center);
    }

    public int getDimensions() {
        return this.dimensions;
    }

    public Map<N, Vector> getCoordinates() {
        return Collections.unmodifiableMap(this.coordinates);
    }

    private void recenterNodes(final Vector center) {
        for (N node : this.graph.getNodes()) {
            this.coordinates.put(node, this.coordinates.get(node).calculateRelativeTo(center));
        }
    }

    Set<N> getNeighbors(N nodeToQuery) {
        return new HashSet<N>(this.graph.getAdjacentNodes(nodeToQuery));
    }

    final private Vector align(N nodeToAlign) {
        //calculate equilibrium with neighbors
        final Vector location = this.coordinates.get(nodeToAlign);
        final Set<N> neighbors = this.getNeighbors(nodeToAlign);

        Vector compositeVector = new Vector(location.getDimensions());
        for (N neighbor : neighbors) {
            Vector neighborVector = this.coordinates.get(neighbor).calculateRelativeTo(location);
            if (Math.abs(neighborVector.getDistance()) > equilibriumDistance) {
                double newDistance = Math.pow(Math.abs(neighborVector.getDistance()) - equilibriumDistance, attractionForce);
                if (Math.abs(newDistance) > Math.abs(Math.abs(neighborVector.getDistance()) - equilibriumDistance)) {
                    newDistance = Math.abs(Math.abs(neighborVector.getDistance()) - equilibriumDistance);
                }
                neighborVector = neighborVector.setDistance(Math.signum(neighborVector.getDistance()) * newDistance);
            } else {
                double newDistance = -1.0 * atanh((equilibriumDistance - Math.abs(neighborVector.getDistance())) / equilibriumDistance);
                if (Math.abs(newDistance) > Math.abs(equilibriumDistance - Math.abs(neighborVector.getDistance()))) {
                    newDistance = -1.0 * (equilibriumDistance - Math.abs(neighborVector.getDistance()));
                }
                neighborVector = neighborVector.setDistance(Math.signum(neighborVector.getDistance()) * newDistance);
            }

            compositeVector = compositeVector.add(neighborVector);
        }

        //calculate repulsion with all non-neighbors
        for (N node : this.graph.getNodes()) {
            if ((neighbors.contains(node) == false) && (node != nodeToAlign) && (this.graph.getAdjacentNodes(node).contains(nodeToAlign) == false)) {
                Vector nodeVector = this.coordinates.get(node).calculateRelativeTo(location);
                double newDistance = -1.0 / Math.pow(nodeVector.getDistance(), repulsionForce);
                if (Math.abs(newDistance) > Math.abs(equilibriumDistance)) {
                    newDistance = -1.0 * equilibriumDistance;
                }
                nodeVector = nodeVector.setDistance(newDistance);

                compositeVector = compositeVector.add(nodeVector);
            }
        }

        compositeVector = compositeVector.setDistance(compositeVector.getDistance() * learningRate);

        Vector newLocation = location.add(compositeVector);
        this.coordinates.put(nodeToAlign, newLocation);
        return newLocation;
    }

    /**
     * Obtains a Vector with random coordinates for the specified number of
     * dimensions.
     *
     *
     * @param dimensions Number of dimensions for the random Vector
     * @return New random Vector
     * @since 1.0
     */
    public static Vector randomCoordinates(int dimensions) {
        double[] randomCoords = new double[dimensions];
        for (int randomCoordsIndex = 0; randomCoordsIndex < dimensions; randomCoordsIndex++) {
            randomCoords[randomCoordsIndex] = (RANDOM.nextDouble() * 2.0) - 1.0;
        }

        return new Vector(randomCoords);
    }

    private static double atanh(double value) {
        return 0.5 * Math.log(Math.abs((value + 1.0) / (1.0 - value)));
    }
//	private List<Future<Vector>> submitFutureAligns()
//	{
//		final ArrayList<Future<Vector>> futures = new ArrayList<Future<Vector>>();
//		for(N node : this.graph.getNodes())
//			futures.add(this.threadExecutor.submit(new Align(node)));
//		return futures;
//	}
//	private Vector waitAndProcessFutures(final List<Future<Vector>> futures) throws InterruptedException
//	{
//		//wait for all nodes to finish aligning and calculate new center point
//		Vector pointSum = new Vector(this.dimensions);
//		try
//		{
//			for(Future<Vector> future : futures)
//			{
//				Vector newPoint = future.get();
//				for(int dimensionIndex = 1; dimensionIndex <= this.dimensions; dimensionIndex++)
//					pointSum = pointSum.setCoordinate(pointSum.getCoordinate(dimensionIndex) + newPoint.getCoordinate(dimensionIndex), dimensionIndex);
//			}
//		}
//		catch(ExecutionException caught)
//		{
//			LOGGER.error("Align had an unexcepted problem executing.", caught);
//			throw new UnexpectedDannError("Unexpected execution exception. Get should block indefinately", caught);
//		}
//
//		return pointSum;
//	}
}