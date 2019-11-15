package topica.linhnv5.video.teaching.util;

import java.awt.Color;
import java.util.HashMap;

/**
 * Color utility, decode and encode color
 * 
 * @author ljnk975
 */
public class ColorUtil {

    public static HashMap<String, Color> HTMLColors;

    static {
        HTMLColors = new HashMap<>();
        HTMLColors.put("red", Color.red);
        HTMLColors.put("green", Color.green);
        HTMLColors.put("blue", Color.blue);
        HTMLColors.put("cyan", Color.cyan);
        HTMLColors.put("magenta", Color.magenta);
        HTMLColors.put("yellow", Color.yellow);
        HTMLColors.put("black", Color.black);
        HTMLColors.put("white", Color.white);
        HTMLColors.put("gray", Color.gray);
        HTMLColors.put("darkgray", Color.darkGray);
        HTMLColors.put("lightgray", Color.lightGray);
        HTMLColors.put("orange", Color.orange);
        HTMLColors.put("pink", Color.pink);
    }
        
    public static Color getColorForName(String name, Color defaultColor) {
        if (HTMLColors.containsKey(name.toLowerCase()))
            return (Color)HTMLColors.get(name.toLowerCase());
        return defaultColor;
    }

	public static Color decodeColor(String color, Color defaultColor) {
		color.trim();
		if (color.startsWith("#")) {
			String colorVal = color.substring(1);
			try {
				colorVal = new Integer(Integer.parseInt(colorVal, 16)).toString();
				return Color.decode(colorVal.toLowerCase());
			} catch (Exception ex) {
			}
			return defaultColor;
		}
		return getColorForName(color, defaultColor);
	}
    
    public static String encodeColor(Color color) {        
        return "#"+Integer.toHexString(color.getRGB()-0xFF000000).toUpperCase();  
    }

    public static Color decodeColor(String color) {
    	return decodeColor(color, null);
    }

}
