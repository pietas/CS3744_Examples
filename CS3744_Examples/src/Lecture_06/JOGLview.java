package Lecture_06;
import javax.media.opengl.GLProfile;

/**
 * Lecture 6: A minimal program that shows JOGL profiles.
 *
 * @author Denis Gracanin
 * @version 1
 */
public class JOGLview {

  public static void main( String [] args ) {
    System.out.println("Default device: " + GLProfile.getDefaultDevice());    	
    System.out.println("Default profile: " + GLProfile.getDefault());
    System.out.println("Maximum profile: " + GLProfile.getMaximum(false));
    System.out.println("Maximum programmable profile: " + GLProfile.getMaxProgrammable(false));
    System.out.println("Minimum profile: " + GLProfile.getMinimum(false));
    System.out.println("Availability: " + GLProfile.glAvailabilityToString());
  }
}