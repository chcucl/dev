package com.testcode.musicsite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class MusicListLoader
{
	private static final String MUSIC_URL = "http://api.androidhive.info/music/music.xml";
	
	private MusicImageCache mImageCache;
	
	public MusicListLoader(Context context) {
		mImageCache = new MusicImageCache(context);
	}
	
	public List<MusicItem> loadMusicList() {
		
		List<MusicItem> list = new ArrayList<MusicItem>();
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MUSIC_URL);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String xml = EntityUtils.toString(httpEntity);
			
			List<MusicNode> nodeList = parserXml(xml);
			ListIterator<MusicNode> itr = nodeList.listIterator();
			
			for (; itr.hasNext(); ) {
				
				MusicNode musicNode = itr.next();
				Bitmap bitmap = null;
				
				// first, try to load it from file
				
				bitmap = mImageCache.loadImageFromFile(musicNode.ThumbURL);
				
				// load failed, try to load it from net.
				if (bitmap == null) {
					URL imageURL = new URL(musicNode.ThumbURL);
					HttpURLConnection URLConn = (HttpURLConnection)imageURL.openConnection();
					URLConn.setConnectTimeout(3000);
					URLConn.setReadTimeout(3000);
					InputStream instream = URLConn.getInputStream();
	
					if (instream != null) {
					
						/*
						 * NOTE: here needs to convert input stream to byte array. 
						 * Because the input stream maybe closed by BitmapFactory.decodeStream
						 * */
						
						byte[] array = readStream(instream);
						bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
						
						// store bitmap to file
						mImageCache.saveImageToFile(musicNode.ThumbURL, array, array.length);
					}
				}
				
				MusicItem musicItem = new MusicItem(musicNode.Id, musicNode.Title, musicNode.Artist, musicNode.Duration, bitmap);
			
				list.add(musicItem);
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

	private byte[] readStream(InputStream is) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		
		outStream.close(); // ???
		is.close();
		
		return outStream.toByteArray();
	}
	
	class MusicNode {
		public String Id;
		public String Title;
		public String Artist;
		public String Duration;
		public String ThumbURL;
	}
	
	private final String KEY_ID = "id";
	private final String KEY_TITLE = "title";
	private final String KEY_ARTIST = "artist";
	private final String KEY_DURATION = "duration";
	private final String KEY_URL = "thumb_url";

	List<MusicNode> parserXml(final String xml) {
		List<MusicNode> list = new ArrayList<MusicNode>();
		
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xml));
			doc = docBuilder.parse(inputSource);
			
			NodeList nodeList = doc.getElementsByTagName("song");
			final int nodeListLen = nodeList.getLength();
			for (int i = 0; i < nodeListLen; ++i) {
				MusicNode musicNode = null;
				
				Element element = (Element)nodeList.item(i);
				if (element.hasChildNodes()) {
					musicNode = decodeMusicNode(element);
					list.add(musicNode);
				/*	
					Log.v("musicnode", "id: " + musicNode.Id +
							", title " + musicNode.Title +
							", artist: " + musicNode.Artist +
							", time: " + musicNode.Duration +
							", URL: " + musicNode.ThumbURL);
				*/
				}
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	public MusicNode decodeMusicNode(Element element) {
		MusicNode musicNode = new MusicNode();
		
		for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
			
		//	Log.v("decode", "name: " + child.getNodeName() + " value: " + getElementValue(child));
			
			if (child.getNodeName().equals(KEY_ID)) {
				musicNode.Id = getElementValue(child);
			}
			else if (child.getNodeName().equals(KEY_TITLE)) {
				musicNode.Title = getElementValue(child);
			}
			else if (child.getNodeName().equals(KEY_ARTIST)) {
				musicNode.Artist = getElementValue(child);
			}
			else if (child.getNodeName().equals(KEY_DURATION)) {
				musicNode.Duration = getElementValue(child);
			}
			else if (child.getNodeName().equals(KEY_URL)) {
				musicNode.ThumbURL = getElementValue(child);
			}
		}
		
		return musicNode;
	}
	
	public String getElementValue(Node node) {
		Node child;
		if (node != null) {
			if (node.hasChildNodes()) {
				for (child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		
		return "";
	}
}
