package edu.washington.apl.weather.gwt.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GWTWeatherServiceAsync {
  public void getIndoor(AsyncCallback callback);
  public void getOutdoor(AsyncCallback callback);
  public void getWind(AsyncCallback callback);
  public void getRain(AsyncCallback callback);
}