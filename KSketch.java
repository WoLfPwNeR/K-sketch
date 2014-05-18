import javax.swing.JFrame;

import model.*;
import view.*;
import java.awt.*;

public class KSketch {

	public static void main(String[] args) {
		model.SketchModel model = new SketchModel();
		MenuView vButton = new MenuView(model);
		DrawpadView vDrawpad = new DrawpadView(model);
		BottomMenuView vPanel = new BottomMenuView(model);

		JFrame frame = new JFrame("K-Sketch");
		frame.getContentPane().setLayout(new BorderLayout(1, 1));
		frame.getContentPane().add(vButton, BorderLayout.NORTH);
		frame.getContentPane().add(vDrawpad, BorderLayout.CENTER);
		frame.getContentPane().add(vPanel, BorderLayout.SOUTH);

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
