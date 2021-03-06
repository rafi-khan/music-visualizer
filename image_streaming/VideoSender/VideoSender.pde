import processing.video.*;

import javax.imageio.*;
import java.awt.image.*; 

// This is the port we are sending to
int clientPort = 10001; 
// This is our object that sends UDP out
DatagramSocket ds; 
// Capture object
Capture cam;

void setup() {
  size(320,240);
  // Setting up the DatagramSocket, requires try/catch
  try {
    ds = new DatagramSocket();
  } catch (SocketException e) {
    e.printStackTrace();
  }
}

void draw() {
  background(frameCount%255, 0, 0);
  broadcast();
}


// Function to broadcast a PImage over UDP
// Special thanks to: http://ubaa.net/shared/processing/udp/
// (This example doesn't use the library, but you can!)
void broadcast() {
  // We need a buffered image to do the JPG encoding
  BufferedImage bimg = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );

  // Transfer pixels from localFrame to the BufferedImage
  loadPixels();
  bimg.setRGB( 0, 0, width, height, pixels, 0, width);

  // Need these output streams to get image as bytes for UDP communication
  ByteArrayOutputStream baStream	= new ByteArrayOutputStream();
  BufferedOutputStream bos		= new BufferedOutputStream(baStream);

  // Turn the BufferedImage into a JPG and put it in the BufferedOutputStream
  // Requires try/catch
  try {
    ImageIO.write(bimg, "jpg", bos);
  } 
  catch (IOException e) {
    e.printStackTrace();
  }

  // Get the byte array, which we will send out via UDP!
  byte[] packet = baStream.toByteArray();

  // Send JPEG data as a datagram
  println("Sending datagram with " + packet.length + " bytes");
  try {
    ds.send(new DatagramPacket(packet,packet.length, InetAddress.getByName("localhost"),clientPort));
  } 
  catch (Exception e) {
    e.printStackTrace();
  }
}

