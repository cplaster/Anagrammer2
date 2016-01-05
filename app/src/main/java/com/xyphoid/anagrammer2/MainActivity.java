package com.xyphoid.anagrammer2;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends ActionBarActivity {

    Map<String, Integer> results;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager assetManager = getAssets();

        try {
            // old Processor methods
            //InputStream input = assetManager.open("enable.bin");
            final InputStream input = assetManager.open("enablenew.bin");
            //final Processor p = new Processor();
            //Log.d("Anagrammer2:", "Loading enable.bin");
            //p.Load(input);
            //Log.d("Anagrammer2:", "Done loading");

            final ProgressDialog progress = new ProgressDialog(this);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.setMax(100);
            progress.setCancelable(false);
            progress.setMessage("Loading dictionary...");
            progress.show();

            final AnagramSolver anagramSolver = new AnagramSolver(2, input, this);

            progress.dismiss();

            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    String mess = bundle.getString("status");
                    progress.setMessage(mess);
                }
            };

            final Button button1 = (Button)findViewById(R.id.button1);
            final Button button2 = (Button)findViewById(R.id.button2);
            final Button button3 = (Button)findViewById(R.id.button3);
            final Button button4 = (Button)findViewById(R.id.button4);
            final Button button5 = (Button)findViewById(R.id.button5);
            final EditText search = (EditText)findViewById(R.id.editText1);
            final TextView anagrams = (TextView)findViewById(R.id.textView2);
            final Spinner spinner = (Spinner)findViewById(R.id.spinner);

            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    anagrams.setText("");

                    results = Processor.sortByComparator(results, false);

                    Set<String> keys = results.keySet();


                    for(String key : keys){
                        anagrams.append(key + " : " + results.get(key) + "\n");
                    }

                }
            });

            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    anagrams.setText("");

                    Map<String, Integer> treeMap = new TreeMap<String, Integer>(results);

                    for(String key : treeMap.keySet()){
                        anagrams.append(key + " : " + results.get(key) + "\n");
                    }

                }
            });

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    anagrams.setText("");

                    HashMap<String, Integer> combos = anagramSolver.findExactSubsets(search.getText().toString());
                    results = AnagramSolverHelper.sortByComparator(combos, false);

                    //HashMap<String, Integer> combos = p.ShowAll(search.getText().toString());
                    //results = p.sortByComparator(combos, false);

                    if(results != null) {
                        Set<String> keys = results.keySet();
                        for (String key : keys) {
                            anagrams.append(key + " : " + results.get(key) + "\n");
                        }
                    } else {
                        anagrams.append("No matches.");
                    }

                }
            });


            button5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    anagrams.setText("");

                    progress.show();
                    progress.setMessage("Finding anagrams...");

                    int minWordLength = Integer.parseInt(String.valueOf(spinner.getSelectedItem()));
                    Set<Set<String>> anagram;

                    if (minWordLength == 0) {
                        anagram = anagramSolver.findAllAnagrams(search.getText().toString());
                    }
                    else {
                        anagram = anagramSolver.findAllAnagrams(search.getText().toString(), minWordLength);
                    }

                    if(!anagram.isEmpty()) {

                        for (Set<String> combo : anagram) {
                            for (String s : combo) {
                                anagrams.append(s + " ");
                            }
                            anagrams.append("\n");
                        }
                    } else {
                        anagrams.append("No matches found.\n");
                    }

                    progress.dismiss();


                }
            });


            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    anagrams.setText("");

                    /*
                    String results = p.Show(search.getText().toString());
                    String[] r = results.split(",");
                    */


                    Set<String> r = anagramSolver.findExactAnagrams(search.getText().toString());

                    if(r != null) {
                        for (String s : r) {
                            anagrams.append(s + "\n");
                        }
                    } else {
                        anagrams.append("No matches.");
                    }



                }
            });

        } catch(IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public Handler getHandler(){
        return handler;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
