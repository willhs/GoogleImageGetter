import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;


public class ImageGetter {

	public ImageGetter() throws Exception{
		String search = "monkey";
		
	//	URL url = new URL("https://www.google.co.nz/search?q=google&um=1&ie=UTF-8&hl=en&tbm=isch&source=og&sa=N&tab=wi&ei=UtdkUpu0IaykiAeuioGgBA#hl=en&q="+search+"&tbm=isch&um=1");
		URL url = new URL("https://www.google.com/search?q="+search+"&tbm=isch");
		HttpURLConnection httpCon = (HttpURLConnection)url.openConnection();
		httpCon.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.17 Safari/537.36"); // Chrome/20 worked too
		BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));

		FileWriter filewrite = new FileWriter(new File("urlfile.txt"));		
		String line;
		String html="";
		
		// reads the whole html file into a string.
		while((line = in.readLine()) != null){			
			System.out.println(line);
			filewrite.write(line);		
			html += line;
		}
		
		in.close();
		filewrite.close();
		// finds the first image url from the search url
		String imgTypes = "\\.jpg|\\.png|\\.bmp|\\.gif|\\.jpeg"; // this will be at the end of an image url.
		Pattern imgRegex = Pattern.compile("\\=.{7,50}"+imgTypes);
		Matcher matcher = imgRegex.matcher(html);
		while(matcher.find()){
			String imgURL = matcher.group().substring(1); // .substring(1) to remove '='
            System.out.printf("I found the text" +
                    " \"%s\" starting at " +
                    "index %d and ending at index %d.%n",
                    imgURL,
                    matcher.start(),
                    matcher.end());
            try{ // TESTS TO SEE IF THE STRING IS A VALID URL.
            URL testURL = new URL(imgURL);            
            	testURL.toURI();
            }catch(MalformedURLException e){ //malformed url exception
            	continue;
            }
            
            Matcher nameFinder = Pattern.compile("/([^/]+"+imgTypes+")").matcher(imgURL);
            if(!nameFinder.find()) fail("couldn't find name");
            String imgName = nameFinder.group(1);
            String formatName =imgURL.substring(imgURL.length()-3);            
    		
    		System.out.println("Image URL: "+imgURL+" img format name: "+formatName+" img name: "+imgName);    		
    		BufferedImage image = ImageIO.read(new URL(imgURL));
    		if(image == null) continue;
    		ImageIO.write(image, formatName, new File(imgName));
            
		}
		
		
		

				
		
	}
	
	public static void main(String[] args) throws Exception{
		new ImageGetter();
	}
	public void fail(String text){
		System.err.println("An error occured: "+text);
		System.exit(1);
	}
}
