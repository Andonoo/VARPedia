package wikiSpeakModel;

import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
	 * Get ImageView element
	 * @return
	 */
	public ImageView getImageView() {
		ImageView view = new ImageView();
		view.setImage(_image);
		return view;
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