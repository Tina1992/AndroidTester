package com.example.androidtester.TestTool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import com.example.androidtester.R;

public class MainActivity extends Activity {

    private static final int FILE_SELECT_CODE = 1;
    private static final int SELECT_FOLDER = 2;
    private Button btnLoadPlan, btnResultado,btnChooseDir;
    private ParserPlan parser=new ParserPlan(this);
    private Tester tester =new Tester();

    private File archivoPlan;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadPlan = (Button)findViewById(R.id.btnLoad);
        btnResultado = (Button)findViewById(R.id.btnResultado);
        btnChooseDir= (Button)findViewById(R.id.btnChooseDir);

        btnChooseDir.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                Uri startDir = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()));
                intent.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.directory");
                intent.putExtra("explorer_title", "Select Folder...");
                startActivityForResult(intent, SELECT_FOLDER);
            }

        });

        btnLoadPlan.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Elegir un archivo:
                Intent intent = new Intent();
                intent.setType("file/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(intent, FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(MainActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnResultado.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                    generateResult();
                    Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_LONG).show();
                }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            case FILE_SELECT_CODE:
                if(resultCode==RESULT_OK){
                    //Obtencion del archivo
                    archivoPlan=new File(data.getData().getPath());
                    parser.decodePlan(archivoPlan);
                    Toast.makeText(MainActivity.this, "Plan decodificado: "+archivoPlan.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
                break;
            case SELECT_FOLDER:
                if(resultCode==RESULT_OK){
                    String dirFolder=data.getData().getPath();
                    parser.addDirectory(dirFolder);
                    Toast.makeText(MainActivity.this, "Carpeta cargada: "+dirFolder, Toast.LENGTH_LONG).show();
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generateResult(){
        tester.test();

    }
}
