package doublePendulum;

import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;


public class DoublePendulumApp extends Application{
	public void start(Stage primaryStage) {
		// Headline "Adjusmtents"
		StackPane adjustmentHeadlinePane = new StackPane();
		Label adjustmentHeadline = new Label("Adjustments");
		adjustmentHeadline.setStyle("-fx-font-size: 20; -fx-font-weight: bold;" );
		adjustmentHeadlinePane.getChildren().add(adjustmentHeadline);
		
		// AdjustmentPane:
		GridPane adjustmentPane = new GridPane();
		ArrayList<TextField> textFieldList = new ArrayList<>();
		adjustmentPane.setAlignment(Pos.TOP_CENTER);
		adjustmentPane.setVgap(5);
		adjustmentPane.setHgap(15);
		CheckBox resistanceCB = new CheckBox("Friction & Air Resistance");
		adjustmentPane.addColumn(0, new Label("Length1 (m)"), new Label("Length2 (m)"), new Label("Theta1 (degrees)"), new Label("Theta2 (degrees)"));
		adjustmentPane.addColumn(2, new Label("Mass1 (kg)"), new Label("Mass2 (kg)"), new Label("Gravitation (m/s^2)"), resistanceCB);
		
		TextField length1TF = new TextField("50"); 
		TextField length2TF = new TextField("50");
		TextField theta1TF = new TextField("120");
		TextField theta2TF = new TextField("225");
		TextField mass1TF = new TextField("4");
		TextField mass2TF = new TextField("4");	
		TextField gravitationTF = new TextField("9.8");
		textFieldList.add(length1TF);
		textFieldList.add(length2TF);
		textFieldList.add(theta1TF);
		textFieldList.add(theta2TF);
		textFieldList.add(mass1TF);
		textFieldList.add(mass2TF);
		textFieldList.add(gravitationTF);
		for (TextField f : textFieldList) {
			f.setMaxWidth(50);			
		}
		adjustmentPane.addColumn(1, length1TF, length2TF, theta1TF, theta2TF);
		adjustmentPane.addColumn(3, mass1TF, mass2TF, gravitationTF);
		
		// Button Pane:
		HBox btnPane = new HBox();
		btnPane.setSpacing(10);
		btnPane.setAlignment(Pos.CENTER);
		Button runAnimBtn = new Button("Run Animation");
		Button pauseAnimBtn = new Button("Pause Animation");
		Button clearTraceLineBtn = new Button("Clear Trace Line");
		Button resetBtn = new Button("Reset");		
		btnPane.getChildren().addAll(runAnimBtn, pauseAnimBtn, clearTraceLineBtn, resetBtn);
		
		// Animation
		PendulumAnimationPane animationPane = new PendulumAnimationPane(length1TF, length2TF, theta1TF, theta2TF, mass1TF, mass2TF, gravitationTF);
		
		
		// Actions when TextFields change value
		length1TF.textProperty().addListener((observable, oldValue, newValue) -> {if (!newValue.isEmpty()) animationPane.setLengthl1(newValue);});
		length2TF.textProperty().addListener((observable, oldValue, newValue) -> {if (!newValue.isEmpty()) animationPane.setLengthl2(newValue);});
		theta1TF.textProperty().addListener((observable, oldValue, newValue) -> {if (!newValue.isEmpty()) animationPane.setTheta1Degrees(newValue);});
		theta2TF.textProperty().addListener((observable, oldValue, newValue) -> {if (!newValue.isEmpty()) animationPane.setTheta2Degrees(newValue);});
		mass1TF.textProperty().addListener((observable, oldValue, newValue) -> {if (!newValue.isEmpty()) animationPane.setMassm1(newValue);});
		mass2TF.textProperty().addListener((observable, oldValue, newValue) -> {if (!newValue.isEmpty()) animationPane.setMassm2(newValue);});
		gravitationTF.textProperty().addListener((observable, oldValue, newValue) -> {if (!newValue.isEmpty()) animationPane.setGravitation(newValue);});
		for (TextField f : textFieldList) {
			f.textProperty().addListener((observable, oldValue, newValue) -> {
				animationPane.pauseAnimation();
				animationPane.clearTraceLine();
			});
		}
		
		// button and check box actions
		runAnimBtn.setOnMouseClicked(e -> animationPane.runAnimation());
		pauseAnimBtn.setOnMouseClicked(e -> animationPane.pauseAnimation());
		clearTraceLineBtn.setOnMouseClicked(e -> animationPane.clearTraceLine());
		resetBtn.setOnMouseClicked(e -> {
			animationPane.pauseAnimation();
			animationPane.setInitialAngularVelocities(0, 0);
			animationPane.clearTraceLine();
			for (TextField f : textFieldList) {
				f.setText("");
			}
			length1TF.setText("50");
			length2TF.setText("50");
			theta1TF.setText("120");
			theta2TF.setText("225");
			mass1TF.setText("4");
			mass2TF.setText("4");
			gravitationTF.setText("9.8");
		});
		resistanceCB.setOnMouseClicked(e -> {
			if (resistanceCB.isSelected())
				animationPane.setResistance(true);
			else
				animationPane.setResistance(false);
		});
		
		// Top pane and substage:
		VBox topPane = new VBox();
		topPane.setSpacing(10);
		topPane.getChildren().addAll(adjustmentHeadlinePane, adjustmentPane, btnPane, animationPane);
		Scene scene = new Scene(topPane, 400, 600);
		Stage subStage = new Stage();
		subStage.setScene(scene);
		subStage.setResizable(false);
		subStage.setTitle("Double Pendulum");
		subStage.initOwner(primaryStage);
		subStage.initModality(Modality.APPLICATION_MODAL);
		subStage.show();
	}

	
	class PendulumAnimationPane extends Pane{
		boolean isRunning = false;
		boolean resistance = false;
		int x0 = 200;
		int y0 = 200;
		private Line line1 = new Line();
		private Line line2 = new Line();
		private double L1; // Length of line1 in cm
		private double L2; // length of line2 in cm
		private Circle circle1 = new Circle();
		private Circle circle2 = new Circle();
		private double M1; // mass 1
		private double M2; // mass 2
		private double th1; // first angle in degrees
		private double th2; // second angle in degrees
		private double g; 
		private double th1V = 0; // Initial angular velocity for th1.
		private double th2V = 0; // Initial angular velocity for th2		
		private final Timeline animation = new Timeline(new KeyFrame(Duration.millis(3), e -> animation()));
		private Polyline traceLine = new Polyline();
		
