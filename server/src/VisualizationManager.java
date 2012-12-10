/*
 * http://www.ctctlabs.com/index.php/blog/detail/how_to_use_processing_from_the_command_line_to_generate_images/
 * 
 */
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PGraphics;
import processing.net.Client;
import processing.net.Server;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.javasound.JSMinim;
import ddf.minim.spi.MinimServiceProvider;
import org.tritonus.share.sampled.file.TAudioFileReader;
import javazoom.spi.mpeg.sampled.file.tag.TagParseListener;

public class VisualizationManager extends PApplet {
	Minim minim;					// Sound manager
	MinimServiceProvider js;
	private AudioRenderer well;		// The  renderers
	private AudioPlayer song;		// The song
	private int port, exitFrame;	// Port on which the server listens
	private Server server;			// For receiving touch events
	private PGraphics img;
	
	byte[] buffer;					// Buffer into which to read client data
	
	public VisualizationManager(String id) {
		port = Integer.parseInt(id);
		System.out.println("New VisualizationManager on port "+port);
	}
	
	public void setup() {
		size(320, 240);				// Standard phone size (landscape)
		frameRate(30);				// As good as we'll ever need
		exitFrame = (int) (10*frameRate);
		
		js = new JSMinim(this);
		minim = new Minim(js);
		song = minim.loadFile("paris.mp3", 1024);
		song.play();
		
		img = createGraphics(width, height);
		well = new WellRenderer(song);
		song.addListener(well);
		well.setImg(img);
		well.setup();

		server = new Server(this, port);
		buffer = new byte[16];
	}
	public void draw() {
		well.draw();
		// img.beginDraw();
		// img.background(frameCount%255, 0, 0);
		// img.endDraw();
		Client client = server.available();
		if(client != null) {
			// Launch new thread to read bytes and process here
			client.readBytes(buffer);
		}
		// Launch new thread to do the broadcasting here
		img = well.getImg();
        broadcast(img);
		image(img,0,0);
		well.setG(get());
	}
	
	public void stop() {
		song.close();
		minim.stop();
		super.stop();
	}

	// Function to broadcast a PImage over the Server (UDP probably)
	// Special thanks to: http://ubaa.net/shared/processing/udp/
	// (This example doesn't use the library, but you can!)
	void broadcast(PImage img) {
		if(img == null) println("IMG IS NULL IN BROADCAST");
		BufferedImage bimg = new BufferedImage( img.width,img.height, BufferedImage.TYPE_INT_RGB );
		// Transfer pixels from localFrame to the BufferedImage
		img.loadPixels();
		bimg.setRGB( 0, 0, img.width, img.height, img.pixels, 0, img.width);
		ByteArrayOutputStream baStream	= new ByteArrayOutputStream();
		BufferedOutputStream bos		= new BufferedOutputStream(baStream);
		try {
			ImageIO.write(bimg, "jpg", bos);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	  	byte[] packet = baStream.toByteArray();
	  	server.write(packet);
	}
}

