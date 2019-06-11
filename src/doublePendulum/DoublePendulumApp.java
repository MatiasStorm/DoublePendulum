package doublePendulum;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


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
				animationPane.resetVelocities();
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
}





