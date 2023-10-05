import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * @author Tayeb Mohammadi
 */
public class RegionFinder {
	private static final int maxColorDiff = 30;             // how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50;                // how many points in a region to be worth considering

	public BufferedImage image;                            // the image in which to find regions
	public BufferedImage recoloredImage;                   // the image with identified regions recolored

	public final ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();

	// a region is a list of points
	// so the identified regions are in a list of lists of points
	private BufferedImage blackPixelImage;                  // the image with black pixels to track visitation

	private ArrayList<Point> toVisit;		//holds neighboring points that are going to be visited
	private ArrayList<Point> region;		//contains visited points of desired color
	private int radius = 1;					// distance to pixel neighbor
	private Color randomColor;    			//random color to be used to paint regions


	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public RegionFinder(BufferedImage image, Color targetColor) {
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE
		blackPixelImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB); // declare black image for keeping track of visited pixels
		for (int y = 0; y < image.getHeight(); y++) { // loop through the all the pixels of the image
			for (int x = 0; x < image.getWidth(); x++) {
				// as long as I reach a different unvisited pixel, of the target color, enter if condition
				if (blackPixelImage.getRGB(x, y) == 0 && colorMatch(new Color(image.getRGB(x, y)), new Color(targetColor.getRGB()))) {
					toVisit = new ArrayList<Point>();  // the array storing the pixels to be visited
					toVisit.add(new Point(x, y)); // setting the first pixel to be visited in the tovist array
					region = new ArrayList<Point>(); // creating a new region
					while (!toVisit.isEmpty()) {
						Point removedPoint = toVisit.remove(0);
						blackPixelImage.setRGB(removedPoint.x,removedPoint.y, 1); // change the color on the black pixel to mark it as visited
						region.add(removedPoint); // add the visited point to the region started
						// loop through its neighbors to grow the region
						for (int ny = Math.max(0, removedPoint.y - radius);
							 ny < Math.min(image.getHeight(), removedPoint.y + radius + 1);
							 ny++) {
							for (int nx = Math.max(0, removedPoint.x - radius);
								 nx < Math.min(image.getWidth(), removedPoint.x + radius + 1);
								 nx++) {

								Color colorFound = new Color(image.getRGB(nx,ny));

								if(colorMatch(targetColor, colorFound) && blackPixelImage.getRGB(nx, ny) == 0){  // if the neighbor is of correct color, grab it.
									toVisit.add(new Point(nx, ny));
									blackPixelImage.setRGB(nx,ny, 1);
								}
							}
						}

					}
					//if a region has number of points above or similar to the min region limit, add it to regions
					if(region.size() >= minRegion){
						regions.add(region);

					}

				}

			}
		}
	}
	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color color1, Color color2) {
		// comparing each color channel`s absolute value to a maximum difference value.
		return Math.abs(color1.getRed()- color2.getRed()) < maxColorDiff && Math.abs(color1.getBlue()- color2.getBlue()) < maxColorDiff && Math.abs(color1.getGreen()- color2.getGreen()) < maxColorDiff;
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		//finding the largest region from the regions array list
		if(regions.size()==0)return new ArrayList<Point>();
		ArrayList<Point> largest = regions.get(0);
		for (int j = 0; j<regions.size();j++){
			if(regions.get(j).size() > largest.size()){
				largest = regions.get(j);
			}
		}
		return largest;
	}

	/**
	 * Sets recoloredImage to be a copy of image,
	 * but with each region a uniform random color,
	 * so we can see where they are
	 */
	public void recolorImage() {
		//recoloring the image
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		for (ArrayList<Point> region : regions) {
			randomColor = new Color((int) (Math.random() * 16777216));
			for (Point point : region) {
				int x = point.x;
				int y = point.y;
				recoloredImage.setRGB(x, y, randomColor.getRGB());
			}
		}
	}
}
