package com.alcatel.smartlinkv3.samba;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import org.cybergarage.http.HTTPRequest;
import org.cybergarage.http.HTTPResponse;
import org.cybergarage.http.HTTPServerList;
import org.cybergarage.http.HTTPStatus;

import com.alcatel.smartlinkv3.common.file.FileUtils;


public class SmbHttpServer extends Thread implements org.cybergarage.http.HTTPRequestListener
{
	
	public static final String CONTENT_EXPORT_URI = "/smb";

	private HTTPServerList httpServerList = new HTTPServerList();
	
	public int HTTPPort = 9011;

	
	public int getHTTPPort()
	{
		return HTTPPort;
	}

	public void setHTTPPort(int hTTPPort)
	{
		HTTPPort = hTTPPort;
	}

	public HTTPServerList getHttpServerList()
	{
		return httpServerList;
	}

	@Override
	public void run()
	{
		super.run();

		int retryCnt = 0;

		int bindPort = getHTTPPort();

		HTTPServerList hsl = getHttpServerList();
		while (hsl.open(bindPort) == false)
		{
			retryCnt++;
		
			if (100 < retryCnt)
			{
				return;
			}
			setHTTPPort(bindPort + 1);
			bindPort = getHTTPPort();
		}
		
		hsl.addRequestListener(this);
	
		hsl.start(); 		
		
		SmbUtils.port =  bindPort;
		 
	}

	@Override
	public void httpRequestRecieved(HTTPRequest httpReq)
	{

		String uri = httpReq.getURI();

		if (uri.startsWith(CONTENT_EXPORT_URI) == false)
		{
			httpReq.returnBadRequest();
			return;
		}

		try
		{
			uri = URLDecoder.decode(uri, "UTF-8");
		}
		catch (UnsupportedEncodingException e1)
		{ 
			e1.printStackTrace();
		}
	
		String filePaths = "smb://" + uri.substring(5);
		
	
		int indexOf = filePaths.indexOf("&");
		
        if (indexOf != -1)
        {
        	filePaths = filePaths.substring(0, indexOf);
        }
		

		try
		{
			SmbFile file = new SmbFile(filePaths, SmbUtils.AUTH);
			
			
			long contentLen = file.length();
			
			String contentType = FileUtils.getMimeType(filePaths);
		
			InputStream contentIn = file.getInputStream();

			if (contentLen <= 0 || contentType.length() <= 0
					|| contentIn == null)
			{
				httpReq.returnBadRequest();
				return;
			} 		 
			
			HTTPResponse httpRes = new HTTPResponse();
			httpRes.setContentType(contentType);
			httpRes.setStatusCode(HTTPStatus.OK);
			httpRes.setContentLength(contentLen);
			httpRes.setContentInputStream(contentIn);

			httpReq.post(httpRes);

		    contentIn.close(); 
		}
		catch (MalformedURLException e)
		{
			httpReq.returnBadRequest();
			return;
		}
		catch (SmbException e)
		{
			httpReq.returnBadRequest();
			return;
		}
		catch (IOException e)
		{
			httpReq.returnBadRequest();
			return;
		}

	}

}
