package com.example.androidtester.Metrics;

import com.example.androidtester.Services.AbsService;
import com.example.androidtester.Services.ImageProc;

public abstract class AbsMetric {
	
	public abstract Object getDato(AbsService service, ImageProc image);
	
	public abstract String getName(); 
	
	public abstract String getMedida();

}
