package edu.washington.apl.weather.gwt.client.service;

import com.google.gwt.user.client.rpc.RemoteService;

public interface GWTWeatherService extends RemoteService {
  public String getIndoor();
  public String getOutdoor();
  public String getWind();
  public String getRain();
}