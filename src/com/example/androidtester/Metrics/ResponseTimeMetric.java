package com.example.androidtester.Metrics;

import com.example.androidtester.Services.AbsService;
import com.example.androidtester.Services.ImageProc;

public class ResponseTimeMetric extends AbsMetric {

	@Override
	public Object getDato(AbsService service, ImageProc image) {
		return service.getResponseTime();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Response Time";
	}

	@Override
	public String getMedida() {
		// TODO Auto-generated method stub
		return "second";
	}

}
