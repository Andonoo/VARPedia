package wikiSpeakModel;

import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class to represent an image selection by the user to be put in a creation.
 */
public class ImageItem {
	Image _image;
	CheckBox _checkBox;
	
	/**
	 * Represent a ImageItem UI element with check box
	 * @param image
	 */
	public ImageItem(Image image) {
		_image = image;
		_checkBox = new CheckBox();
	}
	
	/**
	 * Get the image URL
	 * @return URL of the image
	 */
	public String getPath() {
		return _image.getUrl();
	}
	
	/**
	 * Get ImageView element
	 * @return
	 */
	public ImageView getImageView() {
		ImageView view = new ImageView();
		// TODO To be tweaked
		view.setFitHeight(200);
		view.setFitWidth(200);
		view.setImage(_image);
		return view;
	}
	
	/**
	 * Select/Deselect the ImageItem
	 * @return the status of ImageItem, selected or not.
	 */
	public boolean toggleSelect() {
		boolean newState = !_checkBox.isSelected();
		_checkBox.setSelected(newState);
		return newState;
	}
	
	/**
	 * Get check box UI element
	 * @return
	 */
	public CheckBox getCheckBox() {
		return _checkBox;
	}
	
	/**
	 * Determine if the check box is selected
	 * @return
	 */
	public Boolean isSelected() {
		return _checkBox.isSelected();
	}
}
