import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import java.io.*;

import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class AsciiImageConverter
{
    public static char[] getGrayScale(int style)
    {
        switch(style)
        {
            case 2:
                return new char[]{'M', 'N', 'F', 'V', '$', 'I', '*', ':', '.'};
            case 3:
                return new char[]{'@', '#', '+', 39, ';', ':', ',', '.', '`', ' '};
            case 4:
                return new char[]{'$', '@', 'B', '%', '8', '&', 'W', 'M', '#', '*', 'o', 'a', 'h', 'k', 'b', 'd', 'p',
                        'q', 'w', 'm', 'Z', 'O', '0', 'Q', 'L', 'C', 'J', 'U', 'Y', 'X', 'z', 'c', 'v', 'u', 'n', 'x',
                        'r', 'j', 'f', 't', '/', '\\', '|', '(', ')', '1', '{', '}', '[', ']', '?', '-', '_', '+', '~',
                        '<', '>', 'i', '!', 'l', 'I', ';', ':', ',', '"', '^', '`', 39, '.', ' '};
            default:
                return new char[]{'M', 'N', 'm', 'd', 'h', 'y', 's', 'o', '+', '/', ':', '-', '.', 39, ' '};
        }
    }

    public static boolean yesOrNo(String answer)
    {
        String[] validAnswers = {"y", "yes"};
        for(String validAnswer:validAnswers)
            if(answer.equalsIgnoreCase(validAnswer))
                return true;
        return false;
    }

    public static String pixelsToCharacters(final char[] grayScale, int[] pixels, int[] numOfPixels)
    {
        String asciiLine = "";
        for(int i = 0; i < pixels.length; i++)
        {
            pixels[i]/=numOfPixels[i];
            asciiLine+=grayScale[Math.min(pixels[i]/(255/grayScale.length), grayScale.length-1)];
        }
        Arrays.fill(pixels, 0);
        Arrays.fill(numOfPixels,0);
        return asciiLine;
    }

    public static void saveToFile(String imageName, String image) throws IOException
    {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(imageName+".txt")));
        out.print(image);
        out.close();
    }

    public static void main(String[] args) throws IOException
    {
        final char[] grayScale;

        String fileName;
        String imageName;
        String asciiString = "";
        int width;
        int height;
        int chunkHeight;
        int charSize;
        boolean save;
        BufferedImage image;
        Color c;
        JFrame imageFrame;
        JTextArea asciiImage;

        Scanner in = new Scanner(System.in);

        System.out.print("Enter the name of image you want to convert along with its extension (Ex. picture.jpg): ");
        fileName = in.nextLine();
        imageName = fileName.substring(0, fileName.lastIndexOf('.'));

        System.out.print("Choose an ascii style (Choice 1 is the preferred style): ");
        grayScale = getGrayScale(in.nextInt());

        System.out.print("Enter the desired width of the image in characters (1-230): ");
        width = in.nextInt();

        System.out.print("Enter the desired font size of the image (Default - 20): ");
        charSize = in.nextInt();

        System.out.print("Would you like to save the ascii image to a text file [y/n]: ");
        save = in.next().equals("y");

        image = ImageIO.read(new File(fileName));

        height = image.getHeight()*width/image.getWidth();
        chunkHeight = (int)(height/2.25);

        int[] pixelChunks = new int[width];
        int[] pixelsInChunk = new int[width];
        int chunkWidth = image.getWidth()/width;

        int pixelIndex = -1;
        for(int y = 0; y<image.getHeight(); y++)
        {
            if((int)(y%((double)image.getHeight()/chunkHeight))==0 && y != 0)
                asciiString += pixelsToCharacters(grayScale, pixelChunks, pixelsInChunk)+"\n";
            for(int x = 0; x<image.getWidth(); x++)
            {
                if((int)(x%((double)image.getWidth()/width))==0)
                    pixelIndex++;
                c = new Color(image.getRGB(x,y));
                pixelChunks[pixelIndex] += (c.getRed() + c.getGreen() + c.getBlue())/3;
                pixelsInChunk[pixelIndex] += 1;
            }
            pixelIndex=-1;
        }
        asciiString += pixelsToCharacters(grayScale, pixelChunks, pixelsInChunk);

        if(save)
            saveToFile(imageName, asciiString);

        imageFrame = new JFrame("Ascii "+imageName);
        asciiImage = new JTextArea();
        //default font size should be 20
        asciiImage.setFont(new Font(Font.MONOSPACED, Font.PLAIN, charSize));
        asciiImage.append(asciiString);
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageFrame.setResizable(true);
        imageFrame.setSize((int)((width*12+12*2)*(double)charSize/20),(int)((chunkHeight*27+100)*(double)charSize/20-27));
        imageFrame.setLocationRelativeTo(null);
        imageFrame.add(asciiImage);
        imageFrame.setVisible(true);
        in.close();
    }
}