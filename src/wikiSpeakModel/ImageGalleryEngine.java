package wikiSpeakModel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;

public class ImageGalleryEngine {
	private List<ImageItem> _images;
	
	public ImageGalleryEngine(File path) throws Exception {
		_images = new ArrayList<ImageItem>();
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				Image image = new Image(f.toURI().toString());
				ImageItem imageItem = new ImageItem(image);
				_images.add(imageItem);
			}
		} else {
			throw new Exception("Need to be directory");
		}
	}
	
	/**
	 * Get the list of selected images
	 * @return List<ImageItem>
	 */
	public List<ImageItem> getSelectedImage() {
		List<ImageItem> selectedImages = new ArrayList<ImageItem>();
		for (ImageItem image : _images) {
			if (image.isSelected()) {
				selectedImages.add(image);
			}
		}
		return selectedImages;
	}
	
	/**
	 * Get the list of all images
	 * @return List<ImageItem>
	 */
	public List<ImageItem> getImages(){
		return _images;
	}
}