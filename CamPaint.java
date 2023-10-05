import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;


/**
 * Webcam based testing
 * Each region is a list of contiguous points with colors similar to a target color.
 * @author Tayeb Mohammadi
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor ;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();  //initializing region finder and clearing paint
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
//	protected void clearRecoloredImage() { finder.recoloredImage = image;}
	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		if(displayMode == 'w'){
			//'w': live webcam
			super.draw(g);
		} else if(displayMode == 'r'){
			//'r': recolored image
			finder.recolorImage();
			g.drawImage(finder.getRecoloredImage(), 0, 0, null);
		} else if(displayMode == 'p'){
			//'p': painting
			processImage();
			g.drawImage(painting, 0, 0, null);
		}

	}
	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// TODO: YOUR CODE HERE
		finder = new RegionFinder(image); // call RegionFinder
		if(targetColor!=null){
			finder.findRegions(targetColor);
		ArrayList<Point> paintbrush = finder.largestRegion();
		for(Point point: paintbrush){
			painting.setRGB(point.x,point.y, paintColor.getRGB());
		}
		finder.recolorImage();
	}}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
		targetColor = new Color(this.image.getRGB(x,y)); //set target color by clicking
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
