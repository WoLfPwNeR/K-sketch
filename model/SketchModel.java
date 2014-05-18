package model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Timer;

public class SketchModel extends Object {
	
	public ArrayList<IView> views = new ArrayList<IView>();
	
	private int modes = 1;
	private boolean drag = false;
	public int pressedX, pressedY, draggedX, draggedY;
	
	private ArrayList<ArrayList<Line2D.Float>> shapes = new ArrayList<ArrayList<Line2D.Float>>();
	private ArrayList<HashMap<Integer, Point2D.Float>> delta = new ArrayList<HashMap<Integer, Point2D.Float>>();
	private ArrayList<Color> colours = new ArrayList<Color>();
	private ArrayList<Integer> strokes = new ArrayList<Integer>();
	private ArrayList<Boolean> selected = new ArrayList<Boolean>();
	
	private ArrayList<Point2D.Float> coordChanges = new ArrayList<Point2D.Float>();
	private ArrayList<Double> timeChanges = new ArrayList<Double>();
	
	private ArrayList<Line2D.Float> lasso = new ArrayList<Line2D.Float>();
	private ArrayList<Integer> six = new ArrayList<Integer>();
	
	private Color currentColour = Color.BLACK;
	private Integer currentStroke = 1;
	
	private Point2D.Float eraser = new Point2D.Float();
	
