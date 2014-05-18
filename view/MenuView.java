package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.*;

public class MenuView extends JPanel {
	private SketchModel model;
	
	private JButton btnDraw = new JButton("Draw");
	private JButton btnErase = new JButton("Erase");
	private JButton btnSelect  = new JButton("Select");
	
	private String[] colours = {"Black", "Red", "Yellow", "Blue"};
	private JLabel lblColour = new JLabel("Colour:");
	private JComboBox cbColour = new JComboBox(colours);
	private String[] strokes = {"1", "2", "3", "4", "5", "6"};
	private JLabel lblStroke = new JLabel("Stroke Size:");
	private JComboBox cbStroke = new JComboBox(strokes);
	
	private JButton btnSave = new JButton("Save");
	private JButton btnLoad = new JButton("Load");
	//TODO add more buttons
	public MenuView(SketchModel aModel) {
		super();
		this.model = aModel;

		this.layoutView();
		this.registerControllers();

		this.model.addView(new IView() {
			public void updateView() {
				// Updating the view includes enabling/disabling components!
				btnDraw.setEnabled(model.getMode() != 1);
				btnErase.setEnabled(model.getMode() != 2);
				btnSelect.setEnabled(model.getMode() != 3);
				//TODO add more buttons
			}

		});
	}
	
	private void layoutView() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.add(btnDraw);
		this.add(btnErase);
		this.add(btnSelect);
		this.add(new JLabel("     "));
		this.add(lblColour);
		this.add(cbColour);
		this.add(new JLabel("     "));
		this.add(lblStroke);
		this.add(cbStroke);
		this.add(new JLabel("     "));
		this.add(btnSave);
		this.add(btnLoad);
	}


	private void registerControllers() {
		this.btnDraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				model.setMode(1);
				model.resetSelected();
				model.stopTimer();
				System.out.println(model.getMode());
			}
		});

		this.btnErase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				model.setMode(2);
				model.resetSelected();
				model.stopTimer();
				System.out.println(model.getMode());
			}
		});
		
		this.btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				model.setMode(3);
				System.out.println(model.getMode());
			}
		});
		
		this.cbColour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(cbColour.getSelectedIndex() == 0){
					model.setCurrentColour(Color.BLACK);
				}else if(cbColour.getSelectedIndex() == 1){
					model.setCurrentColour(Color.RED);
				}else if(cbColour.getSelectedIndex() == 2){
					model.setCurrentColour(Color.YELLOW);
				}else if(cbColour.getSelectedIndex() == 3){
					model.setCurrentColour(Color.BLUE);
				}
			}
		});
		
		this.cbStroke.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if(cbStroke.getSelectedIndex() == 0){
					model.setCurrentStroke(1);
				}else if(cbStroke.getSelectedIndex() == 1){
					model.setCurrentStroke(3);
				}else if(cbStroke.getSelectedIndex() == 2){
					model.setCurrentStroke(5);
				}else if(cbStroke.getSelectedIndex() == 3){
					model.setCurrentStroke(7);
				}else if(cbStroke.getSelectedIndex() == 4){
					model.setCurrentStroke(9);
				}else if(cbStroke.getSelectedIndex() == 5){
					model.setCurrentStroke(11);
				}
			}
		});
		
		this.btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				model.saveGame();
			}
		});
		
		this.btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				model.loadGame();
			}
		});
	}
}
