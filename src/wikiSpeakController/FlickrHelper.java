package wikiSpeakController;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.Size;

/**
 * Class for dealing with Flickr API calls.
 * 
 * @author andrew
 *
 */
public class FlickrHelper {
	/**
	 * Fetches 10 images from flickr with the given search term and puts them
	 * in a directory corresponding to the provided creation name.
	 * @param creationName
	 * @param searchTerm
	 */
	public static void getImages(String creationName, String searchTerm) {
		try {
			String command = "mkdir -p " + "Creations/" + creationName + "/.tempPhotos";
			ShellHelper.execute(command);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			String apiKey = getAPIKey("key");
			String secret = getAPIKey("secret");
			
			Flickr searcher = new Flickr(apiKey, secret, new REST());
			
			PhotosInterface photos = searcher.getPhotosInterface();
			SearchParameters params = new SearchParameters();
			params.setSort(SearchParameters.RELEVANCE);
			params.setMedia("photos");
			params.setText(searchTerm);
			
			PhotoList<Photo> results = photos.search(params, 10, 0);
			
			int count = 1;
			
			for (Photo photo: results) {
	        	try {
	        		BufferedImage image = photos.getImage(photo,Size.LARGE);
		        	String filename = searchTerm + count + ".jpg";
		        	count ++;
		        	File outputfile = new File("Creations/" + creationName + "/.tempPhotos",filename);
		        	ImageIO.write(image, "jpg", outputfile);
		        	if (count == 11) {
		        		break;
		        	}
	        	} catch (FlickrException fe) {
	        		System.err.println("Ignoring image " +photo.getId() +": "+ fe.getMessage());
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the key corresponding to the given string. 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private static String getAPIKey(String key) throws IOException{
		File file = new File("flickr-api-keys.txt"); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
				
		String line;
		while ( (line = br.readLine()) != null ) {
			if (line.trim().startsWith(key+"=")) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find key in config file " + file.getName());
	}
}
