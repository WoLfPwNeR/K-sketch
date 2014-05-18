package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import model.IView;
import model.SketchModel;

public class DrawpadView extends JComponent{
	private SketchModel model;
	
	public DrawpadView(SketchModel aModel) {
		super();
		this.model = aModel;
		this.layoutView();
		this.registerControllers();
		this.model.addView(new IView() {

			/** The model changed. Ask the system to repaint the triangle. */
			public void updateView() {
				repaint();
			}

		});
	}
	
	/** How should it look on the screen? */
	private void layoutView() {
		this.setPreferredSize(new Dimension(800, 600));
		this.setBorder(BorderFactory.createEtchedBorder());
	}

	/** Register event Controllers for mouse clicks and motion. */
	private void registerControllers() {
		MouseInputListener mil = new MController();
		this.addMouseListener(mil);
		this.addMouseMotionListener(mil);
	}

	/** Paint the triangle, and "handles" for resizing if it was selected. */
	public void paintComponent(Graphics g2) {
		super.paintComponent(g2);
		Graphics2D g = (Graphics2D)g2;
		
		for(int i = 0; i < model.getShapes().size(); ++i){//for every shape
			g.setStroke(new BasicStroke(model.getStrokes().get(i)));
			if(model.getSelected().get(i) == false){
				g.setColor(model.getColours().get(i));
			}else{
				g.setColor(Color.GREEN);
				//System.out.println("brighter!");
			}
			int dx = 0, dy = 0;
			try{
				dx = (int)(model.getDelta().get(i).get(model.toPrevFrame(model.getTime())).x);
				dy = (int)(model.getDelta().get(i).get(model.toPrevFrame(model.getTime())).y);
			}catch(NoSuchElementException ex){}catch(NullPointerException ex2){}
			//System.out.println("dx:"+dx+" dy:"+dy);
			if(model.pressedX==-dx&&model.pressedY==-dy){
				dx=0;dy=0;
			}
			g.translate(dx, dy);
			for(int j = 0; j < model.getShapes().get(i).size(); ++j){//for every line in shape
				g.drawLine((int)model.getShapes().get(i).get(j).x1, (int)model.getShapes().get(i).get(j).y1, (int)model.getShapes().get(i).get(j).x2, (int)model.getShapes().get(i).get(j).y2);
			}
			g.translate(-dx, -dy);
		}
		for(Line2D.Float line : model.getLasso()){
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.GREEN);
			g.drawLine((int)line.x1, (int)line.y1, (int)line.x2, (int)line.y2);
		}
		if(model.getMode() == 2){
			g.setStroke(new BasicStroke(1));
			g.setColor(Color.BLACK);
			g.drawOval((int)model.getEraser().x-5, (int)model.getEraser().y-5, 10, 10);
		}
	}
	
	private class MController extends MouseInputAdapter {

		int xPressed, yPressed, xDragged, yDragged, xReleased, yReleased, xMoved, yMoved;
		ArrayList<Line2D.Float> tempShape;

		
		public void mousePressed(MouseEvent e) {			
			if(model.getMode() == 1){//drawing
				Line2D.Float line = new Line2D.Float(e.getX(), e.getY(), e.getX(), e.getY());
				
				ArrayList<ArrayList<Line2D.Float>> shapes = model.getShapes();
				ArrayList<Line2D.Float> shape = new ArrayList<Line2D.Float>();
				shape.add(line);
				shapes.add(shape);
				model.setShapes(shapes);
				
				ArrayList<Color> colours = model.getColours();
				colours.add(model.getCurrentColour());
				model.setColours(colours);
				
				ArrayList<Integer> strokes = model.getStrokes();
				strokes.add(model.getCurrentStroke());
				model.setStrokes(strokes);
				
				ArrayList<Boolean> selected = model.getSelected();
				selected.add(false);
				model.setSelected(selected);
				
				ArrayList<HashMap<Integer, Point2D.Float>> delta = model.getDelta();
				delta.add(new HashMap<Integer, Point2D.Float>());
				model.setDelta(delta);
				
			}else if(model.getMode() == 2){//erasing
				model.setEraser(new Point2D.Float(e.getX(), e.getY()));
				
				for(int i = 0; i < model.getShapes().size(); ++i){//each shape		
					int dx = 0, dy = 0;
					try{
						dx = (int)(model.getDelta().get(i).get(model.toPrevFrame(model.getTime())).x);
						dy = (int)(model.getDelta().get(i).get(model.toPrevFrame(model.getTime())).y);
					}catch(NoSuchElementException ex){}catch(NullPointerException ex2){}
					//System.out.println("dx:"+dx+" dy:"+dy);
					if(model.pressedX==-dx&&model.pressedY==-dy){
						dx=0;dy=0;
					}
					
					boolean delete = false;
					for(int j = 0; j < model.getShapes().get(i).size(); ++j){//each segment
						if(model.getShapes().get(i).get(j).ptSegDist(e.getX()-dx, e.getY()-dy) < 5){
							delete = true;
							//System.out.println("delete?");
							break;
						}
					}
					if(delete){
						model.getShapes().remove(i);
						model.getStrokes().remove(i);
						model.getColours().remove(i);
						model.getSelected().remove(i);
						model.getDelta().remove(i);
						--i;
					}
				}
				
			}else if(model.getMode() == 3){//selecting+moving
				boolean selected = false;
				ArrayList<Integer> six = new ArrayList<Integer>();
				for(int i=0;i<model.getShapes().size();++i){//for shape
					
					int dx = 0, dy = 0;
					try{
						dx = (int)(model.getDelta().get(i).get(model.toPrevFrame(model.getTime())).x);
						dy = (int)(model.getDelta().get(i).get(model.toPrevFrame(model.getTime())).y);
					}catch(NoSuchElementException ex){}catch(NullPointerException ex2){}
					//System.out.println("dx:"+dx+" dy:"+dy);
					if(model.pressedX==-dx&&model.pressedY==-dy){
						dx=0;dy=0;
					}
					//System.out.println("dx:"+dx+" dy:"+dy+"getx:"+e.getX()+" gety:"+e.getY());
					
					
					for(int j=0;j<model.getShapes().get(i).size();++j){//for line
						if(model.getShapes().get(i).get(j).ptSegDist(e.getX()-dx, e.getY()-dy) < 5 && model.getSelected().get(i)){
							selected = true;
							for(int k=0;k<model.getSelected().size();++k){
								if(!six.contains(k) && model.getSelected().get(k)){
									six.add(k);
								}
							}
							
							//System.out.println("delete?");
							break;
						}
					}
				}

				model.setSIX(six);
				if(selected){//if sth alrdy selected and touches close
					model.setDrag(true);
					model.pressedX = e.getX(); model.pressedY = e.getY();
					//System.out.println("pressedX:"+e.getX()+" pressedY:"+e.getY());
				}else{
					model.setDrag(false);
					
					
					
					Line2D.Float line = new Line2D.Float(e.getX(), e.getY(), e.getX(), e.getY());
					ArrayList<Line2D.Float> lasso = model.getLasso();
					lasso.add(line);
					model.setLasso(lasso);
				}
			}
			
			xPressed = e.getX(); yPressed = e.getY();
			model.pressedX=e.getX(); model.pressedY=e.getY();
			xDragged = xPressed; yDragged = yPressed;
			model.mouseUp = false;
			//repaint();
		} // mouseDragged
		
		
		public void mouseDragged(MouseEvent e) {
			if(model.getMode() == 1){
				Line2D.Float line = new Line2D.Float(xDragged, yDragged, e.getX(), e.getY());
				ArrayList<ArrayList<Line2D.Float>> shapes = model.getShapes();
				shapes.get(shapes.size() - 1).add(line);
				model.setShapes(shapes);
				
			}else if(model.getMode() == 2){
				model.setEraser(new Point2D.Float(e.getX(), e.getY()));
				
				for(int i = 0; i < model.getShapes().size(); ++i){//each shape
					boolean delete = false;
					
					int dx = 0, dy = 0;
					try{
						dx = (int)(model.getDelta().get(i).get(model.toPrevFrame(model.getTime())).x);
						dy = (int)(model.getDelta().get(i).get(model.toPrevFrame(model.getTime())).y);
					}catch(NoSuchElementException ex){}catch(NullPointerException ex2){}
					//System.out.println("dx:"+dx+" dy:"+dy);
					if(model.pressedX==-dx&&model.pressedY==-dy){
						dx=0;dy=0;
					}
					
					for(int j = 0; j < model.getShapes().get(i).size(); ++j){//each segment
						if(model.getShapes().get(i).get(j).ptSegDist(e.getX()-dx, e.getY()-dy) < 5){
							delete = true;
							System.out.println("delete?");
							break;
						}
					}
					if(delete){
						model.getShapes().remove(i);
						model.getStrokes().remove(i);
						model.getColours().remove(i);
						model.getSelected().remove(i);
						model.getDelta().remove(i);
						--i;
					}
				}
			}else if(model.getMode() == 3){
				if(model.getDrag()){//if gonna drag
					model.draggedX = e.getX(); model.draggedY = e.getY();
				}else{
					Line2D.Float line = new Line2D.Float(xDragged, yDragged, e.getX(), e.getY());
					ArrayList<Line2D.Float> lasso = model.getLasso();
					lasso.add(line);
					model.setLasso(lasso);
				}
			}
			
			xDragged = e.getX(); yDragged = e.getY();
			model.draggedX=e.getX();model.draggedY=e.getY();
			//repaint();
		} // mouseDragged
		
		
		public void mouseReleased(MouseEvent e) {
			if(model.getMode() == 1){

			}else if(model.getMode() == 2){
				
			}else if(model.getMode() == 3){
				if(model.getDrag()){
					model.setDrag(false);
					if(model.getTime()>model.curMax){
						model.curMax=model.getTime();
					}
				}else{
					Polygon selection = new Polygon();
					for(Line2D.Float line : model.getLasso()){
						selection.addPoint((int)line.x2, (int)line.y2);
					}
					for(int i = 0; i < model.getShapes().size(); ++i){//for every shape
						boolean flag = true;
						for(int j = 0; j < model.getShapes().get(i).size(); ++j){//for every segment
							if(!selection.contains(model.getShapes().get(i).get(j).x2, model.getShapes().get(i).get(j).y2)){
								flag = false;
							}
						}
						
						ArrayList<Boolean> selected = model.getSelected();
						selected.set(i, flag);
						model.setSelected(selected);
					}
					//System.out.println("selectedIndexes.size(): "+model.getSelectedIndexes().size());
					ArrayList<Line2D.Float> lasso = model.getLasso();
					lasso.clear();
					model.setLasso(lasso);
				}
			}
			
			xReleased = e.getX(); yReleased = e.getY();
			model.mouseUp = true;
			//repaint();
		} // mouseDragged*/
		
		public void mouseMoved(MouseEvent e) {
			if(model.getMode() == 2){
				model.setEraser(new Point2D.Float(e.getX(), e.getY()));
			}
			
			xMoved = e.getX(); yMoved = e.getY();
			//repaint();
		} // mouseDragged*/
	} // MController
}
