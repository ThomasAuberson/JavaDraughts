import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/*
 * Java Draughts
 * 
 * Author: Thomas Auberson
 * Version: 0.12
 * 
 * This class controls the mouse action listener.  Extracted from Template Library v0.13b
 */

public class MouseHandler implements MouseListener, MouseWheelListener, MouseMotionListener {
	
	//FIELDS
	private Display display;
	
	//CONSTRUCTOR
	public MouseHandler(Display display){
		this.display = display;
		//System.out.println("Mouse Listener Active");
	}
	
	//ACTION LISTENERS
	@Override
	public void mouseClicked(MouseEvent m) {
		// TODO Auto-generated method stub
		//display.mouseClicked(m.getLocationOnScreen(), m.getButton());
	}

	@Override
	public void mouseEntered(MouseEvent m) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent m) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent m) {	
		// TODO Auto-generated method stub
		//display.mousePressed(m.getLocationOnScreen(), m.getButton());
	}

	@Override
	public void mouseReleased(MouseEvent m) {
		// TODO Auto-generated method stub
		display.mouseReleased(m.getLocationOnScreen(), m.getButton());
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent mw) {
		// TODO Auto-generated method stub
		//display.mouseWheelMoved(mw.getWheelRotation());
	}

	@Override
	public void mouseDragged(MouseEvent mm) {
		// TODO Auto-generated method stub
		//display.mouseDragged(mm.getPoint());
		
	}

	@Override
	public void mouseMoved(MouseEvent mm) {
		// TODO Auto-generated method stub
		//display.mouseMoved(mm.getPoint());		
	}
}
