import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class KMeans {
	private ArrayList<Cluster> clusters;

	public KMeans(List<TrainingDataItem> trainingData, int k) {

		this.clusters = new ArrayList<Cluster>();

		// trainingData is shuffled
		for (int i = 0; i < k; i++) {
			clusters.add(new Cluster(trainingData.get(i)));
		}

		while (true) {
			for (TrainingDataItem item : trainingData) {
				
				// get best cluster for given item
				Cluster bestCluster = clusters.get(0);
				for (Cluster c : clusters) {
					if (c.distance(item) < bestCluster.distance(item))
						bestCluster = c;
				}
				
				// assign item to cluster
				bestCluster.add(item);
			}

			// calculate new center
			
			
			int n = 0; // number of times a cluster changed its center
			for (Cluster c : clusters) {

				System.out.println(c.center);
				System.out.println("Number of Items: " + c.list.size());

				if (c.newCenter())
					n++;

				// remove all data from list
				c.clear();
			}

			// if no center were changed -> break loop
			if (n == 0)
				break;
		}
	}

	class Cluster {
		List<TrainingDataItem> list;
		TrainingDataItem center;

		public Cluster(TrainingDataItem item) {
			list = new ArrayList<TrainingDataItem>();
			list.add(item);
			center = item;
		}

		// caluclate new center of points
		// return: was a new center set?
		public boolean newCenter() {
			TrainingDataItem oldCenter = center;
			center = new TrainingDataItem(list);
			return center.distance(oldCenter) != 0;
		}

		public double distance(TrainingDataItem item) {
			return center.distance(item);
		}

		public void add(TrainingDataItem item) {
			list.add(item);
		}

		public void clear() {
			list.clear();
		}

		public String target() {
			return center.getTargetClass();
		}

	}

	public String classify(TrainingDataItem item) {
		// get closest cluster
		Cluster best = clusters.get(0);
		for (Cluster c : clusters) {
			if (c.distance(item) < best.distance(item))
				best = c;
		}
		return best.target();
	}

	// returns error of classifier
	public double testAgainstTestItems(List<TrainingDataItem> testData) {
		int fails = 0;
		for (TrainingDataItem i : testData) {
			if (!classify(i).equals(i.getTargetClass()))
				fails++;
		}
		return fails / (double) testData.size();
	}

	// read data from path
	public static ArrayList<TrainingDataItem> readData(String path) {
		ArrayList<TrainingDataItem> items = new ArrayList<TrainingDataItem>();

		File file = new File(path);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String s = null;

			do {
				s = reader.readLine();
				if (s != null) {
					TrainingDataItem i = new TrainingDataItem(s);
					items.add(i);
				}
			} while (s != null && s != "");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return items;
	}

	public static void main(String[] args) {

		// reading data from file
		ArrayList<TrainingDataItem> items = readData("car.data");

		do {
			System.out.println("Enter k");
			Scanner reader = new Scanner(System.in);
			int k = reader.nextInt();
			System.out.println("K = " + k);

			int runs = 100;
			double summedError = 0;
			for (int i = 0; i < runs; i++) {

				// shuffle list
				Collections.shuffle(items);

				// build classifier
				KMeans km = new KMeans(items, k);

				// test against test data
				summedError += km.testAgainstTestItems(items);
			}

			System.out.println("Summed Mean Error: " + summedError / runs * 100
					+ "%");
		} while (true);
	}
}