		PendulumAnimationPane(TextField len1TF, TextField len2TF, TextField th1TF, TextField th2TF, TextField m1TF, TextField m2TF, TextField gTF){
			// Setting up initial values:
			this.setStyle("-fx-background-color: white;");
			animation.setCycleCount(Timeline.INDEFINITE);
			g = 9*(Double.parseDouble(gTF.getText())*100)/(1000*1000);// Gravitational force, cm/(ms^-2) (This is used to fit the KeyFrame time of the animation)
			Circle anchorCircle = new Circle(x0, y0, 5, Color.BLACK);
			circle1.setFill(Color.RED);
			circle2.setFill(Color.RED);
			line1.setStartX(x0);
			line1.setStartY(y0);
			setTheta1Degrees(th1TF.getText());
			setTheta2Degrees(th2TF.getText());
			setMassm1(m1TF.getText());
			setMassm2(m2TF.getText());
			setLengthl1(len1TF.getText());
			setLengthl2(len2TF.getText());
			
			// Handling trace line and trace line check box:
			traceLine.setStrokeWidth(0.25);
			traceLine.setVisible(false);
			CheckBox showTraceLineCB = new CheckBox("Show Trace Line");
			showTraceLineCB.setLayoutX(10);
			showTraceLineCB.setLayoutY(370);
			showTraceLineCB.setOnMouseClicked(e -> {
				if (showTraceLineCB.isSelected())
					traceLine.setVisible(true);
				else
					traceLine.setVisible(false);
			});
			
			// Event handling for the two circles:
			circle1.setOnMouseDragged(e -> {
				double x = e.getX();
				double y = e.getY();
				double length = Math.sqrt(Math.pow(x-x0, 2) + Math.pow(y-y0, 2));
				len1TF.setText(length + "");
				if (y-y0 < 0)
					length = -length;
				L1 = length;
				double angle = Math.asin((x-x0)/length);
				updateTheta1(angle);
				th1TF.setText(Math.toDegrees(angle)+"");
			});
			
			circle2.setOnMouseDragged(e -> {
				double x1 = circle1.getCenterX();
				double y1 = circle1.getCenterY();
				double x2 = e.getX();
				double y2 = e.getY();
				double length = Math.sqrt( Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
				len2TF.setText(length + "");
				if (y2 - y1 < 0)
					length = -length;
				L2 = length;
				double angle = Math.asin((x2-x1)/length);
				updateTheta2(angle);
				th2TF.setText(Math.toDegrees(angle) + "");
			});
			
			this.getChildren().addAll(anchorCircle, line1, line2, circle1, circle2, traceLine, showTraceLineCB);			
		}
		
		
		private void animation() {
			// Calculation of angular accelerations for theta1 and theta2
			double th1A = ( -g * (2 * M1 + M2) * Math.sin(th1) - M2 * g * Math.sin(th1-2*th2) - 2*Math.sin(th1-th2)*M2 * (th2V*th2V*L2 + th1V*th1V*L1*Math.cos(th1-th2)) )/
					      (L1*(2*M1 + M2 - M2*Math.cos(2*th1-2*th2)));
			double th2A = ( 2 * Math.sin(th1-th2) * (th1V*th1V * L1 * (M1 + M2) + g * (M1 + M2) * Math.cos(th1) + th2V*th2V * L2 * M2 * Math.cos(th1-th2)) ) / 
						  (L2 * (2*M1 + M2 - M2 * Math.cos(2*th1 - 2*th2)));
	
			// adding angular acceleration to the angular velocity
			th1V += th1A; 
			th2V += th2A;
			// Adding angular velocity to angles
			th1 += th1V;
			th2 += th2V;
			// Air resistance and friction
			if (resistance) {
				th1V *= 0.9999;
				th2V *= 0.9999;				
			}
			// Updating angles
			updateTheta1(th1);
			updateTheta2(th2);
		}
		
		public void clearTraceLine() {
			traceLine.getPoints().clear();
		}
		
		public void runAnimation() {
			animation.play();
			isRunning = true;			
		}
		
		
		public void pauseAnimation() {
			animation.pause();
			isRunning = false;
		}
		
		public void setResistance(boolean res) {
			resistance = res;
		}

		public void setLengthl1(String textValue) {
			try {
				double len = Double.parseDouble(textValue);				
				L1 = len;
				double x = L1 * Math.sin(th1) + x0;
				double y = L1 * Math.cos(th1) + y0;
				line1.setEndX(x);
				line1.setEndY(y);
				circle1.setCenterX(x);
				circle1.setCenterY(y);
				line2.setStartX(x);
				line2.setStartY(y);
				updateLine2();
			}
			catch (NumberFormatException e) {
			}
		}
		
		public void setLengthl2(String textValue) {
			try {
				double len = Double.parseDouble(textValue);
				L2 = len;
				double x = L1 * Math.sin(th1) + L2 * Math.sin(th2) + x0;
				double y = L1 * Math.cos(th1) + L2 * Math.cos(th2) + y0;
				line2.setEndX(x);
				line2.setEndY(y);
				circle2.setCenterX(x);
				circle2.setCenterY(y);				
			}
			catch (NumberFormatException e) {
			}
		}
	
		public void setMassm1(String textValue) {
			try {
				double mass = Double.parseDouble(textValue);
				M1 = mass*1000; // Convert mass from kg into grams
				circle1.setRadius(mass);				
			}
			catch (NumberFormatException e) {
				
			}
		}
		
		public void setMassm2(String textValue) {
			try {
				double mass = Double.parseDouble(textValue);
				M2 = mass*1000; // Convert mass from kg into grams
				circle2.setRadius(mass);				
			}
			catch (NumberFormatException e) {
				
			}
		}
	
		public void setTheta1Degrees(String textValue) {
			try {
				double degrees = Double.parseDouble(textValue);
				th1 = Math.toRadians(degrees); // Input is in degrees, translated into radians
				updateLine1();				
			}
			catch (NumberFormatException e) {
				
			}
		}
		
		public void setTheta2Degrees(String textValue) {
			try {
				double degrees = Double.parseDouble(textValue);
				th2 = Math.toRadians(degrees); // Input in degrees translated into radians.
				updateLine2();				
			}
			catch (NumberFormatException e) {
				
			}
		}
		
		public void setGravitation(String textValue) {
			g = 9*(Double.parseDouble(textValue)*100)/(1000*1000);
		}
		
		public void setInitialAngularVelocities(double v1, double v2) {
			th1V = v1;
			th2V = v2;
		}
		
		private void updateLine1() {
			double x = L1 * Math.sin(th1)  + x0;
			double y = L1 * Math.cos(th1)  + y0;
			line1.setEndX(x);
			line1.setEndY(y);
			circle1.setCenterX(x);
			circle1.setCenterY(y);
			line2.setStartX(x);
			line2.setStartY(y);
			updateLine2();
		}
		
		private void updateLine2() {
			double x = L1 * Math.sin(th1)  + L2 * Math.sin(th2)  + x0;
			double y = L1 * Math.cos(th1) + L2 * Math.cos(th2)  + y0;
			line2.setEndX(x);
			line2.setEndY(y);
			circle2.setCenterX(x);
			circle2.setCenterY(y);
			if (isRunning) {
				traceLine.getPoints().addAll(new Double[] {x,y});
				if (traceLine.getPoints().size() > 10000) {
					traceLine.getPoints().remove(0);
					traceLine.getPoints().remove(0);
				}
			}
		}
		
		private void updateTheta1(double radians) {
			th1 = radians;
			updateLine1();
		}
		
		
		private void updateTheta2(double radians) {
			th2 = radians;
			updateLine2();
		}
		
		
	}
}





