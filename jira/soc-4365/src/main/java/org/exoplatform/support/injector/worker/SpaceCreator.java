package org.exoplatform.support.injector.worker;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by eXo Platform MEA on 26/08/14.
 *
 * @author <a href="mailto:mtrabelsi@exoplatform.com">Marwen Trabelsi</a>
 */
public class SpaceCreator implements Runnable
{
  private final Logger LOG = LoggerFactory.getLogger("org.exoplatform.support.injector.worker.SpaceCreator");
  private static final char EQUAL_CHAR = '=';
  private static final char AMPERSAND = '&';
  private final String SPACE_INJECTION_FINAL_URL;
  private final CloseableHttpClient httpClient;

  public SpaceCreator(CloseableHttpClient httpClient,
                      String spaceInjectionURL,
                      Map<String,String> queryParams)
  {
    this.httpClient = httpClient;
    StringBuilder urlBuilder = new StringBuilder(spaceInjectionURL);
    boolean isFirstParam = true;
    for(Map.Entry param : queryParams.entrySet())
    {
      if(isFirstParam)
      {
        urlBuilder.append("?");
        isFirstParam = false;
      } else
      {
        urlBuilder.append(AMPERSAND);
      }
      urlBuilder.append(param.getKey()).append(EQUAL_CHAR).append(param.getValue());
    }
    SPACE_INJECTION_FINAL_URL = urlBuilder.toString();
  }

  @Override
  public void run()
  {
    CloseableHttpResponse response;
    try
    {
      HttpGet injectRequest = new HttpGet(SPACE_INJECTION_FINAL_URL);
      response = httpClient.execute(injectRequest);
      try {
        HttpEntity entity = response.getEntity();

        LOG.info("----------------------------------------");
        if (response.getStatusLine().getStatusCode() == 200)
        {
          LOG.info(String.valueOf(response.getStatusLine()));
          if (entity != null) {
            LOG.info("Response content length: " + entity.getContentLength());
          }
          LOG.info(Thread.currentThread().getName() + " has injected a space successfully ");
        }
        LOG.info("----------------------------------------");
      } finally {
        response.close();
      }
    } catch (UnsupportedEncodingException e)
    {
      LOG.error("An error occurred while injecting the space:", e);
    } catch (ClientProtocolException e)
    {
      LOG.error("An error occurred while injecting the space:", e);
    } catch (IOException e)
    {
      LOG.error("An error occurred while injecting the space:", e);
    }
  }
}
