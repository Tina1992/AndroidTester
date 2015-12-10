package com.example.androidtester.Services;


import android.graphics.Point;
import android.graphics.Rect;

import com.mashape.relocation.ParseException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Hashtable;

public class SkyBiometryService extends AbsRemoteService {

	public SkyBiometryService() {
		super();
		cantMax=100;
		addAvailableAtt("Eyes");
		addAvailableAtt("Nose");
		addAvailableAtt("Mouth");
		addAvailableAtt("Gender");
		addAvailableAtt("Glasses");
		addAvailableAtt("SunGlasses");
		addAvailableAtt("Smile");
		addAvailableAtt("Ears");
	}

	@Override
	public HttpResponse<JsonNode> post(File file,
			Hashtable<String, Boolean> options) throws UnirestException {
		// TODO Auto-generated method stub
		String s = "";
		imageProc = new ImageProc(file.getAbsolutePath());
		if (options.get("Gender")) {
			s = s.concat("Gender");
		}
		if ((options.get("Glasses")) || (options.get("Sunglasses"))) {
			if (s.equals(""))
				s = s.concat("Glasses");
			else
				s = s.concat(", Glasses");
		}
		if (options.get("Smile")) {
			if (s.equals(""))
				s = s.concat("Smiling");
			else
				s = s.concat(", Smiling");
		}
		if (s.equals("")) {
			s = "none";
		}
		return Unirest
				.post("https://face.p.mashape.com/faces/detect?api_key=771b5991a5ff4ce1a7cfcef2e1d91ae5&api_secret=cfc895f2c40e475eb1032be25b63f969")
				.header("X-Mashape-Key",
						"kCKZDyhuAxmsh2l2E7GXVfOLFe9hp1w77PbjsnmUiFR69J94RG")
				.field("attributes", s).field("detector", "Aggressive")
				.field("files", file).asJson();
	}

	@Override
	public ImageProc parse(HttpResponse<JsonNode> response,
			Hashtable<String, Boolean> options) {
		JSONObject auxiliar = new JSONObject();
		try {
			JSONObject obj = response.getBody().getObject();
			int cantPhotos = ((JSONArray) obj.get("photos")).length();
			JSONArray photos = (JSONArray) obj.get("photos");
			for (int j = 0; j < cantPhotos; j++) {
				int imgHeight = (int) ((JSONArray) obj.get("photos"))
						.getJSONObject(j).get("height");
				int imgWidth = (int) ((JSONArray) obj.get("photos"))
						.getJSONObject(j).get("width");

				JSONArray faces = photos.getJSONObject(j).getJSONArray("tags");

				for (int i = 0; i < faces.length(); i++) {
					JSONObject face = (JSONObject) faces.get(i);
					JSONObject coordenadas = face.getJSONObject("center");
					double faceH = (double) face.get("height") / 100
							* imgHeight;
					double faceW = (double) face.get("width") / 100 * imgWidth;
					double valueX = (((double) coordenadas.get("x")) / 100 * imgWidth)
							- faceW / 2;
					double valueY = ((double) coordenadas.get("y")) / 100
							* imgHeight - faceH / 2;
					double valueHeight = faceH;
					double valueWidth = faceW;
					auxiliar.put("x", valueX);
					auxiliar.put("y", valueY);
					auxiliar.put("height", valueHeight);
					auxiliar.put("width", valueWidth);
					imageProc.addFace(new Rect((int) valueX, (int) valueY,
							(int) valueHeight, (int) valueWidth));
					for (String s : options.keySet()) {
						addPoint(s, options, face, imgHeight, imgWidth);
					}
				}
			}
			return imageProc;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addPoint(String op, Hashtable<String, Boolean> options,
			JSONObject face, double h, double w) {
		JSONObject coor = null;
		if (options.get(op)) {
			switch (op) {
			case "Mouth": {
				try {
					coor = face.getJSONObject("mouth_center");
					double x = (double) coor.get("x") / 100 * w;
					double y = (double) coor.get("y") / 100 * h;
					imageProc.addMouthPoint(new Point((int) x, (int) y));
					break;
				} catch (Exception e) {
					imageProc.addMouthPoint(null);
					break;
				}
			}
			case "Eyes": {
				try {
					coor = face.getJSONObject("eye_right");
					double x = (double) coor.get("x") / 100 * w;
					double y = (double) coor.get("y") / 100 * h;
					imageProc.addEyesPoint(new Point((int) x, (int) y));
					coor = face.getJSONObject("eye_left");
					x = (double) coor.get("x") / 100 * w;
					y = (double) coor.get("y") / 100 * h;
					imageProc.addEyesPoint(new Point((int) x, (int) y));
					break;
				} catch (Exception e) {
					imageProc.addEyesPoint(null);
					break;
				}

			}
			case "Ears": {
				try {
					coor = face.getJSONObject("ear_right");
					double x = (double) coor.get("x") / 100 * w;
					double y = (double) coor.get("y") / 100 * h;
					imageProc.addEarPoint(new Point((int) x, (int) y));
					coor = face.getJSONObject("ear_left");
					x = (double) coor.get("x") / 100 * w;
					y = (double) coor.get("y") / 100 * h;
					imageProc.addEarPoint(new Point((int) x, (int) y));
					break;
				} catch (Exception e) {
					imageProc.addEarPoint(null);
					break;
				}
			}
			case "Nose": {
				try {
					coor = face.getJSONObject("nose");
					double x = (double) coor.get("x") / 100 * w;
					double y = (double) coor.get("y") / 100 * h;
					imageProc.addNosePoint(new Point((int) x, (int) y));
					break;
				} catch (Exception e) {
					imageProc.addNosePoint(null);
					break;
				}
			}
			case "Gender": {
				try {
					JSONObject att = face.getJSONObject("attributes");
					JSONObject gen = att.getJSONObject("gender");
					String genVal = (String) gen.get("value");
					imageProc.addGender(genVal);
					break;
				} catch (Exception e) {
					imageProc.addGender("undefined");
					break;
				}
			}
			case "Glasses": {
				try {
					JSONObject att = face.getJSONObject("attributes");
					JSONObject gen = att.getJSONObject("glasses");
					String genVal = (String) gen.get("value");
					Boolean b = false;
					if (genVal.equals("true")) {
						b = true;
					}
					imageProc.addGlasses(b);
					break;
				} catch (Exception e) {
					imageProc.addGlasses(null);
					break;
				}

			}
			case "Smile": {
				try {
					JSONObject att = face.getJSONObject("attributes");
					JSONObject gen = att.getJSONObject("smiling");
					String genVal = (String) gen.get("value");
					Boolean b = false;
					if (genVal.equals("true")) {
						b = true;
					}
					imageProc.addSmile(b);
					break;
				} catch (Exception e) {
					imageProc.addSmile(null);
					break;
				}
			}
			case "SunGlasses": {
				try {
					JSONObject att = face.getJSONObject("attributes");
					JSONObject gen = att.getJSONObject("dark_glasses");
					String genVal = (String) gen.get("value");
					Boolean b = false;
					if (genVal.equals("true")) {
						b = true;
					}
					imageProc.addSunGlasses(b);
					break;
				} catch (Exception e) {
					imageProc.addSunGlasses(null);
					break;
				}
			}
			}
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "SkyBiometry";
	}

}
