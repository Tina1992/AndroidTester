package com.example.androidtester.Metrics;

import com.example.androidtester.Services.AbsService;
import com.example.androidtester.Services.ImageProc;


public class ImageFormatMetric extends AbsMetric {

	@Override
	public Object getDato(AbsService service, ImageProc image){
		// TODO Auto-generated method stub
		String name=image.getFile_path();
		return name.substring(name.length()-3);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Image Format";
	}

	@Override
	public String getMedida() {
		// TODO Auto-generated method stub
		return null;
	}

}
