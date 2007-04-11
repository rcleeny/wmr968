package edu.washington.apl.weather.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import edu.washington.apl.weather.gwt.client.service.GWTWeatherServiceAsync;
import edu.washington.apl.weather.gwt.client.service.GWTWeatherService;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTWeather implements EntryPoint {
	
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
  	// GWT RPC Setup
  	final GWTWeatherServiceAsync asyncService = (GWTWeatherServiceAsync) GWT.create(GWTWeatherService.class);
	  ServiceDefTarget endpoint = (ServiceDefTarget) asyncService;
	  endpoint.setServiceEntryPoint(GWT.getModuleBaseURL() + "service");
	  // End GWT RPC Setup

  	Grid grid = new Grid(4, 2);
  	
    Button indoorButton = new Button("Click for Latest Indoor Temp");
    grid.setWidget(0, 0, indoorButton);
    
    // indoor section
    final Label indoorLabel = new Label();
    grid.setWidget(0, 1, indoorLabel);
    
    indoorButton.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
		  	AsyncCallback callback = new AsyncCallback() {
		    	public void onSuccess(Object result) {
		    		indoorLabel.setText(result.toString());
		    	}
		
		    	public void onFailure(Throwable caught) {
		    		indoorLabel.setText("Failure: " + caught);
		    	}
		  	};
		  	
				asyncService.getIndoor(callback);
      }
    });
    
    // outdoor section
    Button outdoorButton = new Button("Click for Latest Outdoor Temp");
    grid.setWidget(1, 0, outdoorButton);
    
    final Label outdoorLabel = new Label();
    grid.setWidget(1, 1, outdoorLabel);
    
    outdoorButton.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
      	AsyncCallback callback = new AsyncCallback() {
		    	public void onSuccess(Object result) {
		    		outdoorLabel.setText(result.toString());
		    	}
		
		    	public void onFailure(Throwable caught) {
		    		outdoorLabel.setText("Failure: " + caught);
		    	}
		  	};
		  	
				asyncService.getOutdoor(callback);
      }
    });
    
    

		// wind section
    Button windButton = new Button("Click for Latest Wind");
    grid.setWidget(2, 0, windButton);
    
    final Label windLabel = new Label();
    grid.setWidget(2, 1, windLabel);
    
    windButton.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
      	AsyncCallback callback = new AsyncCallback() {
		    	public void onSuccess(Object result) {
		    		windLabel.setText(result.toString());
		    	}
		
		    	public void onFailure(Throwable caught) {
		    		windLabel.setText("Failure: " + caught);
		    	}
		  	};
		  	
				asyncService.getWind(callback);
      }
    });

		// rain section
    Button rainButton = new Button("Click for Latest Rain");
    grid.setWidget(3, 0, rainButton);
    
    final Label rainLabel = new Label();
    grid.setWidget(3, 1, rainLabel);
    
    rainButton.addClickListener(new ClickListener() {
      public void onClick(Widget sender) {
      	AsyncCallback callback = new AsyncCallback() {
		    	public void onSuccess(Object result) {
		    		rainLabel.setText(result.toString());
		    	}
		
		    	public void onFailure(Throwable caught) {
		    		rainLabel.setText("Failure: " + caught);
		    	}
		  	};
		  	
				asyncService.getRain(callback);
      }
    });
    
    RootPanel.get().add(grid);

    // Assume that the host HTML has elements defined whose
    // IDs are "slot1", "slot2".  In a real app, you probably would not want
    // to hard-code IDs.  Instead, you could, for example, search for all 
    // elements with a particular CSS class and replace them with widgets.
    //
    // RootPanel.get("slot1").add(button);
    // RootPanel.get("slot2").add(label);
  }
}
