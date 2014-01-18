package ru.mihver1.android.yaph;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
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
import ru.mihver1.android.yaph.gui.PortraitRowAdapter;
import ru.mihver1.android.yaph.gui.WebImageView;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainPage extends Activity {
    /**
     * Called when the activity is first created.
     */

    private String FLICKR_API_KEY;
    private FlickrTopImages fti;

    // http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-timeouts
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    ArrayList<WebImageView> images;
    ArrayList<String> urls;

    private void getImagesFromFlickrToDB(ImageStorage is) throws ExecutionException, InterruptedException {
        fti = new FlickrTopImages();
        Log.d("YOLO", "test");
        fti.setCtx(this, is);
        fti.execute(FLICKR_API_KEY);
        Log.d("YOLO", "test");

    }

    class FlickrTopImages extends AsyncTask<String, Void, ArrayList<String>> {

        Activity ctx;
        ImageStorage us;

        void setCtx(Activity ctx1, ImageStorage is) {
            ctx = ctx1;
            us = is;
        }



        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> answer = new ArrayList<String>();
            String api_key = params[0];
            String request_string = "http://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key="
                    +api_key+"&per_page=20&extras=url_o";
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
                NodeList list = doc.getElementsByTagName("photo");
                for(int i = 0; i < list.getLength(); ++i) {
                    Node node = list.item(i);
                    if(node instanceof Element) {
                        Element child = (Element) node;
                        String url = child.getAttribute("url_o");
                        answer.add(url);

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
            if(getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_PORTRAIT) {
                PortraitRowAdapter adapter = new PortraitRowAdapter(ctx, urls, us);
                listView.setAdapter(adapter);
            }
        }
    }

    public ImageStorage is = new ImageStorage();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FLICKR_API_KEY = getString(R.string.flickr_key);

        if(isOnline()) {
            try {
                getImagesFromFlickrToDB(is);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
