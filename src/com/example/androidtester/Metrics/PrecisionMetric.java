package com.example.androidtester.Metrics;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.example.androidtester.Services.AbsService;
import com.example.androidtester.Services.ImageProc;


public class PrecisionMetric extends AbsMetric {

	// ------Metodos utilizados para la creaci�n del dataset--------

	private static void createDirectory(String workingDir, File path) {
		if (path.isFile() && path.getAbsolutePath().contains(".jpg")) {
			path.renameTo(new File(workingDir + "\\" + path.getName()));
		} else {
			if (path.isDirectory()) {
				File[] dirs = path.listFiles();
				for (File d : dirs) {
					createDirectory(workingDir, d);
				}
			}
		}

	}

    /*
	@SuppressWarnings("unused")
	private static void createDataset() {
		String workingDir = System.getProperty("user.dir");
		Path path = FileSystems.getDefault().getPath(workingDir, "Faces");
		File oldPath = new File(workingDir, "lfw");
		try {
			Files.createDirectory(path);
			createDirectory(path.toString(), oldPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void createPrecisionDir() {
		String workingDir = System.getProperty("user.dir");
		Path path = FileSystems.getDefault().getPath(workingDir, "Faces");
		Path newPath = FileSystems.getDefault()
				.getPath(workingDir, "Precision");
		try {
			Files.createDirectories(newPath);
			File dir = new File(path.toString());
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					newPath = FileSystems.getDefault().getPath(
							workingDir + "\\Precision",
							child.getName().replace(".jpg", ".txt"));
					Files.createFile(newPath);
					PrintWriter writer = new PrintWriter(newPath.toString(),
							"UTF-8");
					writer.println("1");
					writer.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	// -------Fin de metodos de creaci�n del dataset-----------

	@Override
	public Object getDato(AbsService service, ImageProc image) {
		// TODO Auto-generated method stub
        if (image.getError()==null){
        String file_name=new File(image.getFile_path()).getName();
        file_name=file_name.replace(".jpg",".txt");
        String prec_path= Environment.getExternalStorageDirectory().getPath()+"/windows/BstSharedFolder/Precision/"+file_name;
		Log.d("ARCHIVO PRECISION", prec_path);
        try {
            if (image.getFaces() != null) {
                BufferedReader reader = new BufferedReader(new FileReader(prec_path));
                String line = reader.readLine();
                if (line != null) {
                    Integer p=image.getFaces().size();
                    Integer tp = new Integer(line);
					if (p<tp){
						tp=p;
					}
					Integer fp=0;
					if (p>tp){
						fp= p-tp;
					}
                    return (float)tp  / (float) (tp+fp);
                }
            } else
                return (float)-3;
        } catch (IOException e) {
            return (float)-4;
        }
        return (float)0;}
        else
        {
            return (float)-5;
        }
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Precision";
	}

	@Override
	public String getMedida() {
		// TODO Auto-generated method stub
		return "per cent";
	}
}
