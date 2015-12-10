package com.example.androidtester.Metrics;

import java.io.File;
import java.util.Hashtable;

import com.example.androidtester.Services.AbsService;
import com.example.androidtester.Services.ImageProc;

public class HausdorffPromMetric {
	
	private final static double errorRelativo=25;
	private int imgdetectadas=0;
	
	public float getDato(AbsService service, Hashtable<String, Boolean> opts) throws Exception{
		String workingDir = System.getProperty("user.dir");
		File dir=new File(workingDir+"\\Eyeimages\\");
		File[] directoryListing = dir.listFiles();
		
		for (int i=0; i<100; i++) {
			File archivoIMG=directoryListing[i];
			ImageProc im=service.getFaceRecognition(archivoIMG, opts);
			HausdorffDistanceMetric hm=new HausdorffDistanceMetric();
			float distancia=(float)hm.getDato(service, im);
			if (distancia<errorRelativo){
				imgdetectadas++;
			}
			System.out.println(archivoIMG.getAbsolutePath());
		}
		return (float)imgdetectadas/(float)directoryListing.length;
	}

}
