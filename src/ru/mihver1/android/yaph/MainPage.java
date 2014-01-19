package ru.mihver1.android.yaph;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ru.mihver1.android.yaph.db.ImageStorage;
import ru.mihver1.android.yaph.db.UrlRecord;
import ru.mihver1.android.yaph.gui.LandscapeRowAdapter;
import ru.mihver1.android.yaph.gui.PortraitRowAdapter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainPage extends Activity {
    /**
     * Called when the activity is first created.
     */

    private String FLICKR_API_KEY;

    // http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    ArrayList<String> urls;
    ArrayList<String> fullscreenUrls = new ArrayList<String>();
    ArrayList<UrlRecord> session_cache = null;

    public void getImagesFromFlickrToDB() throws ExecutionException, InterruptedException {
        fullscreenUrls.clear();
        Log.d("YOLO", "test");
        new FlickrTopImages().execute(FLICKR_API_KEY);
        Log.d("YOLO", "test");
    }

    class FlickrTopImages extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> answer = new ArrayList<String>();
            String api_key = params[0];
            String request_string = "http://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key="
                    +api_key+"&per_page=20&extras=url_m,url_l";
            HttpGet uri = new HttpGet(request_string);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse resp = null;
            try {
                resp = client.execute(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert resp != null;
            StatusLine status = resp.getStatusLine();
            if (status.getStatusCode() != 200) {
                Log.d("YOLO", "HTTP error, invalid server status code: " + resp.getStatusLine());
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            try {
                Document doc = builder != null ? builder.parse(resp.getEntity().getContent()) : null;
                NodeList list = null;
                if (doc != null) {
                    list = doc.getElementsByTagName("photo");
                }
                if (list != null) {
                    for(int i = 0; i < list.getLength(); ++i) {
                        Node node = list.item(i);
                        if(node instanceof Element) {
                            Element child = (Element) node;
                            String url = child.getAttribute("url_m");
                            answer.add(url);
                            fullscreenUrls.add(child.getAttribute("url_l"));
                            Log.d("YOLO", "M "+url);
                            Log.d("YOLO", "L "+child.getAttribute("url_l"));
                        }
                    }
                }
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("YOLO", "COMPLETE");
            return answer;

        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            Log.d("YOLO", "onPostExecute");
            urls = strings;
            Log.d("YOLO", "Size " + Integer.toString(strings.size()));

            ListView listView = (ListView) findViewById(R.id.listView);

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                PortraitRowAdapter adapter = new PortraitRowAdapter(MainPage.this, urls, fullscreenUrls, cache, null);
                listView.setAdapter(adapter);
            } else {
                LandscapeRowAdapter adapter = new LandscapeRowAdapter(MainPage.this, urls, fullscreenUrls, cache, null);
                listView.setAdapter(adapter);
            }
        }
    }

    public IconCache cache;
    public ImageStorage database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FLICKR_API_KEY = getString(R.string.flickr_key);

        database = new ImageStorage(this);
        cache = new IconCache(database);

        ListView listView = (ListView) findViewById(R.id.listView);
        View header = getLayoutInflater().inflate(R.layout.header, null);
        listView.addHeaderView(header);
        Button btn = (Button)header.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(isOnline()) {
                        database.dropCache();
                        getImagesFromFlickrToDB();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        if(isOnline()) {
            try {
                database.dropCache();
                getImagesFromFlickrToDB();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            session_cache = database.getCache();
            if(session_cache != null) {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    PortraitRowAdapter adapter = new PortraitRowAdapter(MainPage.this, null, null, null, session_cache);
                    listView.setAdapter(adapter);
                } else {
                    LandscapeRowAdapter adapter = new LandscapeRowAdapter(MainPage.this, null, null, null, session_cache);
                    listView.setAdapter(adapter);
                }
            } else {
                Toast.makeText(this, R.string.nodata, 2).show();
            }
        }


    }
}
