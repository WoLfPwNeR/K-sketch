package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.*;

public class BottomMenuView extends JPanel {
	private SketchModel model;
	
	private JButton btnPlayPause = new JButton("Play");
	private JSlider jsTimer = new JSlider(0, SketchModel.timeMax, 0);
	private JLabel lblTimer = new JLabel("0:00");
	public BottomMenuView(SketchModel aModel) {
		super();
		this.model = aModel;
		jsTimer.setPreferredSize(new Dimension(600,40));
		jsTimer.setMajorTickSpacing(5);
		jsTimer.setMinorTickSpacing(1);
		jsTimer.setPaintTicks(true);
		jsTimer.setPaintLabels(true);

		this.layoutView();
		this.registerControllers();

		this.model.addView(new IView() {
			public void updateView() {
				// Updating the view includes enabling/disabling components!
				if(model.getPlaying()){
					btnPlayPause.setText("Pause");
				}else{
					btnPlayPause.setText("Play");
				}
				
				jsTimer.setValue(model.getTime()/1000);
				
				String append = "";
				if(model.getTime()%60000/1000 < 10){
					append = "0";
				}
				lblTimer.setText(model.getTime()/60000+":"+append+model.getTime()%60000/1000);
				
				jsTimer.setMaximum(SketchModel.timeMax);
			}

		});
	}
	
	private void layoutView() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.add(btnPlayPause);
		this.add(jsTimer);
		this.add(lblTimer);
	}

	private void registerControllers() {
		this.btnPlayPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				model.setPlaying(!model.getPlaying());
				if(model.getPlaying()){
					model.startTimer();
				}else{
					model.stopTimer();
				}
				System.out.println(model.getPlaying());
			}
		});

		this.jsTimer.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent evt){
				model.setTime(jsTimer.getValue()*1000);
				//model.stopTimer();
				System.out.println("set time to "+jsTimer.getValue()*1000+" ms");
			}
		});
	}
}
