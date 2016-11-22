package brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch;


import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.activities.ShowBooks;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.activities.ShowDescription;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.activities.ShowSchedule;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.activities.ShowSyllabus;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.activities.ShowTeacher;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers.ExtructionURLs;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers.Producers;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers.ProducersBooksInfo;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers.ProducersClassDescription;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers.ProducersClassSchdule;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers.ProducersSyllabus;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers.ProducersTearcherInfo;
import brandeisclasssearchproject.cs.brandies.edu.brandeisclasssearch.producers.inpInterpreter;

/*

 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ClassSearchingTask CST;
    HashMap<String, ArrayList<String>> datas;
    ArrayList<HashMap<String,ArrayList<String>>> datasMap;
    AsyncTask dataLoader;
    ArrayList<Producers> producersList = new ArrayList<Producers>();
    InfoListAdapter adapter;
    ProgressBar pb;
    ListView lv;
    SearchView sv;
    MenuItem mi;


    final int[] terms=new int[]{1171,1163,1162,1161,1152,1151,1153} ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb=(ProgressBar) findViewById(R.id.theProgressBar);
        pb.setVisibility(View.INVISIBLE);

//        dataLoader=new DataLoader(new DataLoader.AsyncResponse() {
//            @Override
//            public void processFinish(HashMap<String, ArrayList<String>> output) {
//                datas=output;//set the hashmap for use in the main thread;
//            }
//        },getApplicationContext());
//        Log.i("Main","dataLoader.execute()");
//        //dataLoader.execute();
        new LoadingData().execute();//not ready yet


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //declare the fab buttom, which is the round button floating on the screen

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Save this Course?", Snackbar.LENGTH_SHORT)
                        .setAction("√ SAVE", new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this,"Nothing yet",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });//set the on Click of fab buttom

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Find the drawer
        //which is the entire layout we can see

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //default generated code.


        //The list view is the one we put all the infomations
        lv = (ListView) findViewById(R.id.theContentList);
        //SearchView sv = (SearchView) findViewById(R.id.searchClass);
        //SearchView sv = (SearchView) findViewById(R.id.action_settings);
        //adapter = new InfoListAdapter(producersList);
        //lv.setAdapter(adapter);




    }

    private void setSV(){
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener(){


            @Override
            public boolean onQueryTextSubmit(String query) {
                if(datas!=null){
                    //if(dataLoader.getStatus() == AsyncTask.Status.FINISHED){
                    //lv.setVisibility(View.INVISIBLE);
                    CST= new ClassSearchingTask(query);
                    CST.execute();
                    return true;
                }
                else {
                    pb.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Loading not Finished yet, please wait few more seconds~", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }


            //implement suggestion here!
            @Override
            public boolean onQueryTextChange(String newText) {

                polulateAdapter();

                return false;
            }
        });
    }

    //http://stackoverflow.com/questions/23658567/android-actionbar-searchview-suggestions-with-a-simple-string-array
    private void polulateAdapter() {
        //final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "className" });
        //do something


    }


    /*
    The method defines the behaviour of user clicking the back button
    The back button is the triangle button on the bottom of the screen
    it can be on the right or left depending on the systems
    usually on the left
     */
    @Override
    public void onBackPressed() {
        Log.i("mylog","onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //Toast.makeText(getApplicationContext(), "close", Toast.LENGTH_SHORT).show();
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*
    Option Menu
    set up and inflate option menu
    and defines select behaviors
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mi = menu.findItem(R.id.action_settings);
        sv = (SearchView) MenuItemCompat.getActionView(mi);
        setSV();
        //
        //sv=(SearchView)findViewById(R.id.action_settings);

        return true;
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
    /*
    Option Menu stuffs
     */





    /*
    This method defines the behavior of click items in the navigation bar
    Important
    implement it later
    after implementation of the searching mechanisms
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {

        } else if (id == R.id.nav_my) {

        } else if (id == R.id.nav_time) {

        }  else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id==R.id.nav_maj){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private class ClassSearchingTask extends AsyncTask<Object,Void,Void> {
        private ArrayList<String> classInfos;
        private String classId;
        //private Boolean isDone;

        public ClassSearchingTask(String s) {
            classId=s.toUpperCase();
            classInfos=new inpInterpreter(classId).getClassInfos();
        }



        @Override
        protected void onPreExecute() {
            lv.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            pb.setVisibility(View.INVISIBLE);
            lv.setVisibility(View.VISIBLE);
            //update the list
            if(producersList==null){
                Toast.makeText(getApplicationContext(), "We cannot find relevant information, maybe the class ID is wrong?", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "Showing", Toast.LENGTH_SHORT).show();
            ListView lv = (ListView) findViewById(R.id.theContentList);
            adapter = new InfoListAdapter(producersList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Producers p = producersList.get(position);
                    //Toast.makeText(getApplicationContext(), String.valueOf(position)+" "+p.getName(), Toast.LENGTH_SHORT).show();//debug purpose only
                    Intent i = null;
                    if(p instanceof ProducersClassDescription){
                        i = new Intent(getApplicationContext(),ShowDescription.class);
                    }else if(p instanceof ProducersTearcherInfo){
                        i = new Intent(getApplicationContext(),ShowTeacher.class);
                    }else if(p instanceof ProducersBooksInfo){
                        i = new Intent(getApplicationContext(),ShowBooks.class);
                    }else if(p instanceof ProducersSyllabus){
                        i = new Intent(getApplicationContext(),ShowSyllabus.class);
                    }else if(p instanceof ProducersClassSchdule){
                        i = new Intent(getApplicationContext(),ShowSchedule.class);
                    }
                    if(i != null){
                        i.putExtra("list",p.getResult());
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);
                    }

                }
            });


            //adapter.notifyDataSetChanged();

        }

        @Override
        protected Void doInBackground(Object... params) {
            while (datas==null){Log.i("ClassSearchTask","waiting for map");}
            if (classInfos != null) {
                Log.i("ClassSearchTask", "array list classInfos is OK. Initialize extractionURLs");
                //extractionUrls = new ExtructionURLs(classInfos, AcademicSeason.FALL, AcademicYear._2016, datas);
                producersList = new ExtructionURLs(classId,datas).getProducers();
                if (producersList==null){
                    //isDone=true;
                    Log.i("ClassSearchTask", "Class not found");
                    return null;

                }
                Log.i("ClassSearchTask", "found it ");
                for (Producers p : producersList) {
                    ArrayList<String> al = p.getResult();
                    if (p instanceof ProducersTearcherInfo) {
                        for (String s : al) {
                            Log.i("teacher", s);
                        }
                    } else if (p instanceof ProducersBooksInfo) {
                        for (String s : al) {
                            Log.i("books", s);
                        }
                    } else if (p instanceof ProducersClassDescription) {
                        for (String s : al) {
                            Log.i("class description", s);
                        }
                    }
                    //isDone = true;
                }
            }return null;
        }
    }

    private class LoadingData extends AsyncTask<Object,Void,Void>{
        long startTime;
        //ProgressDialog pDialog;
        ArrayList<HashMap<String,ArrayList<String>>> dataMap;
        private String filename = "DATA.txt";

        public LoadingData() {
            startTime = System.currentTimeMillis();
            //pDialog=new ProgressDialog(MainActivity.this);
            this.dataMap = null;
        }



        @Override
        protected Void doInBackground(Object... params) {

            AssetManager am = getApplicationContext().getAssets();
            try {
                InputStream is = am.open(filename);
                InputStreamReader isr= new InputStreamReader(is);
                dataMap=putInMap(isr);
            } catch (IOException e) {
                Log.wtf("Main.LoadingData","DATA.txt not found");
            }



            return null;
        }


//        @Override
//        protected void onPreExecute() {
//            pb.setVisibility(View.VISIBLE);
//        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            if(pDialog.isShowing()){
//                pDialog.dismiss();
//            }
            pb.setVisibility(View.INVISIBLE);
            datasMap=dataMap;
            Log.i("Main.LoadingData","Done.\nTakes "+String.valueOf((System.currentTimeMillis()-startTime)/1000.0)+"s.");
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            pb.setVisibility(View.INVISIBLE);
        }

        private ArrayList<HashMap<String,ArrayList<String>>> putInMap(InputStreamReader isr) throws IOException {
            ArrayList<HashMap<String,ArrayList<String>>> data = new ArrayList<>(terms.length);
            HashMap<String,ArrayList<String>> hm = new HashMap<>();
            BufferedReader br = new BufferedReader(isr);
            String temp;
            String title=null;
            ArrayList<String> tempArray=new ArrayList<>();
            String updateDate = br.readLine().split(" ")[0];
            int counter=1;
            while((temp=br.readLine())!=null ){
                //Log.i("DataLoader",temp);
                if(counter%14==1){
                    title=temp;
                    if(title.length()>updateDate.length()){
                        if(title.substring(0,updateDate.length()).equals(updateDate)){
                            counter--;
                            if (datas==null){
                                datas=hm;
                                publishProgress();


                            }
                            data.add(hm);
                            hm = new HashMap<>();

                        }

                    }
                }else if(counter%14==0){
                    Log.i("DataLoader",String.valueOf(hm.size())+" "+title);
                    if(!temp.equals(".")){
                        tempArray.add(temp);
                    }
                    ArrayList<String> tt =new ArrayList<String>();
                    tt.addAll(tempArray);
                    hm.put(title,tt);

                    tempArray.clear();
                    Log.i("DataLoader","key: "+title);
                    Log.i("DataLoader","list size "+String.valueOf(tt.size()));
                }else{
                    if(!temp.equals(".")){
                        tempArray.add(temp);
                    }
                }
                counter++;
            }
            Log.i("DataLoader","The size of the map is "+ hm.size());

            br.close();
            return data;
        }




    }

}
