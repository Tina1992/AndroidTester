package com.example.androidtester.Services;

import java.io.File;
import java.util.Hashtable;

import com.example.androidtester.TestTool.ParserPlan;


public abstract class AbsService {
	protected float timeout;
	protected int cantMax=-1;
	protected ImageProc imageProc;
	
	private Hashtable<String, Boolean> disp = new Hashtable<String, Boolean>();
	
	protected float startTime;
	protected float endTime;
	
	public AbsService(){
		for (String s: ParserPlan.atributos.keySet()){
			disp.put(s, false);
		}
	}
	
	public boolean IsAvailableAtt(String at){
		return disp.get(at);
	}
	
	public void addAvailableAtt(String at){
		disp.put(at, true);
	}
	
	public abstract ImageProc getFaceRecognition(File f, Hashtable<String, Boolean> opts);
	
	public float getResponseTime(){
		return (endTime-startTime)/1000000000;	//En segundos
	}

	public abstract String getName();
	
	public int getLimRequest(){
		return cantMax;
	}
	
}
