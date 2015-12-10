package com.example.androidtester.Services;

import android.os.AsyncTask;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.File;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;

public abstract class AbsRemoteService extends AbsService{

	public AbsRemoteService(){
		super();
	}

	public abstract HttpResponse<JsonNode> post(File file, Hashtable<String, Boolean> options) throws UnirestException;

	public abstract ImageProc parse(HttpResponse<JsonNode> response, Hashtable<String, Boolean> options);

	public ImageProc getFaceRecognition(final File f, final Hashtable<String, Boolean> opts){
		try {
			AsyncTask<Object, Integer, ImageProc> esclavito=new AsyncTask() {
				@Override
				protected ImageProc doInBackground(Object[] objects) {
					try {
						startTime=System.nanoTime();
						HttpResponse<JsonNode> res=post(f,opts);
						if (res.getCode()==200){
							imageProc=parse(res, opts);}
						else{
							imageProc = new ImageProc(f.getAbsolutePath());
							imageProc.setError(res.getCode());
						}
						endTime=System.nanoTime();
						return imageProc;
					} catch (UnirestException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}

			};
			return esclavito.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}
}
