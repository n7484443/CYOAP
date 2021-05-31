module com.project.cyoap{
	requires java.desktop;
	requires javafx.graphics;
	requires javafx.fxml;
	requires javafx.controls;
	
	exports core;	
	exports design;
	opens design.controller;
}