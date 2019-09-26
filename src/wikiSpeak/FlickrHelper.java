package wikiSpeak;

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

public class FlickrHelper {
	public static void getImages(String searchTerm) {
		try {
			String apiKey = getKey();
			String secret = getSecret();
			
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
		        	File outputfile = new File("Creations/.tempPhotos",filename);
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

	private static String getSecret() throws IOException {
		File file = new File("/home/student/Documents/flickrSecret.txt"); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
				
		String line;
		while ( (line = br.readLine()) != null ) {
			if (line.trim().startsWith("secret=")) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find secret in config file " + file.getName());
	}

	private static String getKey() throws IOException {
		File file = new File("/home/student/Documents/flickrKey.txt"); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
				
		String line;
		while ( (line = br.readLine()) != null ) {
			if (line.trim().startsWith("key=")) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find key in config file " + file.getName());
	}
}