	private boolean playing = false;
	private int time = 0;
	public static int timeMax = 5;
	public static final int FPS = 40;
	public static int curMax = 0;
	public static boolean mouseUp = true;

	
	private Timer timer = new Timer(1, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			incrementTimer();
		}
	});
	
	// Override the default construtor, making it private.
	public SketchModel() {
	}
	
	
	public void saveGame(){
		try{
			FileOutputStream saveFile = new FileOutputStream("sketch.sav");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			
			save.writeObject(modes);
			save.writeObject(drag);
			save.writeObject(pressedX);save.writeObject(pressedY);save.writeObject(draggedX);save.writeObject(draggedY);
			save.writeObject(shapes);
			save.writeObject(delta);
			save.writeObject(colours);
			save.writeObject(strokes);
			save.writeObject(selected);
			save.writeObject(coordChanges);
			save.writeObject(timeChanges);
			save.writeObject(lasso);
			save.writeObject(six);
			save.writeObject(currentColour);
			save.writeObject(currentStroke);
			save.writeObject(eraser);
			save.writeObject(playing);
			save.writeObject(time);
			save.writeObject(timeMax);
			save.writeObject(curMax);
			save.writeObject(mouseUp);
			
			save.close();
		}catch(Exception e){}
	}
	public void loadGame(){
		try{
			FileInputStream saveFile = new FileInputStream("sketch.sav");
			ObjectInputStream save = new ObjectInputStream(saveFile);
			
			modes=(Integer)save.readObject();
			drag=(Boolean)save.readObject();
			pressedX=(Integer)save.readObject();
			pressedY=(Integer)save.readObject();
			draggedX=(Integer)save.readObject();
			draggedY=(Integer)save.readObject();
			shapes=(ArrayList<ArrayList<Line2D.Float>>)save.readObject();
			delta=(ArrayList<HashMap<Integer, Point2D.Float>>)save.readObject();
			colours=(ArrayList<Color>)save.readObject();
			strokes=(ArrayList<Integer>)save.readObject();
			selected=(ArrayList<Boolean>)save.readObject();
			coordChanges=(ArrayList<Point2D.Float>)save.readObject();
			timeChanges=(ArrayList<Double>)save.readObject();
			lasso=(ArrayList<Line2D.Float>)save.readObject();
			six=(ArrayList<Integer>)save.readObject();
			currentColour=(Color)save.readObject();
			currentStroke=(Integer)save.readObject();
			eraser=(Point2D.Float)save.readObject();
			playing=(Boolean)save.readObject();
			time=(Integer)save.readObject();
			timeMax=(Integer)save.readObject();
			curMax=(Integer)save.readObject();
			mouseUp=(Boolean)save.readObject();
			
			save.close();
		}catch(Exception e){}
	}
	
	
	
	public int toPrevFrame(int time){
		return time - time%(1000/FPS);
	}
	
	public ArrayList<Integer> getSIX(){
		return six;
	}
	public void setSIX(ArrayList<Integer> six){
		this.six = six;
		this.updateAllViews();
	}
	
	public boolean getDrag(){
		return drag;
	}
	public void setDrag(boolean drag){
		this.drag = drag;
		if(drag){
			startTimer();
		}else{
			stopTimer();
		}
		this.updateAllViews();
	}
	
	public ArrayList<HashMap<Integer, Point2D.Float>> getDelta(){
		return delta;
	}
	public void setDelta(ArrayList<HashMap<Integer, Point2D.Float>> delta){
		this.delta = delta;
		this.updateAllViews();
	}
	
	public void startTimer(){
		timer.start();
		playing = true;
		this.updateAllViews();
	}
	public void stopTimer(){
		timer.stop();
		playing = false;
		this.updateAllViews();
	}
	
	public void incrementTimer(){
		
		if(time%(1000/FPS) == 0){
			if(drag){
				for(Integer i:six){
					if(delta.get(i).get(toPrevFrame(time))!=null){
						delta.get(i).put(time, new Point2D.Float(draggedX-pressedX+draggedX-delta.get(i).get(toPrevFrame(time)).x, draggedY-pressedY+draggedX-delta.get(i).get(toPrevFrame(time)).x));
						//delta.get(i).put(time, new Point2D.Float(draggedX-pressedX, draggedY-pressedY));
					}else{
						delta.get(i).put(time, new Point2D.Float(draggedX-pressedX, draggedY-pressedY));
					}
					
				}
			}
			if(time>=timeMax*1000){
				timeMax+=1;
				System.out.println("doubled");
			}
			if(time>=curMax && mouseUp){
				System.out.println("time>=curMax:"+curMax);
				stopTimer();
			}
			this.updateAllViews();
			//System.out.println("update@"+time);
		}
		time++;
	}
	
	public ArrayList<Double> getTimeChanges(){
		return timeChanges;
	}
	public void setTimeChanges(ArrayList<Double> timeChanges){
		this.timeChanges = timeChanges;
		this.updateAllViews();
	}
	
	public ArrayList<Point2D.Float> getCoordChanges(){
		return coordChanges;
	}
	public void setCoordChanges(ArrayList<Point2D.Float> coordChanges){
		this.coordChanges = coordChanges;
		this.updateAllViews();
	}
	
	public int getTimeMax(){
		return timeMax;
	}
	public void setTimeMax(int timeMax){
		this.timeMax = timeMax;
		this.updateAllViews();
	}
	
	public int getTime(){
		return time;
	}
	public void setTime(int time){
		this.time = time;
		this.updateAllViews();
	}
	
	public boolean getPlaying(){
		return playing;
	}
	public void setPlaying(boolean playing){
		this.playing = playing;
		this.updateAllViews();
	}
	
	public void resetSelected(){
		for(int i = 0; i < selected.size(); ++i){
			selected.set(i, false);
		}
		this.updateAllViews();
	}
	
	public Point2D.Float getEraser(){
		return eraser;
	}
	public void setEraser(Point2D.Float eraser){
		this.eraser = eraser;
		this.updateAllViews();
	}
	
	public ArrayList<Boolean> getSelected(){
		return selected;
	}
	public void setSelected(ArrayList<Boolean> selected){
		this.selected = selected;
		this.updateAllViews();
	}
	
	public Integer getCurrentStroke(){
		return currentStroke;
	}
	public void setCurrentStroke(Integer currentStroke){
		this.currentStroke = currentStroke;
		this.updateAllViews();
	}
	
	public Color getCurrentColour(){
		return currentColour;
	}
	public void setCurrentColour(Color currentColour){
		this.currentColour = currentColour;
		this.updateAllViews();
	}
	
	public ArrayList<Integer> getStrokes(){
		return strokes;
	}
	public void setStrokes(ArrayList<Integer> strokeSizes){
		this.strokes = strokeSizes;
		this.updateAllViews();
	}
	
	public ArrayList<Color> getColours(){
		return colours;
	}
	public void setColours(ArrayList<Color> colours){
		this.colours = colours;
		this.updateAllViews();
	}
	
	public ArrayList<Line2D.Float> getLasso(){
		return lasso;
	}
	public void setLasso(ArrayList<Line2D.Float> lasso){
		this.lasso = lasso;
		this.updateAllViews();
	}
	
	public ArrayList<ArrayList<Line2D.Float>> getShapes(){
		return shapes;
	}
	public void setShapes(ArrayList<ArrayList<Line2D.Float>> shapes){
		this.shapes = shapes;
		this.updateAllViews();
	}
	
	public int getMode(){
		return this.modes;
	}
	public void setMode(int mode){
		this.modes = mode;
		this.updateAllViews();
	}
	
	/** Add a new view of this triangle. */
	public void addView(IView view) {
		this.views.add(view);
		view.updateView();
	}

	/** Remove a view from this triangle. */
	public void removeView(IView view) {
		this.views.remove(view);
	}

	/** Update all the views that are viewing this triangle. */
	private void updateAllViews() {
		for (IView view : this.views) {
			view.updateView();
		}
	}
}
