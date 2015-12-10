package com.example.androidtester.Services;

import android.graphics.Rect;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Hashtable;

public class FaceRectService extends AbsRemoteService {

	public FaceRectService() {
		super();
		addAvailableAtt("Eyes");
		addAvailableAtt("Nose");
		addAvailableAtt("Mouth");
		addAvailableAtt("FaceOrientation");
	}

	@Override
	public HttpResponse<JsonNode> post(File file,
			Hashtable<String, Boolean> options) throws UnirestException {
		// TODO Auto-generated method stub
		imageProc = new ImageProc(file.getAbsolutePath());
		if (options.get("Eyes") || options.get("Nose") || options.get("Mouth")) {
			return Unirest
					.post("https://apicloud-facerect.p.mashape.com/process-file.json")
					.header("X-Mashape-Key",
							"kCKZDyhuAxmsh2l2E7GXVfOLFe9hp1w77PbjsnmUiFR69J94RG")
					.field("features", true).field("image", file).asJson();
		}
		return Unirest
				.post("https://apicloud-facerect.p.mashape.com/process-file.json")
				.header("X-Mashape-Key",
						"kCKZDyhuAxmsh2l2E7GXVfOLFe9hp1w77PbjsnmUiFR69J94RG")
				.field("image", file).asJson();
	}

	@Override
	public ImageProc parse(HttpResponse<JsonNode> response,
			Hashtable<String, Boolean> options) {
		// TODO Auto-generated method stub
		if (response.getCode()==200){
		JSONObject obj = response.getBody().getObject();
		try{
		JSONArray faces = (JSONArray) obj.get("faces");

		int cantfaces = faces.length();
		for (int i = 0; i < cantfaces; i++) {
			int x = 0;
			try {
				x = faces.getJSONObject(i).getInt("x");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			int y = faces.getJSONObject(i).getInt("y");
			int height = faces.getJSONObject(i).getInt("height");
			int width = faces.getJSONObject(i).getInt("width");
			String orientacion = faces.getJSONObject(i)
					.getString("orientation");
			if (options.get("FaceOrientation")) {
				imageProc.addOrientation(orientacion);
			}
			if (orientacion.equals("frontal")) {
				imageProc.addFace(new Rect(x, y, height, width));
				if (options.get("Eyes") || options.get("Nose")
						|| options.get("Mouth")) {
					JSONObject face = faces.getJSONObject(i);
					try {
						JSONObject features = (JSONObject) face.get("features");
						if (options.get("Eyes")) {
							try {
								JSONArray eyes = (JSONArray) features
										.get("eyes");
								for (int j = 0; j < eyes.length(); j++) {
									x = eyes.getJSONObject(j).getInt("x");
									y = eyes.getJSONObject(j).getInt("y");
									height = eyes.getJSONObject(j).getInt(
											"height");
									width = eyes.getJSONObject(j).getInt(
											"width");
									imageProc.addEyeRect(new Rect(x, y,
											height, width));
								}
							} catch (Exception e) {
								imageProc.addEyeRect(null); // Si no hay ojos
							}
						}
						if (options.get("Nose")) {
							try {
								x = features.getJSONObject("nose").getInt("x");
								y = features.getJSONObject("nose").getInt("y");
								height = features.getJSONObject("nose").getInt(
										"height");
								width = features.getJSONObject("nose").getInt(
										"width");
								imageProc.addNoseRect(new Rect(x, y,
										height, width));
							} catch (Exception e) {
								imageProc.addNoseRect(null); // Si no hay nariz
							}
						}
						if (options.get("Mouth")) {
							try {
								x = features.getJSONObject("mouth").getInt("x");
								y = features.getJSONObject("mouth").getInt("y");
								height = features.getJSONObject("mouth")
										.getInt("height");
								width = features.getJSONObject("mouth").getInt(
										"width");
								imageProc.addMouthRect(new Rect(x, y,
										width, height));
							} catch (Exception e) {
								imageProc.addMouthRect(null); // Si no hay boca
							}
						}
					} catch (Exception e) {
						//No hacer nada
					}
				}
			}
		}

		} catch (JSONException e) {
			e.printStackTrace();
		}}
		else
		{
			imageProc.setError(response.getCode());
		}
		return imageProc;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "FaceRect";
	}

}
