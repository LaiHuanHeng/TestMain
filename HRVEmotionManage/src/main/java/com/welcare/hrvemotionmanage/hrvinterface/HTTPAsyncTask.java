package com.welcare.hrvemotionmanage.hrvinterface;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

public class HTTPAsyncTask extends AsyncTask<Object, Integer, String>{
	  private RequestCallback rCallback;
	  
	  public HTTPAsyncTask(@NonNull RequestCallback rCallback) {
		  this.rCallback = rCallback;
	  }
	  
	
      @Override
      protected void onCancelled() {
          //当任务被取消时回调
          super.onCancelled();
          rCallback.onError("Task cancellation 任务取消");
      }

      @Override
      protected void onProgressUpdate(Integer... values) {
          super.onProgressUpdate(values);
          //更新进度
//          mProgressBar.setProgress(values[0]);
          rCallback.onProgress(values[0]);
      }

    
	
	
	
	@Override
	protected String doInBackground(Object... arg0) {
		// TODO  这是在后台子线程中执行的
		JSONObject result = new JSONObject();
		try{
			result.put("type",-2);//0或以上成功,0以下失败
			String data = "";
			int type = -2;
			if(arg0.length >= 2)
			{
				String url = (String)arg0[0];
				@SuppressWarnings("unchecked")
				List<NameValuePair> nvps = (List<NameValuePair>)arg0[1];
				if(arg0.length>=3)
				{
					
					if(arg0[2] instanceof byte[])
					{
						type = 0;
						data = requestAsyncHttpFileClient(url, nvps, (byte[])arg0[2]);
						
						
					}else if(arg0[2] instanceof File)
					{
						type = 0;
						data = requestAsyncHttpFileClient(url, nvps, (File)arg0[2]);
					}else
					{
						type = -3;
						data = "Uploading third parameter type errors 上传第三个参数类型错误";	
						
					}
				}else
				{
					type = 0;
					data = requestAsyncHttpClient(url, nvps);
				}
				
			}
			result.put("type",type);
			result.put("value",data);
			 
				
		}catch(Exception e)
		{
			e.printStackTrace();
			try{
				result.put("type",-4);
				result.put("value",e.getMessage()+"");
			}catch(JSONException e1)
			{
				e1.printStackTrace();
				return "{\"type\":\"-5\",\"value\":\""+e1.getMessage()+"\"}";
				
			}
		}
		
        
		return result.toString();
	}
	
	
	
	
	@Override
	protected void onPostExecute(String result) {
		// TODO  当任务执行完成是调用,在UI线程
		super.onPostExecute(result);
		try{
			JSONObject json = new JSONObject(result);
			String value = json.getString("value").length()<=0?result:json.getString("value");
			int type = json.getInt("type");
			if(type>=0)
			{
				rCallback.onSuccess(value);
					
			}
			else
			{
				rCallback.onError(value);
			}
			
			rCallback.onCode(type);
		}catch(Exception e)
		{
			e.printStackTrace();
			rCallback.onError(e.getMessage()+"");
			rCallback.onCode(-6);
		}
	}
	
	
	 /**post提交
     * @param url
     * @param url
     * @param pairList
     */
    private  String  requestAsyncHttpClient(String url,List<NameValuePair> pairList)
    {
    	
    	try {
    		
	    	HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
				         pairList);
			
	         // URL使用基本URL即可，其中不需要加参数
	         HttpPost httpPost = new HttpPost(url);
	         // 将请求体内容加入请求中
	         httpPost.setEntity(requestHttpEntity);
	         // 需要客户端对象来发送请求
	         HttpClient httpClient = new DefaultHttpClient();
	         // 发送请求
	         HttpResponse response = httpClient.execute(httpPost);
	         
	         HttpEntity httpEntity = response.getEntity();
	        
	         InputStream inputStream = httpEntity.getContent();
	         
	         BufferedReader reader = new BufferedReader(new InputStreamReader(
	                 inputStream));
	         String result = "";
	         String line = "";
	         while (null != (line = reader.readLine()))
	         {
	             result += line;
	
	         }
	         return result;
         
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage()+"";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage()+"";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage()+"";
		} catch (Exception e)
		{
			e.printStackTrace();
			return e.getMessage()+"";
		}
    	
    }
    /** 提交文件
     * @param url  接口地址
     * @param paramsMap 请求参数
     * @param datas 上传的byte数据
     */
    private String requestAsyncHttpFileClient(String url,List<NameValuePair> paramsMap,byte[] datas) {
    	StringBuilder tempParams = new StringBuilder();
    		
    	 String base_URL = url;
    	 
		try{       
				//处理参数
		            int pos = 0;
		           for (NameValuePair nvp : paramsMap) {
		        	  if (pos > 0) {
		                    tempParams.append("&");
		                }
		                //对参数进行URLEncoder
		                tempParams.append(String.format("%s=%s", nvp.getName(),nvp.getValue()));
		                pos++;
	
		           }
		           String requestUrl = String.format("%s?%s", base_URL,tempParams.toString());
		           
		           PrintWriter out = null;
			        BufferedReader in = null;
			        String result = ""; 
			        try {
			            URL realUrl = new URL(requestUrl);
			            URLConnection conn = realUrl.openConnection();
	//		            //转换为字节数组
	//		            byte[] data = (obj.toString()).getBytes();
	//		            // 设置文件长度
	//		            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
	//
	//		            // 设置文件类型:
	//		            conn.setRequestProperty("contentType", "application/json");
			            conn.setRequestProperty("Content-Type", "application/octet-stream");
			            conn.setDoOutput(true);
			            conn.setDoInput(true);
			            conn.getOutputStream().write(datas);
			            
			            conn.connect();
			            
			            in = new BufferedReader(
			                    new InputStreamReader(conn.getInputStream(),"utf-8"));
			            String line;
			            while ((line = in.readLine()) != null) {
			                result += line;
			            }
			         
			            return result;
			        } catch (Exception e) {
			            e.printStackTrace();
			            return e.getMessage()+"";
			        }
			        finally{
			            try{
			                if(out!=null){
			                    out.close();
			                }
			                if(in!=null){
			                    in.close();
			                }
			            }
			            catch(IOException ex){
			                ex.printStackTrace();
			            }
			        }
			         
		            
	
			    	
		}catch(Exception e)
		{
	//		Logs.e("网络请求失败", e.getMessage());
			return e.getMessage()+"";
		}

    }
    /** 提交文件
     * @param url  接口地址
     * @param paramsMap 请求参数
     * @param file 上传的文件
     */
    private  String requestAsyncHttpFileClient(String url,List<NameValuePair> paramsMap,File file) 
    {
    	return requestAsyncHttpFileClient(url, paramsMap, File2byte(file));
    }
    
    /**file转byte**/
    private byte[] File2byte(File file )  
	 {  
	        byte[] buffer = null;  
	        try  
	        {  
	            FileInputStream fis = new FileInputStream(file);  
	            ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	            byte[] b = new byte[1024];  
	            int n;  
	            while ((n = fis.read(b)) != -1)  
	            {  
	                bos.write(b, 0, n);  
	            }  
	            fis.close();  
	            bos.close();  
	            buffer = bos.toByteArray();  
	        }  
	        catch (FileNotFoundException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        catch (IOException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return buffer;  
	 }
	 
}
